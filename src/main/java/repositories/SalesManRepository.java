
package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.SalesMan;

@Repository
public interface SalesManRepository extends JpaRepository<SalesMan, Integer> {

	@Query("select m from SalesMan m join m.userAccount u where u.username = ?1")
	public SalesMan getSalesManByUsername(String username);

	@Query("select s from SalesMan s join s.products p where p.id = ?1")
	public SalesMan getSalesManOfProduct(int productId);
}
