
package services;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Configuration;
import domain.Product;
import domain.SalesMan;
import domain.TypeProduct;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class ProductServiceTest extends AbstractTest {

	@Autowired
	private SalesManService			salesManService;

	@Autowired
	private ProductService			productService;

	@Autowired
	private ConfigurationService	configurationService;


	/**
	 * SENTENCE COVERAGE: - ProductServiceTest = 29.8%
	 */

	/**
	 * R51. An actor who is authenticated as a SalesMan must be able to:
	 * 
	 * 1. Manage his products, which includes listing and showing them.
	 * 
	 * Ratio of data coverage: 100% - Access as a SalesMan or not.
	 * 
	 **/
	@Test
	public void driverListProducts() {

		Object testingData[][] = {

			/**
			 * POSITIVE TEST: SalesMan is listing his or her products
			 **/
			{
				"salesMan1", null
			},
			/**
			 * NEGATIVE TEST: Another user is trying to list his or her
			 * products
			 **/
			{
				"socialWorker1", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.listProductsTemplate((String) testingData[i][0], (Class<?>) testingData[i][1]);
		}

	}

	private void listProductsTemplate(String salesMan, Class<?> expected) {

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(salesMan);

			List<Product> products = this.salesManService.getProductsOfLoggedSalesman();

			Assert.isTrue(this.salesManService.loggedSalesMan().getProducts().equals(products));

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	/**
	 * R51. An actor who is authenticated as a SalesMan must be able to:
	 * 
	 * 1. Manage his products, which includes creating them.
	 * 
	 * Ratio of data coverage: 5/8 = 62.5% - Access as a SalesMan or not. - 6
	 * attributes with restrictions
	 * 
	 **/
	@Test
	public void driverCreateProducts() {

		Configuration configuration = this.configurationService.getConfiguration();
		List<TypeProduct> typeProducts = configuration.getTypeProducts();

		TypeProduct typeProduct = typeProducts.get(0);

		Object testingData[][] = {

			/**
			 * POSITIVE TEST: SalesMan is creating a product with correct
			 * information
			 **/
			{
				"salesMan1", "Name", "Description", typeProduct, 5, 10, true, null
			},
			/**
			 * NEGATIVE TEST: Another user is trying to create a product
			 **/
			{
				"socialWorker1", "Name", "Description", typeProduct, 5, 10, true, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: SalesMan is trying to create a product with
			 * the name in blank
			 **/
			{
				"salesMan1", "", "Description", typeProduct, 5, 10, true, ConstraintViolationException.class
			},
			/**
			 * NEGATIVE TEST: SalesMan is trying to create a product with
			 * the type product as null
			 **/
			{
				"salesMan1", "Name", "Description", null, 5, 10, true, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: SalesMan is trying to create a product with
			 * negative price
			 **/
			{
				"salesMan1", "Name", "Description", typeProduct, -100, 10, true, ConstraintViolationException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.createProductsTemplate((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (TypeProduct) testingData[i][3], (Integer) testingData[i][4], (Integer) testingData[i][5], (Boolean) testingData[i][6],
				(Class<?>) testingData[i][7]);
		}

	}

	private void createProductsTemplate(String salesMan, String name, String description, TypeProduct typeProduct, Integer price, Integer stock, Boolean isDraftMode, Class<?> expected) {

		Product product = new Product();
		product.setName(name);
		product.setDescription(description);
		product.setType(typeProduct);
		product.setPrice(price);
		product.setStock(stock);
		product.setIsDraftMode(isDraftMode);

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(salesMan);

			SalesMan sm = this.salesManService.loggedSalesMan();
			Integer number1 = sm.getProducts().size();

			this.productService.addProduct(product);

			sm = this.salesManService.loggedSalesMan();
			Integer number2 = sm.getProducts().size();

			Assert.isTrue(number1 + 1 == number2);

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	/**
	 * R51. An actor who is authenticated as a SalesMan must be able to:
	 * 
	 * 1. Manage his products, which includes updating them. A product can only
	 * be updated in draft mode.
	 * 
	 * Ratio of data coverage: 7/10 = 70% - Access as a SalesMan or not. - The
	 * product is in draft mode or not - SalesMan owns the product or not - 6
	 * attributes with restrictions
	 * 
	 **/
	@Test
	public void driverUpdateProducts() {

		Configuration configuration = this.configurationService.getConfiguration();
		List<TypeProduct> typeProducts = configuration.getTypeProducts();

		TypeProduct typeProduct = typeProducts.get(0);

		Object testingData[][] = {

			/**
			 * POSITIVE TEST: SalesMan is updating a product with correct
			 * information
			 **/
			{
				"salesMan1", super.getEntityId("product2"), "Name", "Description", typeProduct, 5, 10, true, null
			},
			/**
			 * NEGATIVE TEST: Another user is trying to update a product
			 **/
			{
				"socialWorker1", super.getEntityId("product2"), "Name", "Description", typeProduct, 5, 10, true, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: SalesMan is trying to update a product that is
			 * in final mode
			 **/
			{
				"salesMan1", super.getEntityId("product1"), "Name", "Description", typeProduct, 5, 10, true, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: SalesMan is trying to update a product of
			 * another salesman
			 **/
			{
				"salesMan1", super.getEntityId("product4"), "Name", "Description", typeProduct, 5, 10, true, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: SalesMan is trying to update a product with
			 * the name in blank
			 **/
			{
				"salesMan1", super.getEntityId("product2"), "", "Description", typeProduct, 5, 10, true, ConstraintViolationException.class
			},
			/**
			 * NEGATIVE TEST: SalesMan is trying to update a product with
			 * the type product as null
			 **/
			{
				"salesMan1", super.getEntityId("product2"), "Name", "Description", null, 5, 10, true, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: SalesMan is trying to update a product with
			 * negative price
			 **/
			{
				"salesMan1", super.getEntityId("product2"), "Name", "Description", typeProduct, -100, 10, true, ConstraintViolationException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.updateProductsTemplate((String) testingData[i][0], (Integer) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (TypeProduct) testingData[i][4], (Integer) testingData[i][5], (Integer) testingData[i][6],
				(Boolean) testingData[i][7], (Class<?>) testingData[i][8]);
		}

	}

	private void updateProductsTemplate(String salesMan, Integer productId, String name, String description, TypeProduct typeProduct, Integer price, Integer stock, Boolean isDraftMode, Class<?> expected) {

		Product product = this.productService.findOne(productId);
		product.setName(name);
		product.setDescription(description);
		product.setType(typeProduct);
		product.setPrice(price);
		product.setStock(stock);
		product.setIsDraftMode(isDraftMode);

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(salesMan);

			this.productService.updateProduct(product);

			super.flushTransaction();

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	/**
	 * R51. An actor who is authenticated as a SalesMan must be able to:
	 * 
	 * 1. Manage his products, which includes updating them. Only the stock of a
	 * final mode product may be updated.
	 * 
	 * Ratio of data coverage: 100% - Access as a SalesMan or not. - The product
	 * is in final mode or not - SalesMan owns the product or not - 1 attributes
	 * with restrictions
	 * 
	 **/
	@Test
	public void driverRestockProducts() {

		Object testingData[][] = {

			/**
			 * POSITIVE TEST: SalesMan is updating a product with correct
			 * information
			 **/
			{
				"salesMan1", super.getEntityId("product1"), 10, null
			},
			/**
			 * NEGATIVE TEST: Another user is trying to update a product
			 **/
			{
				"socialWorker1", super.getEntityId("product1"), 10, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: SalesMan is trying to update a product that is
			 * in draft mode
			 **/
			{
				"salesMan1", super.getEntityId("product2"), 10, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: SalesMan is trying to update a product of
			 * another salesman
			 **/
			{
				"salesMan1", super.getEntityId("product3"), 10, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: SalesMan is trying to update a product with
			 * negative stock
			 **/
			{
				"salesMan1", super.getEntityId("product1"), -10, IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.restockProductsTemplate((String) testingData[i][0], (Integer) testingData[i][1], (Integer) testingData[i][2], (Class<?>) testingData[i][3]);
		}

	}

	private void restockProductsTemplate(String salesMan, Integer productId, Integer stock, Class<?> expected) {

		Product product = this.productService.findOne(productId);
		product.setStock(stock);

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(salesMan);

			this.productService.restockProduct(product);

			super.flushTransaction();

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	/**
	 * R51. An actor who is authenticated as a SalesMan must be able to:
	 * 
	 * 1. Manage his products, which includes deleting them. A product can only
	 * be updated or deleted in draft mode.
	 * 
	 * Ratio of data coverage: 100% - Access as a SalesMan or not. - The product
	 * is in final mode or not - SalesMan owns the product or not
	 * 
	 **/
	@Test
	public void driverDeleteProducts() {

		Object testingData[][] = {

			/**
			 * POSITIVE TEST: SalesMan is deleting a product
			 **/
			{
				"salesMan1", super.getEntityId("product2"), null
			},
			/**
			 * NEGATIVE TEST: Another user is trying to delete a product
			 **/
			{
				"socialWorker1", super.getEntityId("product2"), IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: SalesMan is trying to delete a product that is
			 * in final mode
			 **/
			{
				"salesMan1", super.getEntityId("product1"), IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: SalesMan is trying to delete a product of
			 * another salesman
			 **/
			{
				"salesMan1", super.getEntityId("product4"), IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.deleteProductsTemplate((String) testingData[i][0], (Integer) testingData[i][1], (Class<?>) testingData[i][2]);
		}

	}

	private void deleteProductsTemplate(String salesMan, Integer productId, Class<?> expected) {

		Product product = this.productService.findOne(productId);

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(salesMan);

			SalesMan sm = this.salesManService.loggedSalesMan();
			Integer number1 = sm.getProducts().size();

			this.productService.delete(product);

			sm = this.salesManService.loggedSalesMan();
			Integer number2 = sm.getProducts().size();

			Assert.isTrue(number1 == number2 + 1);

			super.flushTransaction();

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	@Test
	public void driverBuyProducts() {

		Object testingData[][] = {

			/**
			 * POSITIVE TEST: Prisoner is buying a product
			 **/
			{
				"prisoner1", super.getEntityId("product1"), 1, null
			},
			/**
			 * NEGATIVE TEST: Prisoner is buying a product that cost more point than he has
			 **/
			{
				"prisoner2", super.getEntityId("product1"), 1, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: Prisoner is buying a product in draft mode
			 **/
			{
				"prisoner1", super.getEntityId("product2"), 1, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: Prisoner is buying more stock than the available
			 **/
			{
				"prisoner1", super.getEntityId("product1"), 2, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: Prisoner is buying a negative amount of stock
			 **/
			{
				"prisoner1", super.getEntityId("product1"), -1, IllegalArgumentException.class
			},
			/**
			 * NEGATIVE TEST: An Actor who is not a Prisoner is buying a Product
			 **/
			{
				"warden1", super.getEntityId("product1"), 1, IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.buyProductsTemplate((String) testingData[i][0], (Integer) testingData[i][1], (Integer) testingData[i][2], (Class<?>) testingData[i][3]);
		}

	}

	private void buyProductsTemplate(String usernameLoggued, Integer productId, Integer quantity, Class<?> expected) {

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(usernameLoggued);

			this.productService.buyProductAsPrisoner(productId, quantity);

			super.flushTransaction();

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

}
