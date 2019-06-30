    
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('SOCIALWORKER')">

	<form:form modelAttribute="professionalRecord" action="curriculum/socialWorker/editProfessionalRecord.do">
		
		<!-- Hidden Attributes -->
		<form:hidden path ="id"/>
		<form:hidden path ="version"/>
		
	
		<!-- Title -->
		<acme:input code="professionalRecord.nameCompany" path="nameCompany"/>
		<br />
		
		<!-- Institution -->
		<acme:input code="professionalRecord.role" path="role"/>
		<br />
		
		<!-- Link -->
		<acme:input code="educationRecord.attachment" path="linkAttachment"/>
		<br />
		
		<!-- StartDateStudy -->
		<acme:datebox2 code="educationRecord.startDateStudy" path="startDate"/>
		<br />
		
		<!-- EndDateStudy -->
		<acme:datebox2 code="educationRecord.endDateStudy" path="endDate"/>
		<br />
		
		
		<!-- Buttons -->

		<input type="submit" name="save" value="<spring:message code="button.save" />"/>
		<jstl:if test="${professionalRecord.id > 0}">
		<input type="submit" name="delete" value="<spring:message code="button.delete" />" onclick="return confirm('<spring:message code="delete.confirm" />')"/>
		</jstl:if>
		<acme:cancel url="/curriculum/socialWorker/show.do" code="button.cancel" /> 

	</form:form>

</security:authorize>