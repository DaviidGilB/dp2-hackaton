<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>


<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('GUARD')">



<spring:url var="createUrl" value="/visit/report/create.do?visitId={visitId}">
		<spring:param name="visitId" value="${visitId}" />
</spring:url>					

	<form:form modelAttribute="report" action="${createUrl}">
		<!--Hidden Attributes -->
		<form:hidden path="id" />
		<form:hidden path="version" />
		
		<acme:textarea code="report.description" path="description" />
		
		
		<acme:submit name="save" code="visit.save" />

	</form:form>
	
	<acme:cancel url="/visit/guard/list.do" code="report.cancel" />


</security:authorize>