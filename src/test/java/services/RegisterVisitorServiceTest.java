
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
import domain.Visitor;
import forms.FormObjectVisitor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class RegisterVisitorServiceTest extends AbstractTest {

	@Autowired
	private VisitorService	visitorService;


	@Test
	public void driverRegisterVisitor() {

		/**
		 * 
		 * Number of test: 12
		 * Number of restrictions + positive test: 12
		 * Coverage: 100%
		 * 
		 * */

		Object testingData[][] = {
			{
				//Positive test, create a guard
				"name", "middleName", "surname", "https://www.youtube.com", "666555444", "email@gmail.com", "emergencyEmail@gmail.com", "address", "username", "password", "password", true, null
			}, {
				//Positive test, blank middleName
				"name", "", "surname", "https://www.youtube.com", "666555444", "email@gmail.com", "emergencyEmail@gmail.com", "address", "username", "password", "password", true, null
			}, {
				//Negative test, Blank name
				"", "middleName", "surname", "https://www.youtube.com", "666555444", "email@gmail.com", "emergencyEmail@gmail.com", "address", "username", "password", "password", true, ConstraintViolationException.class
			}, {
				//Negative test, Blank surname
				"name", "middleName", "", "https://www.youtube.com", "666555444", "email@gmail.com", "emergencyEmail@gmail.com", "address", "username", "password", "password", true, ConstraintViolationException.class
			}, {
				//Negative test, no URL photo
				"name", "middleName", "surname", "notURL", "666555444", "email@gmail.com", "emergencyEmail@gmail.com", "address", "username", "password", "password", true, ConstraintViolationException.class
			}, {
				//Negative test, Blank email
				"name", "middleName", "surname", "https://www.youtube.com", "666555444", "", "emergencyEmail@gmail.com", "address", "username", "password", "password", true, ConstraintViolationException.class
			}, {
				//Negative test, Blank username
				"name", "middleName", "surname", "https://www.youtube.com", "666555444", "email@gmail.com", "emergencyEmail@gmail.com", "address", "", "password", "password", true, ConstraintViolationException.class
			}, {
				//Negative test, Blank password
				"name", "middleName", "surname", "https://www.youtube.com", "666555444", "email@gmail.com", "emergencyEmail@gmail.com", "address", "username", "", "sasa", true, NullPointerException.class
			}, {
				//Negative test, not equal password
				"name", "middleName", "surname", "https://www.youtube.com", "666555444", "email@gmail.com", "emergencyEmail@gmail.com", "address", "username", "sav", "sasa", true, NullPointerException.class
			}, {
				//Negative test, false terms
				"name", "middleName", "surname", "https://www.youtube.com", "666555444", "email@gmail.com", "emergencyEmail@gmail.com", "address", "username", "password", "password", false, NullPointerException.class
			}, {
				//Negative test, invalid emergencyEmail
				"name", "middleName", "surname", "https://www.youtube.com", "666555444", "email@gmail.com", "fafafafa", "address", "username", "password", "password", true, ConstraintViolationException.class
			}, {
				//Negative test, blank address
				"name", "middleName", "surname", "https://www.youtube.com", "666555444", "email@gmail.com", "emergencyEmail@gmail.com", "", "username", "password", "password", true, ConstraintViolationException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateRegisterVisitor((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6],
				(String) testingData[i][7], (String) testingData[i][8], (String) testingData[i][9], (String) testingData[i][10], (Boolean) testingData[i][11], (Class<?>) testingData[i][12]);
	}
	protected void templateRegisterVisitor(String name, String middleName, String surname, String photo, String phone, String email, String emergencyEmail, String address, String username, String password, String confirmPassword, Boolean terms,
		Class<?> expected) {

		Class<?> caught = null;

		try {

			//En cada iteraccion comenzamos una transaccion, de esya manera, no se toman valores residuales de otros test
			this.startTransaction();

			FormObjectVisitor formObject = new FormObjectVisitor();

			formObject.setName(name);
			formObject.setMiddleName(middleName);
			formObject.setSurname(surname);
			formObject.setPhoto(photo);
			formObject.setPhoneNumber(phone);
			formObject.setEmail(email);
			formObject.setEmailEmergency(emergencyEmail);
			formObject.setAddress(address);
			formObject.setUsername(username);
			formObject.setPassword(password);
			formObject.setConfirmPassword(confirmPassword);
			formObject.setTermsAndConditions(terms);

			BindingResult binding = null;
			Visitor visitor = this.visitorService.reconstruct(formObject, binding);

			this.visitorService.save(visitor);
			this.visitorService.flush();

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
