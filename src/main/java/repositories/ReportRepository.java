
package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Prisoner;
import domain.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {

	@Query("select p from Prisoner p join p.visits v join v.report r where r.id = ?1")
	public Prisoner getPrisonerOfReport(int reportId);

}
