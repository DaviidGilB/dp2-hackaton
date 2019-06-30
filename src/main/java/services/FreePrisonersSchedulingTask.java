
package services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import domain.Prisoner;

@Component("freedom")
public class FreePrisonersSchedulingTask {

	@Autowired
	private PrisonerService	prisonerService;


	public void printMessage() {
		List<Prisoner> prisonersToCheck = new ArrayList<Prisoner>();

		prisonersToCheck = this.prisonerService.getPrisonersToBeFree();

		if (prisonersToCheck.size() > 0) {
			for (Prisoner p : prisonersToCheck) {
				p.getUserAccount().setIsNotLocked(false);
				p.setFreedom(true);

				this.prisonerService.save(p);
			}

			Date date = new Date();

			System.out.println("Log: " + date + " Checked sucesfully the list of prissoner to get free");
			System.out.println("Prisoners : " + prisonersToCheck.toString() + " are now free");
		}
	}
}
