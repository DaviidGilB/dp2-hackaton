<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags" %>


<security:authorize access="hasRole('WARDEN')">

<form:form modelAttribute="prisoner" action="prisoner/warden/addCharge.do">

	<jstl:if test="${locale =='EN'}">
	<jstl:set var="chargesLanguage" value ="titleEnglish"/>	
	</jstl:if>
	
	<jstl:if test="${locale =='ES'}">
	<jstl:set var="chargesLanguage" value ="titleSpanish"/>	
	</jstl:if>
	<!-- Actor Attributes -->
	<fieldset>
    	<legend><spring:message code="prisoner.charges.select" /></legend>
		
		<acme:selectMandatory path="charges" code="prisoner.charges" items="${charges}" itemLabel="${chargesLanguage}"/>
		
		<input type="hidden" id="prisonerId" name="prisonerId" value="${prisoner.id}">
		<br />
	</fieldset>
	<br />

	<!-- BOTONES -->	
	<input type="submit" name="save" value="<spring:message code="warden.edit" />"/> 
	<acme:cancel url="/anonymous/prisoner/list.do" code="warden.cancel" /> 
	
	</form:form>
	
</security:authorize>