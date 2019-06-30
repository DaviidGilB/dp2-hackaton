    
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('SOCIALWORKER')">

	<form:form modelAttribute="educationRecord" action="curriculum/socialWorker/editEducationRecord.do">
		
		<!-- Hidden Attributes -->
		<form:hidden path ="id"/>
		<form:hidden path ="version"/>
		
	
		<!-- Title -->
		<acme:input code="educationRecord.title" path="title"/>
		<br />
		
		<!-- Institution -->
		<acme:input code="educationRecord.institution" path="institution"/>
		<br />
		
		<!-- Link -->
		<acme:input code="educationRecord.attachment" path="link"/>
		<br />
		
		<!-- StartDateStudy -->
		<acme:datebox2 code="educationRecord.startDateStudy" path="startDateStudy"/>
		<br />
		
		<!-- EndDateStudy -->
		<acme:datebox2 code="educationRecord.endDateStudy" path="endDateStudy"/>
		<br />
		
		
		<!-- Buttons -->

		<input type="submit" name="save" value="<spring:message code="button.save" />"/>
		<jstl:if test="${educationRecord.id > 0}">
		<input type="submit" name="delete" value="<spring:message code="button.delete" />" onclick="return confirm('<spring:message code="delete.confirm" />')"/>
		</jstl:if>
		<acme:cancel url="/curriculum/socialWorker/show.do" code="button.cancel" /> 

	</form:form>

</security:authorize>