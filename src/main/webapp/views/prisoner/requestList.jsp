<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme"  tagdir="/WEB-INF/tags"%>

<%@ page import="domain.ActivityStatus" %>

<security:authorize access="hasRole('PRISONER')">

	<spring:message var ="message" code="request.message" />
		<h2>   <jstl:out value="${message}" />   </h2> 

	<display:table pagesize="5" name="requests" id="row" requestURI="${requestURI}" >
	
	<jstl:choose>
		<jstl:when test="${row.status=='PENDING'}">
		
		<jstl:set var="color" value="grey"/>
		
		</jstl:when>
		
		<jstl:when test="${row.status=='APPROVED'}">
		
		<jstl:set var="color" value="green"/>
		
		</jstl:when>
			
		<jstl:when test="${row.status=='REJECTED'}">
		
		<jstl:set var="color" value="red"/>
		
		</jstl:when>
	
	
	
	</jstl:choose>
	
	<display:column titleKey="request.status" style="color:${color}"> 
		<jstl:out value="${row.status}"/>
	</display:column>
	
	<display:column titleKey="request.motivation" style="color:${color}" > 
		<jstl:out value="${row.motivation}"/>
	</display:column>
	
	<display:column titleKey="request.rejectReason" style="color:${color}" > 
		<jstl:out value="${row.rejectReason}"/>
	</display:column>
	
	<display:column titleKey="request.activity" style="color:${color}" > 
		<jstl:out value="${row.activity.title}"/>
	</display:column>
	
	<display:column> 
		<jstl:if test="${row.status=='PENDING'}">
		
		 <spring:url var="deleteUrl" value="/request/prisoner/delete.do">
		    	<spring:param name="requestId" value="${row.id}" />
		 </spring:url>
		
		<a href="${deleteUrl}"><button onclick="return confirm('<spring:message code="request.confirmation" />')" >
		     	<spring:message var ="deleteRequest" code="request.deleteRequest" />
		       	<jstl:out value="${deleteRequest}" />   
		</button> </a>
		
	
		</jstl:if>

	</display:column>
	
	</display:table>
			
			
</security:authorize>