
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import domain.Finder;
import domain.Prisoner;

public interface FinderRepository extends JpaRepository<Finder, Integer> {

	@Query("select distinct(p) from Prisoner p where p.ticker like ?1 or p.name like ?1 or p.surname like ?1")
	public List<Prisoner> filterByKeyWord(String keyWord);

	@Query("select distinct(p) from Prisoner p join p.charges c where c.titleEnglish like ?1")
	public List<Prisoner> filterByCharge(String charge);

}
