
package services;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.ProfessionalRecordRepository;
import domain.ProfessionalRecord;
import domain.SocialWorker;

@Service
@Transactional
public class ProfessionalRecordService {

	@Autowired
	private ProfessionalRecordRepository	professionalRecordRepository;

	@Autowired
	private SocialWorkerService				socialWorkerService;


	public ProfessionalRecord create() {

		ProfessionalRecord professionalRecord = new ProfessionalRecord();
		professionalRecord.setNameCompany("");
		professionalRecord.setStartDate(null);
		professionalRecord.setEndDate(null);
		professionalRecord.setRole("");
		professionalRecord.setLinkAttachment("");

		return professionalRecord;

	}

	public List<ProfessionalRecord> findAll() {
		return this.professionalRecordRepository.findAll();
	}

	public ProfessionalRecord findOne(Integer id) {
		return this.professionalRecordRepository.findOne(id);
	}

	public void save(ProfessionalRecord professionalRecord) {
		this.professionalRecordRepository.save(professionalRecord);
	}

	public void delete(ProfessionalRecord professionalRecord) {
		this.professionalRecordRepository.delete(professionalRecord);
	}

	public ProfessionalRecord getProfessionalRecordOfLoggedSocialWorker(String professionalRecordId) {
		SocialWorker socialWorker = this.socialWorkerService.loggedSocialWorker();

		Assert.isTrue(StringUtils.isNumeric(professionalRecordId));
		int professionalRecordIdInt = Integer.parseInt(professionalRecordId);

		ProfessionalRecord professionalRecord = this.professionalRecordRepository.findOne(professionalRecordIdInt);
		Assert.isTrue(socialWorker.getCurriculum().getProfessionalRecords().contains(professionalRecord));
		return professionalRecord;
	}
}
