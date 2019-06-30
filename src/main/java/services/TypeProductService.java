package services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import domain.TypeProduct;
import repositories.TypeProductRepository;

@Service
@Transactional
public class TypeProductService {

	@Autowired
	private TypeProductRepository typeProductRepository;

	public List<TypeProduct> findAll() {
		return this.typeProductRepository.findAll();
	}

	public TypeProduct findOne(int typeId) {
		return this.typeProductRepository.findOne(typeId);
	}

	public TypeProduct save(TypeProduct typeProduct) {
		return this.typeProductRepository.save(typeProduct);
	}

	public void delete(TypeProduct typeProduct) {
		this.typeProductRepository.delete(typeProduct);
	}

}
