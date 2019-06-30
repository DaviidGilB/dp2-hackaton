package repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import domain.Activity;
import domain.FinderActivities;

public interface FinderActivitiesRepository extends JpaRepository<FinderActivities, Integer> {

	@Query("select distinct(a) from Activity a where a.title like ?1 or a.description like ?1")
	public List<Activity> filterByKeyWord(String keyWord);

	@Query("select distinct(a) from Activity a where a.realizationDate > ?1 or a.realizationDate = ?1")
	public List<Activity> filterByDateMin(Date min);

	@Query("select distinct(a) from Activity a where a.realizationDate < ?1 or a.realizationDate = ?1")
	public List<Activity> filterByDateMax(Date max);

}
