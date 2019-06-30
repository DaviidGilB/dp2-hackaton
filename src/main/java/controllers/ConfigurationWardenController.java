package controllers;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import domain.Configuration;
import domain.TypeProduct;
import services.ConfigurationService;
import services.TypeProductService;
import services.WardenService;

@Controller
@RequestMapping("/configuration/warden")
public class ConfigurationWardenController extends AbstractController {

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private TypeProductService typeProductService;

	@Autowired
	private WardenService wardenService;

	// Constructors -----------------------------------------------------------

	public ConfigurationWardenController() {
		super();
	}

	// List ---------------------------------------------------------------

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView configurationList() {
		try {
			ModelAndView result;
			Configuration configuration;

			configuration = this.configurationService.getConfiguration();

			result = new ModelAndView("warden/configuration");
			result.addObject("configuration", configuration);
			result.addObject("requestURI", "configuration/warden/list.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/listSpam", method = RequestMethod.GET)
	public ModelAndView spamList() {
		try {
			ModelAndView result;
			Configuration configuration;
			List<String> spamWords;

			configuration = this.configurationService.getConfiguration();
			spamWords = configuration.getSpamWords();

			result = new ModelAndView("spamWords/configuration");
			result.addObject("configuration", configuration);
			result.addObject("spamWords", spamWords);
			result.addObject("requestURI", "configuration/warden/listSpam.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/listTypeProd", method = RequestMethod.GET)
	public ModelAndView typesList() {
		try {
			ModelAndView result;
			Configuration configuration;
			List<TypeProduct> t;
			List<TypeProduct> typesAssigned = this.wardenService.getProductTypesAssigned();
			configuration = this.configurationService.getConfiguration();
			List<TypeProduct> types = this.typeProductService.findAll();

			t = configuration.getTypeProducts();
			t.removeAll(typesAssigned);

			result = new ModelAndView("listTypeProd/configuration");
			result.addObject("configuration", configuration);
			result.addObject("types", types);
			result.addObject("t", t);
			result.addObject("requestURI", "configuration/warden/listTypeProd.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView configurationEdit() {
		try {
			ModelAndView result;

			Configuration configuration = this.configurationService.getConfiguration();

			result = new ModelAndView("warden/editConfiguration");
			result.addObject("configuration", configuration);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/addSpam", method = RequestMethod.GET)
	public ModelAndView addSpam() {
		try {
			ModelAndView result;

			Configuration configuration = this.configurationService.getConfiguration();

			result = new ModelAndView("warden/addSpam");
			result.addObject("configuration", configuration);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/addType", method = RequestMethod.GET)
	public ModelAndView addType() {
		try {
			ModelAndView result;

			Configuration configuration = this.configurationService.getConfiguration();

			result = new ModelAndView("warden/addType");
			result.addObject("configuration", configuration);

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST, params = "save")
	public ModelAndView save(Configuration configuration, BindingResult binding) {
		try {
			ModelAndView result;

			Configuration configurationR = this.configurationService.reconstruct(configuration, binding);

			if (binding.hasErrors())
				result = this.createEditModelAndView(configuration);
			else
				try {
					this.configurationService.saveConfiguration(configurationR);
					result = new ModelAndView("redirect:list.do");
				} catch (Throwable oops) {
					result = this.createEditModelAndView(configuration, "warden.commit.error");
				}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/addSpam", method = RequestMethod.POST, params = "add")
	public ModelAndView saveSpam(@RequestParam String spam, Configuration configuration, BindingResult binding) {
		try {
			ModelAndView result;

			if (binding.hasErrors())
				result = this.createEditModelAndView(configuration);
			else
				try {
					this.configurationService.addSpamWords(spam, binding);
					result = new ModelAndView("redirect:listSpam.do");
				} catch (Throwable oops) {
					result = this.createEditModelAndView(configuration, "warden.commit.error");
				}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/addType", method = RequestMethod.POST, params = "add")
	public ModelAndView saveType(@RequestParam String typeProductEN, @RequestParam String typeProductES,
			Configuration configuration, BindingResult binding) {
		try {
			ModelAndView result;

			if (binding.hasErrors())
				result = this.createEditModelAndView(configuration);
			else
				try {
					this.configurationService.addTypeProducts(typeProductEN, typeProductES, binding);

					result = new ModelAndView("redirect:listTypeProd.do");
				} catch (Throwable oops) {
					result = this.createEditModelAndView(configuration, "warden.commit.error");
				}

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/deleteSpam", method = RequestMethod.GET)
	public ModelAndView deleteSpam(@RequestParam String spamWord) {
		try {
			ModelAndView result;
			Configuration c = this.configurationService.getConfiguration();

			Assert.isTrue(c.getSpamWords().contains(spamWord));
			this.configurationService.deleteSpamWords(spamWord);

			result = new ModelAndView("redirect:listSpam.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/deleteType", method = RequestMethod.GET)
	public ModelAndView deleteType(@RequestParam(required = false) String dataTypeId) {
		try {
			Assert.isTrue(StringUtils.isNumeric(dataTypeId));

			int dataTypeInt = Integer.parseInt(dataTypeId);

			ModelAndView result;

			this.configurationService.deleteTypeProducts(dataTypeInt);

			result = new ModelAndView("redirect:listTypeProd.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

	protected ModelAndView createEditModelAndView(Configuration configuration) {
		ModelAndView result = new ModelAndView("warden/editConfiguration");
		result.addObject("configuration", configuration);
		return result;
	}

	protected ModelAndView createEditModelAndView(Configuration configuration, String message) {
		ModelAndView result = new ModelAndView("warden/editConfiguration");
		result.addObject("configuration", configuration);
		result.addObject("message", message);
		return result;
	}

}
