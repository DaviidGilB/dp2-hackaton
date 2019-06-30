
package controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.WardenService;

@Controller
@RequestMapping("/statistics/warden")
public class WardenStatisticsController {

	@Autowired
	private WardenService	wardenService;


	public WardenStatisticsController() {
		super();
	}

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ModelAndView statistics() {
		try {
			ModelAndView result;
			this.wardenService.loggedAsWarden();

			result = new ModelAndView("statistics/warden/show");
			result.addObject("statistics", this.wardenService.statistics());
			result.addObject("activitiesLargestAvgCrimeRate", this.wardenService.getActivitiesLargestAvgCrimeRate());
			result.addObject("activitiesSmallestAvgCrimeRate", this.wardenService.getActivitiesSmallestAvgCrimeRate());
			result.addObject("activitiesMostSearched", this.wardenService.getActivitiesMostSearched());
			result.addObject("activitiesLargestNumberPrisoners", this.wardenService.getActivitiesLargestNumberPrisoners());
			result.addObject("couplesWithMostVisits", this.wardenService.getCouplesWithMostVisits());
			result.addObject("guardsWithTheLargestNumberOfReportsWritten", this.wardenService.getGuardsWithTheLargestNumberOfReportsWritten());
			result.addObject("prisonersMostRejectedRequestToDifferentActivitiesAndNoApprovedOnThoseActivities", this.wardenService.getPrisonersMostRejectedRequestToDifferentActivitiesAndNoApprovedOnThoseActivities());
			result.addObject("prisonersWithVisitsToMostDifferentVisitors", this.wardenService.getPrisonersWithVisitsToMostDifferentVisitors());
			result.addObject("socialWorkerMostActivitiesFull", this.wardenService.getSocialWorkerMostActivitiesFull());
			result.addObject("socialWorkersLowestRatioPrisonersPerActivity", this.wardenService.getSocialWorkersLowestRatioPrisonersPerActivity());
			result.addObject("top3PrisonersLowestCrimeRate", this.wardenService.getTop3PrisonersLowestCrimeRate());
			result.addObject("top5PrisonersParticipatedMostActivitiesLastMonth", this.wardenService.getTop5PrisonersParticipatedMostActivitiesLastMonth());
			result.addObject("visitorsWithVisitsToMostDifferentPrisoners", this.wardenService.getVisitorsWithVisitsToMostDifferentPrisoners());

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}
}
