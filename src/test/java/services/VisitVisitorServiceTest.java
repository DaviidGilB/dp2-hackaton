
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
public class VisitVisitorServiceTest extends AbstractTest {

	@Autowired
	private VisitService	visitService;

	@Autowired
	private VisitorService	visitorService;

	@Autowired
	private PrisonerService	prisonerService;


	/**
	 * REQUIREMENT 13.3 An actor who is authenticated as a visitor must be able to Accept or Reject a pending Visit from a Prisoner
	 * 
	 * The data of this driver is focused to test the change of the status of a visit by a visitor
	 * A visitor can change the status of a visit referring to him that hasn't been created by him
	 * 
	 */

	@Test
	public void driverChangeStatusPrisoner() {

		/**
		 * 
		 * Number of test: 8
		 * Number of restrictions + positive test: 5
		 * Coverage: 100%
		 * 
		 * */

		Object testingData[][] = {
			{
				//Positive test, a visitor accepts a PENDING visit referring him that has been created by a prisoner
				"visitor2", "visit6", true, null
			}, {
				//Positive test, a visitor rejects a PENDING visit referring him that has been created by a prisoner
				"visitor2", "visit6", false, null
			}, {
				//Negative test, a visitor tries to accept a PENDING visit referring him that has been created by himself
				"visitor2", "visit7", true, IllegalArgumentException.class
			}, {
				//Negative test, a prisoner tries to accept a PENDING visit in which he is not referred
				"visitor1", "visit7", true, IllegalArgumentException.class
			}, {
				//Negative test, an anonymous user tries to accept a PENDING visit
				null, "visit6", true, IllegalArgumentException.class
			}, {
				//Negative test, a prisoner tries to change the status of a visit whose status is PERMITTED
				"visitor1", "visit1", false, IllegalArgumentException.class
			}, {
				//Negative test, a prisoner tries to change the status of a visit whose status is ACCEPTED
				"visitor2", "visit4", false, IllegalArgumentException.class
			}, {
				//Negative test, a prisoner tries to change the status of a visit whose status is REJECTED
				"visitor1", "visit8", false, IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateChangeStatusVisitor((String) testingData[i][0], (String) testingData[i][1], (Boolean) testingData[i][2], (Class<?>) testingData[i][3]);
	}
	protected void templateChangeStatusVisitor(String username, String visit, Boolean accept, Class<?> expected) {

		Class<?> caught = null;

		try {

			//En cada iteraccion comenzamos una transaccion, de esya manera, no se toman valores residuales de otros test
			this.startTransaction();

			super.authenticate(username);	//Autenthicate as visitor

			Visit visitFound = this.visitService.findOne(super.getEntityId(visit));

			Visit saved = this.visitService.editVisitVisitor(visitFound, accept);

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
	 * REQUIREMENT 13.2 An actor who is authenticated as a visitor must be able to Create a Create a Visit referring a prisoner in the system.
	 * 
	 * The data of this driver is focused to test the creation of pending visits by a visitor
	 * referring to one of the prisoners of the system (the prisoner cannot be free or isolated)
	 * 
	 */

	@Test
	public void driverCreateVisitVisitor() {

		Calendar c1 = Calendar.getInstance();
		c1.add(Calendar.YEAR, +1);
		Date date1 = c1.getTime();

		Calendar c2 = Calendar.getInstance();
		c2.add(Calendar.YEAR, -1);
		Date date2 = c2.getTime();

		Calendar c3 = Calendar.getInstance();
		c3.add(Calendar.YEAR, -1);
		Date date3 = c3.getTime();

		/**
		 * 
		 * Number of test: 9
		 * Number of restrictions + positive test: 11
		 * Coverage: 81%
		 * 
		 * */

		Object testingData[][] = {
			{
				//Positive test, a visitor creates a visit referring one of the prisoners of the system inserting valid data in the form
				"visitor1", "prisoner1", date1, "description", Reason.BUSSINESS, null
			}, {
				//Negative test, a visitor tries to create a visit referring a prisoner inserting a blank description in the form
				"prisoner1", "visitor1", date1, "", Reason.BUSSINESS, IllegalArgumentException.class
			}, {
				//Negative test, a visitor tries to create a visit referring a prisoner inserting a null date in the form
				"visitor1", "prisoner1", null, "description", Reason.BUSSINESS, IllegalArgumentException.class
			}, {
				//Negative test, a visitor tries to create a visit referring a prisoner inserting a past date in the form
				"visitor1", "prisoner1", date2, "description", Reason.BUSSINESS, IllegalArgumentException.class
			}, {
				//Negative test, a visitor tries to create a visit referring a prisoner inserting a null reason in the form
				"visitor1", "prisoner1", date1, "description", null, IllegalArgumentException.class
			}, {
				//Negative test, a visitor creates a visit referring a null prisoner
				"visitor1", null, date1, "description", Reason.BUSSINESS, IllegalArgumentException.class
			}, {
				//Negative test, a visitor tries to create a visit referring an isolated Prisoner
				"visitor1", "prisoner3", date1, "description", Reason.BUSSINESS, IllegalArgumentException.class
			}, {
				//Negative test, a visitor tries to create a visit referring a free Prisoner
				"visitor1", "prisoner4", date1, "description", Reason.BUSSINESS, IllegalArgumentException.class
			}, {
				//Negative test, a visitor tries to create a visit that would take place after the exit date of the prisoner
				"visitor1", "prisoner1", date3, "description", Reason.BUSSINESS, IllegalArgumentException.class
			}

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateCreateVisitPrisoner((String) testingData[i][0], (String) testingData[i][1], (Date) testingData[i][2], (String) testingData[i][3], (Reason) testingData[i][4], (Class<?>) testingData[i][5]);
	}
	protected void templateCreateVisitPrisoner(String visitor, String prisoner, Date date, String description, Reason reason, Class<?> expected) {

		Class<?> caught = null;

		try {

			//En cada iteraccion comenzamos una transaccion, de esya manera, no se toman valores residuales de otros test
			this.startTransaction();

			super.authenticate(visitor);	//Autenthicate as visitor

			Prisoner prisonerUser = this.prisonerService.findOne(super.getEntityId(prisoner));

			Visitor visitorUser = this.visitorService.findOne(super.getEntityId(visitor));

			Visit first = this.visitService.createAsVisitor(prisonerUser);

			//Reconstruct
			Visit result = first;

			result.setCreatedByPrisoner(false);
			result.setDate(date);
			result.setDescription(description);
			result.setPrisoner(prisonerUser);
			result.setReason(reason);
			result.setReport(null);
			result.setVisitor(visitorUser);
			result.setVisitStatus(VisitStatus.PENDING);
			//Fin reconstruct

			this.visitService.saveVisitAsVisitor(result);

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
