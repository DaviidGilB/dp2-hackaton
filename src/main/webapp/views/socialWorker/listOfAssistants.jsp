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

	<display:table pagesize="5" name="prisoners" id="row"
		requestURI="${requestURI}">

		<display:column titleKey="activity.name">
			<jstl:out value="${row.name}" />
		</display:column>
		<display:column titleKey="activity.middleName">
			<jstl:out value="${row.middleName}" />
		</display:column>
		<display:column titleKey="activity.surname">
			<jstl:out value="${row.surname}" />
		</display:column>	

	</display:table>
	
	<acme:cancel code="spam.back"
		url="/activity/socialworker/list.do" />

</security:authorize>
