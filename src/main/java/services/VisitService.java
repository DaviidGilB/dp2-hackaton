
package services;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.VisitRepository;
import domain.Guard;
import domain.Prisoner;
import domain.Visit;
import domain.VisitStatus;
import domain.Visitor;

@Service
@Transactional
public class VisitService {

	@Autowired
	private VisitRepository	visitRepository;

	@Autowired
	private PrisonerService	prisonerService;

	@Autowired
	private VisitorService	visitorService;

	@Autowired
	private GuardService	guardService;

	@Autowired
	private Validator		validator;


	// -----------------------------------------SECURITY-----------------------------
	// ------------------------------------------------------------------------------

	public List<Visit> getVisitsByPrisonerAndStatus(VisitStatus visitStatus, int prisonerId) {
		return this.visitRepository.getVisitsByPrisonerAndStatus(visitStatus, prisonerId);
	}

	public List<Visit> getVisitsByVisitorAndStatus(VisitStatus visitStatus, int visitorId) {
		return this.visitRepository.getVisitsByVisitorAndStatus(visitStatus, visitorId);
	}

	public Visit findOne(int visitId) {
		return this.visitRepository.findOne(visitId);
	}

	public Visit save(int visitId) {
		return this.visitRepository.save(this.visitRepository.findOne(visitId));
	}

	// Change Status as Prisoner
	public Visit editVisitPrisoner(Visit visit, boolean accept) {
		// Security
		this.prisonerService.loggedAsPrisoner();
		Prisoner loggedPrisoner = this.prisonerService.loggedPrisoner();
		Assert.isTrue(!visit.isCreatedByPrisoner());
		Assert.isTrue(visit.getPrisoner().equals(loggedPrisoner));
		Assert.isTrue(visit.getVisitStatus() == VisitStatus.PENDING);

		if (accept)
			visit.setVisitStatus(VisitStatus.ACCEPTED);
		else
			visit.setVisitStatus(VisitStatus.REJECTED);

		return this.visitRepository.save(visit);
	}

	// Change Status As Visitor
	public Visit editVisitVisitor(Visit visit, boolean accept) {
		// Security
		this.visitorService.loggedAsVisitor();
		Visitor loggedVisitor = this.visitorService.loggedVisitor();
		Assert.isTrue(visit.isCreatedByPrisoner());
		Assert.isTrue(visit.getVisitor().equals(loggedVisitor));
		Assert.isTrue(visit.getVisitStatus() == VisitStatus.PENDING);

		if (accept)
			visit.setVisitStatus(VisitStatus.ACCEPTED);
		else
			visit.setVisitStatus(VisitStatus.REJECTED);

		return this.visitRepository.save(visit);
	}

	// Change Status As Guard
	public Visit editVisitGuard(Visit visit, boolean permit) {
		// Security
		this.guardService.loggedAsGuard();
		Guard loggedGuard = this.guardService.loggedGuard();
		Assert.isTrue(visit.getVisitStatus() == VisitStatus.ACCEPTED);
		Visit saved = new Visit();

		Date thisMoment = new Date();
		thisMoment.setTime(thisMoment.getTime() - 1);

		Assert.isTrue(visit.getDate().after(thisMoment));

		if (permit) {
			visit.setVisitStatus(VisitStatus.PERMITTED);
			saved = this.visitRepository.save(visit);
			loggedGuard.getVisits().add(saved);
		} else {
			visit.setVisitStatus(VisitStatus.REJECTED);
			saved = this.visitRepository.save(visit);
		}
		return saved;
	}

	// CREATE AS VISITOR
	public Visit createAsVisitor(Prisoner prisoner) {
		this.visitorService.loggedAsVisitor();
		Visitor visitor = this.visitorService.loggedVisitor();

		Assert.isTrue(!prisoner.getFreedom());
		Assert.isTrue(!prisoner.getIsIsolated());

		Visit visit = new Visit();

		visit.setCreatedByPrisoner(false);
		visit.setDate(null);
		visit.setDescription("");
		visit.setPrisoner(prisoner);
		visit.setReason(null);
		visit.setReport(null);
		visit.setVisitor(visitor);
		visit.setVisitStatus(VisitStatus.PENDING);

		return visit;
	}

	// CREATE AS PRISONER
	public Visit createAsPrisoner(Visitor visitor) {
		this.prisonerService.loggedAsPrisoner();
		Prisoner prisoner = this.prisonerService.loggedPrisoner();

		Assert.isTrue(!prisoner.getFreedom());
		Assert.isTrue(!prisoner.getIsIsolated());
		Assert.notNull(visitor);
		Assert.isTrue(this.prisonerService.getVisitorsToCreateVisit(prisoner).contains(visitor));

		Visit visit = new Visit();

		visit.setCreatedByPrisoner(true);
		visit.setDate(null);
		visit.setDescription("");
		visit.setPrisoner(prisoner);
		visit.setReason(null);
		visit.setReport(null);
		visit.setVisitor(visitor);
		visit.setVisitStatus(VisitStatus.PENDING);

		return visit;
	}

	// RECONSTRUCT AS VISITOR
	public Visit reconstructAsVisitor(Visit visit, BindingResult binding) {
		this.visitorService.loggedAsVisitor();
		Visitor visitor = this.visitorService.loggedVisitor();
		Visit result = new Visit();

		result = visit;

		result.setCreatedByPrisoner(false);
		result.setDate(visit.getDate());
		result.setDescription(visit.getDescription());
		result.setPrisoner(visit.getPrisoner());
		result.setReason(visit.getReason());
		result.setReport(null);
		result.setVisitor(visitor);
		result.setVisitStatus(VisitStatus.PENDING);

		this.validator.validate(result, binding);
		return result;
	}

	// RECONSTRUCT AS PRISONER
	public Visit reconstructAsPrisoner(Visit visit, BindingResult binding) {

		this.prisonerService.loggedAsPrisoner();
		Prisoner prisoner = this.prisonerService.loggedPrisoner();
		Visit result = new Visit();

		result = visit;

		result.setCreatedByPrisoner(true);
		result.setDate(visit.getDate());
		result.setDescription(visit.getDescription());
		result.setPrisoner(prisoner);
		result.setReason(visit.getReason());
		result.setReport(null);
		result.setVisitor(visit.getVisitor());
		result.setVisitStatus(VisitStatus.PENDING);

		this.validator.validate(result, binding);
		return result;
	}

	// SAVE VISIT AS VISITOR
	public void saveVisitAsVisitor(Visit visit) {
		this.visitorService.loggedAsVisitor();

		Assert.notNull(visit.getReason());
		Assert.notNull(visit.getPrisoner());
		Assert.notNull(visit.getDate());
		Assert.hasText(visit.getDescription());

		Prisoner prisoner = visit.getPrisoner();

		Date thisMoment = new Date();
		thisMoment.setTime(thisMoment.getTime() - 1);

		Assert.isTrue(visit.getVisitor().equals(this.visitorService.loggedVisitor()));
		Assert.isTrue(!prisoner.getFreedom());
		Assert.isTrue(!prisoner.getIsIsolated());
		Assert.isTrue(visit.getDate().after(thisMoment));
		Assert.isTrue(visit.getDate().before(visit.getPrisoner().getExitDate()));

		this.visitRepository.save(visit);
	}

	// SAVE VISIT AS PRISONER
	public void saveVisitAsPrisoner(Visit visit) {
		this.prisonerService.loggedAsPrisoner();

		Assert.notNull(visit.getReason());
		Assert.notNull(visit.getVisitor());
		Assert.notNull(visit.getDate());
		Assert.hasText(visit.getDescription());

		Visitor visitor = visit.getVisitor();

		Date thisMoment = new Date();
		thisMoment.setTime(thisMoment.getTime() - 1);

		Prisoner loggedPrisoner = this.prisonerService.loggedPrisoner();

		Assert.isTrue(visit.getPrisoner().equals(loggedPrisoner));
		Assert.isTrue(this.prisonerService.getVisitorsToCreateVisit(loggedPrisoner).contains(visitor));
		Assert.isTrue(!loggedPrisoner.getFreedom());
		Assert.isTrue(!loggedPrisoner.getIsIsolated());
		Assert.isTrue(visit.getDate().after(thisMoment));
		Assert.isTrue(visit.getDate().before(visit.getPrisoner().getExitDate()));

		this.visitRepository.save(visit);
	}

	public Visit save(Visit v) {
		return this.visitRepository.save(v);
	}

	public void flush() {
		this.visitRepository.flush();
	}

}
