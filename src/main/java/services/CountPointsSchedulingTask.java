
package services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import domain.ActivityStatus;
import domain.Prisoner;
import domain.Request;

@Component("points")
public class CountPointsSchedulingTask {

	@Autowired
	private PrisonerService	prisonerService;

	@Autowired
	private RequestService	requestService;

	@Autowired
	private ActivityService	activityService;


	public void printMessage() {

		List<Request> request = new ArrayList<Request>();

		request = this.requestService.requestToContabilicePoints();

		if (request.size() > 0) {
			for (Request r : request) {
				r.setStatus(ActivityStatus.CONTABILICEDPOINTS);
				Prisoner prisoner = r.getPrisoner();
				prisoner.setPoints(prisoner.getPoints() + r.getActivity().getRewardPoints());

				this.prisonerService.save(prisoner);
				this.requestService.save(r);

				System.out.println("Log: To prisoner " + prisoner.getName() + ": Added the activity points sucessfully");

			}
			Date date = new Date();

			System.out.println("Log: " + date + " Added all the activity points sucessfully ");
		}

	}
}
