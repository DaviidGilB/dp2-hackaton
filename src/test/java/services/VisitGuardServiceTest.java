
package services;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Report;
import domain.Visit;
import domain.VisitStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class VisitGuardServiceTest extends AbstractTest {

	@Autowired
	private VisitService	visitService;

	@Autowired
	private ReportService	reportService;


	/**
	 * REQUIREMENT 13.3 An actor who is authenticated as a guard must be able to Assign a future visit without an assigned guard to himself
	 * 
	 * The data of this driver is focused to test the change of the status of an accepted visit by a guard
	 * 
	 */

	@Test
	public void driverChangeStatusGuard() {

		/**
		 * 
		 * Number of test: 8
		 * Number of restrictions + positive test: 8
		 * Coverage: 100%
		 * 
		 * */

		Object testingData[][] = {
			{
				//Positive test, a guard permits a future accepted visit
				"guard1", "visit4", true, null
			}, {
				//Positive test, a guard rejects a future accepted visit
				"guard1", "visit4", false, null
			}, {
				//Negative test, a guard tries to permit a past accepted visit
				"guard1", "visit5", true, IllegalArgumentException.class
			}, {
				//Negative test, a guard tries to reject a past accepted visit
				"guard1", "visit5", false, IllegalArgumentException.class
			}, {
				//Negative test, an anonymous user tries to permit a future accepted visit
				null, "visit4", true, IllegalArgumentException.class
			}, {
				//Negative test, a guard tries to change the status of a visit whose status is PERMITTED
				"guard1", "visit1", false, IllegalArgumentException.class
			}, {
				//Negative test, a guard tries to change the status of a visit whose status is PENDING
				"guard1", "visit7", false, IllegalArgumentException.class
			}, {
				//Negative test, a prisoner tries to change the status of a visit whose status is REJECTED
				"guard1", "visit8", false, IllegalArgumentException.class
			}

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateChangeStatusGuard((String) testingData[i][0], (String) testingData[i][1], (Boolean) testingData[i][2], (Class<?>) testingData[i][3]);
	}
	protected void templateChangeStatusGuard(String username, String visit, Boolean accept, Class<?> expected) {

		Class<?> caught = null;

		try {

			//En cada iteraccion comenzamos una transaccion, de esya manera, no se toman valores residuales de otros test
			this.startTransaction();

			super.authenticate(username);	//Autenthicate as visitor

			Visit visitFound = this.visitService.findOne(super.getEntityId(visit));

			Visit saved = this.visitService.editVisitGuard(visitFound, accept);

			if (accept)
				Assert.isTrue(saved.getVisitStatus() == VisitStatus.PERMITTED);
			else
				Assert.isTrue(saved.getVisitStatus() == VisitStatus.REJECTED);

			this.visitService.flush();

			super.authenticate(null);

		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			//Se fuerza el rollback para que no de ningun problema la siguiente iteracion
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	/**
	 * REQUIREMENT 14.4 An actor who is authenticated as a guard must be able to Write a Report about a past visit he has monitored
	 * 
	 * The data of this driver is focused to test the creation of reports by a guard
	 * referring to a past visit he has monitored
	 * 
	 */

	@Test
	public void driverCreateVisitVisitor() {

		/**
		 * 
		 * Number of test: 9
		 * Number of restrictions + positive test: 9
		 * Coverage: 100%
		 * 
		 * */

		Object testingData[][] = {
			{
				//Positive test, a guard creates a report referring one of his past permitted visits
				"guard1", "visit2", "description", null
			}, {
				//Negative test, a guard tries to create a report referring one of his past permitted visits that already has a report
				"guard1", "visit1", "description", IllegalArgumentException.class
			}, {
				//Negative test, a guard tries to create a report referring one of his past permitted visits with a blank description
				"guard1", "visit2", "", IllegalArgumentException.class
			}, {
				//Positive test, a guard tries to create a report referring a future visit
				"guard1", "visit1", "description", IllegalArgumentException.class
			}, {
				//Negative test, a guard tries to create a report referring a visit that belongs to another guard
				"guard2", "visit2", "description", IllegalArgumentException.class
			}, {
				//Negative test, an anonymous user tries to create a report
				null, "visit2", "description", IllegalArgumentException.class
			}, {
				//Negative test, a guard tries to write a report referring an ACCEPTED visit
				"guard1", "visit4", "description", IllegalArgumentException.class
			}, {
				//Negative test, a guard tries to write a report referring a PENDING visit
				"guard1", "visit6", "description", IllegalArgumentException.class
			}, {
				//Negative test, a guard tries to write a report referring a REJECTED visit
				"guard1", "visit8", "description", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateCreateVisitPrisoner((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);
	}
	protected void templateCreateVisitPrisoner(String guard, String visit, String report, Class<?> expected) {

		Class<?> caught = null;

		try {

			//En cada iteraccion comenzamos una transaccion, de esya manera, no se toman valores residuales de otros test
			this.startTransaction();

			super.authenticate(guard);	//Autenthicate as guard

			int visitId = super.getEntityId(visit);

			//Reconstruct
			Report result = new Report();
			result.setDescription(report);
			Date thisMoment = new Date();
			thisMoment.setTime(thisMoment.getTime() - 1);
			result.setDate(thisMoment);
			//FIN Reconstruct

			this.reportService.saveReport(result, visitId);

			this.visitService.flush();

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
