
package services;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import utilities.AbstractTest;
import domain.Actor;
import domain.Box;
import domain.Message;
import domain.PriorityLvl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/junit.xml"
})
@Transactional
public class SpamMessageServiceTest extends AbstractTest {

	@Autowired
	private MessageService	messageService;

	@Autowired
	private ActorService	actorService;

	@Autowired
	private BoxService		boxService;


	@Test
	public void driverSpam() {

		/**
		 * 
		 * Number of test: 4
		 * Number of restrictions + positive test: 4
		 * Coverage: 100%
		 * 
		 * */

		Object testingData[][] = {
			{
				//Positive test, SPAM in subject
				"warden1", "warden1", "knife", "body", "HIGH", "tags", "warden2", null
			}, {
				//Positive test, SPAM in body
				"warden1", "warden1", "subject", "knife", "HIGH", "tags", "warden2", null
			}, {
				//Positive test, SPAM in tags
				"warden1", "warden1", "subject", "body", "HIGH", "knife", "warden2", null
			}, {
				//Negative test, no spam words
				"warden1", "warden1", "subject", "body", "HIGH", "tags", "warden2", IllegalArgumentException.class
			}
		};

		for (int i = 0; i < testingData.length; i++)
			this.templateSpam((String) testingData[i][0], (String) testingData[i][1], (String) testingData[i][2], (String) testingData[i][3], (String) testingData[i][4], (String) testingData[i][5], (String) testingData[i][6], (Class<?>) testingData[i][7]);
	}

	protected void templateSpam(String username, String usernameVerification, String subject, String body, String priority, String tags, String receiver, Class<?> expected) {

		Class<?> caught = null;

		try {

			//En cada iteraccion comenzamos una transaccion, de esya manera, no se toman valores residuales de otros test
			this.startTransaction();

			super.authenticate(username);

			Date thisMoment = new Date();
			thisMoment.setTime(thisMoment.getTime() - 1000);
			Box spamBoxReceiver = new Box();

			Message message = this.messageService.create();
			Actor sender = this.actorService.getActorByUsername(usernameVerification);
			Actor receiverActor = this.actorService.getActorByUsername(receiver);

			spamBoxReceiver = this.boxService.getSuspiciousBoxByActor(receiverActor);
			Integer numMessages = spamBoxReceiver.getMessages().size();

			message.setMoment(thisMoment);
			message.setSubject(subject);
			message.setBody(body);
			message.setPriority(PriorityLvl.HIGH);
			message.setRecipient(receiverActor.getUserAccount().getUsername());
			message.setTags(tags);
			message.setSender(sender.getUserAccount().getUsername());

			this.messageService.sendMessage(message);
			this.messageService.flush();
			Integer numMessagesSentSpam = spamBoxReceiver.getMessages().size();

			Assert.isTrue(numMessagesSentSpam != numMessages);

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
