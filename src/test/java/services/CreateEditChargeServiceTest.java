
package services;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Charge;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class CreateEditChargeServiceTest extends AbstractTest {

	@Autowired
	private ChargeService	chargeService;


	@Test
	public void testCreateCharge() {

		/**
		 * 
		 * Number of test: 8
		 * Number of restrictions + positive test: 8
		 * Coverage: 100%
		 * 
		 * */

		Object testingData[][] = {

			{
				// Positive test: A Warden is creating a new type of product.
				"warden1", "titleSpanish", "titleEnglish", 10, 12, true, null
			}, {
				//Negative test, blank spanishTitle
				"warden1", "", "titleEnglish", 10, 12, true, ConstraintViolationException.class
			}, {
				//Negative test, blank englishTitle
				"warden1", "titleSpanish", "", 10, 12, true, ConstraintViolationException.class
			}, {
				//Negative test, More than 20 months
				"warden1", "titleSpanish", "titleEnglish", 10, 20, true, ConstraintViolationException.class
			}, {
				//Negative test, negative years
				"warden1", "titleSpanish", "titleEnglish", -10, 12, true, ConstraintViolationException.class
			}, {
				//Negative test, negative months
				"warden1", "titleSpanish", "titleEnglish", 10, -12, true, ConstraintViolationException.class
			}, {
				//Negative test, null IsDraftMode
				"warden1", "titleSpanish", "titleEnglish", 10, 12, null, ConstraintViolationException.class
			}, {
				//Negative test, unlogged
				"", "titleSpanish", "titleEnglish", 10, 12, true, IllegalArgumentException.class
			}

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateCreateCharge((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (int) testingData[i][3], (int) testingData[i][4], (Boolean) testingData[i][5], (Class<?>) testingData[i][6]);

	}
	protected void templateCreateCharge(String username, String titleSpanish, String titleEnglish, int years, int months, Boolean isDraftMode, Class<?> expected) {
		Class<?> caught = null;

		try {
			this.startTransaction();
			super.authenticate(username);

			Charge charge = new Charge();

			charge.setIsDraftMode(isDraftMode);
			charge.setMonth(months);
			charge.setTitleEnglish(titleEnglish);
			charge.setTitleSpanish(titleSpanish);
			charge.setYear(years);

			this.chargeService.save(charge);
			this.chargeService.flush();

			super.authenticate(null);

		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	@Test
	public void testEditCharge() {

		Object testingData[][] = {

			/**
			 * 
			 * Number of test: 2
			 * Number of restrictions + positive test: 2
			 * Coverage: 100%
			 * 
			 * */

			{
				// Positive test: edit a draft mode charge
				"warden1", "warCrime", null

			}, {
				//Negative test, final mode charge
				"warden1", "suspicious", IllegalArgumentException.class
			}

		};
		for (int i = 0; i < testingData.length; i++)
			this.templateEditCharge((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}

	protected void templateEditCharge(String username, String charge, Class<?> expected) {
		Class<?> caught = null;

		try {
			this.startTransaction();
			super.authenticate(username);

			Charge chargeRe = new Charge();

			chargeRe = this.chargeService.findOne(super.getEntityId(charge));

			chargeRe.setTitleEnglish("example");

			Assert.isTrue(chargeRe.getIsDraftMode());

			this.chargeService.save(chargeRe);
			this.chargeService.flush();

			super.authenticate(null);

		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

}
