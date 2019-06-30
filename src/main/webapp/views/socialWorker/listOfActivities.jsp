<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('SOCIALWORKER')">

	<display:table pagesize="5" name="activities" id="row"
		requestURI="${requestURI}">

		<display:column titleKey="activity.title">
			<jstl:out value="${row.title}" />
		</display:column>
		<display:column titleKey="activity.description">
			<jstl:out value="${row.description}" />
		</display:column>
		<display:column titleKey="activity.realizationDate">
			<jstl:out value="${row.realizationDate}" />
		</display:column>	
		<display:column titleKey="activity.maxAssistant">
			<jstl:out value="${row.maxAssistant}" />
		</display:column>	
		<display:column titleKey="activity.rewardPoints">
			<jstl:out value="${row.rewardPoints}" />
		</display:column>	
		
			<display:column>
			<spring:url var="requests"
					value="/request/socialWorker/list.do">
					<spring:param name="activityId" value="${row.id}" />
				</spring:url>
				<a href="${requests}"> <spring:message
						code="activity.requests" /></a>
		</display:column>
		
		<display:column>
			<spring:url var="assistants"
					value="/activity/socialworker/listAssistants.do">
					<spring:param name="activityId" value="${row.id}" />
				</spring:url>
				<a href="${assistants}"> <spring:message
						code="activity.assistants" /></a>
		</display:column>
		
		<display:column>
			<jstl:if test="${row.isFinalMode == false}">
				<spring:url var="edit"
					value="/activity/socialworker/edit.do">
					<spring:param name="activityId" value="${row.id}" />
				</spring:url>
				<a href="${edit}"> <spring:message
						code="activity.edit" /></a>
			</jstl:if>
		</display:column>
		
		<display:column>
			<jstl:if test="${row.isFinalMode == false}">
				<spring:url var="delete"
					value="/activity/socialworker/delete.do">
					<spring:param name="activityId" value="${row.id}" />
				</spring:url>
				<a href="${delete}" onclick="return confirm('<spring:message code="delete.confirm" />')"> <spring:message
						code="activity.delete" /></a>
			</jstl:if>
		</display:column>

	</display:table>
	
	<acme:cancel url="/activity/socialworker/create.do" code="activity.create" /> 

</security:authorize>
