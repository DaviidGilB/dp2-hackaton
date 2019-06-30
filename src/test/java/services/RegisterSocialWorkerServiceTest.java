package services;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import domain.SocialWorker;
import forms.FormObjectSocialWorker;
import utilities.AbstractTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/junit.xml" })
@Transactional
public class RegisterSocialWorkerServiceTest extends AbstractTest {

	@Autowired
	private SocialWorkerService socialWorkerService;

	@Test
	public void driverRegisterSocialWorker() {

		/**
		 *
		 * Number of test: 12 Number of restrictions + positive test: 12
		 * Coverage: 100%
		 *
		 */

		Object testingData[][] = { {
				// Positive test, create a socialWorker
				"name", "middleName", "surname", "https://www.youtube.com", "username", "password", "password", "title",
				true, null },
				{
						// Positive test, blank middleName
						"name", "", "surname", "https://www.youtube.com", "username", "password", "password", "title",
						true, null },
				{
						// Negative test, Blank name
						"", "middleName", "surname", "https://www.youtube.com", "username", "password", "password",
						"title", true, ConstraintViolationException.class },
				{
						// Negative test, Blank surname
						"name", "middleName", "", "https://www.youtube.com", "username", "password", "password",
						"title", true, ConstraintViolationException.class },
				{
						// Negative test, no URL photo
						"name", "middleName", "surname", "notURL", "username", "password", "password", "title", true,
						ConstraintViolationException.class },
				{
						// Negative test, Blank username
						"name", "middleName", "surname", "https://www.youtube.com", "", "password", "password", "title",
						true, ConstraintViolationException.class },
				{
						// Negative test, Blank password
						"name", "middleName", "surname", "https://www.youtube.com", "username", "", "sasa", "title",
						true, NullPointerException.class },
				{
						// Negative test, not equal password
						"name", "middleName", "surname", "https://www.youtube.com", "username", "sav", "sasa", "title",
						true, NullPointerException.class },
				{
						// Negative test, false terms
						"name", "middleName", "surname", "https://www.youtube.com", "username", "password", "password",
						"title", false, NullPointerException.class } };

		for (int i = 0; i < testingData.length; i++)
			this.templateRegisterSocialWorker((String) testingData[i][0], (String) testingData[i][1],
					(String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4],
					(String) testingData[i][5], (String) testingData[i][6], (String) testingData[i][7],
					(Boolean) testingData[i][8], (Class<?>) testingData[i][9]);
	}

	protected void templateRegisterSocialWorker(String name, String middleName, String surname, String photo,
			String username, String password, String confirmPassword, String title, Boolean terms, Class<?> expected) {

		Class<?> caught = null;

		try {

			// En cada iteraccion comenzamos una transaccion, de esya manera, no
			// se toman valores residuales de otros test
			this.startTransaction();

			FormObjectSocialWorker formObject = new FormObjectSocialWorker();

			formObject.setName(name);
			formObject.setMiddleName(middleName);
			formObject.setSurname(surname);
			formObject.setPhoto(photo);
			formObject.setUsername(username);
			formObject.setPassword(password);
			formObject.setConfirmPassword(confirmPassword);
			formObject.setTermsAndConditions(terms);
			formObject.setTitle(title);

			BindingResult binding = null;
			SocialWorker socialWorker = this.socialWorkerService.reconstruct(formObject, binding);

			this.socialWorkerService.save(socialWorker);
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
