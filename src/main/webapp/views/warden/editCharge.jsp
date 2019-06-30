<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags" %>


<security:authorize access="hasRole('WARDEN')">

<form:form modelAttribute="charge" action="warden/charge/editCharge.do">
	
	<form:hidden path="id"/>
	<form:hidden path="version"/>
	
	<!-- Actor Attributes -->
	
		
	<acme:textbox path="titleSpanish" code="charge.titleSpanish" />
	<br />
	
	<acme:textbox path="titleEnglish" code="charge.titleEnglish" />
	<br />
	
	<acme:selectNumber min="0" path="year" code="charge.year" />
	<br />
	
	<acme:selectNumber min="0" path="month" code="charge.month" />
	<br />	
	
	
	<acme:boolean code="charge.mode" trueCode="product.draft" falseCode="product.final" path="isDraftMode"/>	
	<br />
	

	<!-- BOTONES -->	
	<input type="submit" name="save" value="<spring:message code="warden.edit" />"/> 
	
	
	</form:form>
	
	<acme:cancel url="/warden/charge/listAll.do" code="warden.cancel" /> 
	
</security:authorize>