
package services;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.EducationRecordRepository;
import domain.EducationRecord;
import domain.SocialWorker;

@Service
@Transactional
public class EducationRecordService {

	@Autowired
	private EducationRecordRepository	educationRecordRepository;

	@Autowired
	private SocialWorkerService			socialProfileService;


	public EducationRecord create() {

		EducationRecord educationRecord = new EducationRecord();
		educationRecord.setTitle("");
		educationRecord.setStartDateStudy(null);
		educationRecord.setEndDateStudy(null);
		educationRecord.setInstitution("");
		educationRecord.setLink("");

		return educationRecord;

	}

	public List<EducationRecord> findAll() {

		return this.educationRecordRepository.findAll();
	}

	public EducationRecord findOne(Integer id) {
		return this.educationRecordRepository.findOne(id);
	}

	public void save(EducationRecord educationRecord) {
		this.educationRecordRepository.save(educationRecord);
	}

	public void delete(EducationRecord educationRecord) {
		this.educationRecordRepository.delete(educationRecord);
	}

	public EducationRecord getEducationRecordOfLoggedSocialWorker(String educationRecordId) {
		SocialWorker socialWorker = this.socialProfileService.loggedSocialWorker();

		Assert.isTrue(StringUtils.isNumeric(educationRecordId));
		int educationRecordIdInt = Integer.parseInt(educationRecordId);

		EducationRecord educationRecord = this.educationRecordRepository.findOne(educationRecordIdInt);
		Assert.notNull(educationRecord);
		Assert.isTrue(socialWorker.getCurriculum().getEducationRecords().contains(educationRecord));
		return educationRecord;
	}
}
