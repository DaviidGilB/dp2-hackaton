
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
import services.WardenService;
import domain.Warden;
import forms.FormObjectWarden;

@Controller
@RequestMapping("/warden/warden")
public class WardenWardenController extends AbstractController {

	@Autowired
	private WardenService	wardenService;

	@Autowired
	private ActorService	actorService;


	public WardenWardenController() {
		super();
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView register() {
		ModelAndView result;

		try {
			this.wardenService.loggedAsWarden();
			FormObjectWarden formWarden = new FormObjectWarden();

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			result = new ModelAndView("warden/registerWarden");
			result.addObject("formWarden", formWarden);
			result.addObject("locale", locale);

		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}

		return result;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@ModelAttribute("formWarden") @Valid FormObjectWarden formWarden, BindingResult binding) {
		ModelAndView result;

		Warden warden = new Warden();
		warden = this.wardenService.reconstruct(formWarden, binding);

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

		List<String> usernames = this.actorService.getAllUsernamesInTheSystem();

		if (usernames.contains(formWarden.getUsername())) {
			result = new ModelAndView("warden/registerWarden");
			result.addObject("formGuard", formWarden);
			result.addObject("locale", locale);
			result.addObject("message", "warden.duplicatedUsername");
			return result;
		}

		if (binding.hasErrors()) {
			result = new ModelAndView("warden/registerWarden");
			result.addObject("formWarden", formWarden);
			result.addObject("locale", locale);
		} else
			try {
				this.wardenService.saveWarden(warden);
				result = new ModelAndView("redirect:/");
			} catch (Throwable oops) {
				result = new ModelAndView("warden/registerWarden");
				result.addObject("formWarden", formWarden);
				result.addObject("locale", locale);
				result.addObject("message", "warden.register.commit.error");
			}

		return result;
	}
}
