<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>


<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('VISITOR')">

	

	<form:form modelAttribute="visit"
		action="visit/visitor/create.do">
		<!--Hidden Attributes -->
		<form:hidden path="id" />
		<form:hidden path="version" />
		<form:hidden path="prisoner" />
		
		<acme:datebox code="visit.date" path="date"/>
		<acme:textbox code="visit.description" path="description" />
		
		<spring:message code="visit.prisoner"/> <b>
													<jstl:out value="${prisoner.name} "/>
													<jstl:out value="${prisoner.middleName}"/>
													<jstl:out value="${prisoner.surname}"/>
												</b>
		<acme:radioSuperior items="${reasons}" code="visit.reason" path="reason"/>
		
		<acme:submit name="save" code="visit.save" />

	</form:form>
	
	<jstl:choose>
	
	<jstl:when test="${finder}">
	
		<acme:cancel url="/finder/visitor/list.do" code="visit.cancel" />
	
	</jstl:when>
	
	<jstl:otherwise>
		
	<acme:cancel url="/anonymous/prisoner/list.do" code="visit.cancel" />
	
	</jstl:otherwise>
	
	
	</jstl:choose>



</security:authorize>