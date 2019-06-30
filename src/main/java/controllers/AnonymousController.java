
package controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.Authority;
import security.LoginService;
import security.UserAccount;
import services.ActorService;
import services.ConfigurationService;
import services.PrisonerService;
import services.SocialWorkerService;
import services.VisitorService;
import domain.Charge;
import domain.Configuration;
import domain.Prisoner;
import domain.SocialWorker;
import domain.Visitor;
import forms.FormObjectSocialWorker;
import forms.FormObjectVisitor;

@Controller
@RequestMapping("/anonymous")
public class AnonymousController extends AbstractController {

	@Autowired
	private PrisonerService			prisonerService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private SocialWorkerService		socialWorkerService;

	@Autowired
	private VisitorService			visitorService;

	@Autowired
	private ActorService			actorService;


	public AnonymousController() {
		super();
	}

	// -------------------------------------------------------------------
	// ---------------------------REGISTER--------------------------------

	@RequestMapping(value = "/socialWorker/create", method = RequestMethod.GET)
	public ModelAndView registerAsSocialWorker() {
		ModelAndView result;
		try {
			FormObjectSocialWorker formObjectSocialWorker = new FormObjectSocialWorker();
			formObjectSocialWorker.setTermsAndConditions(false);

			result = this.createEditModelAndView(formObjectSocialWorker);

		} catch (Exception e) {

			result = new ModelAndView("redirect:/anonymous/socialWorker/create.do");
		}

		return result;

	}

	@RequestMapping(value = "/socialWorker/create", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid FormObjectSocialWorker formObjectSocialWorker, BindingResult binding) {
		try {
			ModelAndView result;

			SocialWorker socialWorker = new SocialWorker();
			socialWorker = this.socialWorkerService.create();
			// Reconstruccion
			socialWorker = this.socialWorkerService.reconstruct(formObjectSocialWorker, binding);

			List<String> usernames = this.actorService.getAllUsernamesInTheSystem();

			if (usernames.contains(formObjectSocialWorker.getUsername())) {
				result = new ModelAndView("anonymous/socialWorker/create");
				result.addObject("formObjectSocialWorker", formObjectSocialWorker);
				result.addObject("message", "warden.duplicatedUsername");

				return result;
			}

			if (binding.hasErrors())
				result = this.createEditModelAndView(formObjectSocialWorker);
			else
				try {
					this.socialWorkerService.saveSocialWorker(socialWorker);

					result = new ModelAndView("redirect:/security/login.do");

				} catch (Throwable oops) {
					result = this.createEditModelAndView(formObjectSocialWorker, "socialWorker.duplicated.user");

				}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/socialWorker/create.do");
		}
	}

	protected ModelAndView createEditModelAndView(FormObjectSocialWorker formObjectSocialWorker) {
		ModelAndView result;

		result = this.createEditModelAndView(formObjectSocialWorker, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(FormObjectSocialWorker formObjectSocialWorker, String messageCode) {
		ModelAndView result;

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

		result = new ModelAndView("anonymous/socialWorker/create");
		result.addObject("formObjectSocialWorker", formObjectSocialWorker);
		result.addObject("message", messageCode);
		result.addObject("locale", locale);

		return result;
	}

	// -------------------------------------------------------------------
	// ---------------------------LIST------------------------------------

	// Listar todos los prisioneros del sistema
	@RequestMapping(value = "/prisoner/list", method = RequestMethod.GET)
	public ModelAndView listPrisoner() {

		ModelAndView result;
		List<Prisoner> prisoners;

		if (this.actorService.loggedAsActorBoolean()) {
			UserAccount user = LoginService.getPrincipal();
			List<Authority> authorities = (List<Authority>) user.getAuthorities();
			if (authorities.get(0).toString().equals("PRISONER"))
				return new ModelAndView("redirect:/");
		}

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

		prisoners = this.prisonerService.getIncarceratedPrisoners();

		result = new ModelAndView("anonymous/prisoner/list");
		result.addObject("prisoners", prisoners);
		result.addObject("locale", locale);
		result.addObject("requestURI", "anonymous/prisoner/list.do");

		return result;
	}
	// Listar todos los cargos de un prisionero
	@RequestMapping(value = "/charge/list", method = RequestMethod.GET)
	public ModelAndView listCharge(@RequestParam(required = false) String prisonerId) {

		ModelAndView result;
		try {

			Assert.isTrue(StringUtils.isNumeric(prisonerId));
			int prisonerIdInt = Integer.parseInt(prisonerId);

			Prisoner prisoner = this.prisonerService.findOne(prisonerIdInt);

			if (prisoner == null)
				return this.listPrisoner();

			List<Charge> charges = prisoner.getCharges();

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			result = new ModelAndView("anonymous/charge/list");
			result.addObject("charges", charges);
			result.addObject("locale", locale);
			result.addObject("requestURI", "anonymous/charge/list.do");
			result.addObject("warden", false);

		} catch (Throwable oops) {
			return this.listPrisoner();
		}
		return result;
	}

	@RequestMapping(value = "/termsAndConditionsEN", method = RequestMethod.GET)
	public ModelAndView listEN(HttpServletRequest request) {
		try {
			ModelAndView result;

			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			result = new ModelAndView("termsAndConditionsEN");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/termsAndConditionsES", method = RequestMethod.GET)
	public ModelAndView listES(HttpServletRequest request) {
		try {
			ModelAndView result;

			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);
			result = new ModelAndView("termsAndConditionsES");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/visitor/create", method = RequestMethod.GET)
	public ModelAndView createAdmin(HttpServletRequest request) {
		try {
			ModelAndView result;

			FormObjectVisitor formObjectVisitor = new FormObjectVisitor();
			formObjectVisitor.setTermsAndConditions(false);

			String imageURL = this.configurationService.getConfiguration().getImageURL();

			request.getSession().setAttribute("imageURL", imageURL);

			result = new ModelAndView("anonymous/visitor/create");

			result = this.createEditModelAndView(formObjectVisitor);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/visitor/create.do");
		}
	}

	@RequestMapping(value = "/visitor/create", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid FormObjectVisitor formObjectVisitor, BindingResult binding) {
		try {
			ModelAndView result;

			Visitor visitor = new Visitor();
			visitor = this.visitorService.create();

			Configuration configuration = this.configurationService.getConfiguration();
			String prefix = configuration.getSpainTelephoneCode();

			List<String> usernames = this.actorService.getAllUsernamesInTheSystem();

			if (usernames.contains(formObjectVisitor.getUsername())) {
				result = new ModelAndView("anonymous/visitor/create");
				result.addObject("formObjectVisitor", formObjectVisitor);
				result.addObject("message", "warden.duplicatedUsername");

				return result;
			}

			// Reconstruccion
			visitor = this.visitorService.reconstruct(formObjectVisitor, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(formObjectVisitor);
			else
				try {

					if (visitor.getPhoneNumber().matches("([0-9]{4,})$"))
						visitor.setPhoneNumber(prefix + visitor.getPhoneNumber());
					this.visitorService.save(visitor);

					result = new ModelAndView("redirect:/security/login.do");

				} catch (Throwable oops) {
					result = this.createEditModelAndView(formObjectVisitor, "company.duplicated.user");

				}
			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/visitor/create.do");
		}
	}

	protected ModelAndView createEditModelAndView(FormObjectVisitor formObjectVisitor) {
		ModelAndView result;

		result = this.createEditModelAndView(formObjectVisitor, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(FormObjectVisitor formObjectVisitor, String messageCode) {
		ModelAndView result;

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

		result = new ModelAndView("anonymous/visitor/create");
		result.addObject("formObjectVisitor", formObjectVisitor);
		result.addObject("message", messageCode);
		result.addObject("locale", locale);

		return result;
	}

}
