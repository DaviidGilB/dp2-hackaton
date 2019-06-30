<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>


<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('SOCIALWORKER')">


<form:form modelAttribute="request"
		action="request/socialWorker/reject.do">
		
		<acme:textarea code="request.rejectReason" path="rejectReason"/>
		
		<input type="hidden" name="activityId" value="${activityId}">
		<input type="hidden" name="id" value="${request.id}">
		
		<acme:submit name="save" code="request.save"/>
		
		</form:form>
		
		<acme:cancel url="request/socialWorker/list.do?activityId=${activityId}" code="request.cancel"/>
		
		
</security:authorize>