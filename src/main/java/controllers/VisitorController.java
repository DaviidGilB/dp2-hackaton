
package controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import services.PrisonerService;
import domain.Prisoner;
import domain.Visitor;

@Controller
@RequestMapping("/visitor")
public class VisitorController extends AbstractController {

	@Autowired
	private PrisonerService	prisonerService;


	public VisitorController() {
		super();
	}

	// Listar Visitantes del prisionero logueado
	@RequestMapping(value = "/prisoner/list", method = RequestMethod.GET)
	public ModelAndView listMyVisitors() {

		try {
			ModelAndView result;
			List<Visitor> visitors;

			Prisoner prisoner = this.prisonerService.loggedPrisoner();

			String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();

			visitors = this.prisonerService.getVisitorsToCreateVisit(prisoner);

			result = new ModelAndView("visitor/prisoner/list");
			result.addObject("visitors", visitors);
			result.addObject("locale", locale);
			result.addObject("requestURI", "visitor/prisoner/list.do");

			return result;
		} catch (Throwable oops) {
			return new ModelAndView("redirect:/");
		}
	}

}
