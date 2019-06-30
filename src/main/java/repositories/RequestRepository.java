
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import domain.Activity;
import domain.Request;
import domain.SocialWorker;

public interface RequestRepository extends JpaRepository<Request, Integer> {

	@Query("select distinct(r) from SocialWorker s join s.activities a join a.requests r where s=?1 and a.id=?2")
	public List<Request> getRequestsFromSocialWorker(SocialWorker socialWorker, Integer activityId);

	@Query("select r from Prisoner p join p.requests r join r.activity a where r.status = 'APPROVED' AND a.realizationDate <= NOW()")
	public List<Request> requestToContabilicePoints();

	@Query("select r from Request r where r.status = 'APPROVED' AND r.activity = ?1")
	public List<Request> getAprovedRequestByPrisoner(Activity activity);
}
