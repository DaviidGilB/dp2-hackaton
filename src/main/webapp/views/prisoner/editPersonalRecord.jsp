    
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('SOCIALWORKER')">

	<form:form modelAttribute="personalRecord" action="curriculum/socialWorker/editPersonalRecord.do">
		
		<!-- Hidden Attributes -->
		<form:hidden path ="id"/>
		<form:hidden path ="version"/>
		
	
		<!-- Full Name -->
		<acme:input code="personalRecord.fullName" path="fullName"/>
		<br />
		
		<!-- Photo -->
		<acme:input code="personalRecord.photo" path="photo"/>
		<br />
		
		<!-- email -->
		<acme:input code="personalRecord.email" path="email"/>
		<br />
		
		<!-- Photo -->
		<acme:input code="personalRecord.phoneNumber" path="phoneNumber"/>
		<br />
		
		<!-- Photo -->
		<acme:input code="personalRecord.urlLinkedInProfile" path="urlLinkedInProfile"/>
		<br />
		
		
		<!-- Buttons -->

		<input type="submit" name="save" value="<spring:message code="button.save" />"/> 
		<acme:cancel url="/curriculum/socialWorker/show.do" code="button.cancel" /> 

	</form:form>

</security:authorize>