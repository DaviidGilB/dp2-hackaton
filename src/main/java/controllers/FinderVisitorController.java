
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ChargeService;
import services.FinderService;
import services.PrisonerService;
import services.VisitService;
import services.VisitorService;
import domain.Charge;
import domain.Finder;
import domain.Prisoner;
import domain.Reason;
import domain.Visit;
import domain.Visitor;

@Controller
@RequestMapping("/finder/visitor")
public class FinderVisitorController extends AbstractController {

	@Autowired
	private FinderService	finderService;

	@Autowired
	private VisitorService	visitorService;

	@Autowired
	private ChargeService	chargeService;

	@Autowired
	private PrisonerService	prisonerService;

	@Autowired
	private VisitService	visitService;


	// Constructors -----------------------------------------------------------

	public FinderVisitorController() {
		super();
	}

	// List --------------------------------------------------------------------

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
		try {
			Visitor visitor = this.visitorService.loggedVisitor();
			List<Charge> charges = this.chargeService.getFinalCharges();
			List<String> values = new ArrayList<>();
			List<String> names = new ArrayList<>();

			names.add("");
			values.add("");

			Finder finder = visitor.getFinder();
			for (int i = 0; i < charges.size(); i++) {
				values.add(charges.get(i).getTitleEnglish());
				names.add(charges.get(i).getTitleSpanish());
			}

			Assert.notNull(finder);

			result = this.createEditModelAndView(finder);
			result.addObject("values", values);
			result.addObject("namesSpanish", names);
			result.addObject("sizeOfList", values.size());
			result.addObject("prisoners", this.finderService.getResults(visitor.getFinder()));
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}
		result.addObject("locale", locale);

		return result;

	}

	// SaveFinder----------------------------------------------------------------------------

	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Finder finderForm, BindingResult binding) {
		ModelAndView result;

		try {

			Finder finder = this.finderService.reconstruct(finderForm, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(finderForm);
			else
				try {
					this.finderService.filter(finder);
					result = new ModelAndView("redirect:list.do");
				} catch (Throwable oops) {
					try {
						result = this.createEditModelAndView(finder, "finder.commit.error");
					} catch (Throwable oops2) {
						result = new ModelAndView("redirect:/");
					}
				}
			return result;
		} catch (Throwable oops2) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/charges", method = RequestMethod.GET)
	public ModelAndView listCharge(@RequestParam(required = false) String prisonerId) {

		try {

			ModelAndView result;

			Assert.isTrue(StringUtils.isNumeric(prisonerId));
			int prisonerIdInt = Integer.parseInt(prisonerId);

			Prisoner prisoner = this.prisonerService.findOne(prisonerIdInt);

			if (prisoner == null)
				return this.list();

			List<Charge> charges = prisoner.getCharges();

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			result = new ModelAndView("anonymous/charge/list");
			result.addObject("charges", charges);
			result.addObject("locale", locale);
			result.addObject("finder", true);
			result.addObject("requestURI", "anonymous/charge/list.do");
			result.addObject("warden", false);

			return result;

		} catch (Throwable oops2) {
			return new ModelAndView("redirect:/");
		}

	}

	// Create as Visitor
	@RequestMapping(value = "/createVisit", method = RequestMethod.GET)
	public ModelAndView createVisitVisitor(@RequestParam(required = false) String prisonerId) {
		ModelAndView result;

		try {

			Assert.isTrue(StringUtils.isNumeric(prisonerId));
			int prisonerIdInt = Integer.parseInt(prisonerId);

			Prisoner prisoner = this.prisonerService.findOne(prisonerIdInt);

			if (prisoner == null)
				return this.list();

			if (prisoner.getFreedom() || prisoner.getIsIsolated())
				return this.list();

			Visit visit = this.visitService.createAsVisitor(prisoner);

			List<Reason> reasons = Arrays.asList(Reason.values());

			result = this.createEditModelAndView(visit);
			result.addObject("visit", visit);
			result.addObject("prisoner", visit.getPrisoner());
			result.addObject("finder", true);
			result.addObject("reasons", reasons);
			result.addObject("requestURI", "visit/visitor/create.do");

			return result;
		} catch (Throwable oops2) {
			return new ModelAndView("redirect:/");
		}
	}

	// PROTECTED MODEL AND VIEW AS VISITOR
	protected ModelAndView createEditModelAndView(Visit visit) {
		ModelAndView result;

		result = this.createEditModelAndView(visit, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(Visit visit, String messageCode) {
		ModelAndView result;

		Prisoner prisoner = visit.getPrisoner();
		List<Reason> reasons = Arrays.asList(Reason.values());

		result = new ModelAndView("visit/visitor/edit");

		result.addObject("visit", visit);
		result.addObject("message", messageCode);
		result.addObject("prisoner", prisoner);
		result.addObject("reasons", reasons);

		return result;
	}

	// CreateEditModelAndView
	protected ModelAndView createEditModelAndView(Finder finder) {
		ModelAndView result;

		result = this.createEditModelAndView(finder, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(Finder finder, String messageCode) {
		ModelAndView result;

		result = new ModelAndView("finder/visitor/list");

		result.addObject("finder", finder);
		result.addObject("message", messageCode);

		return result;

	}

}
