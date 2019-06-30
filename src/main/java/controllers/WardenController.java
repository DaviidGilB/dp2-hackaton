
package controllers;

import java.util.List;

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

import services.ChargeService;
import services.PrisonerService;
import services.WardenService;
import domain.Charge;
import domain.Prisoner;

@Controller
@RequestMapping("/warden")
public class WardenController extends AbstractController {

	@Autowired
	private PrisonerService	prisonerService;

	@Autowired
	private ChargeService	chargeService;

	@Autowired
	private WardenService	wardenService;


	public WardenController() {
		super();
	}

	// Listar Prisioneros liberados
	@RequestMapping(value = "/freePrisoners/list", method = RequestMethod.GET)
	public ModelAndView listFreePrisoners() {

		try {

			ModelAndView result;
			List<Prisoner> prisoners;

			prisoners = this.prisonerService.getFreePrisoners();

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			result = new ModelAndView("anonymous/prisoner/list");
			result.addObject("prisoners", prisoners);
			result.addObject("requestURI", "warden/freePrisoners/list.do");
			result.addObject("locale", locale);
			result.addObject("warden", true);

			return result;

		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}
	@RequestMapping(value = "/charge/list", method = RequestMethod.GET)
	public ModelAndView listChargePrisoner(@RequestParam(required = false) String prisonerId) {

		try {
			ModelAndView result;
			Assert.isTrue(StringUtils.isNumeric(prisonerId));
			int prisonerIdInt = Integer.parseInt(prisonerId);

			Prisoner prisoner = this.prisonerService.findOne(prisonerIdInt);

			if (prisoner == null)
				return this.listFreePrisoners();

			List<Charge> charges = prisoner.getCharges();

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			result = new ModelAndView("anonymous/charge/list");
			result.addObject("charges", charges);
			result.addObject("locale", locale);
			result.addObject("requestURI", "warden/charge/list.do");
			result.addObject("warden", true);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/anonymous/prisoner/list.do");
		}
	}

	@RequestMapping(value = "/charge/listAll", method = RequestMethod.GET)
	public ModelAndView listAllCharge() {

		try {
			ModelAndView result;

			List<Charge> draftCharges = this.chargeService.getDraftCharges();
			List<Charge> finalCharges = this.chargeService.getFinalCharges();

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			result = new ModelAndView("warden/charge/listAll");
			result.addObject("draftCharges", draftCharges);
			result.addObject("finalCharges", finalCharges);
			result.addObject("locale", locale);
			result.addObject("requestURI", "warden/charge/listAll.do");
			result.addObject("warden", true);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/charge/newCharge", method = RequestMethod.GET)
	public ModelAndView newCharge() {
		try {
			ModelAndView result;

			Charge charge = new Charge();
			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			result = new ModelAndView("warden/charge/newCharge");
			result.addObject("locale", locale);
			result.addObject("charge", charge);
			result.addObject("requestURI", "warden/charge/newCharge.do");
			result.addObject("warden", true);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/warden/charge/listAll.do");
		}
	}

	@RequestMapping(value = "/charge/editCharge", method = RequestMethod.GET)
	public ModelAndView editCharge(@RequestParam(required = false) String chargeId) {
		try {
			ModelAndView result;

			Assert.isTrue(StringUtils.isNumeric(chargeId));
			int chargeIdInt = Integer.parseInt(chargeId);

			Charge charge = this.chargeService.findOne(chargeIdInt);

			Assert.isTrue(charge.getIsDraftMode());
			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			result = new ModelAndView("warden/charge/newCharge");
			result.addObject("locale", locale);
			result.addObject("charge", charge);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/warden/charge/listAll.do");
		}
	}

	@RequestMapping(value = "/charge/editCharge", method = RequestMethod.POST, params = "save")
	public ModelAndView newChargeCreate(@Valid Charge charge, BindingResult binding) {

		ModelAndView result;
		try {
			if (charge.getId() == 0)
				Assert.isTrue(this.wardenService.loggedAsWardenBoolean());

			if (charge.getMonth() == 0 && charge.getYear() == 0) {
				result = this.createEditModelAndView(charge, "commit.yearsAndMothns");
				return result;
			} else {

				if (binding.hasErrors())
					result = this.createEditModelAndView(charge);
				else
					try {

						this.chargeService.save(charge);
						result = new ModelAndView("redirect:listAll.do");

					} catch (Throwable oops) {
						result = this.createEditModelAndView(charge, "commit.error");
					}

				return result;
			}
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/warden/charge/listAll");
		}

	}
	@RequestMapping(value = "/charge/deleteCharge", method = RequestMethod.GET)
	public ModelAndView deleteCharge(@RequestParam(required = false) String chargeId) {

		try {
			ModelAndView result;

			Assert.isTrue(StringUtils.isNumeric(chargeId));
			int chargeIdInt = Integer.parseInt(chargeId);

			Charge charge = this.chargeService.findOne(chargeIdInt);

			this.chargeService.delete(charge);

			result = new ModelAndView("redirect:/warden/charge/listAll.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/warden/charge/listAll.do");
		}
	}

	protected ModelAndView createEditModelAndView(Charge charge) {
		ModelAndView result;

		result = this.createEditModelAndView(charge, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(Charge charge, String messageCode) {
		ModelAndView result;

		result = new ModelAndView("warden/charge/newCharge");
		result.addObject("charge", charge);
		result.addObject("message", messageCode);

		return result;
	}

}
