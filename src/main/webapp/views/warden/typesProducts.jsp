<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('WARDEN')">

	<display:table pagesize="5" name="types" id="row"
		requestURI="${requestURI}">

		<display:column titleKey="type.en">
			<jstl:out value="${row.typeProductEN}" />
		</display:column>

		<display:column titleKey="type.es">
			<jstl:out value="${row.typeProductES}" />
		</display:column>

		<display:column>
			<jstl:if test="${t.contains(row)}">
				<spring:url var="deleteUrl"
					value="/configuration/warden/deleteType.do">
					<spring:param name="dataTypeId" value="${row.id}" />
				</spring:url>
				<a href="${deleteUrl}"
					onclick="return confirm('<spring:message code="type.delete.confirmation" />')">
					<spring:message code="spam.delete" var="deleteMessage" /> <jstl:out
						value="${deleteMessage}" />
				</a>
			</jstl:if>
		</display:column>

	</display:table>

	<acme:cancel code="spam.back" url="/configuration/warden/list.do" />
	<acme:cancel code="type.add" url="/configuration/warden/addType.do" />

</security:authorize>