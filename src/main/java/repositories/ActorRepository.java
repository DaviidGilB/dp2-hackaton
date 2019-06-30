
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Actor;
import domain.Box;
import domain.Prisoner;
import domain.Visitor;
import domain.Warden;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Integer> {

	@Query("select a from Actor a join a.userAccount b where b.username = ?1")
	public Actor getActorByUserName(String a);

	@Query("select a from Actor a")
	public List<Actor> getActors();

	@Query("select u.username from Actor a join a.userAccount u")
	public List<String> usernamesOfActors();

	@Query("select a from Actor a join a.userAccount b where b.username != ?1")
	public List<Actor> getActorsExceptOne(String username);

	@Query("select c.boxes from Actor c where c = ?1")
	public List<Box> listOfBoxes(Actor actor);

	@Query("select u.username from Prisoner p join p.userAccount u where p.crimeRate <= -0.5 and p.isIsolated = false and p.freedom = false and p.isSuspect = false")
	public List<Prisoner> getListOfPrisonersWithLowCrimRate();

	@Query("select distinct u.username from Visitor v join v.userAccount u join v.visits w where w.prisoner = ?1")
	public List<Visitor> getVisitorsWithVisitsOfPrisoner(Prisoner prisoner);

	@Query("select u.username from Actor a join a.userAccount u where a not in (select p from Prisoner p join p.userAccount u where u.isNotLocked = false or p.freedom = true or p.isIsolated = true)")
	public List<String> getUsernamesOfActorsAndGoodPrisoners();

	@Query("select u.username from Actor a join a.userAccount u")
	public List<String> getAllUsernamesInTheSystem();

	@Query("select u.username from Warden v join v.userAccount u")
	public List<Warden> getUsernamesOfWardens();
}
