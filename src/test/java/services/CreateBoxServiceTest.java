
package services;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import utilities.AbstractTest;
import domain.Box;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class CreateBoxServiceTest extends AbstractTest {

	@Autowired
	private BoxService	boxService;


	@Test
	public void driverCreateBox() {

		/**
		 * 
		 * Number of test: 4
		 * Number of restrictions + positive test: 3
		 * Coverage: 100%
		 * 
		 * */

		Object testingData[][] = {
			{
				//Positive test, create a box
				"warden1", "inBoxAdmin1", null
			}, {
				//Negative test, no name
				"warden1", "", ConstraintViolationException.class
			}, {
				//Negative test, null name
				"warden1", null, ConstraintViolationException.class
			}, {
				//Negative test, not logged
				"", "dadad", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateCreateBox((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);
	}

	protected void templateCreateBox(String username, String name, Class<?> expected) {

		Class<?> caught = null;

		try {

			//En cada iteraccion comenzamos una transaccion, de esya manera, no se toman valores residuales de otros test
			this.startTransaction();

			super.authenticate(username);

			Box box = this.boxService.create();

			box.setName(name);

			this.boxService.save(box);

			this.boxService.flush();

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
