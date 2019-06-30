
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.FinderActivities;
import domain.Prisoner;
import domain.Request;
import domain.SocialWorker;

@Repository
public interface SocialWorkerRepository extends JpaRepository<SocialWorker, Integer> {

	@Query("select m from SocialWorker m join m.userAccount u where u.username = ?1")
	public SocialWorker getSocialWorkerByUsername(String username);

	@Query("select r from SocialWorker s join s.activities a join a.requests r where s= ?1")
	public List<Request> getRequestsBySocialWorker(SocialWorker socialWorker);

	@Query("select p from Prisoner p join p.finderActivities f where f= ?1")
	public Prisoner getPrisonerFromFinder(FinderActivities finderActiviti1es);
}
