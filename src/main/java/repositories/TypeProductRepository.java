package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import domain.TypeProduct;

@Repository
public interface TypeProductRepository extends JpaRepository<TypeProduct, Integer> {

}
