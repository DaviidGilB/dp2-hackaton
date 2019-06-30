
package controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import security.LoginService;
import services.CurriculumService;
import services.MiscellaneousRecordService;
import services.SocialWorkerService;
import domain.Curriculum;
import domain.MiscellaneousRecord;
import domain.SocialWorker;

@Controller
@RequestMapping("/curriculum")
public class MiscellaneousRecordController extends AbstractController {

	@Autowired
	private MiscellaneousRecordService	miscellaneousRecordService;
	@Autowired
	private SocialWorkerService			socialWorkerService;
	@Autowired
	private CurriculumService			curriculumService;


	// Constructor
	public MiscellaneousRecordController() {
		super();
	}

	@RequestMapping(value = "/socialWorker/addMiscellaneousRecord", method = RequestMethod.GET)
	public ModelAndView addMiscellaneousRecord() {

		ModelAndView result;

		try {
			SocialWorker socialWorker = this.socialWorkerService.getSocialWorkerByUsername(LoginService.getPrincipal().getUsername());
			Curriculum curriculum = socialWorker.getCurriculum();
			Assert.notNull(curriculum);

			MiscellaneousRecord miscellaneousRecord = this.miscellaneousRecordService.create();

			result = this.createEditModelAndView(miscellaneousRecord);
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:show.do");
		}

		return result;
	}

	@RequestMapping(value = "/socialWorker/editMiscellaneousRecord", method = RequestMethod.GET)
	public ModelAndView editMiscellaneousRecord(@RequestParam(required = false) String miscellaneousRecordId) {

		ModelAndView result;

		try {

			MiscellaneousRecord miscellaneousRecord = this.miscellaneousRecordService.getMiscellaneousRecordOfLoggedSocialWorker(miscellaneousRecordId);
			result = this.createEditModelAndView(miscellaneousRecord);
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:show.do");
		}

		return result;

	}

	@RequestMapping(value = "/socialWorker/editMiscellaneousRecord", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid MiscellaneousRecord miscellaneousRecord, BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors()) {
			result = this.createEditModelAndView(miscellaneousRecord);
		} else {
			try {
				if (miscellaneousRecord.getId() == 0) {
					this.curriculumService.addMiscellaneousRecord(miscellaneousRecord);
				} else {
					this.curriculumService.updateMiscellaneousRecord(miscellaneousRecord);
				}

				result = new ModelAndView("redirect:show.do");

			} catch (Throwable oops) {
				result = this.createEditModelAndView(miscellaneousRecord, "commit.error");
			}
		}
		return result;
	}

	@RequestMapping(value = "/socialWorker/editMiscellaneousRecord", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(@Valid MiscellaneousRecord miscellaneousRecord, BindingResult binding) {
		ModelAndView result;

		try {
			this.curriculumService.deleteMiscellaneousRecord(miscellaneousRecord);

			result = new ModelAndView("redirect:show.do");

		} catch (Throwable oops) {
			result = this.createEditModelAndView(miscellaneousRecord, "commit.error");
		}

		return result;
	}

	protected ModelAndView createEditModelAndView(MiscellaneousRecord miscellaneousRecord) {
		ModelAndView result;

		result = this.createEditModelAndView(miscellaneousRecord, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(MiscellaneousRecord miscellaneousRecord, String messageCode) {
		ModelAndView result;

		result = new ModelAndView("curriculum/socialWorker/editMiscellaneousRecord");
		result.addObject("miscellaneousRecord", miscellaneousRecord);
		result.addObject("message", messageCode);

		return result;
	}
}
