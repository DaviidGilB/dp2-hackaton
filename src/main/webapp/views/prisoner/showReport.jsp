<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<%@ page import="java.util.Date" %>
<%@ page import="java.sql.Timestamp"%>
  
<%
java.util.Date utilDate = new java.util.Date();
java.sql.Timestamp now = new java.sql.Timestamp(utilDate.getTime());
%>
<jstl:set var="now" value="<%=now%>"/>

<security:authorize access="hasAnyRole('PRISONER', 'VISITOR', 'GUARD', 'WARDEN')">

	<display:table name="report" id="row" requestURI="${requestURI}" >
		
	<display:column titleKey="report.description" >
		<jstl:out value="${row.description}"/>
	</display:column>
	
	
	<display:column titleKey="report.date" >
			<jstl:out value="${row.date}"/>
	</display:column> 
		
	</display:table>
	
	
	<security:authorize access="hasRole('VISITOR')" >
	<spring:url var="backUrl" value="/visit/visitor/list.do"/>
	
	<a href="${backUrl}">
		<spring:message code="report.back" />
	</a>
	</security:authorize>
	
	<security:authorize access="hasRole('PRISONER')" >
	<spring:url var="backUrl" value="/visit/prisoner/list.do"/>
	
	<a href="${backUrl}">
		<spring:message code="report.back" />
	</a>
	</security:authorize>
	
	<security:authorize access="hasRole('GUARD')" >
	<spring:url var="backUrl" value="/visit/guard/list.do"/>
	
	<a href="${backUrl}">
		<spring:message code="report.back" />
	</a>
	</security:authorize>

</security:authorize>