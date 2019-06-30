
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
public class DeleteGuardServiceTest extends AbstractTest {

	@Autowired
	private GuardService	guardService;


	@Test
	public void driverRegisterGuard() {

		/**
		 * 
		 * Number of test: 3
		 * Number of restrictions + positive test: 2
		 * Coverage: 100%
		 * 
		 * */

		Object testingData[][] = {
			{
				//Positive test, delete your usserAccount
				"guard1", null
			}, {
				//Positive test, delete a guard logged as prisoner
				"prisoner1", IllegalArgumentException.class
			}, {
				//Positive test, delete a guard logged as warden
				"warden1", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateRegisterGuard((String) testingData[i][0], (Class<?>) testingData[i][1]);
	}
	protected void templateRegisterGuard(String loggedUsername, Class<?> expected) {

		Class<?> caught = null;

		try {

			//En cada iteraccion comenzamos una transaccion, de esya manera, no se toman valores residuales de otros test
			this.startTransaction();

			super.authenticate(loggedUsername);

			this.guardService.deleteLoggedGuard();
			this.guardService.flush();

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
