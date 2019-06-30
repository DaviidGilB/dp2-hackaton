
package services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Charge;
import domain.Prisoner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class AddChargeWardenServiceTest extends AbstractTest {

	@Autowired
	private ChargeService	chargeService;

	@Autowired
	private PrisonerService	prisonerService;

	@Autowired
	private WardenService	wardenService;


	@Test
	public void driverAddCharge() {

		/**
		 * 
		 * Number of test: 4
		 * Number of restrictions: 3 + 1 positive test
		 * Coverture: 100%
		 * 
		 * */

		Object testingData[][] = {
			{
				//Positive test, add a final mode charge to prisoner
				"warden1", "suspicious", "prisoner1", null
			}, {
				//Negative test, add draft mode charge to prisoner
				"warden1", "warCrime", "prisoner2", IllegalArgumentException.class
			}, {
				//Negative test, prisoner add a charge
				"prisoner1", "warCrime", "prisoner2", IllegalArgumentException.class
			}, {
				//Negative test, add a charge that prisoner already have
				"warden1", "terrorism", "prisoner1", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateAddCharge((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (Class<?>) testingData[i][3]);
	}

	protected void templateAddCharge(String username, String charge, String prisoner, Class<?> expected) {

		Class<?> caught = null;

		try {

			//En cada iteraccion comenzamos una transaccion, de esya manera, no se toman valores residuales de otros test
			this.startTransaction();

			super.authenticate(username);
			this.wardenService.loggedAsWarden();

			Charge chargeRe = new Charge();
			Prisoner prisonerRe = new Prisoner();

			chargeRe = this.chargeService.findOne(super.getEntityId(charge));
			prisonerRe = this.prisonerService.findOne(super.getEntityId(prisoner));

			Assert.isTrue(!prisonerRe.getCharges().contains(chargeRe));
			Assert.isTrue(chargeRe.getIsDraftMode() == false);

			prisonerRe.getCharges().add(chargeRe);

			this.prisonerService.save(prisonerRe);

			this.chargeService.flush();

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
