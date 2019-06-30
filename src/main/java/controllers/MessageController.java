
package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.Authority;
import security.LoginService;
import security.UserAccount;
import services.ActorService;
import services.BoxService;
import services.MessageService;
import services.PrisonerService;
import domain.Actor;
import domain.Box;
import domain.Message;
import domain.PriorityLvl;
import domain.Prisoner;

@Controller
@RequestMapping("/message/actor")
public class MessageController extends AbstractController {

	@Autowired
	private MessageService	messageService;

	@Autowired
	private BoxService		boxService;

	@Autowired
	private ActorService	actorService;

	@Autowired
	private PrisonerService	prisonerService;


	//List
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(@RequestParam(required = false) String boxId) {
		try {

			Assert.isTrue(StringUtils.isNumeric(boxId));
			int boxIdInt = Integer.parseInt(boxId);

			String locale = LocaleContextHolder.getLocale().getLanguage();
			this.actorService.loggedAsActor();
			Box box = new Box();
			box = this.boxService.findOne(boxIdInt);
			UserAccount userAccount = LoginService.getPrincipal();
			Boolean crimRate = false;

			Actor a = this.actorService.getActorByUsername(userAccount.getUsername());
			if (!(this.actorService.getlistOfBoxes(a).contains(box)))
				return new ModelAndView("redirect:/box/actor/list.do");

			ModelAndView result;

			List<Message> messages;

			Actor actor = new Actor();
			List<Box> boxes = new ArrayList<Box>();

			actor = this.actorService.getActorByUsername(userAccount.getUsername());
			boxes = this.actorService.getlistOfBoxes(actor);

			messages = this.messageService.getMessagesByBox(box);
			String username = userAccount.getUsername();
			List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
			if (authorities.get(0).toString().equals("PRISONER")) {
				Prisoner prisoner = this.prisonerService.getPrisonerByUsername(username);
				if (prisoner.getCrimeRate() > -0.5)
					crimRate = true;

			}
			result = new ModelAndView("message/actor/list");
			result.addObject("messages", messages);
			result.addObject("crimRate", crimRate);
			result.addObject("boxName", box.getName());
			result.addObject("currentBox", box);
			result.addObject("boxId", boxIdInt);
			result.addObject("boxes", boxes);
			result.addObject("locale", locale);
			result.addObject("requestURI", "message/actor/list.do");

			return result;
		} catch (Throwable oops2) {
			return new ModelAndView("redirect:/");
		}
	}
	//Create
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() {

		try {
			this.actorService.loggedAsActor();
			ModelAndView result;
			Message message;
			UserAccount userAccount = LoginService.getPrincipal();
			String username = userAccount.getUsername();
			List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
			if (authorities.get(0).toString().equals("PRISONER")) {
				Prisoner prisoner = this.prisonerService.getPrisonerByUsername(username);
				if (prisoner.getCrimeRate() > -0.5)
					return new ModelAndView("redirect:/box/actor/list.do");
			}

			message = this.messageService.create();
			result = this.createEditModelAndView(message);

			return result;
		} catch (Throwable oops2) {
			return new ModelAndView("redirect:/list.do");
		}
	}

	//Save
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@ModelAttribute("messageTest") domain.Message messageTest, BindingResult binding) {
		try {
			this.actorService.loggedAsActor();
			ModelAndView result;
			domain.Message savedMessage;
			List<Box> boxes;
			Box box;
			UserAccount userAccount = LoginService.getPrincipal();
			String username = userAccount.getUsername();
			List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
			if (authorities.get(0).toString().equals("PRISONER")) {
				Prisoner prisoner = this.prisonerService.getPrisonerByUsername(username);
				if (prisoner.getCrimeRate() > -0.5)
					return new ModelAndView("redirect:/box/actor/list.do");
			}

			messageTest = this.messageService.reconstruct(messageTest, binding);

			Assert.isTrue(userAccount.getUsername().equals(messageTest.getSender()));

			if (binding.hasErrors())
				result = this.createEditModelAndView(messageTest);
			else
				try {
					savedMessage = this.messageService.sendMessage(messageTest);
					boxes = this.boxService.getCurrentBoxByMessage(savedMessage);
					box = boxes.get(0);
					result = new ModelAndView("redirect:list.do?boxId=" + box.getId());
				} catch (Throwable oops) {
					result = this.createEditModelAndView(messageTest, "message.commit.error");
				}
			return result;
		} catch (Throwable oops2) {
			return new ModelAndView("redirect:/list.do");
		}
	}
	//Create
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam int rowId) {
		this.actorService.loggedAsActor();
		ModelAndView result;
		Message message;

		message = this.messageService.findOne(rowId);

		Assert.notNull(message);
		result = this.createEditModelAndView(message);

		return result;
	}

	//Save
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView delete(@RequestParam(required = false) String rowId, @RequestParam(required = false) String boxId) {
		try {
			this.actorService.loggedAsActor();
			Assert.isTrue(StringUtils.isNumeric(rowId));
			int messageIdInt = Integer.parseInt(rowId);

			Assert.isTrue(StringUtils.isNumeric(boxId));
			int boxIdInt = Integer.parseInt(boxId);

			Message message = this.messageService.findOne(messageIdInt);

			UserAccount userAccount = LoginService.getPrincipal();
			Box currentBox = this.boxService.findOne(boxIdInt);
			Actor a = this.actorService.getActorByUsername(userAccount.getUsername());

			List<Message> messagesOfActor = this.messageService.messagesOfActor(a);

			Assert.isTrue(messagesOfActor.contains(message));

			ModelAndView result;

			Box trashBox = this.boxService.getTrashBoxByActor(a);

			if (!(userAccount.getUsername().equals(message.getSender()) || userAccount.getUsername().equals(message.getRecipient())))
				return new ModelAndView("redirect:/box/actor/list.do");

			try {
				if (currentBox.equals(trashBox)) {
					this.messageService.deleteMessageFinal(message);
					this.messageService.flush();
				} else {
					this.messageService.deleteMessageToTrashBox(message);
					this.messageService.flush();
				}
				result = new ModelAndView("redirect:/box/actor/list.do");
			} catch (Throwable oops) {
				result = this.createEditModelAndView(message, "message.commit.error");

			}
			return result;
		} catch (Throwable oops2) {
			return new ModelAndView("redirect:/list.do");
		}
	}
	//Create
	@RequestMapping(value = "/createmove", method = RequestMethod.GET)
	public ModelAndView createMove() {
		try {
			this.actorService.loggedAsActor();
			ModelAndView result;
			Message message;

			message = this.messageService.create();
			result = this.createEditModelAndViewMove(message);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:list.do");
		}
	}

	@RequestMapping(value = "/move", method = RequestMethod.GET)
	public ModelAndView update(@RequestParam(required = false) String messageId, @RequestParam(required = false) String boxId) {
		try {
			Assert.isTrue(StringUtils.isNumeric(messageId));
			int messageIdInt = Integer.parseInt(messageId);

			Assert.isTrue(StringUtils.isNumeric(boxId));
			int boxIdInt = Integer.parseInt(boxId);

			this.actorService.loggedAsActor();
			ModelAndView result;
			Message message;
			Box box;

			message = this.messageService.findOne(messageIdInt);

			box = this.boxService.findOne(boxIdInt);

			try {
				this.messageService.updateMessage(message, box);
				this.messageService.flush();
				result = new ModelAndView("redirect:/box/actor/list.do");
			} catch (Throwable oops) {
				result = this.createEditModelAndViewMove(message, "message.commit.error");
			}
			return result;
		} catch (Throwable oops2) {
			return new ModelAndView("redirect:/list.do");
		}
	}
	@RequestMapping(value = "/copy", method = RequestMethod.GET)
	public ModelAndView copy(@RequestParam(required = false) String messageId, @RequestParam(required = false) String boxId) {
		try {

			Assert.isTrue(StringUtils.isNumeric(messageId));
			int messageIdInt = Integer.parseInt(messageId);

			Assert.isTrue(StringUtils.isNumeric(boxId));
			int boxIdInt = Integer.parseInt(boxId);

			ModelAndView result;
			Message message;
			Box box;

			message = this.messageService.findOne(messageIdInt);
			box = this.boxService.findOne(boxIdInt);

			try {
				this.messageService.copyMessage(message, box);
				this.messageService.flush();
				result = new ModelAndView("redirect:/box/actor/list.do");
			} catch (Throwable oops) {
				result = this.createEditModelAndViewMove(message, "message.commit.error");
			}
			return result;
		} catch (Throwable oops2) {
			return new ModelAndView("redirect:/list.do");
		}
	}
	protected ModelAndView createEditModelAndViewMove(Message message) {
		ModelAndView result;

		result = this.createEditModelAndViewMove(message, null);

		return result;
	}

	protected ModelAndView createEditModelAndViewMove(Message message, String messageCode) {
		ModelAndView result;

		UserAccount userAccount = LoginService.getPrincipal();
		String username = userAccount.getUsername();
		Actor actor = new Actor();

		actor = this.actorService.getActorByUsername(username);
		List<Actor> actors = new ArrayList<Actor>();
		actors = this.actorService.findAll();

		List<Box> actorBoxes = new ArrayList<Box>();
		actorBoxes = this.actorService.getlistOfBoxes(actor);

		List<PriorityLvl> priority = new ArrayList<PriorityLvl>();
		priority = Arrays.asList(PriorityLvl.values());

		result = new ModelAndView("message/actor/move");
		result.addObject("messageTest", message);
		result.addObject("actors", actors);
		result.addObject("actorBoxes", actorBoxes);
		result.addObject("priority", priority);

		result.addObject("message", messageCode);

		return result;
	}
	//CreateEditModelAndView
	protected ModelAndView createEditModelAndView(Message messageTest) {
		ModelAndView result;

		result = this.createEditModelAndView(messageTest, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(Message messageTest, String messageCode) {
		ModelAndView result;

		UserAccount userAccount = LoginService.getPrincipal();
		String username = userAccount.getUsername();
		Actor actor = new Actor();

		actor = this.actorService.getActorByUsername(username);
		List<String> actors = new ArrayList<String>();
		actors = this.actorService.getUsernamesOfActorsAndGoodPrisoners();

		List<PriorityLvl> priority = new ArrayList<PriorityLvl>();
		priority = Arrays.asList(PriorityLvl.values());

		List<Box> actorBoxes = new ArrayList<Box>();
		actorBoxes = this.actorService.getlistOfBoxes(actor);
		result = new ModelAndView("message/actor/create");

		List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();

		if (authorities.get(0).toString().equals("PRISONER")) {
			Prisoner prisoner = this.prisonerService.getPrisonerByUsername(username);
			if (prisoner.getCrimeRate() > -0.5)
				return new ModelAndView("redirect:/box/actor/list.do");
			List<Actor> actorsToSendMessageOfPrisoners = this.actorService.getActorsToSendMessageOfPrisoners();
			result.addObject("actors", actorsToSendMessageOfPrisoners);
		} else
			result.addObject("actors", actors);
		result.addObject("messageTest", messageTest);

		result.addObject("actorBoxes", actorBoxes);
		result.addObject("priority", priority);

		result.addObject("message", messageCode);

		return result;
	}
}
