
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
import services.SalesManService;
import services.WardenService;
import domain.SalesMan;
import forms.FormObjectSalesman;

@Controller
@RequestMapping("/salesman/warden")
public class SalesmanWardenController extends AbstractController {

	@Autowired
	private WardenService	wardenService;

	@Autowired
	private SalesManService	salesManService;

	@Autowired
	private ActorService	actorService;


	public SalesmanWardenController() {
		super();
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView register() {
		ModelAndView result;

		try {
			this.wardenService.loggedAsWarden();
			FormObjectSalesman formSalesman = new FormObjectSalesman();

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			result = new ModelAndView("warden/registerSalesman");
			result.addObject("formSalesman", formSalesman);
			result.addObject("locale", locale);

		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}

		return result;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@ModelAttribute("formSalesman") @Valid FormObjectSalesman formSalesman, BindingResult binding) {
		ModelAndView result;

		SalesMan salesman = new SalesMan();
		salesman = this.salesManService.reconstruct(formSalesman, binding);

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
		List<String> usernames = this.actorService.getAllUsernamesInTheSystem();

		if (usernames.contains(formSalesman.getUsername())) {
			result = new ModelAndView("warden/registerSalesman");
			result.addObject("formSalesman", formSalesman);
			result.addObject("locale", locale);
			result.addObject("message", "warden.duplicatedUsername");

			return result;
		}

		if (binding.hasErrors()) {
			result = new ModelAndView("warden/registerSalesman");
			result.addObject("formSalesman", formSalesman);
			result.addObject("locale", locale);
		} else
			try {
				this.salesManService.saveSalesman(salesman);
				result = new ModelAndView("redirect:/");
			} catch (Throwable oops) {
				result = new ModelAndView("warden/registerSalesman");
				result.addObject("formSalesman", formSalesman);
				result.addObject("locale", locale);
				result.addObject("message", "warden.register.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;

		try {
			List<SalesMan> salesmen = this.salesManService.getSalesMenAsWarden();

			result = new ModelAndView("warden/salesman");
			result.addObject("salesmen", salesmen);
			result.addObject("requestURI", "/salesman/warden/list.do");
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}

		return result;
	}
}
