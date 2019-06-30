
package controllers;

import java.util.List;

import javax.validation.Valid;

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

import services.ActorService;
import services.ChargeService;
import services.ConfigurationService;
import services.PrisonerService;
import services.WardenService;
import domain.Charge;
import domain.Prisoner;
import forms.FormObjectPrisoner;

@Controller
@RequestMapping("/prisoner/warden")
public class PrisonerWardenController extends AbstractController {

	@Autowired
	private ChargeService			chargeService;

	@Autowired
	private WardenService			wardenService;

	@Autowired
	private PrisonerService			prisonerService;

	@Autowired
	private ConfigurationService	configurationService;

	@Autowired
	private ActorService			actorService;


	public PrisonerWardenController() {
		super();
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView register() {
		ModelAndView result;

		try {
			this.wardenService.securityAndWarden();
			FormObjectPrisoner formPrisoner = new FormObjectPrisoner();

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			List<Charge> finalCharges = this.chargeService.getFinalCharges();

			result = new ModelAndView("warden/registerPrisoner");
			result.addObject("formPrisoner", formPrisoner);
			result.addObject("locale", locale);
			result.addObject("finalCharges", finalCharges);

		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}

		return result;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@ModelAttribute("formPrisoner") @Valid FormObjectPrisoner formPrisoner, BindingResult binding) {

		try {
			ModelAndView result;

			Prisoner prisoner = new Prisoner();
			prisoner = this.prisonerService.reconstruct(formPrisoner, binding);

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
			List<Charge> finalCharges = this.chargeService.getFinalCharges();

			List<String> usernames = this.actorService.getAllUsernamesInTheSystem();

			if (usernames.contains(formPrisoner.getUsername())) {
				result = new ModelAndView("warden/registerPrisoner");
				result.addObject("formPrisoner", formPrisoner);
				result.addObject("locale", locale);
				result.addObject("message", "warden.duplicatedUsername");

				return result;
			}

			if (binding.hasErrors()) {
				result = new ModelAndView("warden/registerPrisoner");
				result.addObject("formPrisoner", formPrisoner);
				result.addObject("locale", locale);
				result.addObject("finalCharges", finalCharges);
			} else
				try {
					this.prisonerService.savePrisoner(prisoner);
					result = new ModelAndView("redirect:/");
				} catch (Throwable oops) {
					result = new ModelAndView("warden/registerPrisoner");
					result.addObject("formPrisoner", formPrisoner);
					result.addObject("locale", locale);
					result.addObject("finalCharges", finalCharges);
					result.addObject("message", "warden.register.commit.error");
				}

			return result;
		} catch (Throwable oops2) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/listSuspects", method = RequestMethod.GET)
	public ModelAndView listSuspects() {
		ModelAndView result;

		try {
			List<Prisoner> prisoners = this.prisonerService.getIncarceratedPrisoners();

			List<Charge> possibleCharges = this.chargeService.findAll();

			Charge charge = this.wardenService.getSuspiciousCharge();

			possibleCharges.remove(charge);

			result = new ModelAndView("prisoner/warden/listSuspects");
			result.addObject("prisoners", prisoners);
			result.addObject("requestURI", "prisoner/warden/listSuspects.do");

			result.addObject("possibleCharges", possibleCharges);

		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}

		return result;
	}
	@RequestMapping(value = "/isolate", method = RequestMethod.GET)
	public ModelAndView isolate(@RequestParam(required = false) String prisonerId) {
		ModelAndView result;

		try {
			Assert.isTrue(StringUtils.isNumeric(prisonerId));
			Integer prisonerIdInt = Integer.parseInt(prisonerId);

			Prisoner prisoner = this.prisonerService.findOne(prisonerIdInt);

			this.wardenService.isolatePrisoner(prisoner);
			result = new ModelAndView("redirect:/prisoner/warden/listSuspects.do");
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/prisoner/warden/listSuspects.do");
		}

		return result;
	}

	@RequestMapping(value = "/listSuspectCharges", method = RequestMethod.GET)
	public ModelAndView listCharge(@RequestParam(required = false) String prisonerId) {

		ModelAndView result;

		try {

			Assert.isTrue(StringUtils.isNumeric(prisonerId));
			int prisonerIdInt = Integer.parseInt(prisonerId);

			Prisoner prisoner = this.prisonerService.findOne(prisonerIdInt);
			List<Prisoner> prisoners = this.prisonerService.getIncarceratedPrisoners();

			if (prisoner == null || !prisoners.contains(prisoner))
				return this.listSuspects();

			List<Charge> charges = prisoner.getCharges();

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			result = new ModelAndView("anonymous/charge/list");
			result.addObject("charges", charges);
			result.addObject("locale", locale);
			result.addObject("requestURI", "prisoner/warden/listSuspectCharges.do");
			result.addObject("warden", true);
			result.addObject("suspect", true);

		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/prisoner/warden/listSuspects.do");
		}

		return result;
	}

	@RequestMapping(value = "/addCharge", method = RequestMethod.POST)
	public ModelAndView addChargesSave(Charge charge, @RequestParam int prisonerId) {

		ModelAndView result;

		try {
			this.wardenService.loggedAsWarden();

			// Assert.isTrue(StringUtils.isNumeric(prisonerId));
			// int prisonerIdInt = Integer.parseInt(prisonerId);

			// risoner prisoner = this.prisonerService.findOne(prisonerId);
			List<Prisoner> prisoners = this.prisonerService.getIncarceratedPrisoners();

			Prisoner realPrisoner = this.prisonerService.findOne(prisonerId);
			Assert.notNull(charge.getId());
			Assert.isTrue(!realPrisoner.getCharges().contains(charge));

			Charge testCharge = this.chargeService.findOne(charge.getId());

			Assert.isTrue(testCharge.getIsDraftMode() == false);

			if (realPrisoner == null || !prisoners.contains(realPrisoner))
				return this.listSuspects();

			charge = this.chargeService.findOne(charge.getId());

			realPrisoner.getCharges().add(charge);
			this.prisonerService.calculateExitDateForProsioner(realPrisoner);

			this.prisonerService.savePrisoner(realPrisoner);
			result = new ModelAndView("redirect:/prisoner/warden/listSuspects.do");

		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/prisoner/warden/listSuspects.do");
		}

		return result;
	}

	/*
	 * @RequestMapping(value = "/addCharge", method = RequestMethod.GET) public
	 * ModelAndView addCharges(@RequestParam int prisonerId) {
	 * 
	 * ModelAndView result;
	 * 
	 * try {
	 * 
	 * Prisoner prisoner = this.prisonerService.findOne(prisonerId);
	 * List<Prisoner> prisoners =
	 * this.prisonerService.getIncarceratedPrisoners();
	 * 
	 * if (prisoner == null || !prisoners.contains(prisoner)) return
	 * this.listSuspects();
	 * 
	 * List<Charge> allCharges =
	 * this.chargeService.getChargesNotAssignedToPrisoner(prisoner);
	 * 
	 * String locale =
	 * LocaleContextHolder.getLocale().getLanguage().toUpperCase();
	 * 
	 * result = new ModelAndView("prisoner/warden/addCharge");
	 * result.addObject("charges", allCharges); result.addObject("prisoner",
	 * prisoner); result.addObject("locale", locale); result.addObject("warden",
	 * true); result.addObject("suspect", true);
	 * 
	 * } catch (Throwable oops) { result = new
	 * ModelAndView("redirect:/prisoner/warden/listSuspectCharges.do"); }
	 * 
	 * return result; }
	 */

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView editPrisoner(@RequestParam(required = false) String prisonerId) {
		ModelAndView result;

		try {
			Assert.isTrue(StringUtils.isNumeric(prisonerId));
			int prisonerIdInt = Integer.parseInt(prisonerId);

			Prisoner prisoner = this.wardenService.getPrisonerAsWarden(prisonerIdInt);

			result = new ModelAndView("warden/editPrisoner");
			result.addObject("prisoner", prisoner);
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}
		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView editPrisoner(Prisoner prisoner, BindingResult binding) {

		try {
			ModelAndView result;

			Prisoner prisonerReconstructed = new Prisoner();
			prisonerReconstructed = this.prisonerService.reconstruct(prisoner, binding);

			if (binding.hasErrors()) {
				result = new ModelAndView("warden/editPrisoner");
				result.addObject("prisoner", prisonerReconstructed);
			} else
				try {
					this.prisonerService.savePrisoner(prisonerReconstructed);

					result = new ModelAndView("redirect:/prisoner/warden/listSuspects.do");
				} catch (Throwable oops) {
					result = new ModelAndView("warden/editPrisoner");
					result.addObject("prisoner", prisonerReconstructed);
					result.addObject("message", "warden.save.commit.error");
				}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ModelAndView showPrisoner(@RequestParam(required = false) String prisonerId) {
		ModelAndView result;

		try {
			Assert.isTrue(StringUtils.isNumeric(prisonerId));
			int prisonerIdInt = Integer.parseInt(prisonerId);

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
			Prisoner prisoner = this.prisonerService.getPrisonerAsWarden(prisonerIdInt);

			result = new ModelAndView("warden/showPrisoner");
			result.addObject("prisoner", prisoner);
			result.addObject("size", prisoner.getCharges().size());
			result.addObject("locale", locale);
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}

		return result;
	}
}
