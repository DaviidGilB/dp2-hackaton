package repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Activity;
import domain.FinderActivities;
import domain.Prisoner;
import domain.SocialWorker;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {

	@Query("select count(r) from Activity a join a.requests r where a = ?1 and r.status='APPROVED'")
	public int getNumberOfApprobedRequest(Activity activity);

	@Query("select a from Activity a where a.realizationDate > ?1")
	public List<Activity> getPostActivities(Date date);

	@Query("select p from Activity a join a.requests r join r.prisoner p where r.status = 'APPROVED' and a = ?1")
	public List<Prisoner> getPrisonersPerActivity(Activity a);

	@Query("select a from SocialWorker s join s.activities a where a.isFinalMode = true and s = ?1")
	public List<Activity> getFinalActivitiesSocialWorker(SocialWorker sw);

	@Query("select f from FinderActivities f join f.activities a where a=?1")
	public List<FinderActivities> getFindersByActivity(Activity a);

}
