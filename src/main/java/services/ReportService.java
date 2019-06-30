
package services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.ReportRepository;
import domain.Guard;
import domain.Prisoner;
import domain.Report;
import domain.Visit;
import domain.VisitStatus;

@Service
@Transactional
public class ReportService {

	@Autowired
	private ReportRepository	reportRepository;

	@Autowired
	private GuardService		guardService;

	@Autowired
	private VisitService		visitService;

	@Autowired
	private WardenService		wardenService;

	@Autowired
	private Validator			validator;


	public void saveReport(Report report, int visitId) {
		this.guardService.loggedAsGuard();
		Guard loggedGuard = this.guardService.loggedGuard();
		Visit visit = this.visitService.findOne(visitId);

		Date thisMoment = new Date();
		thisMoment.setTime(thisMoment.getTime() - 1);

		Assert.notNull(visit);
		Assert.isNull(visit.getReport());
		Assert.isTrue(loggedGuard.getVisits().contains(visit));
		Assert.isTrue(visit.getDate().before(thisMoment));
		Assert.isTrue(visit.getVisitStatus() == VisitStatus.PERMITTED);
		Assert.hasText(report.getDescription());

		Report saved = this.reportRepository.save(report);

		visit.setReport(saved);

	}

	public void delete(int reportId) {
		this.reportRepository.delete(reportId);
	}

	// RECONSTRUCT
	public Report reconstruct(Report report, BindingResult binding) {
		Report result = new Report();

		result = report;

		Date thisMoment = new Date();
		thisMoment.setTime(thisMoment.getTime() - 1);
		result.setDate(thisMoment);

		this.validator.validate(result, binding);
		return result;
	}

	public Map<Report, Prisoner> getReportsAsWarden() {
		this.wardenService.loggedAsWarden();

		Map<Report, Prisoner> reportsAndPrisoner = new HashMap<>();

		List<Report> reports = this.reportRepository.findAll();

		for (Report r : reports) {
			Prisoner prisoner = this.reportRepository.getPrisonerOfReport(r.getId());
			reportsAndPrisoner.put(r, prisoner);
		}

		return reportsAndPrisoner;
	}

}
