<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme"  tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('WARDEN')">

	<form action="configuration/warden/addType.do" name="addType" id="addType" method="post">
		
		<p><spring:message code="type.productEN" /></p>	
		<input type="text" name="typeProductEN" value="${typeProductEN}" required />
		
		<p><spring:message code="type.productES" /></p>
		<input type="text" name="typeProductES" value="${typeProductES}" required />
		<br/>
	
		<input type="submit" name="add" value="<spring:message code="type.addType" />"/> 

		<acme:cancel url="configuration/warden/listTypeProd.do" code="spam.back" /> 
		
	</form>

</security:authorize>
