<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<security:authorize access="hasRole('WARDEN')">

<display:table pagesize="5" name="configuration" id="row" class="displaytag" 
					requestURI="configuration/warden/list.do">
					
	<display:column titleKey="configuration.finderResult" > 
		<jstl:out value="${row.finderResult}" />
	</display:column>
					
	<display:column titleKey="configuration.minFinderResults" >
		<jstl:out value="${row.minFinderResults}" />
	</display:column>
	
	<display:column titleKey="configuration.maxFinderResults" >
		<jstl:out value="${row.maxFinderResults}" />
	</display:column>
	
	<display:column titleKey="configuration.timeFinderPrisoner" > 
		<jstl:out value="${row.timeFinderPrisoners}" />
	</display:column>
	
	<display:column titleKey="configuration.timeFinderActivities" > 
		<jstl:out value="${row.timeFinderActivities}" />
	</display:column>
					
	<display:column titleKey="configuration.spainTelephoneCode" >
		<jstl:out value="${row.spainTelephoneCode}" />
	</display:column>

	<display:column titleKey="configuration.welcomeMessageEnglish" >
		<jstl:out value="${row.welcomeMessageEnglish}" />
	</display:column>
	
	<display:column titleKey="configuration.welcomeMessageSpanish" >
		<jstl:out value="${row.welcomeMessageSpanish}" />
	</display:column>
	
	<display:column titleKey="configuration.systemName" >
		<jstl:out value="${row.systemName}" />
	</display:column>
	
	<display:column titleKey="configuration.imageURL" >
		<jstl:out value="${row.imageURL}" />
	</display:column>
	
</display:table>

<input type="button" onclick="javascript:relativeRedir('configuration/warden/edit.do');"  value="<spring:message code="configuration.edit.button"/>" />	
<input type="button" onclick="javascript:relativeRedir('configuration/warden/listSpam.do');"  value="<spring:message code="configuration.spam.button"/>" />	
<input type="button" onclick="javascript:relativeRedir('configuration/warden/listTypeProd.do');"  value="<spring:message code="configuration.typeProd.button"/>" />

</security:authorize>
