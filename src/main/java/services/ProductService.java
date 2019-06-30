
package services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import domain.Configuration;
import domain.Prisoner;
import domain.Product;
import domain.SalesMan;
import repositories.ProductRepository;

@Service
@Transactional
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private SalesManService salesManService;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private PrisonerService prisonerService;

	@Autowired
	private Validator validator;

	public List<Product> getProductsFinalModeWithStock() {
		return this.productRepository.getProductsFinalModeWithStock();
	}

	public List<Product> getProductsFinalMode() {
		return this.productRepository.getProductsFinalMode();
	}

	public List<Product> getProductsFinalModeOfSalesMen() {
		return this.productRepository.getProductsFinalModeOfSalesMen();
	}

	public List<Product> getProductsFinalModeWithStockBySalesMan(int salesManId) {
		return this.productRepository.getProductsFinalModeWithStockBySalesMan(salesManId);
	}

	public Product create() {
		Product product = new Product();

		product.setDescription("");
		product.setIsDraftMode(true);
		product.setName("");
		product.setPrice(1);
		product.setStock(1);
		product.setQuantity(0);
		product.setType(null);

		return product;
	}

	public Product save(Product product) {
		return this.productRepository.save(product);
	}

	public Product findOne(int productId) {
		return this.productRepository.findOne(productId);
	}

	public Product reconstruct(Product product, BindingResult binding) {
		Product result = this.create();
		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

		if (product.getId() == 0)
			result = product;
		else {
			Product copy = this.findOne(product.getId());

			if (copy.getIsDraftMode()) {

				result = product;
				result.setVersion(copy.getVersion());
				result.setId(copy.getId());

			} else {

				result = product;
				result.setDescription(copy.getDescription());
				result.setIsDraftMode(copy.getIsDraftMode());
				result.setName(copy.getName());
				result.setPrice(copy.getPrice());

				result.setType(copy.getType());
				result.setVersion(copy.getVersion());

				if (result.getStock() < copy.getStock())
					if (locale.contains("ES"))
						binding.addError(new FieldError("product", "stock", product.getStock(), false, null, null,
								"La cantidad no puede ser menor a la anterior"));
					else
						binding.addError(new FieldError("product", "stock", product.getStock(), false, null, null,
								"Stock can not be less than before"));

			}

		}

		this.validator.validate(result, binding);
		return result;
	}

	public void addProduct(Product pro) {
		SalesMan salesman = this.salesManService.loggedSalesMan();
		Configuration configuration = this.configurationService.getConfiguration();
		Assert.isTrue(configuration.getTypeProducts().contains(pro.getType()) && pro.getId() == 0);
		Product product = this.save(pro);
		salesman.getProducts().add(product);
		this.salesManService.save(salesman);
	}

	public void updateProduct(Product pro) {
		SalesMan salesman = this.salesManService.loggedSalesMan();
		Configuration configuration = this.configurationService.getConfiguration();
		Assert.notNull(this.productRepository.getProductInDraftModeOfLoggedSalesMan(salesman.getId(), pro.getId()));
		Assert.isTrue(configuration.getTypeProducts().contains(pro.getType()) && salesman.getProducts().contains(pro));
		this.save(pro);
	}

	public void restockProduct(Product pro) {
		SalesMan salesman = this.salesManService.loggedSalesMan();
		Configuration configuration = this.configurationService.getConfiguration();
		Product product = this.productRepository.getProductInFinalModeOfLoggedSalesMan(salesman.getId(), pro.getId());
		Assert.notNull(product);
		Assert.isTrue(configuration.getTypeProducts().contains(pro.getType()) && salesman.getProducts().contains(pro));
		Assert.isTrue(pro.getStock() >= product.getStock());
		this.save(pro);
	}

	public void delete(Product product) {
		SalesMan salesman = this.salesManService.loggedSalesMan();
		Assert.isTrue(product.getIsDraftMode() && salesman.getProducts().contains(product));
		salesman.getProducts().remove(product);
		this.salesManService.save(salesman);
		this.productRepository.delete(product);

	}

	public void deleteProductToDeleteSalesman(Product product) {
		this.productRepository.delete(product);
	}

	public Product getProductAsPrisonerToBuy(int productId) {
		this.prisonerService.loggedAsPrisoner();
		Product product = this.findOne(productId);
		Assert.notNull(product);
		Assert.isTrue(product.getIsDraftMode() == false && product.getStock() > 0);
		return product;
	}

	public void buyProductAsPrisoner(int productId, int quantity) {
		Prisoner prisoner = this.prisonerService.securityAndPrisoner();
		Product product = this.findOne(productId);

		// Comprobaciones basicas
		Assert.notNull(product);
		Assert.isTrue(product.getIsDraftMode() == false);
		Assert.isTrue(product.getStock() > 0);

		// Comprobaciones de limite de stock y puntos del prisionero
		Assert.isTrue(quantity <= product.getStock());
		Assert.isTrue((product.getPrice() * quantity) <= prisoner.getPoints());
		Assert.isTrue(quantity > 0);

		// Aumentamos los puntos del vendedor
		SalesMan salesman = this.salesManService.getSalesManOfProduct(productId);
		Integer totalPointsOfSM = salesman.getPoints() + (product.getPrice() * quantity);
		salesman.setPoints(totalPointsOfSM);
		this.salesManService.save(salesman);

		// Reducimos el stock del producto
		Integer totalStock = product.getStock() - quantity;
		product.setStock(totalStock);
		this.save(product);

		// Creamos y guardamos el nuevo producto copia del prisionero
		Product productOfP = new Product();
		productOfP.setName(product.getName());
		productOfP.setDescription(product.getDescription());
		productOfP.setIsDraftMode(product.getIsDraftMode());
		productOfP.setPrice(product.getPrice());
		productOfP.setStock(0);
		productOfP.setType(product.getType());
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MILLISECOND, -1);
		Date moment = c.getTime();
		productOfP.setPurchaseMoment(moment);
		productOfP.setQuantity(quantity);
		Product newProduct = this.save(productOfP);

		// Asignamos la copia del producto al prisionero y reducimos su numero
		// de puntos
		List<Product> productsOfP = prisoner.getProducts();
		productsOfP.add(newProduct);
		Integer totalPointsOfP = prisoner.getPoints() - (product.getPrice() * quantity);
		prisoner.setPoints(totalPointsOfP);
		prisoner.setProducts(productsOfP);
		this.prisonerService.save(prisoner);
	}

	public Product getProductInDraftModeOfLoggedSalesMan(Integer productId) {
		SalesMan salesMan = this.salesManService.loggedSalesMan();
		Product product = this.productRepository.getProductInDraftModeOfLoggedSalesMan(salesMan.getId(), productId);
		Assert.notNull(product);
		return product;
	}

	public Product getProductInFinalModeOfLoggedSalesMan(Integer productId) {
		SalesMan salesMan = this.salesManService.loggedSalesMan();
		Product product = this.productRepository.getProductInFinalModeOfLoggedSalesMan(salesMan.getId(), productId);
		Assert.notNull(product);
		return product;
	}

	public List<Product> findAll() {
		return this.productRepository.findAll();
	}

	public void flush() {
		this.productRepository.flush();
	}

}
