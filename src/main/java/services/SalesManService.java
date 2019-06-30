
package services;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import domain.Box;
import domain.Product;
import domain.SalesMan;
import forms.FormObjectSalesman;
import repositories.SalesManRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;

@Service
@Transactional
public class SalesManService {

	@Autowired
	private SalesManRepository salesManRepository;

	@Autowired
	private WardenService wardenService;

	@Autowired
	private BoxService boxService;

	@Autowired
	private PrisonerService prisonerService;

	@Autowired
	private ProductService productService;

	@Autowired
	private Validator validator;

	public SalesMan loggedSalesMan() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
		Assert.isTrue(authorities.get(0).toString().equals("SALESMAN"));
		return this.salesManRepository.getSalesManByUsername(userAccount.getUsername());
	}

	public void loggedAsSalesMan() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
		Assert.isTrue(authorities.get(0).toString().equals("SALESMAN"));
	}

	public List<SalesMan> findAll() {
		return this.salesManRepository.findAll();
	}

	public SalesMan findOne(int salesManId) {
		return this.salesManRepository.findOne(salesManId);
	}

	public SalesMan save(SalesMan salesMan) {
		return this.salesManRepository.save(salesMan);
	}

	public SalesMan create() {

		// SE DECLARA EL WARDEN
		SalesMan s = new SalesMan();

		// SE CREAN LAS LISTAS VACIAS
		List<Box> boxes = new ArrayList<Box>();
		List<Product> products = new ArrayList<Product>();

		// SE AÑADE EL USERNAME Y EL PASSWORD
		UserAccount userAccountActor = new UserAccount();
		userAccountActor.setUsername("");
		userAccountActor.setPassword("");

		// SE AÑADEN TODOS LOS ATRIBUTOS
		s.setName("");
		s.setMiddleName("");
		s.setSurname("");
		s.setPhoto("");
		s.setPoints(0);
		s.setStoreName("");
		s.setVATNumber("");
		s.setBoxes(boxes);
		s.setProducts(products);

		List<Authority> authorities = new ArrayList<Authority>();

		Authority authority = new Authority();
		authority.setAuthority(Authority.SALESMAN);
		authorities.add(authority);

		userAccountActor.setAuthorities(authorities);
		// NOTLOCKED A TRUE EN LA INICIALIZACION, O SE CREARA UNA CUENTA BANEADA
		userAccountActor.setIsNotLocked(true);

		s.setUserAccount(userAccountActor);
		return s;
	}

	public void saveSalesman(SalesMan salesman) {

		if (salesman.getId() == 0) {
			this.wardenService.loggedAsWarden();

			List<Box> boxes = new ArrayList<>();

			// Boxes
			Box box1 = this.boxService.createSystem();
			box1.setName("SUSPICIOUSBOX");
			Box saved1 = this.boxService.saveSystem(box1);
			boxes.add(saved1);

			Box box2 = this.boxService.createSystem();
			box2.setName("TRASHBOX");
			Box saved2 = this.boxService.saveSystem(box2);
			boxes.add(saved2);

			Box box3 = this.boxService.createSystem();
			box3.setName("OUTBOX");
			Box saved3 = this.boxService.saveSystem(box3);
			boxes.add(saved3);

			Box box4 = this.boxService.createSystem();
			box4.setName("INBOX");
			Box saved4 = this.boxService.saveSystem(box4);
			boxes.add(saved4);

			salesman.setBoxes(boxes);
		} else {
			SalesMan s = this.loggedSalesMan();
			Assert.isTrue(s.getId() == salesman.getId());
		}

		this.salesManRepository.save(salesman);
	}

	public SalesMan reconstruct(FormObjectSalesman formSalesman, BindingResult binding) {
		SalesMan result = this.create();

		result.setName(formSalesman.getName());
		result.setMiddleName(formSalesman.getMiddleName());
		result.setSurname(formSalesman.getSurname());
		result.setPhoto(formSalesman.getPhoto());
		result.setStoreName(formSalesman.getStoreName());
		result.setVATNumber(formSalesman.getVATNumber());

		// USER ACCOUNT
		UserAccount userAccount = new UserAccount();

		// Authorities
		List<Authority> authorities = new ArrayList<Authority>();
		Authority authority = new Authority();
		authority.setAuthority(Authority.SALESMAN);
		authorities.add(authority);
		userAccount.setAuthorities(authorities);

		// locked
		userAccount.setIsNotLocked(true);

		// Username
		userAccount.setUsername(formSalesman.getUsername());

		// Password
		Md5PasswordEncoder encoder;
		encoder = new Md5PasswordEncoder();
		userAccount.setPassword(encoder.encodePassword(formSalesman.getPassword(), null));

		result.setUserAccount(userAccount);

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

		// Confirmacion contrasena
		if (!formSalesman.getPassword().equals(formSalesman.getConfirmPassword()))
			if (locale.contains("ES"))
				binding.addError(new FieldError("formSalesman", "password", formSalesman.getPassword(), false, null,
						null, "Las contrasenas no coinciden"));
			else
				binding.addError(new FieldError("formSalesman", "password", formSalesman.getPassword(), false, null,
						null, "Passwords don't match"));

		// Confirmacion terminos y condiciones
		if (!formSalesman.getTermsAndConditions())
			if (locale.contains("ES"))
				binding.addError(
						new FieldError("formSalesman", "termsAndConditions", formSalesman.getTermsAndConditions(),
								false, null, null, "Debe aceptar los terminos y condiciones"));
			else
				binding.addError(
						new FieldError("formSalesman", "termsAndConditions", formSalesman.getTermsAndConditions(),
								false, null, null, "You must accept the terms and conditions"));

		return result;
	}

	public List<SalesMan> getSalesMenAsWarden() {
		this.wardenService.loggedAsWarden();
		return this.findAll();
	}

	public SalesMan reconstruct(SalesMan salesman, BindingResult binding) {
		SalesMan result = this.create();
		SalesMan founded = this.findOne(salesman.getId());

		result = salesman;

		result.setVersion(founded.getVersion());
		result.setBoxes(founded.getBoxes());
		result.setPoints(founded.getPoints());
		result.setProducts(founded.getProducts());
		result.setUserAccount(founded.getUserAccount());

		this.validator.validate(result, binding);

		return result;
	}

	public void deleteLoggedSalesman() {
		SalesMan salesman = this.loggedSalesMan();

		List<Product> products = salesman.getProducts();

//		List<Prisoner> prisoners = this.prisonerService.getPrisonersWithProductsOfASalesMan(salesman.getId());
//
//		for (Prisoner p : prisoners) {
//			List<Product> productsOfPrisoner = p.getProducts();
//			productsOfPrisoner.removeAll(products);
//			p.setProducts(productsOfPrisoner);
//
//			this.prisonerService.save(p);
//		}

//		salesman.setProducts(new ArrayList<Product>());
//		this.save(salesman);

		for (Product p : products)
			this.productService.deleteProductToDeleteSalesman(p);

		this.salesManRepository.delete(salesman);
	}

	public SalesMan getSalesManOfProduct(int productId) {
		return this.salesManRepository.getSalesManOfProduct(productId);
	}

	public List<Product> getProductsOfLoggedSalesman() {
		SalesMan salesman = this.loggedSalesMan();
		List<Product> products = salesman.getProducts();
		return products;
	}

	public void flush() {
		this.salesManRepository.flush();

	}

	public SalesMan findSalesManByUsername(String username) {
		return this.salesManRepository.getSalesManByUsername(username);
	}

}
