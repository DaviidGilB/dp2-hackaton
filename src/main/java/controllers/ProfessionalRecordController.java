
package controllers;

import javax.validation.Valid;

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

import security.LoginService;
import services.CurriculumService;
import services.ProfessionalRecordService;
import services.SocialWorkerService;
import domain.Curriculum;
import domain.ProfessionalRecord;
import domain.SocialWorker;

@Controller
@RequestMapping("/curriculum")
public class ProfessionalRecordController extends AbstractController {

	@Autowired
	private ProfessionalRecordService	professionalRecordService;
	@Autowired
	private SocialWorkerService			socialWorkerService;
	@Autowired
	private CurriculumService			curriculumService;


	// Constructor
	public ProfessionalRecordController() {
		super();
	}

	@RequestMapping(value = "/socialWorker/addProfessionalRecord", method = RequestMethod.GET)
	public ModelAndView addProfessionalRecord() {
		ModelAndView result;

		try {
			SocialWorker socialWorker = this.socialWorkerService.getSocialWorkerByUsername(LoginService.getPrincipal().getUsername());
			Curriculum curriculum = socialWorker.getCurriculum();
			Assert.notNull(curriculum);

			ProfessionalRecord professionalRecord = this.professionalRecordService.create();
			result = this.createEditModelAndView(professionalRecord);
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:show.do");
		}

		return result;

	}

	@RequestMapping(value = "/socialWorker/editProfessionalRecord", method = RequestMethod.GET)
	public ModelAndView editProfessionalRecord(@RequestParam(required = false) String professionalRecordId) {

		ModelAndView result;

		try {

			ProfessionalRecord professionalRecord = this.professionalRecordService.getProfessionalRecordOfLoggedSocialWorker(professionalRecordId);

			result = this.createEditModelAndView(professionalRecord);
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:show.do");
		}

		return result;

	}

	@RequestMapping(value = "/socialWorker/editProfessionalRecord", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid ProfessionalRecord professionalRecord, BindingResult binding) {
		ModelAndView result;

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

		if (professionalRecord.getEndDate() != null && professionalRecord.getStartDate() != null && professionalRecord.getStartDate().after(professionalRecord.getEndDate())) {
			if (locale.contains("ES")) {
				binding.addError(new FieldError("professionalRecord", "endDate", professionalRecord.getEndDate(), false, null, null, "La fecha de fin no puede ser anterior a la de inicio"));
			} else {
				binding.addError(new FieldError("professionalRecord", "endDate", professionalRecord.getEndDate(), false, null, null, "The end date can not be before the start date"));
			}
		}

		if (binding.hasErrors()) {
			result = this.createEditModelAndView(professionalRecord);
		} else {
			try {
				if (professionalRecord.getId() == 0) {
					this.curriculumService.addProfessionalRecord(professionalRecord);
				} else {
					this.curriculumService.updateProfessionalRecord(professionalRecord);
				}

				result = new ModelAndView("redirect:show.do");

			} catch (Throwable oops) {
				result = this.createEditModelAndView(professionalRecord, "commit.error");
			}
		}
		return result;
	}

	@RequestMapping(value = "/socialWorker/editProfessionalRecord", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(@Valid ProfessionalRecord professionalRecord, BindingResult binding) {
		ModelAndView result;

		try {
			this.curriculumService.deleteProfessionalRecord(professionalRecord);

			result = new ModelAndView("redirect:show.do");

		} catch (Throwable oops) {
			result = this.createEditModelAndView(professionalRecord, "commit.error");
		}

		return result;
	}

	protected ModelAndView createEditModelAndView(ProfessionalRecord professionalRecord) {
		ModelAndView result;

		result = this.createEditModelAndView(professionalRecord, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(ProfessionalRecord professionalRecord, String messageCode) {
		ModelAndView result;

		result = new ModelAndView("curriculum/socialWorker/editProfessionalRecord");
		result.addObject("professionalRecord", professionalRecord);
		result.addObject("message", messageCode);

		return result;
	}
}
