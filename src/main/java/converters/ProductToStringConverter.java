
package converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import domain.Product;

@Component
@Transactional
public class ProductToStringConverter implements Converter<Product, String> {

	@Override
	public String convert(Product product) {
		String result;

		if (product == null)
			result = null;
		else
			result = String.valueOf(product.getId());
		return result;
	}

}
