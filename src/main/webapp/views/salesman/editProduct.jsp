    
<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('SALESMAN')">

	<form:form modelAttribute="product" action="product/salesman/edit.do">
		
		<!-- Hidden Attributes -->
		<form:hidden path ="id"/>
		<form:hidden path ="version"/>
		
	
		<jstl:choose>
			<jstl:when test="${locale == 'EN'}">
				<acme:select items="${productType}" itemLabel="typeProductEN" code="product.type" path="type"/>
			</jstl:when><jstl:otherwise>
				<acme:select items="${productType}" itemLabel="typeProductES" code="product.type" path="type"/>
			</jstl:otherwise>
		</jstl:choose>
		<br />
		<!-- Name -->
		<acme:input code="product.name" path="name"/>
		<br />
		
		<!-- Description -->
		<acme:input code="product.description" path="description"/>
		<br />
		
		<!-- Price -->
		<acme:selectNumber  max ="2147483647" min="1" code="product.price" path="price"/>
		<br />
		
		<!-- Stock -->
		<acme:selectNumber max ="2147483647" min="1" code="product.stock" path="stock"/>
		<br />
		
		<!-- Status -->
		<acme:boolean code="product.status" trueCode="product.draft" falseCode="product.final" path="isDraftMode"/>	
		<br />
		
		
		<!-- Buttons -->

		<input type="submit" name="save" value="<spring:message code="button.save" />"/>
		<jstl:if test="${product.id != 0}">
			<input type="submit" name="delete" value="<spring:message code="button.delete" />" onclick="return confirm('<spring:message code="delete.confirm" />')"/>
		</jstl:if>
		<acme:cancel url="/product/salesman/list.do" code="button.cancel" /> 

	</form:form>

</security:authorize>