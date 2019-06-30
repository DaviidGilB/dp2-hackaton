
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
import domain.SalesMan;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class SalesManServiceTest extends AbstractTest {

	@Autowired
	private SalesManService	salesManService;

	@Autowired
	private ProductService	productService;


	@Test
	public void driverEditSalesman() {

		Object testingData[][] = {
			{
				//Positive test, edit a salesMan
				"salesMan1", "salesMan1", "name", "middleName", "surname", "https://www.youtube.com", "ESA0011012B", "storeName", null
			}, {
				//Positive test, blank middleName
				"salesMan1", "salesMan1", "name", "", "surname", "https://www.youtube.com", "ESA0011012B", "storeName", null
			}, {
				//Negative test, Blank name
				"salesMan1", "salesMan1", "", "middleName", "surname", "https://www.youtube.com", "ESA0011012B", "storeName", ConstraintViolationException.class
			}, {
				//Negative test, Blank surname
				"salesMan1", "salesMan1", "name", "middleName", "", "https://www.youtube.com", "ESA0011012B", "storeName", ConstraintViolationException.class
			}, {
				//Negative test, no URL photo
				"salesMan1", "salesMan1", "name", "middleName", "surname", "notURL", "ESA0011012B", "storeName", ConstraintViolationException.class
			}, {
				//Negative test, Blank store name
				"salesMan1", "salesMan1", "name", "middleName", "surname", "https://www.youtube.com", "ESA0011012B", "", ConstraintViolationException.class
			}, {
				//Negative test, Vat number invalid
				"salesMan1", "salesMan1", "name", "middleName", "surname", "https://www.youtube.com", "invalid", "storeName", ConstraintViolationException.class
			}, {
				//Negative test, editing others data
				"salesMan2", "salesMan1", "name", "middleName", "surname", "https://www.youtube.com", "ESA0011012B", "storeName", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.templateEditSalesman((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6],
				(String) testingData[i][7], (Class<?>) testingData[i][8]);
		}
	}

	protected void templateEditSalesman(String loggedUsername, String username, String name, String middleName, String surname, String photo, String VATnumber, String storeName, Class<?> expected) {

		Class<?> caught = null;

		try {

			//En cada iteraccion comenzamos una transaccion, de esya manera, no se toman valores residuales de otros test
			this.startTransaction();

			super.authenticate(loggedUsername);

			SalesMan salesMan = this.salesManService.findSalesManByUsername(username);

			salesMan.setName(name);
			salesMan.setMiddleName(middleName);
			salesMan.setSurname(surname);
			salesMan.setPhoto(photo);
			salesMan.setVATNumber(VATnumber);
			salesMan.setStoreName(storeName);

			this.salesManService.saveSalesman(salesMan);
			this.salesManService.flush();

			super.unauthenticate();

		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			//Se fuerza el rollback para que no de ningun problema la siguiente iteracion
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

	@Test
	public void driverDeleteSalesMan() {

		Object testingData[][] = {

			/**
			 * POSITIVE TEST: delete own accountr
			 **/
			{
				"salesman1", null
			},
			/**
			 * NEGATIVE TEST: Another user is trying to delete the account
			 **/
			{
				"prisoner1", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++) {
			this.deleteSalesmanTemplate((String) testingData[i][0], (Class<?>) testingData[i][1]);
		}
	}

	private void deleteSalesmanTemplate(String usernameLoggued, Class<?> expected) {

		Class<?> caught = null;

		try {
			super.startTransaction();

			super.authenticate(usernameLoggued);

			Integer p = this.productService.findAll().size();

			this.salesManService.deleteLoggedSalesman();

			this.productService.flush();

			Assert.isTrue(p - 2 == this.productService.findAll().size());

			super.unauthenticate();
		} catch (Throwable oops) {
			caught = oops.getClass();
		} finally {
			this.rollbackTransaction();
		}

		super.checkExceptions(expected, caught);

	}

}
