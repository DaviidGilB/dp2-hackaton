
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
import domain.Prisoner;
import domain.SocialWorker;
import services.ActivityService;
import services.PrisonerService;
import services.SocialWorkerService;

@Controller
@RequestMapping("/activity/socialworker")
public class ActivityController extends AbstractController {

	@Autowired
	private ActivityService activityService;

	@Autowired
	private PrisonerService prisonerService;

	@Autowired
	private SocialWorkerService socialWorkerService;

	public ActivityController() {
		super();
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {
		ModelAndView result;

		SocialWorker sw = this.socialWorkerService.loggedSocialWorker();

		List<Activity> activities = sw.getActivities();

		result = new ModelAndView("activity/socialworker/list");
		result.addObject("activities", activities);
		result.addObject("requestURI", "activity/socialworker/list.do");

		return result;
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView createActivity() {
		try {
			ModelAndView result = null;

			SocialWorker socialWorker = this.socialWorkerService.loggedSocialWorker();

			Activity activity = this.activityService.createActivity();

			result = this.createEditModelAndView(activity);
			result.addObject("activity", activity);

			return result;

		} catch (Throwable oops) {
			ModelAndView result = new ModelAndView("redirect:list");
			SocialWorker socialWorker = this.socialWorkerService.loggedSocialWorker();
			result.addObject("activities", socialWorker.getActivities());
			return result;
		}
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView editActivity(@RequestParam(required = false) String activityId) {
		try {
			Assert.isTrue(StringUtils.isNumeric(activityId));
			int idR = Integer.parseInt(activityId);

			ModelAndView result = null;

			SocialWorker socialWorker = this.socialWorkerService.loggedSocialWorker();

			Activity activity = this.activityService.findOne(idR);

			Assert.isTrue(!activity.getIsFinalMode());

			result = this.createEditModelAndView(activity);
			result.addObject("activity", activity);

			return result;

		} catch (Throwable oops) {
			ModelAndView result = new ModelAndView("redirect:list.do");
			SocialWorker socialWorker = this.socialWorkerService.loggedSocialWorker();
			result.addObject("activities", socialWorker.getActivities());
			return result;
		}
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Activity activity, BindingResult binding) {
		try {
			ModelAndView result;

			Activity a = new Activity();
			SocialWorker sw = this.socialWorkerService.loggedSocialWorker();

			a = this.activityService.reconstruct(activity, binding);
			if (binding.hasErrors())
				result = this.createEditModelAndView(activity);
			else
				try {
					this.activityService.saveActivity(activity);

					result = new ModelAndView("redirect:list.do");

				} catch (Throwable oops) {
					result = this.createEditModelAndView(activity, "commit.error");
				}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:list.do");
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView delete(@RequestParam(required = false) String activityId) {
		try {

			Assert.isTrue(StringUtils.isNumeric(activityId));

			int activityIdT = Integer.parseInt(activityId);
			ModelAndView result;

			Activity activity = this.activityService.findOne(activityIdT);
			SocialWorker sw = this.socialWorkerService.loggedSocialWorker();

			Assert.isTrue(!activity.getIsFinalMode());

			try {
				this.activityService.deleteActivity(activity, sw);

				result = new ModelAndView("redirect:list.do");

			} catch (Throwable oops) {
				result = new ModelAndView("redirect:list.do");
			}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:list.do");
		}
	}

	@RequestMapping(value = "/listAssistants", method = RequestMethod.GET)
	public ModelAndView listAssistants(@RequestParam(required = false) String activityId) {

		try {
			Assert.isTrue(StringUtils.isNumeric(activityId));
			int activityIdInt = Integer.parseInt(activityId);

			ModelAndView result;
			List<Prisoner> prisoners;
			Activity a;

			SocialWorker sw = this.socialWorkerService.loggedSocialWorker();

			a = this.activityService.findOne(activityIdInt);
			prisoners = this.activityService.getPrisonersPerActivity(a);

			result = new ModelAndView("activity/socialworker/listAssistants");
			result.addObject("prisoners", prisoners);
			result.addObject("requestURI", "activity/socialworker/listAssistants.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:list.do");
		}
	}

	protected ModelAndView createEditModelAndView(Activity activity) {
		ModelAndView result;

		result = this.createEditModelAndView(activity, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(Activity activity, String messageCode) {
		ModelAndView result;

		result = new ModelAndView("activity/socialworker/create");

		result.addObject("activity", activity);
		result.addObject("message", messageCode);

		return result;
	}

}
