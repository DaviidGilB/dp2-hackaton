<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme"  tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('WARDEN')">
			
	<display:table pagesize="5" name="salesmen" id="row" requestURI="${requestURI}" >
		
		<display:column titleKey="salesman.name" >
			<jstl:out value="${row.name}" />
		</display:column>
		<display:column titleKey="salesman.middleName" >
			<jstl:out value="${row.middleName}" />
		</display:column>
		<display:column titleKey="salesman.surname" >
			<jstl:out value="${row.surname}" />
		</display:column>
		
		<display:column titleKey="salesman.photo">
			<a href="${row.photo}">
				<spring:message code="salesman.viewPhoto"/>
			</a>
		</display:column> 
		
		<display:column titleKey="salesman.VATNumber" > 
			<jstl:out value="${row.VATNumber}" />
		</display:column>
		
		<display:column titleKey="salesman.storeName" >
			<jstl:out value="${row.storeName}" />
		</display:column> 
        
	</display:table>
	
</security:authorize>
