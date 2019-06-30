<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags" %>


<form:form modelAttribute="formObjectSocialWorker" action="anonymous/socialWorker/create.do">

	<!-- ELECCIÓN DEL FORMATO DE LA FECHA -->
	<jstl:if test="${locale =='EN'}">
		<jstl:set var="url" value ="anonymous/termsAndConditionsEN.do"/>		
	</jstl:if>
	
	<jstl:if test="${locale =='ES'}">
		<jstl:set var="url" value ="anonymous/termsAndConditionsES.do"/>
	</jstl:if>
	

	<!-- User Account Attributes -->
	<fieldset>
    	<legend><spring:message code="warden.userAccountData" /></legend>
	
		<acme:textbox path="username" code="guard.username" />
		<br />
		
		<acme:password path="password" code="guard.password" />
		<br />
		
		<acme:password path="confirmPassword" code="guard.confirmPassword" />
		<br />
	
	</fieldset>
	<br />
	
	<!-- Actor Attributes -->
	<fieldset>
    	<legend><spring:message code="warden.personalData" /></legend>
    	
    	<acme:textbox path="title" code="socialWorker.title" />
		<br />
		
		<acme:textbox path="name" code="guard.name" />
		<br />
		
		<acme:textbox path="middleName" code="guard.middleName" />
		<br />
		
		<acme:textbox path="surname" code="guard.surname" />
		<br />
		
		<acme:textbox path="photo" code="guard.photo" />
		<br />
		
		
	</fieldset>
	<br />
	
	<!-- TERMS AND CONDITIONS -->
	<fieldset>
    	<legend><spring:message code="warden.termsAndConditions" /></legend>
    
    <form:checkbox path="termsAndConditions" /> 
		<spring:message code="warden.acceptTemsConditions" />
			<a href="${url}" target="_blank"> 
					<spring:message code="warden.termsAndConditions" /> </a>
					<form:errors path="termsAndConditions" cssClass="error" />
	<br />
	</fieldset>
	<br />

	<!-- BOTONES -->	
	<input type="submit" name="save" value="<spring:message code="warden.save" />" /> 
	<acme:cancel url="/" code="warden.cancel" /> 
	
	</form:form>
	
	
	