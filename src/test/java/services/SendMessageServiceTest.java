
package services;

import java.util.Date;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import utilities.AbstractTest;
import domain.Actor;
import domain.Message;
import domain.PriorityLvl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class SendMessageServiceTest extends AbstractTest {

	@Autowired
	private MessageService	messageService;

	@Autowired
	private ActorService	actorService;


	@Test
	public void driverSendMessage() {

		/**
		 * 
		 * Number of test: 8
		 * Number of restrictions + positive test: 8
		 * Coverage: 100%
		 * 
		 * */

		Object testingData[][] = {
			{
				//Positive test
				"warden1", "warden1", "subject", "body", "tags", "warden2", null
			}, {
				//Positive test Blank tags
				"warden1", "warden1", "subject", "body", "", "warden2", null
			}, {
				//Negative test not logged
				"", "warden1", "subject", "body", "tags", "warden2", IllegalArgumentException.class
			}, {
				//Negative test Blank subject
				"warden1", "warden1", "", "body", "tags", "warden2", ConstraintViolationException.class
			}, {
				//Negative test Blank body
				"warden1", "warden1", "subject", "", "tags", "warden2", ConstraintViolationException.class
			}, {
				//Negative test blank receiver
				"warden1", "warden1", "subject", "body", "tags", "", NullPointerException.class
			}, {
				//Negative test trying to send message to null actor
				"warden1", "warden1", "subject", "HIGH", "tags", null, NullPointerException.class
			}, {
				//Negative test null subject
				"warden1", "warden1", null, "body", "tags", "warden2", NullPointerException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateSendMessage((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (Class<?>) testingData[i][6]);
	}

	protected void templateSendMessage(String username, String usernameVerification, String subject, String body, String tags, String receiver, Class<?> expected) {

		Class<?> caught = null;

		try {

			//En cada iteraccion comenzamos una transaccion, de esya manera, no se toman valores residuales de otros test
			this.startTransaction();

			super.authenticate(username);

			Date thisMoment = new Date();
			thisMoment.setTime(thisMoment.getTime() - 1000);

			Message message = this.messageService.create();
			Actor sender = this.actorService.getActorByUsername(usernameVerification);
			Actor receiverActor = this.actorService.getActorByUsername(receiver);

			message.setMoment(thisMoment);
			message.setSubject(subject);
			message.setBody(body);
			message.setPriority(PriorityLvl.HIGH);
			message.setRecipient(receiverActor.getUserAccount().getUsername());
			message.setTags(tags);
			message.setSender(sender.getUserAccount().getUsername());

			this.messageService.sendMessage(message);
			this.messageService.flush();

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
