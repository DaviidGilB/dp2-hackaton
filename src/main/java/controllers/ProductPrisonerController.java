
package controllers;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.PrisonerService;
import services.ProductService;
import domain.Prisoner;
import domain.Product;

@Controller
@RequestMapping("/product/prisoner")
public class ProductPrisonerController extends AbstractController {

	@Autowired
	private PrisonerService	prisonerService;

	@Autowired
	private ProductService	productService;


	public ProductPrisonerController() {
		super();
	}

	@RequestMapping(value = "/store", method = RequestMethod.GET)
	public ModelAndView listProducts() {
		ModelAndView result;

		try {
			Prisoner prisoner = this.prisonerService.securityAndPrisoner();
			List<Product> products = this.productService.getProductsFinalModeWithStock();

			result = this.createEditModelAndView("prisoner/store", products, prisoner.getPoints());
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}

		return result;
	}

	@RequestMapping(value = "/buy", method = RequestMethod.GET)
	public ModelAndView buyProduct(@RequestParam(required = false) String productId) {
		ModelAndView result;

		try {
			Assert.isTrue(StringUtils.isNumeric(productId));
			int productIdInt = Integer.parseInt(productId);

			Product product = this.productService.getProductAsPrisonerToBuy(productIdInt);

			Prisoner prisoner = this.prisonerService.loggedPrisoner();
			result = this.createEditModelAndView("prisoner/buy", product, prisoner.getPoints());
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/product/prisoner/store.do");
		}

		return result;
	}

	@RequestMapping(value = "/buy", method = RequestMethod.POST, params = "save")
	public ModelAndView buyProductSave(@RequestParam(required = false) String productId, @RequestParam(required = false) String quantity) {
		ModelAndView result;

		try {
			Assert.isTrue(StringUtils.isNumeric(productId));
			int productIdInt = Integer.parseInt(productId);

			Assert.isTrue(StringUtils.isNumeric(quantity));
			int quantityInt = Integer.parseInt(quantity);

			this.productService.buyProductAsPrisoner(productIdInt, quantityInt);

			result = new ModelAndView("redirect:/product/prisoner/store.do");
		} catch (Throwable oops) {
			try {
				Assert.isTrue(StringUtils.isNumeric(productId));
				int productIdInt = Integer.parseInt(productId);

				Assert.isTrue(StringUtils.isNumeric(quantity));
				int quantityInt = Integer.parseInt(quantity);

				Product product = this.productService.getProductAsPrisonerToBuy(productIdInt);
				Prisoner prisoner = this.prisonerService.securityAndPrisoner();

				String message = "";

				Boolean stock = quantityInt <= product.getStock();
				Boolean points = (product.getPrice() * quantityInt) <= prisoner.getPoints();
				Boolean zeroOrNegative = quantityInt > 0;

				if (!stock && !points)
					message = "prisoner.purchase.stockAndPoints.error";
				else if (!stock)
					message = "prisoner.purchase.stock.error";
				else if (!points)
					message = "prisoner.purchase.points.error";
				else if (!zeroOrNegative)
					message = "product.purchase.zeroAndNegativeError";
				else
					message = "prisoner.purchase.error";

				result = this.createEditModelAndView("prisoner/buy", product, prisoner.getPoints(), message);
			} catch (Throwable oops2) {
				result = new ModelAndView("redirect:/product/prisoner/store.do");
			}
		}

		return result;
	}

	private ModelAndView createEditModelAndView(String tiles, List<Product> products, Integer points) {
		ModelAndView result = new ModelAndView(tiles);

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

		result.addObject("products", products);
		result.addObject("points", points);
		result.addObject("prisoner", true);
		result.addObject("store", true);
		result.addObject("locale", locale);
		result.addObject("requestURI", "/product/prisoner/store");

		return result;
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ModelAndView listPurchasedProducts() {
		ModelAndView result;

		try {
			List<Product> products = this.prisonerService.getProductsOfLoggedPrisoner();

			result = this.createEditModelAndView("prisoner/purchasedProducts", products);
			result.addObject("requestURI", "/product/prisoner/all.do");
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}

		return result;
	}

	private ModelAndView createEditModelAndView(String tiles, Product product, Integer points) {
		ModelAndView result = new ModelAndView(tiles);

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

		result.addObject("product", product);
		result.addObject("points", points);
		result.addObject("locale", locale);

		return result;
	}

	private ModelAndView createEditModelAndView(String tiles, List<Product> products) {
		ModelAndView result = new ModelAndView(tiles);

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

		result.addObject("products", products);
		result.addObject("locale", locale);

		return result;
	}

	private ModelAndView createEditModelAndView(String tiles, Product product, Integer points, String message) {
		ModelAndView result = this.createEditModelAndView(tiles, product, points);
		result.addObject("message", message);

		return result;
	}

}
