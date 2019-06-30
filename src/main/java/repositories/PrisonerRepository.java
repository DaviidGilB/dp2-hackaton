
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Prisoner;
import domain.Visitor;

@Repository
public interface PrisonerRepository extends JpaRepository<Prisoner, Integer> {

	@Query("select m from Prisoner m join m.userAccount u where u.username = ?1")
	public Prisoner getPrisonerByUsername(String username);

	@Query("select distinct vtor from Visit v join v.visitor vtor join v.prisoner p where p.id=?1")
	public List<Visitor> getVisitorsToCreateVisit(int prisonerId);

	@Query("select p from Prisoner p where p.freedom = true")
	public List<Prisoner> getFreePrisoners();

	@Query("select p from Prisoner p where p.freedom = false")
	public List<Prisoner> getIncarceratedPrisoners();

	@Query("select p from Prisoner p join p.userAccount u where p.isSuspect = true and p.freedom = false and p.isIsolated = false and u.isNotLocked = true")
	public List<Prisoner> getSuspectPrisoners();

	@Query("select distinct p from Prisoner p join p.products p1 where p1 in (select p2 from SalesMan s join s.products p2 where s.id = ?1)")
	public List<Prisoner> getPrisonersWithProductsOfASalesMan(int salesmanId);

	@Query("select sum(c.year) from Prisoner p join p.charges c where p = ?1")
	public Integer totalYearsOfCharges(Prisoner p);

	@Query("select sum(c.month) from Prisoner p join p.charges c where p = ?1")
	public Integer totalMonthsOfCharges(Prisoner p);

	@Query("select p from Prisoner p where p.exitDate <= NOW()")
	public List<Prisoner> getPrisonersToBeFree();
}
