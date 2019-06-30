
package services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.ChargeRepository;
import domain.Charge;
import domain.Prisoner;

@Transactional
@Service
public class ChargeService {

	@Autowired
	private ChargeRepository	chargeRepository;

	@Autowired
	private WardenService		wardenService;


	// CRUDS

	public Charge findOne(Integer id) {
		return this.chargeRepository.findOne(id);
	}

	public List<Charge> findAll() {
		return this.chargeRepository.findAll();
	}

	public Charge save(Charge charge) {
		return this.chargeRepository.save(charge);
	}

	public void delete(Charge charge) {
		this.wardenService.loggedAsWarden();
		Assert.isTrue(charge.getIsDraftMode());

		this.chargeRepository.delete(charge);
	}

	public List<Charge> getFinalCharges() {
		return this.chargeRepository.getFinalCharges();
	}

	public List<Charge> getChargesNotAssignedToPrisoner(Prisoner prisoner) {
		return this.chargeRepository.getChargesNotAssignedToPrisoner(prisoner);
	}

	public List<Charge> getDraftCharges() {
		return this.chargeRepository.getDraftCharges();
	}

	public void flush() {
		this.chargeRepository.flush();
	}

	public List<Charge> getCharge(String charge) {
		return this.chargeRepository.getCharge(charge);
	}
}
