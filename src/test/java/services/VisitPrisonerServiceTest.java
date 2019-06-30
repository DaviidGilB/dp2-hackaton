
package services;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Prisoner;
import domain.Reason;
import domain.Visit;
import domain.VisitStatus;
import domain.Visitor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class VisitPrisonerServiceTest extends AbstractTest {

	@Autowired
	private VisitService	visitService;

	@Autowired
	private VisitorService	visitorService;

	@Autowired
	private PrisonerService	prisonerService;


	/**
	 * REQUIREMENT 12.2 An actor who is authenticated as a prisoner must be able to Accept or Reject a pending Visit from a Visitor
	 * 
	 * The data of this driver is focused to test the change of the status of a visit by a prisoner
	 * A prisoner can change the status of a visit referring to him that hasn't been created by him
	 * 
	 */

	@Test
	public void driverChangeStatusPrisoner() {

		/**
		 * 
		 * Number of test: 10
		 * Number of restrictions + positive test: 5
		 * Coverage: 100%
		 * 
		 * */

		Object testingData[][] = {
			{
				//Positive test, a prisoner accepts a PENDING visit referring him that has been created by a visitor
				"prisoner1", "visit7", true, null
			}, {
				//Positive test, a prisoner rejects a PENDING visit referring him that has been created by a visitor
				"prisoner1", "visit7", false, null
			}, {
				//Negative test, a prisoner tries to accept a PENDING visit referring him that has been created by himself
				"prisoner1", "visit6", true, IllegalArgumentException.class
			}, {
				//Negative test, a prisoner tries to reject a PENDING visit referring him that has been created by himself
				"prisoner1", "visit6", false, IllegalArgumentException.class
			}, {
				//Negative test, a prisoner tries to accept a PENDING visit in which he is not referred
				"prisoner2", "visit7", true, IllegalArgumentException.class
			}, {
				//Negative test, a prisoner tries to reject a PENDING visit in which he is not referred
				"prisoner2", "visit7", false, IllegalArgumentException.class
			}, {
				//Negative test, an anonymous user tries to accept a PENDING visit
				null, "visit7", true, IllegalArgumentException.class
			}, {
				//Negative test, a prisoner tries to change the status of a visit whose status is PERMITTED
				"prisoner1", "visit1", false, IllegalArgumentException.class
			}, {
				//Negative test, a prisoner tries to change the status of a visit whose status is ACCEPTED
				"prisoner1", "visit4", false, IllegalArgumentException.class
			}, {
				//Negative test, a prisoner tries to change the status of a visit whose status is REJECTED
				"prisoner1", "visit8", false, IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateChangeStatusPrisoner((String) testingData[i][0], (String) testingData[i][1], (Boolean) testingData[i][2], (Class<?>) testingData[i][3]);
	}

	protected void templateChangeStatusPrisoner(String username, String visit, Boolean accept, Class<?> expected) {

		Class<?> caught = null;

		try {

			//En cada iteraccion comenzamos una transaccion, de esya manera, no se toman valores residuales de otros test
			this.startTransaction();

			super.authenticate(username);	//Autenthicate as prisoner

			Visit visitFound = this.visitService.findOne(super.getEntityId(visit));

			Visit saved = this.visitService.editVisitPrisoner(visitFound, accept);

			if (accept)
				Assert.isTrue(saved.getVisitStatus() == VisitStatus.ACCEPTED);
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
	 * REQUIREMENT 12.3 An actor who is authenticated as a prisoner must be able to Create a Pending Visit referring a Visitor that has shared a Visit with him previously
	 * 
	 * The data of this driver is focused to test the creation of pending visits by a prisoner
	 * referring to one of his previous visitors
	 * 
	 */

	@Test
	public void driverCreateVisitPrisoner() {

		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.YEAR, +1);
		Date date1 = c1.getTime();

		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.YEAR, -1);
		Date date2 = c2.getTime();

		Calendar c3 = Calendar.getInstance();
		c3.add(Calendar.YEAR, +500);
		Date date3 = c3.getTime();

		/**
		 * 
		 * Number of test: 9
		 * Number of restrictions + positive test: 10
		 * Coverage: 90%
		 * 
		 * */

		Object testingData[][] = {
			{
				//Positive test, a prisoner creates a visit referring of his visitors inserting valid data in the form
				"prisoner1", "visitor1", date1, "description", Reason.BUSSINESS, null
			}, {
				//Negative test, a prisoner tries to create a visit referring of his visitors inserting a blank description in the form
				"prisoner1", "visitor1", date1, "", Reason.BUSSINESS, IllegalArgumentException.class
			}, {
				//Negative test, a prisoner tries to create a visit referring of his visitors inserting a null date in the form
				"prisoner1", "visitor1", null, "description", Reason.BUSSINESS, IllegalArgumentException.class
			}, {
				//Negative test, a prisoner tries to create a visit referring of his visitors inserting a past date in the form
				"prisoner1", "visitor1", date2, "description", Reason.BUSSINESS, IllegalArgumentException.class
			}, {
				//Negative test, a prisoner tries to create a visit referring of his visitors inserting a null reason in the form
				"prisoner1", "visitor1", date1, "description", null, IllegalArgumentException.class
			}, {
				//Negative test, a prisoner tries to create a visit referring a visitor that has not visited him 
				"prisoner2", "visitor1", date1, "description", Reason.BUSSINESS, IllegalArgumentException.class
			}, {
				//Negative test, a prisoner creates a visit referring of his visitors inserting a blank description in the form
				"prisoner1", "visitor1", date1, "", Reason.BUSSINESS, IllegalArgumentException.class
			}, {
				//Negative test, a prisoner creates a visit referring a null visitor
				"prisoner1", null, date1, "description", Reason.BUSSINESS, IllegalArgumentException.class
			}, {
				//Negative test, a prisoner tries to create a visit that would take place after his exit date
				"prisoner1", "visitor1", date3, "description", Reason.BUSSINESS, IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateCreateVisitPrisoner((String) testingData[i][0], (String) testingData[i][1], (Date) testingData[i][2], (String) testingData[i][3], (Reason) testingData[i][4], (Class<?>) testingData[i][5]);
	}
	protected void templateCreateVisitPrisoner(String prisoner, String visitor, Date date, String description, Reason reason, Class<?> expected) {

		Class<?> caught = null;

		try {

			//En cada iteraccion comenzamos una transaccion, de esya manera, no se toman valores residuales de otros test
			this.startTransaction();

			super.authenticate(prisoner);	//Autenthicate as prisoner

			Prisoner prisonerUser = this.prisonerService.findOne(super.getEntityId(prisoner));

			Visitor visitorUser = this.visitorService.findOne(super.getEntityId(visitor));

			Visit first = this.visitService.createAsPrisoner(visitorUser);

			//Reconstruct
			Visit result = first;

			result.setCreatedByPrisoner(true);
			result.setDate(date);
			result.setDescription(description);
			result.setPrisoner(prisonerUser);
			result.setReason(reason);
			result.setReport(null);
			result.setVisitor(visitorUser);
			result.setVisitStatus(VisitStatus.PENDING);
			//Fin reconstruct

			this.visitService.saveVisitAsPrisoner(result);

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
