<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>		
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<security:authorize access="hasRole('WARDEN')">

<form:form action="configuration/warden/save.do" modelAttribute="configuration">
		<!-- Hidden Attributes -->
		<form:hidden path="id"/>
		<form:hidden path="version" />
		
		<acme:input code="configuration.finderResult" path="finderResult"/>
		<br/>
		<acme:input code="configuration.minFinderResults" path="minFinderResults"/>
		<br/>
		<acme:input code="configuration.maxFinderResults" path="maxFinderResults"/>
		<br/>
		<acme:input code="configuration.timeFinderPrisoner" path="timeFinderPrisoners"/>
		<br/>
		<acme:input code="configuration.timeFinderActivities" path="timeFinderActivities"/>
		<br/>
		<acme:input code="configuration.spainTelephoneCode" path="spainTelephoneCode"/>
		<br/>
		<acme:input code="configuration.welcomeMessageEnglish" path="welcomeMessageEnglish"/>
		<br/>
		<acme:input code="configuration.welcomeMessageSpanish" path="welcomeMessageSpanish"/>
		<br/>
		<acme:input code="configuration.systemName" path="systemName"/>
		<br/>
		<acme:input code="configuration.imageURL" path="imageURL"/>
		<br/>
		<acme:submit name="save" code="configuration.save.button"/>
		<br/>
		<acme:cancel url="/configuration/warden/list.do" code="configuration.cancel.button"/>
		
</form:form>

</security:authorize>