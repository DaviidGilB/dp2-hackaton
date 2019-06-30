<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('SOCIALWORKER')">

<form:form modelAttribute="activity" action="activity/socialworker/save.do">
		
		<!-- Hidden Attributes -->
		<form:hidden path ="id"/>
		<form:hidden path ="version"/>
		
	
		<acme:input code="activity.title" path="title"/>
		<br />
		
		<acme:input code="activity.description" path="description"/>
		<br />
		
		<acme:datebox code="activity.realizationDate" path="realizationDate"/>
		<br />
		
		<acme:input code="activity.maxAssistant" path="maxAssistant"/>
		<br />
		
		<acme:input code="activity.rewardPoints" path="rewardPoints"/>
		<br />
		
		<acme:boolean code="activity.isFinalMode" trueCode="activity.final" falseCode="activity.draft" path="isFinalMode"/>	
		<br />
		
		<input type="submit" name="save" value="<spring:message code="button.save" />"/>
		<acme:cancel url="/activity/socialworker/list.do" code="button.cancel" /> 

	</form:form>

</security:authorize>