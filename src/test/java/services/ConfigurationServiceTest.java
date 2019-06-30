package services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import domain.Configuration;
import utilities.AbstractTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/junit.xml" })
@Transactional
public class ConfigurationServiceTest extends AbstractTest {

	@Autowired
	private ConfigurationService configurationService;

	/**
	 * We are going to test the Requirement 53.2:
	 *
	 * 53. An actor who is authenticated as a warden must be able to:
	 *
	 * 2. Manage the types of products in the system, which includes listing
	 * them, updating them, and deleting them. A type of product can only be
	 * deleted if it’s not assigned to any product.
	 *
	 */
	@Test
	public void testCreateProductType() {

		Object testingData[][] = {
				// Positive test: A Warden is creating a new type of product.
				{ "warden1", "Test", "Prueba", null },

				// Negative test: A Prisoner is trying to create a new type of
				// product.
				{ "prisoner1", "Test", "Prueba", IllegalArgumentException.class },

				// Negative test: A Social Worker is trying to create a new type
				// of product.
				{ "socialWorker1", "Test", "Prueba", IllegalArgumentException.class },

				// Negative test: A Sales man is trying to create a new type
				// of product.
				{ "salesman1", "Test", "Prueba", IllegalArgumentException.class },

				// Negative test: A Visitor is trying to create a new type
				// of product.
				{ "visitor1", "Test", "Prueba", IllegalArgumentException.class },

				// Negative test: A Visitor is trying to create a new type
				// of product.
				{ "guard1", "Test", "Prueba", IllegalArgumentException.class } };

		/*
		 * Data coverage: 6/9 -> 66%
		 *
		 * We can't test the "No blank" restriction because of the binding
		 */

		for (int i = 0; i < testingData.length; i++)
			this.templateCreateProductType((String) testingData[i][0], (String) testingData[i][1],
					(String) testingData[i][2], (Class<?>) testingData[i][3]);

	}

	@Test
	public void testDeleProductType() {

		Object testingData[][] = {
				// Positive test: A Warden is deleting a type of product that is
				// not in use.
				{ "warden1", "type5", null },

				// Negative test: A Warden is trying deleting a type of product
				// that is in use.
				{ "warden1", "type1", IllegalArgumentException.class },

				// Negative test: A Prisoner is trying deleting a type of
				// product
				{ "prisoner1", "type5", IllegalArgumentException.class },

				// Negative test: A Social Worker is trying deleting a type of
				// product
				{ "socialWorker1", "type5", IllegalArgumentException.class },

				// Negative test: A Guard is trying deleting a type of
				// product
				{ "guard1", "type5", IllegalArgumentException.class },

				// Negative test: A Salesman is trying deleting a type of
				// product
				{ "salesman1", "type5", IllegalArgumentException.class },

				// Negative test: A Visitor is trying deleting a type of
				// product
				{ "visitor1", "type5", IllegalArgumentException.class } };

		/*
		 * Data coverage: 7/7 -> 100%
		 *
		 */

		for (int i = 0; i < testingData.length; i++)
			this.templateDeleteProductType((String) testingData[i][0], (String) testingData[i][1],
					(Class<?>) testingData[i][2]);

	}

	/*
	 * Now we are going to test the web page configuration edition
	 */

	@Test
	public void testEdtiConfiguration() {

		Object testingData[][] = {
				// Positive test: A Warden is editing the System name
				{ "warden1", "test", null },

				// Negative test: A Prisoner is trying to edit the System name
				{ "prisoner1", "test", IllegalArgumentException.class },

				// Negative test: A Social Worker is trying to edit the System
				// name
				{ "socialWorker1", "test", IllegalArgumentException.class },

				// Negative test: A Guard is trying to edit the System name
				{ "guard1", "test", IllegalArgumentException.class },

				// Negative test: A Salesman is trying to edit the System name
				{ "salesman1", "test", IllegalArgumentException.class },

				// Negative test: A Visitor is trying to edit the System name
				{ "visitor1", "test", IllegalArgumentException.class } };

		/*
		 * Data coverage: 6/17 -> 35.3%
		 *
		 * We can't test the the NotBlank annotation and others because of the
		 * binding
		 */

		for (int i = 0; i < testingData.length; i++)
			this.templateEditConfiguration((String) testingData[i][0], (String) testingData[i][1],
					(Class<?>) testingData[i][2]);

	}

	protected void templateEditConfiguration(String username, String systemName, Class<?> expected) {
		Class<?> caught = null;

		try {
			this.startTransaction();
			super.authenticate(username);

			Configuration config = this.configurationService.getConfiguration();

			config.setSystemName(systemName);

			this.configurationService.saveConfiguration(config);

			super.authenticate(null);

		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	protected void templateDeleteProductType(String username, String type, Class<?> expected) {

		Class<?> caught = null;

		try {
			this.startTransaction();
			super.authenticate(username);

			Integer id = this.getEntityId(type);
			this.configurationService.deleteTypeProducts(id);

			super.authenticate(null);

		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	protected void templateCreateProductType(String username, String typeEN, String typeES, Class<?> expected) {

		Class<?> caught = null;

		try {
			this.startTransaction();
			super.authenticate(username);
			this.configurationService.addTypeProducts(typeEN, typeES, null);

			super.authenticate(null);

		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

}
