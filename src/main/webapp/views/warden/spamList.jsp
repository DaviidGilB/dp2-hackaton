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

	<display:table pagesize="5" name="spamWords" id="row"
		requestURI="${requestURI}">

		<display:column titleKey="spamWords.title">
			<jstl:out value="${row}" />
		</display:column>
		
		<display:column>
		<spring:url var="deleteUrl" value="/configuration/warden/deleteSpam.do">
		<spring:param name="spamWord" value="${row}" />
			</spring:url>
			<a href="${deleteUrl}" onclick="return confirm('<spring:message code="spam.delete.confirmation" />')">
				<spring:message code="spam.delete" var = "deleteMessage" />
				<jstl:out value="${deleteMessage}"/>
			</a>
		</display:column>

	</display:table>
	
	<acme:cancel code="spam.back"
		url="/configuration/warden/list.do" />
	<acme:cancel code="spam.add"
		url="/configuration/warden/addSpam.do" />

</security:authorize>