package services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import utilities.AbstractTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/junit.xml" })
@Transactional
public class DeleteSocialWorkerServiceTest extends AbstractTest {

	@Autowired
	private SocialWorkerService socialWorkerService;

	@Test
	public void driverDeleteSocialWorker() {

		/**
		 *
		 * Number of test: 3 Number of restrictions + positive test: 2 Coverage:
		 * 100%
		 * 
		 */

		Object testingData[][] = { {
				// Positive test, delete your usserAccount
				"socialWorker1", null },
				{
						// Positive test, delete a socialWorker logged as
						// prisoner
						"prisoner1", IllegalArgumentException.class },
				{
						// Positive test, delete a socialWorker logged as warden
						"warden1", IllegalArgumentException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.templateDeleteSocialWorker((String) testingData[i][0], (Class<?>) testingData[i][1]);
	}

	protected void templateDeleteSocialWorker(String loggedUsername, Class<?> expected) {

		Class<?> caught = null;

		try {

			// En cada iteraccion comenzamos una transaccion, de esya manera, no
			// se toman valores residuales de otros test
			this.startTransaction();

			super.authenticate(loggedUsername);

			this.socialWorkerService.deleteLogguedSocialWorker();
			this.socialWorkerService.flush();

			super.authenticate(null);

		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			// Se fuerza el rollback para que no de ningun problema la siguiente
			// iteracion
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

}
