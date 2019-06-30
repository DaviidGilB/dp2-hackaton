
package controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import domain.Curriculum;
import domain.EducationRecord;
import domain.Guard;
import domain.MiscellaneousRecord;
import domain.PersonalRecord;
import domain.Product;
import domain.ProfessionalRecord;
import domain.SalesMan;
import domain.SocialWorker;
import domain.Visitor;
import domain.Warden;
import services.GuardService;
import services.SalesManService;
import services.SocialWorkerService;
import services.VisitorService;
import services.WardenService;

@Controller
@RequestMapping("/export")
public class ExportDataController {

	@Autowired
	public WardenService wardenService;

	@Autowired
	public GuardService guardService;

	@Autowired
	public SalesManService salesManService;

	@Autowired
	public VisitorService visitorService;

	@Autowired
	public SocialWorkerService socialWorkerService;

	@RequestMapping(value = "/warden", method = RequestMethod.GET)
	public @ResponseBody ModelAndView export(
			@RequestParam(value = "id", defaultValue = "-1", required = false) String id, HttpServletResponse response)
			throws IOException {

		ModelAndView result;

		try {
			Assert.isTrue(StringUtils.isNumeric(id));
			int idInt = Integer.parseInt(id);

			this.wardenService.loggedAsWarden();

			Warden warden = new Warden();
			warden = this.wardenService.findOne(idInt);

			// Defines un StringBuilder para construir tu string
			StringBuilder sb = new StringBuilder();

			// linea
			sb.append("Personal data:").append(System.getProperty("line.separator"));
			sb.append("- Name: " + warden.getName()).append(System.getProperty("line.separator"));
			sb.append("- Middle name: " + warden.getMiddleName()).append(System.getProperty("line.separator"));
			sb.append("- Surname: " + warden.getSurname()).append(System.getProperty("line.separator"));
			sb.append("- Photo: " + warden.getPhoto()).append(System.getProperty("line.separator"));

			// Defines el nombre del archivo y la extension
			response.setContentType("text/txt");
			response.setHeader("Content-Disposition", "attachment;filename=exportDataWarden.txt");

			// Con estos comandos permites su descarga cuando clickas
			ServletOutputStream outStream = response.getOutputStream();
			outStream.println(sb.toString());
			outStream.flush();
			outStream.close();

			// El return no llega nunca, es del metodo viejo
			result = new ModelAndView("redirect:/");

		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}

		return result;
	}

	@RequestMapping(value = "/guard", method = RequestMethod.GET)
	public @ResponseBody ModelAndView exportGuard(
			@RequestParam(value = "id", defaultValue = "-1", required = false) String id, HttpServletResponse response)
			throws IOException {

		ModelAndView result;
		try {
			Assert.isTrue(StringUtils.isNumeric(id));
			int idInt = Integer.parseInt(id);

			this.guardService.loggedAsGuard();

			Guard guard = new Guard();
			guard = this.guardService.findOne(idInt);

			// Defines un StringBuilder para construir tu string
			StringBuilder sb = new StringBuilder();

			// linea
			sb.append("Personal data:").append(System.getProperty("line.separator"));
			sb.append("- Name: " + guard.getName()).append(System.getProperty("line.separator"));
			sb.append("- Middle name: " + guard.getMiddleName()).append(System.getProperty("line.separator"));
			sb.append("- Surname: " + guard.getSurname()).append(System.getProperty("line.separator"));
			sb.append("- Photo: " + guard.getPhoto()).append(System.getProperty("line.separator"));
			sb.append("- Phone: " + guard.getPhone()).append(System.getProperty("line.separator"));
			sb.append("- Email: " + guard.getEmail()).append(System.getProperty("line.separator"));

			// Defines el nombre del archivo y la extension
			response.setContentType("text/txt");
			response.setHeader("Content-Disposition", "attachment;filename=exportDataGuard.txt");

			// Con estos comandos permites su descarga cuando clickas
			ServletOutputStream outStream = response.getOutputStream();
			outStream.println(sb.toString());
			outStream.flush();
			outStream.close();

			// El return no llega nunca, es del metodo viejo
			result = new ModelAndView("redirect:/");

		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}

		return result;
	}

	@RequestMapping(value = "/salesman", method = RequestMethod.GET)
	public @ResponseBody ModelAndView exportSalesman(
			@RequestParam(value = "id", defaultValue = "-1", required = false) String id, HttpServletResponse response)
			throws IOException {
		ModelAndView result;
		try {
			Assert.isTrue(StringUtils.isNumeric(id));
			int idInt = Integer.parseInt(id);

			this.salesManService.loggedAsSalesMan();

			SalesMan salesman = new SalesMan();
			salesman = this.salesManService.findOne(idInt);

			// Defines un StringBuilder para construir tu string
			StringBuilder sb = new StringBuilder();

			// linea
			sb.append("Personal data:").append(System.getProperty("line.separator"));
			sb.append("- Name: " + salesman.getName()).append(System.getProperty("line.separator"));
			sb.append("- Middle name: " + salesman.getMiddleName()).append(System.getProperty("line.separator"));
			sb.append("- Surname: " + salesman.getSurname()).append(System.getProperty("line.separator"));
			sb.append("- Photo: " + salesman.getPhoto()).append(System.getProperty("line.separator"));
			sb.append("- VAT Number: " + salesman.getVATNumber()).append(System.getProperty("line.separator"));
			sb.append("- Store name: " + salesman.getStoreName()).append(System.getProperty("line.separator"));
			sb.append("- Points: " + salesman.getPoints()).append(System.getProperty("line.separator"));
			sb.append(System.getProperty("line.separator"));

			List<Product> products = salesman.getProducts();
			sb.append("List of Products").append(System.getProperty("line.separator"));
			sb.append("---").append(System.getProperty("line.separator"));
			for (Product p : products) {
				sb.append("Product '" + p.getName() + "':").append(System.getProperty("line.separator"));
				sb.append("- Description: " + p.getDescription()).append(System.getProperty("line.separator"));
				sb.append("- Price: " + p.getPrice()).append(System.getProperty("line.separator"));
				sb.append("- Stock: " + p.getStock()).append(System.getProperty("line.separator"));
				sb.append("- �Is draft mode?: " + p.getIsDraftMode()).append(System.getProperty("line.separator"));
				sb.append(System.getProperty("line.separator"));
			}

			// Defines el nombre del archivo y la extension
			response.setContentType("text/txt");
			response.setHeader("Content-Disposition", "attachment;filename=exportDataSalesman.txt");

			// Con estos comandos permites su descarga cuando clickas
			ServletOutputStream outStream = response.getOutputStream();
			outStream.println(sb.toString());
			outStream.flush();
			outStream.close();
			result = new ModelAndView("redirect:/");

			// El return no llega nunca, es del metodo viejo
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}

		return result;
	}

	@RequestMapping(value = "/visitor", method = RequestMethod.GET)
	public @ResponseBody ModelAndView exportVisitor(
			@RequestParam(value = "id", defaultValue = "-1", required = false) String id, HttpServletResponse response)
			throws IOException {

		ModelAndView result;
		try {
			Assert.isTrue(StringUtils.isNumeric(id));
			int idInt = Integer.parseInt(id);

			this.visitorService.loggedAsVisitor();

			Visitor visitor = new Visitor();
			visitor = this.visitorService.findOne(idInt);

			// Defines un StringBuilder para construir tu string
			StringBuilder sb = new StringBuilder();

			// linea
			sb.append("Personal data:").append(System.getProperty("line.separator"));

			sb.append("- Name: " + visitor.getName()).append(System.getProperty("line.separator"));
			sb.append("- Middle name: " + visitor.getMiddleName()).append(System.getProperty("line.separator"));
			sb.append("- Surname: " + visitor.getSurname()).append(System.getProperty("line.separator"));
			sb.append("- Photo: " + visitor.getPhoto()).append(System.getProperty("line.separator"));
			sb.append("- Email: " + visitor.getEmail()).append(System.getProperty("line.separator"));
			sb.append("- Emergency email: " + visitor.getEmergencyEmail()).append(System.getProperty("line.separator"));
			sb.append("- Phone: " + visitor.getPhoneNumber()).append(System.getProperty("line.separator"));
			sb.append("- Address: " + visitor.getAddress()).append(System.getProperty("line.separator"));

			// Defines el nombre del archivo y la extension
			response.setContentType("text/txt");
			response.setHeader("Content-Disposition", "attachment;filename=exportDataVisitor.txt");

			// Con estos comandos permites su descarga cuando clickas
			ServletOutputStream outStream = response.getOutputStream();
			outStream.println(sb.toString());
			outStream.flush();
			outStream.close();

			// El return no llega nunca, es del metodo viejo
			result = new ModelAndView("redirect:/");

			// El return no llega nunca, es del metodo viejo
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}

		return result;

	}

	@RequestMapping(value = "/socialWorker", method = RequestMethod.GET)
	public @ResponseBody ModelAndView exportSocialWorker(
			@RequestParam(value = "id", defaultValue = "-1", required = false) String id, HttpServletResponse response)
			throws IOException {

		ModelAndView result;
		try {
			Assert.isTrue(StringUtils.isNumeric(id));
			int idInt = Integer.parseInt(id);

			this.socialWorkerService.loggedAsSocialWorker();

			SocialWorker socialWorker = new SocialWorker();
			socialWorker = this.socialWorkerService.findOne(idInt);

			// Defines un StringBuilder para construir tu string
			StringBuilder sb = new StringBuilder();

			// linea
			sb.append("Personal data:").append(System.getProperty("line.separator"));

			sb.append("- Name: " + socialWorker.getName()).append(System.getProperty("line.separator"));
			sb.append("- Middle name: " + socialWorker.getMiddleName()).append(System.getProperty("line.separator"));
			sb.append("- Surname: " + socialWorker.getSurname()).append(System.getProperty("line.separator"));
			sb.append("- Photo: " + socialWorker.getPhoto()).append(System.getProperty("line.separator"));
			sb.append("- Title: " + socialWorker.getTitle()).append(System.getProperty("line.separator"));

			sb.append("").append(System.getProperty("line.separator"));

			if (socialWorker.getCurriculum() != null) {
				Curriculum curriculum = socialWorker.getCurriculum();
				PersonalRecord personalRecord = curriculum.getPersonalRecord();
				List<MiscellaneousRecord> miscellaneousRecords = curriculum.getMiscellaneousRecords();
				List<EducationRecord> educationRecords = curriculum.getEducationRecords();
				List<ProfessionalRecord> professionalRecords = curriculum.getProfessionalRecords();

				sb.append("Curriculum: ").append(System.getProperty("line.separator"));

				sb.append("").append(System.getProperty("line.separator"));

				sb.append("- Personal Record: ").append(System.getProperty("line.separator"));

				sb.append("").append(System.getProperty("line.separator"));

				sb.append("-Full Name: " + personalRecord.getFullName()).append(System.getProperty("line.separator"));
				sb.append("-Phone Number: " + personalRecord.getPhoneNumber())
						.append(System.getProperty("line.separator"));
				sb.append("-Email: " + personalRecord.getEmail()).append(System.getProperty("line.separator"));
				sb.append("-Photo: " + personalRecord.getPhoto()).append(System.getProperty("line.separator"));
				sb.append("-Linkedin Profile link: " + personalRecord.getUrlLinkedInProfile())
						.append(System.getProperty("line.separator"));

				sb.append("").append(System.getProperty("line.separator"));
				sb.append("Education Records: ").append(System.getProperty("line.separator"));

				for (int i = 0; i < educationRecords.size(); i++) {
					sb.append("").append(System.getProperty("line.separator"));
					sb.append("Education Record " + i + 1 + ": ").append(System.getProperty("line.separator"));
					sb.append("-Title: " + educationRecords.get(i).getTitle())
							.append(System.getProperty("line.separator"));
					sb.append("-Link: " + educationRecords.get(i).getLink())
							.append(System.getProperty("line.separator"));
					sb.append("-Institution: " + educationRecords.get(i).getInstitution())
							.append(System.getProperty("line.separator"));
					sb.append("-Start Date: " + educationRecords.get(i).getStartDateStudy())
							.append(System.getProperty("line.separator"));
					sb.append("-End Date: " + educationRecords.get(i).getEndDateStudy())
							.append(System.getProperty("line.separator"));
				}
				sb.append("").append(System.getProperty("line.separator"));
				sb.append("Professional Records: ").append(System.getProperty("line.separator"));

				for (int i = 0; i < professionalRecords.size(); i++) {
					sb.append("").append(System.getProperty("line.separator"));
					sb.append("Education Record " + i + 1 + ": ").append(System.getProperty("line.separator"));
					sb.append("-Comapny Name: " + professionalRecords.get(i).getNameCompany())
							.append(System.getProperty("line.separator"));
					sb.append("-Attachment link: " + professionalRecords.get(i).getLinkAttachment())
							.append(System.getProperty("line.separator"));
					sb.append("-Role: " + professionalRecords.get(i).getRole())
							.append(System.getProperty("line.separator"));
					sb.append("-Start date: " + professionalRecords.get(i).getStartDate())
							.append(System.getProperty("line.separator"));
					sb.append("-End date: " + professionalRecords.get(i).getEndDate())
							.append(System.getProperty("line.separator"));
				}
				sb.append("").append(System.getProperty("line.separator"));
				sb.append("Miscellaneous Records: ").append(System.getProperty("line.separator"));

				for (int i = 0; i < miscellaneousRecords.size(); i++) {
					sb.append("").append(System.getProperty("line.separator"));
					sb.append("Miscellaneous Record " + i + 1 + ": ").append(System.getProperty("line.separator"));
					sb.append("-Title: " + miscellaneousRecords.get(i).getTitle())
							.append(System.getProperty("line.separator"));
					sb.append("-Attachment link: " + miscellaneousRecords.get(i).getLinkAttachment())
							.append(System.getProperty("line.separator"));

				}
			}

			// Defines el nombre del archivo y la extension
			response.setContentType("text/txt");
			response.setHeader("Content-Disposition", "attachment;filename=exportDataSocialWorker.txt");

			// Con estos comandos permites su descarga cuando clickas
			ServletOutputStream outStream = response.getOutputStream();
			outStream.println(sb.toString());
			outStream.flush();
			outStream.close();

			// El return no llega nunca, es del metodo viejo
			result = new ModelAndView("redirect:/");

			// El return no llega nunca, es del metodo viejo
		} catch (Throwable oops) {
			result = new ModelAndView("redirect:/");
		}

		return result;
	}

}
