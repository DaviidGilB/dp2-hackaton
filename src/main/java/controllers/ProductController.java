
package controllers;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.ConfigurationService;
import services.PrisonerService;
import services.ProductService;
import services.SalesManService;
import domain.Configuration;
import domain.Prisoner;
import domain.Product;
import domain.SalesMan;
import domain.TypeProduct;

@Controller
@RequestMapping("/product")
public class ProductController extends AbstractController {

	@Autowired
	private ProductService			productService;

	@Autowired
	private SalesManService			salesManService;

	@Autowired
	private PrisonerService			prisonerService;

	@Autowired
	private ConfigurationService	configurationService;


	public ProductController() {
		super();
	}

	// Listar Visitantes del prisionero logueado
	@RequestMapping(value = "/prisoner/list", method = RequestMethod.GET)
	public ModelAndView listPrisoner(@RequestParam(required = false) String salesmanId) {

		ModelAndView result;
		List<Product> products;

		try {

			Assert.isTrue(StringUtils.isNumeric(salesmanId));
			int salesmanIdInt = Integer.parseInt(salesmanId);

			SalesMan salesMan = this.salesManService.findOne(salesmanIdInt);

			if (salesMan == null)
				return this.listSalesManPrisoner();

			products = this.productService.getProductsFinalModeWithStockBySalesMan(salesmanIdInt);

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			Prisoner loggedPrisoner = this.prisonerService.loggedPrisoner();
			int points = loggedPrisoner.getPoints();

			result = new ModelAndView("anonymous/product/list");
			result.addObject("products", products);
			result.addObject("points", points);
			result.addObject("locale", locale);
			result.addObject("prisoner", true);
			result.addObject("requestURI", "product/prisoner/list.do");
		} catch (Throwable oops) {
			result = this.listSalesManPrisoner();
		}

		return result;
	}
	// Listar Visitantes del prisionero logueado
	@RequestMapping(value = "/anonymous/list", method = RequestMethod.GET)
	public ModelAndView listAnonymous() {

		try {
			ModelAndView result;
			List<Product> products;

			products = this.productService.getProductsFinalModeOfSalesMen();

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			result = new ModelAndView("anonymous/product/list");
			result.addObject("products", products);
			result.addObject("locale", locale);
			result.addObject("prisoner", false);
			result.addObject("requestURI", "product/anonymous/list.do");

			return result;
		} catch (Throwable oops2) {
			return new ModelAndView("redirect:/");
		}
	}

	// Listar Visitantes del prisionero logueado
	@RequestMapping(value = "/salesman/prisoner/list", method = RequestMethod.GET)
	public ModelAndView listSalesManPrisoner() {

		try {
			ModelAndView result;
			List<SalesMan> salesMen;

			salesMen = this.salesManService.findAll();

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			result = new ModelAndView("prisoner/salesMen/list");
			result.addObject("salesMen", salesMen);
			result.addObject("locale", locale);
			result.addObject("requestURI", "product/salesman/prisoner/list.do");

			return result;
		} catch (Throwable oops2) {
			return new ModelAndView("redirect:/");
		}
	}

	// TODO: TESTING DAVID
	@RequestMapping(value = "/salesman/list", method = RequestMethod.GET)
	public ModelAndView listSalesman() {
		ModelAndView result;

		try {
			List<Product> products = this.salesManService.getProductsOfLoggedSalesman();

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			result = new ModelAndView("product/salesman/list");
			result.addObject("products", products);
			result.addObject("locale", locale);
			result.addObject("salesman", true);
			result.addObject("requestURI", "product/salesman/list.do");
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}

		return result;
	}

	@RequestMapping(value = "/salesman/create", method = RequestMethod.GET)
	public ModelAndView create() {
		try {

			ModelAndView result;

			Product product = this.productService.create();

			result = this.createEditModelAndView(product);

			return result;
		} catch (Throwable oops2) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/salesman/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam(required = false) String productId) {
		ModelAndView result;

		try {
			Assert.isTrue(StringUtils.isNumeric(productId));
			Integer productIdInt = Integer.parseInt(productId);

			Product product = this.productService.getProductInDraftModeOfLoggedSalesMan(productIdInt);

			result = this.createEditModelAndView(product);
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:list.do");
		}

		return result;
	}

	@RequestMapping(value = "/salesman/restock", method = RequestMethod.GET)
	public ModelAndView restock(@RequestParam(required = false) String productId) {
		ModelAndView result;

		try {
			Assert.isTrue(StringUtils.isNumeric(productId));
			Integer productIdInt = Integer.parseInt(productId);

			Product product = this.productService.getProductInFinalModeOfLoggedSalesMan(productIdInt);

			result = this.restockModelAndView(product);
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:list.do");
		}

		return result;
	}

	@RequestMapping(value = "/salesman/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Product product, BindingResult binding) {
		ModelAndView result;

		try {
			Product pro = this.productService.reconstruct(product, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(pro);
			else
				try {
					if (product.getId() == 0)
						this.productService.addProduct(pro);
					else
						this.productService.updateProduct(pro);

					result = new ModelAndView("redirect:list.do");

				} catch (Throwable oops) {
					result = this.createEditModelAndView(product, "commit.error");
				}
			return result;
		} catch (Throwable oops2) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/salesman/edit", method = RequestMethod.POST, params = "delete")
	public ModelAndView delete(Product product, BindingResult binding) {
		ModelAndView result;

		Product pro = this.productService.findOne(product.getId());

		try {
			this.productService.delete(pro);
			result = new ModelAndView("redirect:list.do");

		} catch (Throwable oops) {
			result = this.createEditModelAndView(product, "commit.error");
		}

		return result;
	}

	@RequestMapping(value = "/salesman/restock", method = RequestMethod.POST, params = "save")
	public ModelAndView restock(Product product, BindingResult binding) {
		ModelAndView result;

		try {
			Product pro = this.productService.reconstruct(product, binding);

			if (binding.hasErrors())
				result = this.restockModelAndView(pro);
			else
				try {
					this.productService.restockProduct(pro);

					result = new ModelAndView("redirect:list.do");

				} catch (Throwable oops) {
					result = this.restockModelAndView(product, "commit.error");
				}
			return result;
		} catch (Throwable oops2) {
			return new ModelAndView("redirect:/");
		}
	}

	protected ModelAndView createEditModelAndView(Product product) {
		ModelAndView result;

		result = this.createEditModelAndView(product, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(Product product, String messageCode) {
		ModelAndView result;

		result = new ModelAndView("product/salesman/edit");
		result.addObject("product", product);
		result.addObject("message", messageCode);

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
		Configuration configuration = this.configurationService.getConfiguration();
		List<TypeProduct> productType = configuration.getTypeProducts();

		result.addObject("locale", locale);
		result.addObject("productType", productType);

		return result;
	}

	protected ModelAndView restockModelAndView(Product product) {
		ModelAndView result;

		result = this.restockModelAndView(product, null);

		return result;
	}

	protected ModelAndView restockModelAndView(Product product, String messageCode) {
		ModelAndView result;

		result = new ModelAndView("product/salesman/restock");
		result.addObject("product", product);
		result.addObject("message", messageCode);

		return result;
	}

}
