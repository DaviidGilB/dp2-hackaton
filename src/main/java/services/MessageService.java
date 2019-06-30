
package services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.MessageRepository;
import security.LoginService;
import security.UserAccount;
import domain.Actor;
import domain.Box;
import domain.Message;
import domain.PriorityLvl;
import domain.Prisoner;
import domain.Visit;
import domain.Visitor;

@Service
@Transactional
public class MessageService {

	@Autowired
	private MessageRepository		messageRepository;

	@Autowired
	private ActorService			actorService;

	@Autowired
	private BoxService				boxService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private Validator				validator;

	@Autowired
	private PrisonerService			prisonerService;

	@Autowired
	private WardenService			wardenService;


	// Actualizar caja que tiene el mensaje EN ESTE ORDEN
	// ACTUALIZAR CAJA SIN EL MENSAJE
	// BORRAR EL MENSAJE Y TODAS SUS COPIAS
	public void delete(Message m) {
		this.messageRepository.delete(m);
	}

	public Message sendMessageBroadcasted(Message message) {

		this.actorService.loggedAsActor();

		Box inboxReceipent = new Box();

		Box boxSent = new Box();

		Message messageSaved = this.messageRepository.saveAndFlush(message);
		//Message messageCopy = this.create(messageSaved.getSubject(), messageSaved.getBody(), messageSaved.getPriority(), messageSaved.getTags(), messageSaved.getRecipient());
		//messageCopy.setTags(messageSaved.getTags());

		//Message messageCopySaved = this.messageRepository.save(messageCopy);
		//Actor actorSent = this.actorService.getActorByUsername(messageSaved.getSender());
		Actor actorReceipent = this.actorService.getActorByUsername(messageSaved.getRecipient());

		//boxSent = this.boxService.getSentBoxByActor(actorSent);
		inboxReceipent = this.boxService.getRecievedBoxByActor(actorReceipent);

		// Guardar la box con ese mensaje;

		inboxReceipent.getMessages().add(messageSaved);
		//boxSent.getMessages().add(messageSaved);
		// boxRecieved.setMessages(list);
		//this.boxService.saveSystem(boxSent);
		this.boxService.saveSystem(inboxReceipent);
		//this.actorService.save(actorSent);
		this.actorService.flushSave(actorReceipent);

		return messageSaved;
	}

	// Metodo para enviar un mensaje a un ACTOR (O varios, que tambien puede ser)
	public Message sendMessage(Message message) {

		this.actorService.loggedAsActor();

		Actor actorRecieved = this.actorService.getActorByUsername(message.getRecipient());
		Actor senderActor = this.actorService.getActorByUsername(message.getSender());

		Assert.isTrue(senderActor.getUserAccount().getUsername().equals(message.getSender()));

		Box boxRecieved = new Box();
		Box boxSusupicious = new Box();
		Box boxSent = new Box();
		Box BoxRecievedSender = new Box();

		List<String> spam = new ArrayList<String>();

		spam = this.configurationService.getConfiguration().getSpamWords();

		Message messageSaved = this.messageRepository.save(message);
		Message messageCopy = this.create(messageSaved.getSubject(), messageSaved.getBody(), messageSaved.getPriority(), messageSaved.getTags(), messageSaved.getRecipient());
		Message messageCopySaved = this.messageRepository.save(messageCopy);
		Message messageNotification = this.create();
		Message messageNotificationHard = this.create();
		Message messageNotificationFinal = this.create();

		boxSent = this.boxService.getSentBoxByActor(senderActor);
		boxRecieved = this.boxService.getRecievedBoxByActor(actorRecieved);
		boxSusupicious = this.boxService.getSuspiciousBoxByActor(actorRecieved);
		BoxRecievedSender = this.boxService.getRecievedBoxByActor(senderActor);

		double valueCrim = 0;

		// Guardar la box con ese mensaje;

		if (this.configurationService.isStringSpam(messageSaved.getBody(), spam) || this.configurationService.isStringSpam(messageSaved.getSubject(), spam) || this.configurationService.isStringSpam(messageSaved.getTags(), spam)) {
			boxSent.getMessages().add(messageSaved);
			boxSusupicious.getMessages().add(messageCopySaved);

			if (this.prisonerService.booleanLogedAsPrisoner()) {
				Prisoner prisoner = this.prisonerService.loggedPrisoner();
				prisoner.setIsSuspect(true);

				if (prisoner.getCrimeRate() != 1.0) {
					valueCrim = prisoner.getCrimeRate() + 0.05;
					String s = String.format("%.2f", valueCrim);
					double val = Double.parseDouble(s);
					prisoner.setCrimeRate(val);

					if (valueCrim == -0.75) {
						messageNotification
							.setBody("Se le informa de que si continua teniendo un comportamiento inadecuado nos veremos obligados a restringir su mensajería. / You are advised that if you continue to behave inappropriately we will be forced to restrict your messaging.");
						messageNotification.setPriority(PriorityLvl.HIGH);
						messageNotification.setRecipient(senderActor.getUserAccount().getUsername());
						messageNotification.setSender("SYSTEM");
						messageNotification.setSubject("Bad behavior warning/Aviso de mala condcuta");
						messageNotification.setTags("NOTIFICATION / NOTIFICACION");
						Message savedMessageNotification = this.messageRepository.save(messageNotification);
						BoxRecievedSender.getMessages().add(savedMessageNotification);
					}
					if (valueCrim == -0.5) {
						messageNotificationFinal
							.setBody("Esto es un aviso final, si no rectifica su conducta su mensajeria sera bloqueada hasta que muestre un buen comportamiento. / This is a final warning, if you do not rectify your behavior your messaging will be blocked until it shows good behavior.");
						messageNotificationFinal.setPriority(PriorityLvl.HIGH);
						messageNotificationFinal.setRecipient(senderActor.getUserAccount().getUsername());
						messageNotificationFinal.setSender("SYSTEM");
						messageNotificationFinal.setSubject("Bad behavior final warning/Aviso final por mala conducta");
						messageNotificationFinal.setTags("NOTIFICATION / NOTIFICACION");
						Message savedMessageNotificationFinal = this.messageRepository.save(messageNotificationFinal);
						BoxRecievedSender.getMessages().add(savedMessageNotificationFinal);
					}
					if (valueCrim > -0.5) {
						messageNotificationHard.setBody("Se le informa de que su sistema de mensajeria ha sido restringido por mal comportamiento. / You are informed that your messaging system has been restricted for misbehavior.");
						messageNotificationHard.setPriority(PriorityLvl.HIGH);
						messageNotificationHard.setRecipient(senderActor.getUserAccount().getUsername());
						messageNotificationHard.setSender("SYSTEM");
						messageNotificationHard.setSubject("Bad behavior ban/Bloqueo por mala condcuta");
						messageNotificationHard.setTags("NOTIFICATION / NOTIFICACION");
						Message savedMessageNotificationHard = this.messageRepository.save(messageNotificationHard);
						BoxRecievedSender.getMessages().add(savedMessageNotificationHard);
					}
				}
			}
			this.boxService.saveSystem(boxSent);
			this.boxService.saveSystem(boxSusupicious);
			this.boxService.saveSystem(BoxRecievedSender);
			this.actorService.save(senderActor);
			this.actorService.save(actorRecieved);

		} else {
			boxRecieved.getMessages().add(messageCopySaved);
			boxSent.getMessages().add(messageSaved);
			// boxRecieved.setMessages(list);
			this.boxService.saveSystem(boxSent);
			this.boxService.saveSystem(boxRecieved);
			this.actorService.save(senderActor);
			this.actorService.save(actorRecieved);
		}

		// Calculamos la Polarity y el hasSpam
		//TODO: this.actorService.updateActorSpam(senderActor);
		//TODO: this.configurationService.computeScore(senderActor);
		return messageSaved;
	}
	public Message save(Message message) {
		return this.messageRepository.save(message);

	}

	public Message create() {

		this.actorService.loggedAsActor();

		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();

		Date thisMoment = new Date();
		thisMoment.setTime(thisMoment.getTime() - 1000);

		Message message = new Message();
		String sender = userAccount.getUsername();
		message.setMoment(thisMoment);
		message.setSubject("");
		message.setBody("");
		message.setPriority(PriorityLvl.NEUTRAL);
		message.setRecipient("");
		message.setTags("");
		message.setSender(sender);

		return message;
	}

	public Message createSecurityBreach() {

		this.wardenService.loggedAsWarden();

		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();

		Date thisMoment = new Date();
		thisMoment.setTime(thisMoment.getTime() - 1000);

		Message message = new Message();

		Actor sender = this.actorService.getActorByUsername(userAccount.getUsername());
		Actor receiver = new Actor();

		message.setMoment(thisMoment);
		message.setSubject("Error de seguridad / Security Breach");
		message.setBody("Esto es un mensaje para informar que ha habido una brecha de seguridad // This is a message to inform about a security breach");
		message.setPriority(PriorityLvl.HIGH);
		message.setRecipient(sender.getUserAccount().getUsername());
		message.setTags("Security, Breach, Notification, Urgent, Important");
		message.setSender(sender.getUserAccount().getUsername());

		return message;
	}

	public Message create(String Subject, String body, PriorityLvl priority, String tags, String recipient) {

		this.actorService.loggedAsActor();

		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();

		Date thisMoment = new Date();
		thisMoment.setTime(thisMoment.getTime() - 1);

		Message message = new Message();

		Actor sender = this.actorService.getActorByUsername(userAccount.getUsername());

		message.setMoment(thisMoment);
		message.setSubject(Subject);
		message.setBody(body);
		message.setPriority(priority);
		message.setRecipient(recipient);
		message.setTags(tags);
		message.setSender(sender.getUserAccount().getUsername());

		return message;
	}

	public Message createNotification(String Subject, String body, PriorityLvl priority, String tags, String recipient) {
		this.actorService.loggedAsActor();

		Date thisMoment = new Date();
		thisMoment.setTime(thisMoment.getTime() - 1);

		Message message = new Message();

		Actor sender = this.actorService.getActorByUsername("system");

		message.setMoment(thisMoment);
		message.setSubject(Subject);
		message.setBody(body);
		message.setPriority(priority);
		message.setRecipient(recipient);
		message.setTags(tags);
		message.setSender(sender.getUserAccount().getUsername());

		return message;
	}

	public void updateMessage(Message message, Box box) { // Posible problema
		// con copia

		this.actorService.loggedAsActor();
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		Actor actor = this.actorService.getActorByUsername(userAccount.getUsername());

		for (Box b : actor.getBoxes()) {
			if (b.getMessages().contains(message))
				b.getMessages().remove(message);
			// list.remove(message);
			// b.setMessages(list);
			if (b.getName().equals(box.getName())) {
				List<Message> list = b.getMessages();
				list.add(message);
				b.setMessages(list);
			}
		}
	}

	public void deleteMessageFinal(Message message) {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		Actor actor = this.actorService.getActorByUsername(userAccount.getUsername());

		for (Box b : actor.getBoxes())
			if (b.getMessages().contains(message))
				b.getMessages().remove(message);
		// list.remove(message);
		// b.setMessages(list);
		this.delete(message);
	}

	public void deleteMessageToTrashBox(Message message) {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		Actor actor = this.actorService.getActorByUsername(userAccount.getUsername());

		// Box currentBox = this.boxService.getCurrentBoxByMessage(message);

		List<Box> currentBoxes = new ArrayList<>();

		for (Box b : actor.getBoxes())
			if (b.getMessages().contains(message))
				currentBoxes.add(b);

		Box trash = this.boxService.getTrashBoxByActor(actor);

		// When an actor removes a message from a box other than trash box, it
		// is moved to the trash box;

		for (Box b : actor.getBoxes()) {
			if (b.getMessages().contains(message))
				b.getMessages().remove(message);
			// list.remove(message);
			// b.setMessages(list);
			if (b.equals(trash)) {
				List<Message> list = b.getMessages();
				list.add(message);
				b.setMessages(list);
			}
		}

		/*
		 * for (Box currentBox : currentBoxes)
		 * if (currentBox.equals(trash)) {
		 * for (Box b : actor.getBoxes())
		 * if (b.getMessages().contains(message)) {
		 * b.getMessages().remove(message);
		 * this.messageRepository.delete(message);
		 * }
		 * } else
		 * this.updateMessage(message, trash);
		 */
	}
	public void copyMessage(Message message, Box box) {

		this.actorService.loggedAsActor();
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		Actor actor = this.actorService.getActorByUsername(userAccount.getUsername());
		Assert.isTrue(actor.getBoxes().contains(box));

		box.getMessages().add(message);
	}

	public List<Message> findAll() {
		return this.messageRepository.findAll();
	}

	public List<Message> findAll2() {
		return this.messageRepository.findAll2();
	}

	public Message findOne(int id) {
		return this.messageRepository.findOne(id);
	}

	public List<Message> getMessagesByBox(Box b) {
		return this.messageRepository.getMessagesOfBox(b);
	}

	public domain.Message reconstruct(domain.Message messageTest, BindingResult binding) {

		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		Actor actor = this.actorService.getActorByUsername(userAccount.getUsername());

		domain.Message result;
		if (messageTest.getId() == 0) {
			result = messageTest;
			result.setSender(userAccount.getUsername());
			Date thisMoment = new Date();
			thisMoment.setTime(thisMoment.getTime() - 1000);
			result.setMoment(thisMoment);

		} else {
			result = this.messageRepository.findOne(messageTest.getId());

			result.setBody(messageTest.getBody());
			result.setPriority(messageTest.getPriority());
			result.setTags(messageTest.getTags());
			result.setSubject(messageTest.getSubject());
			result.setRecipient(messageTest.getRecipient());
			// result.setMoment(messageTest.getMoment());
		}

		this.validator.validate(result, binding);
		return result;

	}

	public Message reconstructDelete(Message messageTest) {

		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		Actor actor = this.actorService.getActorByUsername(userAccount.getUsername());

		Message result;

		result = this.messageRepository.findOne(messageTest.getId());

		return result;

	}

	public void deleteInBatch(List<Message> messages) {
		this.messageRepository.deleteInBatch(messages);
	}

	public void sendNotificationChangeStatusOfVisit(Prisoner p, Visitor v, Visit visit) {

		Message messagePrisoner = this.create();

		messagePrisoner.setSubject("Status change in a visit / Cambio de estado de una visita");
		messagePrisoner.setBody("Visit with description '" + visit.getDescription() + "' has changed the status to " + visit.getVisitStatus() + "/ La visita " + visit.getDescription() + " ha cambiado su estado a " + visit.getVisitStatus());
		messagePrisoner.setPriority(PriorityLvl.HIGH);
		messagePrisoner.setRecipient(p.getUserAccount().getUsername());
		messagePrisoner.setSender("SYSTEM");
		messagePrisoner.setTags("NOTIFICATION / NOTIFICACION");

		Message messagePrisonerSaved = this.save(messagePrisoner);

		messagePrisoner.setRecipient(v.getUserAccount().getUsername());
		Message messageVisitorSaved = this.save(messagePrisoner);

		Box boxRecievedPrisoner = new Box();
		Box boxRecievedVisitor = new Box();

		boxRecievedPrisoner = this.boxService.getRecievedBoxByActor(p);
		boxRecievedVisitor = this.boxService.getRecievedBoxByActor(v);

		boxRecievedPrisoner.getMessages().add(messagePrisonerSaved);
		boxRecievedVisitor.getMessages().add(messageVisitorSaved);

		this.actorService.save(v);
		this.actorService.save(p);

	}

	public void flush() {
		this.messageRepository.flush();
	}

	public List<Message> messagesOfActor(Actor a) {
		return this.messageRepository.messagesOfActor(a);
	}
}
