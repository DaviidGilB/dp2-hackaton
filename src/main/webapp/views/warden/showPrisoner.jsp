<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('WARDEN')">
	<!-- Actor Data -->
	<table>
		<tr>
			<td><spring:message code="actor.fullName" />:</td>
			<td><jstl:out
					value="${prisoner.name} ${prisoner.middleName} ${prisoner.surname}" /></td>
		</tr>
		<tr>
			<td><spring:message code="actor.photo" />:</td>
			<td>
	<img src="${prisoner.photo}" alt="${prisoner.photo}"></td>
		</tr>
		<tr>
			<td><spring:message code="prisoner.incomeDate"/>:</td>
			<td><jstl:out value="${prisoner.incomeDate}" /></td>
		</tr>
		<tr>
			<td><spring:message code="prisoner.exitDate"/>:</td>
			<td><jstl:out value="${prisoner.exitDate}" /></td>
		</tr>
		<tr>
			<td><spring:message code="prisoner.points"/>:</td>
			<td><jstl:out value="${prisoner.points}" /></td>
		</tr>
		<tr>
			<td><spring:message code="prisoner.charges"/>:</td>
			<jstl:set value="" var="listOfCharges"/>
			<jstl:set value="1" var="counter"/>
			<jstl:forEach items="${prisoner.charges}" var="charge">
				<jstl:if test="${locale =='EN'}">
					<jstl:set value="${listOfCharges}${charge.titleEnglish}" var="listOfCharges"/>
					<jstl:if test="${size!=counter}">
						<jstl:set value="${listOfCharges}, " var="listOfCharges"/>
						<jstl:set value="${counter + 1}" var="counter"/>
					</jstl:if>
				</jstl:if>
				<jstl:if test="${locale =='ES'}">
					<jstl:set value="${listOfCharges}${charge.titleSpanish}, " var="listOfCharges"/>
					<jstl:if test="${size!=counter}">
						<jstl:set value="${listOfCharges}, " var="listOfCharges"/>
						<jstl:set value="${counter + 1}" var="counter"/>
					</jstl:if>
				</jstl:if>
			</jstl:forEach>
			<td><jstl:out value="${listOfCharges}" /></td>
		</tr>
		<tr>
			<td><spring:message code="prisoner.ticker"/>:</td>
			<td><jstl:out value="${prisoner.ticker}" /></td>
		</tr>
		<tr>
			<td><spring:message code="prisoner.crimeRate"/>:</td>
			<td><jstl:out value="${prisoner.crimeRate}" /></td>
		</tr>
		<tr>
			<td><spring:message code="prisoner.isIsolated"/>:</td>
			<td><jstl:out value="${prisoner.isIsolated}" /></td>
		</tr>
		<tr>
			<td><spring:message code="prisoner.isSuspect"/>:</td>
			<td><jstl:out value="${prisoner.isSuspect}" /></td>
		</tr>
		<tr>
			<td><spring:message code="prisoner.freedom"/>:</td>
			<td><jstl:out value="${prisoner.freedom}" /></td>
		</tr>
	</table>
	
	<acme:cancel url="/report/warden/list.do" code="warden.back" />
</security:authorize>

