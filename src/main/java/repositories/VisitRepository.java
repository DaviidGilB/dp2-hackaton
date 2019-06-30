
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Visit;
import domain.VisitStatus;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Integer> {

	@Query("select v from Prisoner p join p.visits v where v.visitStatus = ?1 and p.id=?2")
	public List<Visit> getVisitsByPrisonerAndStatus(VisitStatus visitStatus, int prisonerId);

	@Query("select v from Visitor p join p.visits v where v.visitStatus = ?1 and p.id=?2")
	public List<Visit> getVisitsByVisitorAndStatus(VisitStatus visitStatus, int visitorId);
}
