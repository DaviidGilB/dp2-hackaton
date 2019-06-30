
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Guard;
import domain.Visit;

@Repository
public interface GuardRepository extends JpaRepository<Guard, Integer> {

	@Query("select v from Visit v where v.date > NOW() and visitStatus = 'ACCEPTED'")
	public List<Visit> getFutureAcceptedVisits();

	@Query("select m from Guard m join m.userAccount u where u.username = ?1")
	public Guard getGuardByUsername(String username);

}
