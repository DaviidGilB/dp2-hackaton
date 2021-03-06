/*
 * WelcomeController.java
 *
 * Copyright (C) 2019 Universidad de Sevilla
 *
 * The use of this project is hereby constrained to the conditions of the
 * TDG Licence, a copy of which you may download from
 * http://www.tdg-seville.info/License.html
 */

package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import domain.Prisoner;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import services.ActorService;
import services.ConfigurationService;
import services.PrisonerService;

@Controller
@RequestMapping("/welcome")
public class WelcomeController extends AbstractController {

	@Autowired
	ConfigurationService configurationService;

	@Autowired
	ActorService actorService;

	@Autowired
	PrisonerService prisonerService;

	// Constructors -----------------------------------------------------------

	public WelcomeController() {
		super();
	}

	// Index ------------------------------------------------------------------

	@RequestMapping(value = "/index")
	public ModelAndView index(@RequestParam(required = false, defaultValue = "John Doe") String name,
			HttpServletRequest request) {
		ModelAndView result;
		SimpleDateFormat formatter;
		String moment;

		formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		moment = formatter.format(new Date());

		String welcomeMessage;
		String systemName = this.configurationService.getConfiguration().getSystemName();
		UserAccount userAccount;
		String username;
		Boolean crimRatePositive = false;

		try {
			userAccount = LoginService.getPrincipal();
			username = userAccount.getUsername();
		} catch (Exception oops) {
			username = "";
		}

		if (this.actorService.loggedAsActorBoolean()) {
			UserAccount userAccountPrisoner = LoginService.getPrincipal();
			String usernamePrisoner = userAccountPrisoner.getUsername();
			List<Authority> authorities = (List<Authority>) userAccountPrisoner.getAuthorities();
			if (authorities.get(0).toString().equals("PRISONER")) {
				Prisoner prisoner = this.prisonerService.getPrisonerByUsername(usernamePrisoner);
				if (prisoner.getCrimeRate() > -0.5)
					crimRatePositive = true;
			}

		}

		String imageURL = this.configurationService.getConfiguration().getImageURL();

		request.getSession().setAttribute("imageURL", imageURL);

		request.getSession().setAttribute("crimRatePositive", crimRatePositive);

		String locale = LocaleContextHolder.getLocale().getLanguage().toUpperCase();
		if (locale.equals("EN"))
			welcomeMessage = this.configurationService.getConfiguration().getWelcomeMessageEnglish();
		else
			welcomeMessage = this.configurationService.getConfiguration().getWelcomeMessageSpanish();

		result = new ModelAndView("welcome/index");
		result.addObject("name", name);
		result.addObject("username", username);
		result.addObject("moment", moment);
		result.addObject("welcomeMessage", welcomeMessage);
		result.addObject("systemName", systemName);

		return result;
	}
}
