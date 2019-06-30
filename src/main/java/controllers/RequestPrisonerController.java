package controllers;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import domain.Activity;
import domain.Request;
import services.ActivityService;
import services.PrisonerService;
import services.RequestService;

@Controller
@RequestMapping("/request/prisoner")
public class RequestPrisonerController extends AbstractController {

	@Autowired
	private RequestService requestService;

	@Autowired
	private ActivityService activityService;

	@Autowired
	private PrisonerService prisonerService;

	public RequestPrisonerController() {
		super();
	}

	// LIST----------------------------------------------------------------------------------

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView listRequests() {
		ModelAndView result;
		try {
			this.prisonerService.loggedAsPrisoner();

			List<Request> requests = this.requestService.getLogguedPrisonerRequests();

			result = new ModelAndView("request/prisoner/list");
			result.addObject("requests", requests);
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}

		return result;

	}

	// CREATE_REQUEST--------------------------------------------------------------

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView createRequest(@RequestParam(required = false) String activityId) {
		ModelAndView result;

		try {
			Assert.isTrue(StringUtils.isNumeric(activityId));
			Integer activityId2 = Integer.parseInt(activityId);

			Activity activity = this.activityService.findOne(activityId2);
			this.activityService.securityActivityForRequests(activity);

			Request request = this.requestService.create();

			result = this.createEditModelAndView(request);

		} catch (Exception e) {
			result = new ModelAndView("redirect:list.do");
		}

		return result;

	}

	@RequestMapping(value = "/create", method = RequestMethod.POST, params = "save")
	public ModelAndView createRequest(Request requestForm, @RequestParam(required = false) String activityId,
			BindingResult binding) {
		ModelAndView result;

		try {
			Assert.isTrue(StringUtils.isNumeric(activityId));
			Integer activityId2 = Integer.parseInt(activityId);
			Request request = this.requestService.reconstructPrisoner(requestForm, activityId2, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(requestForm);
			else
				try {

					this.activityService.securityActivityForRequests(this.activityService.findOne(activityId2));
					this.requestService.assignRequest(request, activityId2);

					result = new ModelAndView("redirect:list.do");

				} catch (Exception e) {
					result = this.createEditModelAndView(request, "finder.commit.error");
				}

		} catch (Exception e) {
			result = new ModelAndView("redirect:list.do");
		}
		return result;
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView deleteRequest(@RequestParam(required = false) String requestId) {
		ModelAndView result;

		try {
			Assert.isTrue(StringUtils.isNumeric(requestId));
			Integer requestId2 = Integer.parseInt(requestId);
			this.requestService.deleteRequestFromPrisoner(this.requestService.findeOne(requestId2));

			result = new ModelAndView("redirect:list.do");

		} catch (Exception e) {
			result = new ModelAndView("redirect:list.do");
			result.addObject("errorDelete", true);
		}

		return result;
	}

	// CreateEditModelAndView----------------------------------------------------------
	protected ModelAndView createEditModelAndView(Request request) {
		ModelAndView result;

		result = this.createEditModelAndView(request, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(Request request, String messageCode) {
		ModelAndView result;

		result = new ModelAndView("request/prisoner/create");

		result.addObject("request", request);
		result.addObject("message", messageCode);

		return result;

	}

}
