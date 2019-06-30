
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Charge;
import domain.Prisoner;

@Repository
public interface ChargeRepository extends JpaRepository<Charge, Integer> {

	@Query("select c from Charge c where c.isDraftMode = false and c.titleEnglish != 'Suspicious'")
	public List<Charge> getFinalCharges();

	@Query("select c from Charge c where c.isDraftMode = true")
	public List<Charge> getDraftCharges();

	@Query("select c from Charge c where c NOT IN (select c.id from Prisoner p join p.charges c where p = ?1)")
	public List<Charge> getChargesNotAssignedToPrisoner(Prisoner prisoner);

	@Query("select a from Charge a where a.titleEnglish = ?1")
	public List<Charge> getCharge(String charge);

}
