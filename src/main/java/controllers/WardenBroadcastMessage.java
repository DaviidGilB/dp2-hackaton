
package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.ConfigurationService;
import services.MessageService;
import services.WardenService;
import domain.Message;
import domain.PriorityLvl;

@Controller
@RequestMapping("/broadcast/warden")
public class WardenBroadcastMessage extends AbstractController {

	@Autowired
	private WardenService			wardenService;

	@Autowired
	private MessageService			messageService;

	@Autowired
	private ConfigurationService	configurationService;


	public WardenBroadcastMessage() {

		super();
	}

	@RequestMapping(value = "/send", method = RequestMethod.GET)
	public ModelAndView create() {
		try {
			if (!this.wardenService.loggedAsWardenBoolean())
				return new ModelAndView("redirect: /");
			ModelAndView result;
			Message message;
			List<PriorityLvl> priority = new ArrayList<PriorityLvl>();
			priority = Arrays.asList(PriorityLvl.values());

			message = this.messageService.create();
			result = new ModelAndView("broadcast/warden/send");
			result.addObject("messageSend", message);
			result.addObject("priority", priority);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}

	}

	@RequestMapping(value = "/sendSecurityBreach", method = RequestMethod.GET)
	public ModelAndView createSecurityBreach() {
		try {
			if (!this.wardenService.loggedAsWardenBoolean())
				return new ModelAndView("redirect: /");
			ModelAndView result;
			Message message;

			List<PriorityLvl> priority = new ArrayList<PriorityLvl>();
			priority = Arrays.asList(PriorityLvl.values());

			message = this.messageService.createSecurityBreach();
			result = new ModelAndView("broadcast/warden/send");
			result.addObject("messageSend", message);
			result.addObject("priority", priority);
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	//Save
	@RequestMapping(value = "/send", method = RequestMethod.POST, params = "send")
	public ModelAndView send(@ModelAttribute("messageSend") Message message, BindingResult binding) {
		try {
			ModelAndView result;
			if (!this.wardenService.loggedAsWardenBoolean())
				return new ModelAndView("redirect: /");
			message = this.wardenService.reconstruct(message, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(message);
			else
				try {
					this.wardenService.broadcastMessage(message);
					result = new ModelAndView("redirect:/box/actor/list.do");

				} catch (Throwable oops) {
					result = this.createEditModelAndView(message, "message.commit.error");
				}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/send", method = RequestMethod.POST, params = "sendGuards")
	public ModelAndView sendGuards(@ModelAttribute("messageSend") Message message, BindingResult binding) {
		try {
			ModelAndView result;
			if (!this.wardenService.loggedAsWardenBoolean())
				return new ModelAndView("redirect: /");
			message = this.wardenService.reconstruct(message, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(message);
			else
				try {
					this.wardenService.broadcastMessageGuards(message);
					result = new ModelAndView("redirect:/box/actor/list.do");

				} catch (Throwable oops) {
					result = this.createEditModelAndView(message, "message.commit.error");
				}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/send", method = RequestMethod.POST, params = "sendPrisoners")
	public ModelAndView sendPrisoners(@ModelAttribute("messageSend") Message message, BindingResult binding) {
		try {
			ModelAndView result;
			if (!this.wardenService.loggedAsWardenBoolean())
				return new ModelAndView("redirect: /");
			message = this.wardenService.reconstruct(message, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(message);
			else
				try {
					this.wardenService.broadcastMessagePrisoners(message);
					result = new ModelAndView("redirect:/box/actor/list.do");

				} catch (Throwable oops) {
					result = this.createEditModelAndView(message, "message.commit.error");
				}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	protected ModelAndView createEditModelAndView(Message message) {
		ModelAndView result;

		result = this.createEditModelAndView(message, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(Message message, String messageCode) {
		ModelAndView result;

		List<PriorityLvl> priority = new ArrayList<PriorityLvl>();
		priority = Arrays.asList(PriorityLvl.values());

		result = new ModelAndView("broadcast/warden/send");
		result.addObject("messageSend", message);
		result.addObject("priority", priority);
		result.addObject("message", messageCode);

		return result;
	}

}
