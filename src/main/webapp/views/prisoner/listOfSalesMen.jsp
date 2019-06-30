<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme"  tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('PRISONER')">
			
	<display:table pagesize="5" name="salesMen" id="row" requestURI="${requestURI}" >
		
		<display:column property="name" titleKey="salesman.name" />
		<display:column property="middleName" titleKey="salesman.middleName" />
		<display:column property="surname" titleKey="salesman.surname" />
		
		<display:column titleKey="salesman.photo">
			<a href="${row.photo}">
				<spring:message code="salesman.viewPhoto"/>
			</a>
		</display:column> 
		
		<display:column titleKey="salesman.VATNumber">
			<jstl:out value="${row.VATNumber}"/>
		</display:column> 
		
		<display:column titleKey="salesman.storeName">
			<jstl:out value="${row.storeName}"/>
		</display:column> 
		
		<display:column titleKey="salesman.products" >
				<spring:url var="productsUrl"
					value="/product/prisoner/list.do?salesmanId={salesmanId}">
					<spring:param name="salesmanId" value="${row.id}" />
				</spring:url>
			<a href="${productsUrl}"><spring:message code="salesman.viewProducts" /></a>
		</display:column> 
		
        
	</display:table>
	
</security:authorize>
