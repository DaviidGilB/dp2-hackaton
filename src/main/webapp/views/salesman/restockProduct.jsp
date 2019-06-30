    
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('SALESMAN')">

	<form:form modelAttribute="product" action="product/salesman/restock.do">
		
		<!-- Hidden Attributes -->
		<form:hidden path ="id"/>
		
	

		
		<!-- Stock -->
		<acme:selectNumber max ="2147483647" min="1" code="product.stock" path="stock"/>
		<br />
		
		
		
		<!-- Buttons -->

		<input type="submit" name="save" value="<spring:message code="button.save" />"/>
		<acme:cancel url="/product/salesman/list.do" code="button.cancel" /> 

	</form:form>

</security:authorize>