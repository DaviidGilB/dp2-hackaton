
package services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import utilities.AbstractTest;
import domain.Guard;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class EditGuardServiceTest extends AbstractTest {

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
				//Positive test, edit your profile
				"guard1", "guard1", null
			}, {
				//Positive test, edit guard profile logged as prisoner
				"prisoner1", "guard1", IllegalArgumentException.class
			}, {
				//Positive test, edit guard profile logged as warden
				"warden1", "guard1", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateRegisterGuard((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
	}
	protected void templateRegisterGuard(String loggedUsername, String guard, Class<?> expected) {

		Class<?> caught = null;

		try {

			//En cada iteraccion comenzamos una transaccion, de esya manera, no se toman valores residuales de otros test
			this.startTransaction();

			super.authenticate(loggedUsername);

			Guard guardRe = this.guardService.findOne(super.getEntityId(guard));

			guardRe.setName("fafa");

			this.guardService.saveEdit(guardRe);
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
