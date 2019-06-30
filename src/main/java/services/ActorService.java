
package services;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.ActorRepository;
import security.LoginService;
import security.UserAccount;
import domain.Actor;
import domain.Box;
import domain.Prisoner;

@Service
@Transactional
public class ActorService {

	@Autowired
	private ActorRepository	actorRepository;

	@Autowired
	private PrisonerService	prisonerService;

	@Autowired
	private WardenService	wardenService;


	public Actor flushSave(Actor actor) {
		return this.actorRepository.saveAndFlush(actor);
	}

	public List<Actor> findAll() {
		return this.actorRepository.findAll();
	}

	public Actor findOne(int id) {
		return this.actorRepository.findOne(id);
	}

	public Actor getActorByUsername(String a) {
		return this.actorRepository.getActorByUserName(a);
	}

	public List<Actor> getActors() {
		return this.actorRepository.getActors();
	}

	public void loggedAsActor() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		Assert.isTrue(userAccount.getAuthorities().size() > 0);
	}

	public Boolean loggedAsActorBoolean() {
		Boolean res = true;
		UserAccount userAccount;
		try {
			userAccount = LoginService.getPrincipal();
			return res;
		} catch (Throwable oops) {
			res = false;
			return res;
		}
	}

	public Boolean loggedAsActorBolean() {
		Boolean res = false;
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		if (userAccount.getAuthorities().size() > 0)
			res = true;
		else
			res = false;
		return res;
	}

	public Actor save(Actor a) {
		return this.actorRepository.save(a);
	}

	public Actor loggedActor() {
		Actor actor;
		UserAccount userAccount;
		try {
			userAccount = LoginService.getPrincipal();
			actor = this.actorRepository.getActorByUserName(userAccount.getUsername());
		} catch (Throwable oops) {
			actor = new Actor();
		}
		return actor;
	}

	public List<String> getUsernamesOfActors() {
		return this.actorRepository.usernamesOfActors();
	}

	public List<Actor> allActorsExceptOne(String username) {
		return this.actorRepository.getActorsExceptOne(username);
	}

	public List<Box> getlistOfBoxes(final Actor actor) {

		return this.actorRepository.listOfBoxes(actor);
	}

	public List<Actor> getActorsToSendMessageOfPrisoners() {
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();

		Prisoner prisoner = this.prisonerService.getPrisonerByUsername(userAccount.getUsername());
		List<Actor> actors = new ArrayList<Actor>();
		actors.addAll(this.actorRepository.getListOfPrisonersWithLowCrimRate());
		actors.addAll(this.actorRepository.getVisitorsWithVisitsOfPrisoner(prisoner));
		actors.addAll(this.actorRepository.getUsernamesOfWardens());
		return actors;
	}

	public List<String> getUsernamesOfActorsAndGoodPrisoners() {
		return this.actorRepository.getUsernamesOfActorsAndGoodPrisoners();
	}

	public List<String> getAllUsernamesInTheSystem() {
		return this.actorRepository.getAllUsernamesInTheSystem();
	}
}
