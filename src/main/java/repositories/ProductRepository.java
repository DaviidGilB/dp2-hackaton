
package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

	@Query("select p from Product p where p.isDraftMode = False and p.stock > 0")
	public List<Product> getProductsFinalModeWithStock();

	@Query("select p from Product p where p.isDraftMode = False")
	public List<Product> getProductsFinalMode();

	@Query("select p from SalesMan s join s.products p where p.isDraftMode = False and p.stock > 0 and s.id = ?1")
	public List<Product> getProductsFinalModeWithStockBySalesMan(int salesManId);

	@Query("select p from SalesMan s join s.products p where p.isDraftMode = false")
	public List<Product> getProductsFinalModeOfSalesMen();

	@Query("select p from SalesMan s join s.products p where s.id = ?1 and p.id = ?2 and p.isDraftMode = true")
	public Product getProductInDraftModeOfLoggedSalesMan(int salesManId, Integer productId);

	@Query("select p from SalesMan s join s.products p where s.id = ?1 and p.id = ?2 and p.isDraftMode = false")
	public Product getProductInFinalModeOfLoggedSalesMan(int salesManId, Integer productId);

}
