package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import domain.Visitor;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Integer> {

	@Query("select m from Visitor m join m.userAccount u where u.username = ?1")
	public Visitor getVisitorByUsername(String username);

}
