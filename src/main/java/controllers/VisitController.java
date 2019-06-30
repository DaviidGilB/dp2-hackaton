
package controllers;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.Authority;
import security.LoginService;
import security.UserAccount;
import services.GuardService;
import services.MessageService;
import services.PrisonerService;
import services.ReportService;
import services.VisitService;
import services.VisitorService;
import domain.Guard;
import domain.Prisoner;
import domain.Reason;
import domain.Report;
import domain.Visit;
import domain.VisitStatus;
import domain.Visitor;

@Controller
@RequestMapping("/visit")
public class VisitController extends AbstractController {

	@Autowired
	private PrisonerService	prisonerService;

	@Autowired
	private VisitorService	visitorService;

	@Autowired
	private VisitService	visitService;

	@Autowired
	private GuardService	guardService;

	@Autowired
	private ReportService	reportService;

	@Autowired
	private MessageService	messageService;


	public VisitController() {
		super();
	}

	// -------------------------------------------------------------------
	// ---------------------------LIST------------------------------------

	// Listar Visitas del prisionero logueado, pasadas y futuras
	@RequestMapping(value = "/prisoner/list", method = RequestMethod.GET)
	public ModelAndView listPrisoner() {
		try {
			ModelAndView result;
			List<Visit> visits;

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			Prisoner loggedPrisoner = this.prisonerService.loggedPrisoner();
			visits = loggedPrisoner.getVisits();

			result = new ModelAndView("visit/list");
			result.addObject("visits", visits);
			result.addObject("locale", locale);
			result.addObject("requestURI", "visit/prisoner/list.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	// Listar Visitas del visitante logueado, pasadas y futuras
	@RequestMapping(value = "/visitor/list", method = RequestMethod.GET)
	public ModelAndView listVisitor() {
		try {
			ModelAndView result;
			List<Visit> visits;

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			Visitor loggedVisitor = this.visitorService.loggedVisitor();
			visits = loggedVisitor.getVisits();

			result = new ModelAndView("visit/list");
			result.addObject("visits", visits);
			result.addObject("locale", locale);
			result.addObject("requestURI", "visit/visitor/list.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	// Listar Visitas aceptadas futuras que no tienen ningun guardia asociado
	@RequestMapping(value = "/guard/listFuture", method = RequestMethod.GET)
	public ModelAndView listGuardFuture() {
		try {
			ModelAndView result;
			List<Visit> visits;

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			visits = this.guardService.getFutureAcceptedVisits();

			result = new ModelAndView("visit/list");
			result.addObject("visits", visits);
			result.addObject("locale", locale);
			result.addObject("requestURI", "visit/guard/listFuture.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	// Listar Visitas aceptadas futuras que no tienen ningun guardia asociado
	@RequestMapping(value = "/guard/list", method = RequestMethod.GET)
	public ModelAndView listGuard() {

		try {
			ModelAndView result;
			List<Visit> visits;

			Guard loggedGuard = this.guardService.loggedGuard();

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			visits = loggedGuard.getVisits();

			result = new ModelAndView("visit/list");
			result.addObject("visits", visits);
			result.addObject("locale", locale);
			result.addObject("requestURI", "visit/guard/list.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	//Ver el Report
	@RequestMapping(value = "/report/list", method = RequestMethod.GET)
	public ModelAndView listReport(@RequestParam(required = false) String visitId) {	//Modificar

		ModelAndView result = null;
		Report report;

		try {
			Assert.isTrue(StringUtils.isNumeric(visitId));
			int visitIdInt = Integer.parseInt(visitId);

			Visit visit = this.visitService.findOne(visitIdInt);

			UserAccount userAccount = LoginService.getPrincipal();
			List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
			Authority authority = authorities.get(0);

			//Comprueba que si eres un prisionero o un visitante, el Report que estas mostrando es tuyo (confidencialidad)
			if (authority.toString().equals("VISITOR")) {
				Visitor loggedVisitor = this.visitorService.loggedVisitor();
				if (!visit.getVisitor().equals(loggedVisitor))
					return this.listVisitor();

			} else if (authority.toString().equals("PRISONER")) {
				Prisoner loggedPrisoner = this.prisonerService.loggedPrisoner();
				if (!visit.getPrisoner().equals(loggedPrisoner))
					return this.listPrisoner();

			}
			if (authority.toString().equals("GUARD")) {
				Guard loggedGuard = this.guardService.loggedGuard();
				if (!loggedGuard.getVisits().contains(visit))
					return this.listGuard();
			}

			report = visit.getReport();
			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			result = new ModelAndView("report/list");
			result.addObject("report", report);
			result.addObject("locale", locale);
			result.addObject("requestURI", "visit/report/list.do");

		} catch (Throwable oops) {
			UserAccount userAccount = LoginService.getPrincipal();
			List<Authority> authorities = (List<Authority>) userAccount.getAuthorities();
			Authority authority = authorities.get(0);
			if (authority.toString().equals("VISITOR"))
				result = this.listVisitor();
			else if (authority.toString().equals("PRISONER"))
				result = this.listPrisoner();
			if (authority.toString().equals("GUARD"))
				result = this.listGuard();

		}
		return result;
	}
	//--------------------------------------------------REFRESH-----------------------------------------
	//--------------------------------------------------------------------------------------------------
	//Refresh Prisoner
	@RequestMapping(value = "/prisoner/filter", method = {
		RequestMethod.POST, RequestMethod.GET
	}, params = "refresh")
	public ModelAndView requestsFilter(@RequestParam(required = false) String fselect) {
		ModelAndView result;

		try {
			//Visit status
			if (fselect.equals("ALL"))
				return this.listPrisoner();
			else {
				String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
				VisitStatus visitStatus = VisitStatus.PERMITTED;
				if (fselect.equals("ACCEPTED"))
					visitStatus = VisitStatus.ACCEPTED;
				else if (fselect.equals("PENDING"))
					visitStatus = VisitStatus.PENDING;
				else if (fselect.equals("REJECTED"))
					visitStatus = VisitStatus.REJECTED;

				Prisoner loggedPrisoner = this.prisonerService.loggedPrisoner();
				List<Visit> visits = this.visitService.getVisitsByPrisonerAndStatus(visitStatus, loggedPrisoner.getId());

				result = new ModelAndView("visit/list");

				result.addObject("visits", visits);
				result.addObject("locale", locale);
				result.addObject("requestURI", "visit/prisoner/list.do");
			}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	//Refresh Visitor
	@RequestMapping(value = "/visitor/filter", method = {
		RequestMethod.POST, RequestMethod.GET
	}, params = "refresh")
	public ModelAndView requestsFilterVisitor(@RequestParam(required = false) String fselect) {
		ModelAndView result;
		try {
			//Visit status
			if (fselect.equals("ALL"))
				return this.listVisitor();
			else {
				String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
				VisitStatus visitStatus = VisitStatus.PERMITTED;
				if (fselect.equals("ACCEPTED"))
					visitStatus = VisitStatus.ACCEPTED;
				else if (fselect.equals("PENDING"))
					visitStatus = VisitStatus.PENDING;
				else if (fselect.equals("REJECTED"))
					visitStatus = VisitStatus.REJECTED;

				Visitor loggedVisitor = this.visitorService.loggedVisitor();
				List<Visit> visits = this.visitService.getVisitsByVisitorAndStatus(visitStatus, loggedVisitor.getId());

				result = new ModelAndView("visit/list");

				result.addObject("visits", visits);
				result.addObject("locale", locale);
				result.addObject("requestURI", "visit/visitor/list.do");
			}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	//-------------------------------------CHANGE STATUS--------------------------------------------
	//----------------------------------------------------------------------------------------------
	//Accept as Prisoner
	@RequestMapping(value = "/prisoner/accept", method = RequestMethod.GET)
	public ModelAndView acceptApplication(@RequestParam(required = false) String visitId) {

		try {

			Assert.isTrue(StringUtils.isNumeric(visitId));
			int visitIdInt = Integer.parseInt(visitId);

			Visit visit;
			visit = this.visitService.findOne(visitIdInt);

			Prisoner prisoner = this.prisonerService.loggedPrisoner();

			Date thisMoment = new Date();
			thisMoment.setTime(thisMoment.getTime() - 1);

			if (visit == null || visit.getVisitStatus() != VisitStatus.PENDING || visit.getDate().before(thisMoment) || visit.isCreatedByPrisoner() || !visit.getPrisoner().equals(prisoner))
				return this.listPrisoner();

			Visit savedVisit = this.visitService.editVisitPrisoner(visit, true);
			this.messageService.sendNotificationChangeStatusOfVisit(prisoner, savedVisit.getVisitor(), savedVisit);
		} catch (Throwable oops) {
			return this.listPrisoner();
		}

		return this.listPrisoner();
	}
	//Reject as Prisoner
	@RequestMapping(value = "/prisoner/reject", method = RequestMethod.GET)
	public ModelAndView rejectApplication(@RequestParam(required = false) String visitId) {

		try {

			Assert.isTrue(StringUtils.isNumeric(visitId));
			int visitIdInt = Integer.parseInt(visitId);

			Visit visit;
			visit = this.visitService.findOne(visitIdInt);

			Prisoner prisoner = this.prisonerService.loggedPrisoner();

			Date thisMoment = new Date();
			thisMoment.setTime(thisMoment.getTime() - 1);

			if (visit == null || visit.getVisitStatus() != VisitStatus.PENDING || visit.getDate().before(thisMoment) || visit.isCreatedByPrisoner() || !visit.getPrisoner().equals(prisoner))
				return this.listPrisoner();

			Visit savedVisit = this.visitService.editVisitPrisoner(visit, false);
			this.messageService.sendNotificationChangeStatusOfVisit(prisoner, savedVisit.getVisitor(), savedVisit);

		} catch (Throwable oops) {
			return this.listPrisoner();
		}

		return this.listPrisoner();
	}

	//Accept as Visitor
	@RequestMapping(value = "/visitor/accept", method = RequestMethod.GET)
	public ModelAndView acceptApplicationVisitor(@RequestParam(required = false) String visitId) {

		try {

			Assert.isTrue(StringUtils.isNumeric(visitId));
			int visitIdInt = Integer.parseInt(visitId);

			Visit visit;
			visit = this.visitService.findOne(visitIdInt);

			Visitor visitor = this.visitorService.loggedVisitor();

			Date thisMoment = new Date();
			thisMoment.setTime(thisMoment.getTime() - 1);

			if (visit == null || visit.getVisitStatus() != VisitStatus.PENDING || visit.getDate().before(thisMoment) || !visit.isCreatedByPrisoner() || !visit.getVisitor().equals(visitor))
				return this.listVisitor();

			Visit savedVisit = this.visitService.editVisitVisitor(visit, true);
			this.messageService.sendNotificationChangeStatusOfVisit(savedVisit.getPrisoner(), visitor, savedVisit);

		} catch (Throwable oops) {
			return this.listVisitor();
		}

		return this.listVisitor();
	}

	//Reject as Visitor
	@RequestMapping(value = "/visitor/reject", method = RequestMethod.GET)
	public ModelAndView rejectApplicationVisitor(@RequestParam(required = false) String visitId) {

		try {

			Assert.isTrue(StringUtils.isNumeric(visitId));
			int visitIdInt = Integer.parseInt(visitId);
			Visit visit;
			visit = this.visitService.findOne(visitIdInt);

			Visitor visitor = this.visitorService.loggedVisitor();

			Date thisMoment = new Date();
			thisMoment.setTime(thisMoment.getTime() - 1);

			if (visit == null || visit.getVisitStatus() != VisitStatus.PENDING || visit.getDate().before(thisMoment) || !visit.isCreatedByPrisoner() || !visit.getVisitor().equals(visitor))
				return this.listVisitor();

			Visit savedVisit = this.visitService.editVisitVisitor(visit, false);
			this.messageService.sendNotificationChangeStatusOfVisit(savedVisit.getPrisoner(), visitor, savedVisit);
		} catch (Throwable oops) {
			return this.listVisitor();
		}
		return this.listVisitor();

	}
	//Permit as Guard
	@RequestMapping(value = "/guard/permit", method = RequestMethod.GET)
	public ModelAndView permitApplicationGuard(@RequestParam(required = false) String visitId) {

		try {

			Assert.isTrue(StringUtils.isNumeric(visitId));
			int visitIdInt = Integer.parseInt(visitId);

			Visit visit;
			visit = this.visitService.findOne(visitIdInt);

			Date thisMoment = new Date();
			thisMoment.setTime(thisMoment.getTime() - 1);

			if (visit == null || visit.getVisitStatus() != VisitStatus.ACCEPTED || visit.getDate().before(thisMoment))
				return this.listGuardFuture();

			Visit savedVisit = this.visitService.editVisitGuard(visit, true);
			this.messageService.sendNotificationChangeStatusOfVisit(savedVisit.getPrisoner(), savedVisit.getVisitor(), savedVisit);
		} catch (Throwable oops) {
			return this.listGuardFuture();
		}
		return this.listGuardFuture();
	}
	//Reject as Guard
	@RequestMapping(value = "/guard/reject", method = RequestMethod.GET)
	public ModelAndView rejectApplicationGuard(@RequestParam(required = false) String visitId) {

		try {

			Assert.isTrue(StringUtils.isNumeric(visitId));
			int visitIdInt = Integer.parseInt(visitId);

			Visit visit;
			visit = this.visitService.findOne(visitIdInt);

			Date thisMoment = new Date();
			thisMoment.setTime(thisMoment.getTime() - 1);

			if (visit == null || visit.getVisitStatus() != VisitStatus.ACCEPTED || visit.getDate().before(thisMoment))
				return this.listGuardFuture();

			Visit savedVisit = this.visitService.editVisitGuard(visit, false);
			this.messageService.sendNotificationChangeStatusOfVisit(savedVisit.getPrisoner(), savedVisit.getVisitor(), savedVisit);

		} catch (Throwable oops) {
			return this.listGuardFuture();
		}
		return this.listGuardFuture();
	}
	//--------------------------------------------CREATE---------------------------------------------
	//-----------------------------------------------------------------------------------------------

	//Create as Visitor
	@RequestMapping(value = "/visitor/create", method = RequestMethod.GET)
	public ModelAndView createVisitVisitor(@RequestParam(required = false) String prisonerId) {
		ModelAndView result;

		try {
			Assert.isTrue(StringUtils.isNumeric(prisonerId));
			int prisonerIdInt = Integer.parseInt(prisonerId);
			Prisoner prisoner = this.prisonerService.findOne(prisonerIdInt);

			if (prisoner == null)
				return this.listVisitor();

			if (prisoner.getFreedom() || prisoner.getIsIsolated())
				return this.listVisitor();

			Visit visit = this.visitService.createAsVisitor(prisoner);

			List<Reason> reasons = Arrays.asList(Reason.values());

			result = this.createEditModelAndView(visit);
			result.addObject("visit", visit);
			result.addObject("prisoner", visit.getPrisoner());
			result.addObject("reasons", reasons);
			result.addObject("finder", false);
		} catch (Throwable oops) {
			result = this.listVisitor();
		}

		return result;
	}

	//Create as Prisoner
	@RequestMapping(value = "/prisoner/create", method = RequestMethod.GET)
	public ModelAndView createVisitPrisoner(@RequestParam(required = false) String visitorId) {
		ModelAndView result;

		try {
			Assert.isTrue(StringUtils.isNumeric(visitorId));
			int visitorIdInt = Integer.parseInt(visitorId);

			Visitor visitor = this.visitorService.findOne(visitorIdInt);
			Prisoner loggedPrisoner = this.prisonerService.loggedPrisoner();

			if (visitor == null)
				return this.listPrisoner();

			if (!this.prisonerService.getVisitorsToCreateVisit(loggedPrisoner).contains(visitor))
				return this.listPrisoner();

			Visit visit = this.visitService.createAsPrisoner(visitor);

			List<Reason> reasons = Arrays.asList(Reason.values());

			result = this.createEditModelAndViewAsPrisoner(visit);
			result.addObject("visit", visit);
			result.addObject("visitor", visit.getVisitor());
			result.addObject("reasons", reasons);

		} catch (Throwable oops) {
			return this.listPrisoner();
		}
		return result;
	}

	//-------------------------------------------SAVE---------------------------------------
	//--------------------------------------------------------------------------------------
	//SAVE VISIT AS VISITOR
	@RequestMapping(value = "/visitor/create", method = RequestMethod.POST, params = "save")
	public ModelAndView saveAsVisitor(Visit visit, BindingResult binding) {
		ModelAndView result;

		try {
			Visit a = new Visit();

			a = this.visitService.reconstructAsVisitor(visit, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(visit);
			else
				try {

					Date thisMoment = new Date();
					thisMoment.setTime(thisMoment.getTime() - 1);

					//Comprobacion es futuro
					if (visit.getDate().before(thisMoment))
						if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES")) {
							binding.addError(new FieldError("visit", "date", visit.getDate(), false, null, null, "La fecha debe ser futura"));
							return this.createEditModelAndView(visit);
						} else {
							binding.addError(new FieldError("visit", "date", visit.getDate(), false, null, null, "The Visit must take place in the future"));
							return this.createEditModelAndView(visit);
						}

					//Comprobacion es anterior a la fecha de salida
					if (visit.getDate().after(visit.getPrisoner().getExitDate()))
						if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES")) {
							binding.addError(new FieldError("visit", "date", visit.getDate(), false, null, null, "La fecha debe ser anterior a la fecha de salida"));
							return this.createEditModelAndView(visit);
						} else {
							binding.addError(new FieldError("visit", "date", visit.getDate(), false, null, null, "The date must be before the exit date"));
							return this.createEditModelAndView(visit);
						}

					this.visitService.saveVisitAsVisitor(a);

					result = new ModelAndView("redirect:list.do");

				} catch (Throwable oops) {

					result = this.createEditModelAndView(visit, "commit.error");
				}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	//SAVE VISIT AS PRISONER
	@RequestMapping(value = "/prisoner/create", method = RequestMethod.POST, params = "save")
	public ModelAndView saveAsPrisoner(Visit visit, BindingResult binding) {
		ModelAndView result;
		try {
			Visit a = new Visit();

			a = this.visitService.reconstructAsPrisoner(visit, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndViewAsPrisoner(visit);
			else
				try {

					Date thisMoment = new Date();
					thisMoment.setTime(thisMoment.getTime() - 1);

					//Comprobacion es futuro
					if (visit.getDate().before(thisMoment))
						if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES")) {
							binding.addError(new FieldError("visit", "date", visit.getDate(), false, null, null, "La fecha debe ser futura"));
							return this.createEditModelAndViewAsPrisoner(visit);
						} else {
							binding.addError(new FieldError("visit", "date", visit.getDate(), false, null, null, "The Visit must take place in the future"));
							return this.createEditModelAndViewAsPrisoner(visit);
						}

					//Comprobacion es anterior a la fecha de salida
					if (visit.getDate().after(visit.getPrisoner().getExitDate()))
						if (LocaleContextHolder.getLocale().getLanguage().toUpperCase().contains("ES")) {
							binding.addError(new FieldError("visit", "date", visit.getDate(), false, null, null, "La fecha debe ser anterior a la fecha de salida"));
							return this.createEditModelAndViewAsPrisoner(visit);
						} else {
							binding.addError(new FieldError("visit", "date", visit.getDate(), false, null, null, "The date must be before the exit date"));
							return this.createEditModelAndViewAsPrisoner(visit);
						}

					this.visitService.saveVisitAsPrisoner(a);

					result = this.listPrisoner();

				} catch (Throwable oops) {

					result = this.createEditModelAndViewAsPrisoner(visit, "commit.error");
				}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	//PROTECTED MODEL AND VIEW AS VISITOR
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

	//PROTECTED MODEL AND VIEW AS PRISONER
	protected ModelAndView createEditModelAndViewAsPrisoner(Visit visit) {
		ModelAndView result;

		result = this.createEditModelAndViewAsPrisoner(visit, null);

		return result;
	}

	protected ModelAndView createEditModelAndViewAsPrisoner(Visit visit, String messageCode) {
		ModelAndView result;

		Visitor visitor = visit.getVisitor();
		List<Reason> reasons = Arrays.asList(Reason.values());

		result = new ModelAndView("visit/prisoner/edit");

		result.addObject("visit", visit);
		result.addObject("message", messageCode);
		result.addObject("visitor", visitor);
		result.addObject("reasons", reasons);

		return result;
	}

	//--------------------------------------------CREATE REPORT-------------------------------------------------------
	//----------------------------------------------------------------------------------------------------------------
	//Create as Visitor
	@RequestMapping(value = "/report/create", method = RequestMethod.GET)
	public ModelAndView createReport(@RequestParam(required = false) String visitId) {
		ModelAndView result;

		try {

			Assert.isTrue(StringUtils.isNumeric(visitId));
			int visitIdInt = Integer.parseInt(visitId);

			Guard loggedGuard = this.guardService.loggedGuard();
			List<Visit> visits = loggedGuard.getVisits();

			Visit visit = this.visitService.findOne(visitIdInt);

			Date thisMoment = new Date();
			thisMoment.setTime(thisMoment.getTime() - 1);

			if (visit == null)
				return this.listGuard();

			if (visit.getReport() != null || thisMoment.before(visit.getDate()) || visit.getVisitStatus() != VisitStatus.PERMITTED || !visits.contains(visit))
				return this.listGuard();

			Report report = new Report();

			result = this.createEditModelAndView(report);
			result.addObject("visitId", visitId);

		} catch (Throwable oops) {
			return this.listGuard();
		}
		return result;
	}

	@RequestMapping(value = "/report/create", method = RequestMethod.POST, params = "save")
	public ModelAndView saveReport(@RequestParam(required = false) String visitId, Report report, BindingResult binding) {
		ModelAndView result;

		try {

			Assert.isTrue(StringUtils.isNumeric(visitId));
			int visitIdInt = Integer.parseInt(visitId);

			Report rep = new Report();

			rep = this.reportService.reconstruct(report, binding);

			if (binding.hasErrors()) {
				result = this.createEditModelAndView(report);
				result.addObject("visitId", visitId);
			} else
				try {

					Date thisMoment = new Date();
					thisMoment.setTime(thisMoment.getTime() - 1);

					this.reportService.saveReport(rep, visitIdInt);

					result = this.listGuard();

				} catch (Throwable oops) {

					result = this.createEditModelAndView(report, "commit.error");
					result.addObject("visitId", visitId);
				}
		} catch (Throwable oops) {
			return this.listGuard();
		}
		return result;
	}
	protected ModelAndView createEditModelAndView(Report report) {
		ModelAndView result;

		result = this.createEditModelAndView(report, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(Report report, String messageCode) {
		ModelAndView result;

		result = new ModelAndView("visit/report/edit");

		result.addObject("report", report);
		result.addObject("message", messageCode);

		return result;
	}
}
