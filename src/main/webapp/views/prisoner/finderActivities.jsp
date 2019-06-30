<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<%@ page import="java.util.Map"%>

<security:authorize access="hasRole('PRISONER')">

	<form:form modelAttribute="finderActivities"
		action="finderActivities/prisoner/edit.do">


		<form:hidden path="id" />

		<acme:textbox code="finder.keyWord" path="keyWord" />


		<acme:datebox code="finder.maxDate" path="maxDate" />

		<br />

		<acme:datebox code="finder.minDate" path="minDate" />

		<br />

		<acme:submit name="save" code="finder.save" />


	</form:form>

	<br />


	<display:table pagesize="5" name="activities" id="row"
		requestURI="${requestURI}">


		<jstl:choose>

			<jstl:when test="${map[row] != row.maxAssistant}">
				<jstl:set var="color" value="black" />
			</jstl:when>
			<jstl:otherwise>
				<jstl:set var="color" value="red" />
			</jstl:otherwise>

		</jstl:choose>

		<display:column titleKey="activity.title">
			<jstl:out value="${row.title}" />
		</display:column>
		<display:column titleKey="activity.description">
			<jstl:out value="${row.description}" />
		</display:column>
		<display:column titleKey="activity.realizationTime">
			<jstl:out value="${row.realizationDate}" />
		</display:column>
		<display:column titleKey="activity.maxAssistant"
			style="color:${color}">

			<jstl:out value="${map[row]} / ${row.maxAssistant}" />

		</display:column>
		<display:column titleKey="activity.rewardPoints">
			<jstl:out value="${row.rewardPoints}" />
		</display:column>

			<jstl:if test="${map[row] != row.maxAssistant}">
				<display:column titleKey="activity.request">

					<spring:url var="create" value="/request/prisoner/create.do">
						<spring:param name="activityId" value="${row.id}" />
					</spring:url>

					<a href="${create}"><button>
							<spring:message var="createRequest" code="request.createRequest" />
							<jstl:out value="${createRequest}" />
						</button> </a>
				</display:column>
			</jstl:if>


	</display:table>


</security:authorize>