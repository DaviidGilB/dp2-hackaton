
package services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import utilities.AbstractTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class ListProductPrisonerServiceTest extends AbstractTest {

	@Autowired
	private PrisonerService	prisonerService;

	@Autowired
	private ProductService	productService;


	@Test
	public void driverListProductsPrisoner() {

		/**
		 * 
		 * Number of test: 2
		 * Number of restrictions + positive test: 3
		 * Coverage: 100%
		 * 
		 * */

		Object testingData[][] = {
			{
				//Positive test, Logged as prisoner
				"prisoner1", null
			}, {
				//Negative test, logged as warden
				"warden1", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateListProductsPrisoner((String) testingData[i][0], (Class<?>) testingData[i][1]);
	}

	protected void templateListProductsPrisoner(String username, Class<?> expected) {

		Class<?> caught = null;

		try {

			//En cada iteraccion comenzamos una transaccion, de esya manera, no se toman valores residuales de otros test
			this.startTransaction();

			super.authenticate(username);

			this.prisonerService.loggedAsPrisoner();
			this.productService.getProductsFinalModeWithStock();
			super.authenticate(null);

		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			//Se fuerza el rollback para que no de ningun problema la siguiente iteracion
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}
}
