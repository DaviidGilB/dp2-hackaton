
package controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.ActorService;
import services.ConfigurationService;
import services.GuardService;
import services.WardenService;
import domain.Guard;
import forms.FormObjectGuard;

@Controller
@RequestMapping("/guard/warden")
public class GuardWardenController extends AbstractController {

	@Autowired
	private WardenService			wardenService;

	@Autowired
	private GuardService			guardService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private ActorService			actorService;


	public GuardWardenController() {
		super();
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView register() {
		ModelAndView result;

		try {
			this.wardenService.loggedAsWarden();
			FormObjectGuard formGuard = new FormObjectGuard();

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			result = new ModelAndView("warden/registerGuard");
			result.addObject("formGuard", formGuard);
			result.addObject("locale", locale);

		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}

		return result;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@ModelAttribute("formGuard") @Valid FormObjectGuard formGuard, BindingResult binding) {
		ModelAndView result;

		Guard guard = new Guard();
		guard = this.guardService.reconstruct(formGuard, binding);

		String prefix = this.configurationService.getConfiguration().getSpainTelephoneCode();

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

		List<String> usernames = this.actorService.getAllUsernamesInTheSystem();

		if (usernames.contains(formGuard.getUsername())) {
			result = new ModelAndView("warden/registerGuard");
			result.addObject("formGuard", formGuard);
			result.addObject("locale", locale);
			result.addObject("message", "warden.duplicatedUsername");

			return result;
		}

		if (binding.hasErrors()) {
			result = new ModelAndView("warden/registerGuard");
			result.addObject("formGuard", formGuard);
			result.addObject("locale", locale);
		} else
			try {

				if (guard.getPhone().matches("([0-9]{4,})$"))
					guard.setPhone(prefix + guard.getPhone());

				this.guardService.saveGuard(guard);
				result = new ModelAndView("redirect:/");
			} catch (Throwable oops) {
				result = new ModelAndView("warden/registerGuard");
				result.addObject("formGuard", formGuard);
				result.addObject("locale", locale);
				result.addObject("message", "warden.register.commit.error");
			}

		return result;
	}
}
