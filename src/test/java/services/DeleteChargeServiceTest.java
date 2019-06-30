
package services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import utilities.AbstractTest;
import domain.Charge;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class DeleteChargeServiceTest extends AbstractTest {

	@Autowired
	private ChargeService	chargeService;


	@Test
	public void testCreateCharge() {

		/**
		 * 
		 * Number of test: 3
		 * Number of restrictions + positive test: 3
		 * Coverage: 100%
		 * 
		 * */

		Object testingData[][] = {

			{
				// Positive test: A warden is deleting a draft charge
				"warden1", "warCrime", null
			}, {
				//Negative test, a warden is deletin a final mode charge
				"warden1", "suspicious", IllegalArgumentException.class
			}, {
				//Negative test, not logged
				"", "warCrime", IllegalArgumentException.class
			}

		};

		for (int i = 0; i < testingData.length; i++)
			this.templateCreateCharge((String) testingData[i][0], (String) testingData[i][1], (Class<?>) testingData[i][2]);

	}
	protected void templateCreateCharge(String username, String charge, Class<?> expected) {
		Class<?> caught = null;

		try {
			this.startTransaction();
			super.authenticate(username);
			Charge chargeRe = new Charge();

			chargeRe = this.chargeService.findOne(super.getEntityId(charge));

			this.chargeService.delete(chargeRe);

			super.authenticate(null);

		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

}
