
package services;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import utilities.AbstractTest;
import domain.Guard;
import forms.FormObjectGuard;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class RegsiterGuardWardenServiceTest extends AbstractTest {

	@Autowired
	private GuardService	guardService;


	@Test
	public void driverRegisterGuard() {

		/**
		 * 
		 * Number of test: 11
		 * Number of restrictions + positive test: 11
		 * Coverage: 100%
		 * 
		 * */

		Object testingData[][] = {
			{
				//Positive test, create a guard
				"warden1", "name", "middleName", "surname", "https://www.youtube.com", "666555444", "email@gmail.com", "username", "password", "password", true, null
			}, {
				//Positive test, blank middleName
				"warden1", "name", "", "surname", "https://www.youtube.com", "666555444", "email@gmail.com", "username", "password", "password", true, null
			}, {
				//Negative test, Blank name
				"warden1", "", "middleName", "surname", "https://www.youtube.com", "666555444", "email@gmail.com", "username", "password", "password", true, ConstraintViolationException.class
			}, {
				//Negative test, Blank surname
				"warden1", "name", "middleName", "", "https://www.youtube.com", "666555444", "email@gmail.com", "username", "password", "password", true, ConstraintViolationException.class
			}, {
				//Negative test, no URL photo
				"warden1", "name", "middleName", "surname", "notURL", "666555444", "email@gmail.com", "username", "password", "password", true, ConstraintViolationException.class
			}, {
				//Negative test, Blank email
				"warden1", "name", "middleName", "surname", "https://www.youtube.com", "666555444", "", "username", "password", "password", true, ConstraintViolationException.class
			}, {
				//Negative test, Blank username
				"warden1", "name", "middleName", "surname", "https://www.youtube.com", "666555444", "email@gmail.com", "", "password", "password", true, ConstraintViolationException.class
			}, {
				//Negative test, Blank password
				"warden1", "name", "middleName", "surname", "https://www.youtube.com", "666555444", "email@gmail.com", "username", "", "sasa", true, NullPointerException.class
			}, {
				//Negative test, not equal password
				"warden1", "name", "middleName", "surname", "https://www.youtube.com", "666555444", "email@gmail.com", "username", "sav", "sasa", true, NullPointerException.class
			}, {
				//Negative test, false terms
				"warden1", "name", "middleName", "surname", "https://www.youtube.com", "666555444", "email@gmail.com", "username", "password", "password", false, NullPointerException.class
			}, {
				//Negative test, register a guard as prisoner
				"prisoner1", "name", "middleName", "surname", "https://www.youtube.com", "666555444", "email@gmail.com", "username", "password", "password", true, IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateRegisterGuard((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6],
				(String) testingData[i][7], (String) testingData[i][8], (String) testingData[i][9], (Boolean) testingData[i][10], (Class<?>) testingData[i][11]);
	}

	protected void templateRegisterGuard(String loggedUsername, String name, String middleName, String surname, String photo, String phone, String email, String username, String password, String confirmPassword, Boolean terms, Class<?> expected) {

		Class<?> caught = null;

		try {

			//En cada iteraccion comenzamos una transaccion, de esya manera, no se toman valores residuales de otros test
			this.startTransaction();

			super.authenticate(loggedUsername);

			FormObjectGuard formObject = new FormObjectGuard();

			formObject.setName(name);
			formObject.setMiddleName(middleName);
			formObject.setSurname(surname);
			formObject.setPhoto(photo);
			formObject.setPhone(phone);
			formObject.setEmail(email);
			formObject.setUsername(username);
			formObject.setPassword(password);
			formObject.setConfirmPassword(confirmPassword);
			formObject.setTermsAndConditions(terms);

			BindingResult binding = null;
			Guard guard = this.guardService.reconstruct(formObject, binding);

			this.guardService.saveGuard(guard);
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
