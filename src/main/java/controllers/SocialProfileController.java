
package controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import domain.Actor;
import domain.Configuration;
import domain.Guard;
import domain.Prisoner;
import domain.SalesMan;
import domain.SocialWorker;
import domain.Visitor;
import domain.Warden;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import services.ActorService;
import services.ConfigurationService;
import services.GuardService;
import services.PrisonerService;
import services.SalesManService;
import services.SocialWorkerService;
import services.VisitorService;
import services.WardenService;

@Controller
@RequestMapping("/authenticated")
public class SocialProfileController extends AbstractController {

	@Autowired
	private ActorService actorService;
	@Autowired
	private SocialWorkerService socialWorkerService;
	@Autowired
	private PrisonerService prisonerService;
	@Autowired
	private SalesManService salesManService;
	@Autowired
	private WardenService wardenService;
	@Autowired
	private GuardService guardService;
	@Autowired
	private VisitorService visitorService;
	@Autowired
	private ConfigurationService configurationService;

	// -------------------------------------------------------------------
	// ---------------------------LIST
	// BROTHERHOOD------------------------------------
	@RequestMapping(value = "/showProfile", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;

		try {
			UserAccount userAccount;
			userAccount = LoginService.getPrincipal();
			Actor logguedActor = new Actor();

			List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			result = new ModelAndView("authenticated/showProfile");

			logguedActor = this.actorService.getActorByUsername(userAccount.getUsername());

			if (authorities.get(0).toString().equals("SOCIALWORKER")) {
				SocialWorker socialWorker = this.socialWorkerService.findOne(logguedActor.getId());

				result.addObject("socialWorker", socialWorker);
				result.addObject("curriculum", socialWorker.getCurriculum());
			}

			if (authorities.get(0).toString().equals("PRISONER")) {
				Prisoner prisoner = this.prisonerService.findOne(logguedActor.getId());

				result.addObject("prisoner", prisoner);
				result.addObject("size", prisoner.getCharges().size());
			}

			if (authorities.get(0).toString().equals("SALESMAN")) {
				SalesMan salesman = this.salesManService.findOne(logguedActor.getId());

				result.addObject("salesman", salesman);
			}

			if (authorities.get(0).toString().equals("GUARD")) {
				Guard guard = this.guardService.findOne(logguedActor.getId());

				result.addObject("guard", guard);
			}

			if (authorities.get(0).toString().equals("VISITOR")) {
				Visitor visitor = this.visitorService.findOne(logguedActor.getId());

				result.addObject("visitor", visitor);
			}
			if (authorities.get(0).toString().equals("WARDEN")) {
				Warden warden = this.wardenService.findOne(logguedActor.getId());

				result.addObject("warden", warden);
			}

			result.addObject("actor", logguedActor);
			result.addObject("requestURI", "authenticated/showProfile.do");
			result.addObject("locale", locale);
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}

		return result;
	}

	@RequestMapping(value = "/editProfile", method = RequestMethod.GET)
	public ModelAndView edit() {
		ModelAndView result;

		try {
			UserAccount userAccount;
			userAccount = LoginService.getPrincipal();
			Actor logguedActor = new Actor();

			List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();

			logguedActor = this.actorService.getActorByUsername(userAccount.getUsername());

			result = new ModelAndView("authenticated/editProfile");

			if (authorities.get(0).toString().equals("WARDEN")) {
				Warden warden = this.wardenService.findOne(logguedActor.getId());
				result.addObject("warden", warden);
			}
			if (authorities.get(0).toString().equals("GUARD")) {
				Guard guard = this.guardService.findOne(logguedActor.getId());
				result.addObject("guard", guard);
			}
			if (authorities.get(0).toString().equals("SALESMAN")) {
				SalesMan salesman = this.salesManService.findOne(logguedActor.getId());
				result.addObject("salesman", salesman);
			}
			if (authorities.get(0).toString().equals("SOCIALWORKER")) {
				SocialWorker socialWorker = this.socialWorkerService.findOne(logguedActor.getId());
				result.addObject("socialWorker", socialWorker);
			}

			if (authorities.get(0).toString().equals("VISITOR")) {
				Visitor visitor = this.visitorService.findOne(logguedActor.getId());
				result.addObject("visitor", visitor);
			}
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}

		return result;
	}

	@RequestMapping(value = "/editProfile", method = RequestMethod.POST, params = "saveSocialWorker")
	public ModelAndView saveSocialWorker(SocialWorker socialWorker, BindingResult binding) {
		ModelAndView result;

		SocialWorker socialWorkerActor;
		socialWorkerActor = this.socialWorkerService.reconstruct(socialWorker, binding);

		if (binding.hasErrors()) {
			result = new ModelAndView("authenticated/editProfile");
			result.addObject("socialWorker", socialWorkerActor);
		} else
			try {
				this.socialWorkerService.saveEdit(socialWorkerActor);
				result = new ModelAndView("redirect:/authenticated/showProfile.do");
			} catch (Throwable oops) {
				result = new ModelAndView("authenticated/editProfile");
				result.addObject("socialWorker", socialWorkerActor);
				result.addObject("message", "socialWorker.save.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/editProfile", method = RequestMethod.POST, params = "saveWarden")
	public ModelAndView saveWarden(Warden warden, BindingResult binding) {
		ModelAndView result;

		Warden wardenActor;
		wardenActor = this.wardenService.reconstruct(warden, binding);

		if (binding.hasErrors()) {
			result = new ModelAndView("authenticated/editProfile");
			result.addObject("warden", wardenActor);
		} else
			try {
				this.wardenService.saveWarden(wardenActor);
				result = new ModelAndView("redirect:/authenticated/showProfile.do");
			} catch (Throwable oops) {
				result = new ModelAndView("authenticated/editProfile");
				result.addObject("warden", wardenActor);
				result.addObject("message", "warden.save.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/editProfile", method = RequestMethod.POST, params = "saveGuard")
	public ModelAndView saveGuard(Guard guard, BindingResult binding) {
		ModelAndView result;

		Guard guardActor;
		guardActor = this.guardService.reconstruct(guard, binding);

		if (binding.hasErrors()) {
			result = new ModelAndView("authenticated/editProfile");
			result.addObject("guard", guardActor);
		} else
			try {
				this.guardService.saveEdit(guardActor);
				result = new ModelAndView("redirect:/authenticated/showProfile.do");
			} catch (Throwable oops) {
				result = new ModelAndView("authenticated/editProfile");
				result.addObject("guard", guardActor);
				result.addObject("message", "warden.save.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/editProfile", method = RequestMethod.POST, params = "saveSalesMan")
	public ModelAndView saveSalesman(@ModelAttribute("salesman") SalesMan salesman, BindingResult binding) {
		ModelAndView result;

		SalesMan salesmanActor;
		salesmanActor = this.salesManService.reconstruct(salesman, binding);

		if (binding.hasErrors()) {
			result = new ModelAndView("authenticated/editProfile");
			result.addObject("salesman", salesmanActor);
		} else
			try {
				this.salesManService.saveSalesman(salesmanActor);
				result = new ModelAndView("redirect:/authenticated/showProfile.do");
			} catch (Throwable oops) {
				result = new ModelAndView("authenticated/editProfile");
				result.addObject("salesman", salesmanActor);
				result.addObject("message", "warden.save.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/editProfile", method = RequestMethod.POST, params = "saveVisitor")
	public ModelAndView saveSalesman(Visitor visitor, BindingResult binding) {
		ModelAndView result;

		Visitor visitorActor;
		visitorActor = this.visitorService.reconstruct(visitor, binding);
		Configuration configuration = this.configurationService.getConfiguration();
		String prefix = configuration.getSpainTelephoneCode();

		if (binding.hasErrors()) {
			result = new ModelAndView("authenticated/editProfile");
			result.addObject("visitor", visitorActor);
		} else
			try {
				if (visitor.getPhoneNumber().matches("([0-9]{4,})$"))
					visitor.setPhoneNumber(prefix + visitor.getPhoneNumber());
				this.visitorService.save(visitorActor);
				result = new ModelAndView("redirect:/authenticated/showProfile.do");
			} catch (Throwable oops) {
				result = new ModelAndView("authenticated/editProfile");
				result.addObject("visitor", visitorActor);
				result.addObject("message", "warden.save.commit.error");
			}

		return result;
	}

	@RequestMapping(value = "/deleteUser", method = RequestMethod.GET)
	public ModelAndView deleteUser() {
		ModelAndView result;

		try {
			UserAccount userAccount;
			userAccount = LoginService.getPrincipal();
			Actor logguedActor = new Actor();

			List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();

			logguedActor = this.actorService.getActorByUsername(userAccount.getUsername());

			result = new ModelAndView("redirect:/j_spring_security_logout");

			if (authorities.get(0).toString().equals("GUARD"))
				this.guardService.deleteLoggedGuard();

			if (authorities.get(0).toString().equals("WARDEN"))
				this.wardenService.deleteLoggedWarden();

			if (authorities.get(0).toString().equals("SALESMAN"))
				this.salesManService.deleteLoggedSalesman();

			if (authorities.get(0).toString().equals("SOCIALWORKER"))
				this.socialWorkerService.deleteLogguedSocialWorker();

			if (authorities.get(0).toString().equals("VISITOR"))
				this.visitorService.deleteLoggedVisitor();

		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}

		return result;
	}

}
