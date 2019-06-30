<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>

	<!-- Actor Data -->
	<table>
		<tr>
			<td><spring:message code="actor.fullName" />:</td>
			<td><jstl:out
					value="${actor.name} ${actor.middleName} ${actor.surname}" /></td>
		</tr>
		<tr>
			<td><spring:message code="actor.photo" />:</td>
			<td>
	<img src="${actor.photo}" alt="${actor.photo}"></td>
		</tr>
		
	<security:authorize access="hasRole('WARDEN')">	
		<tr>
			<td><spring:message code="warden.email" />:</td>
			<td><jstl:out value="${warden.email}" /></td>
		</tr>
	</security:authorize>	
	
	<security:authorize access="hasRole('VISITOR')">	
		<tr>
			<td><spring:message code="visitor.email" />:</td>
			<td><jstl:out value="${visitor.email}" /></td>
		</tr>
		
		<tr>
			<td><spring:message code="visitor.emergencyEmail" />:</td>
			<td><jstl:out value="${visitor.emergencyEmail}" /></td>
		</tr>
		
		<tr>
			<td><spring:message code="visitor.phoneNumber" />:</td>
			<td><jstl:out value="${visitor.phoneNumber}" /></td>
		</tr>
		
		<tr>
			<td><spring:message code="visitor.address" />:</td>
			<td><jstl:out value="${visitor.address}" /></td>
		</tr>
		
		</security:authorize>
		
	<security:authorize access="hasRole('PRISONER')">
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
	</security:authorize>
	<security:authorize access="hasRole('SALESMAN')">
		<tr>
			<td><spring:message code="salesman.VATNumber"/>:</td>
			<td><jstl:out value="${salesman.VATNumber}" /></td>
		</tr>
		<tr>
			<td><spring:message code="salesman.storeName"/>:</td>
			<td><jstl:out value="${salesman.storeName}" /></td>
		</tr>
		<tr>
			<td><spring:message code="salesman.points"/>:</td>
			<td><jstl:out value="${salesman.points}" /></td>
		</tr>
	</security:authorize>
	<security:authorize access="hasRole('SOCIALWORKER')">
		<tr>
			<td><spring:message code="socialworker.title"/>:</td>
			<td><jstl:out value="${socialWorker.title}" /></td>
		</tr>
	<jstl:choose>
		<jstl:when test="${curriculum != null}">
			<tr>
				<td><spring:message code="actor.curriculum" />:</td>
				<td><spring:url var="showCurriculumUrl" value="curriculum/socialWorker/show.do"/>	
					<a href="${showCurriculumUrl}">
						<strong><spring:message code="actor.showCurriculum" /></strong>
					</a>			
				</td>
			</tr>
		</jstl:when><jstl:otherwise>
			<tr>
				<td><spring:message code="actor.curriculum" />:</td>
				<td><spring:url var="showCurriculumUrl" value="curriculum/socialWorker/register.do"/>	
					<a href="${showCurriculumUrl}">
						<strong><spring:message code="actor.registerCurriculum" /></strong>
					</a>			
				</td>
			</tr>
		
		</jstl:otherwise>
	</jstl:choose>
	
	<spring:url var="editProfileUrl" value="authenticated/editProfile.do"/>
		<a href="${editProfileUrl}">
			<strong><spring:message code="actor.editProfile" /></strong>
		</a> / 
		<spring:url var="deleteUserUrl" value="authenticated/deleteUser.do"/>
		<a href="${deleteUserUrl}" onClick="return confirm('<spring:message code="delete.user.confirmation" />')">
			<strong><spring:message code="delete.user"/></strong>
		</a> / 
		<spring:url var="exportDataUrl" value="export/socialWorker.do">
			<spring:param name="id" value="${actor.id}"/>
		</spring:url>
		<a href="${exportDataUrl}">
			<strong><spring:message code="export.data.user"/></strong>
		</a>
	
	
	</security:authorize>
	
	
	<security:authorize access="hasRole('WARDEN')">
		<spring:url var="editProfileUrl" value="authenticated/editProfile.do"/>
		<a href="${editProfileUrl}">
			<strong><spring:message code="actor.editProfile" /></strong>
		</a> / 
		<spring:url var="deleteUserUrl" value="authenticated/deleteUser.do"/>
		<a href="${deleteUserUrl}" onClick="return confirm('<spring:message code="delete.user.confirmation" />')">
			<strong><spring:message code="delete.user"/></strong>
		</a> / 
		<spring:url var="exportDataUrl" value="export/warden.do">
			<spring:param name="id" value="${actor.id}"/>
		</spring:url>
		<a href="${exportDataUrl}">
			<strong><spring:message code="export.data.user"/></strong>
		</a>
	</security:authorize>
	
	<security:authorize access="hasRole('SALESMAN')">
		<spring:url var="editProfileUrl" value="authenticated/editProfile.do"/>
		<a href="${editProfileUrl}">
			<strong><spring:message code="actor.editProfile" /></strong>
		</a> / 
		<spring:url var="deleteUserUrl" value="authenticated/deleteUser.do"/>
		<a href="${deleteUserUrl}" onClick="return confirm('<spring:message code="delete.user.confirmation" />')">
			<strong><spring:message code="delete.user"/></strong>
		</a> / 
		<spring:url var="exportDataUrl" value="export/salesman.do">
			<spring:param name="id" value="${actor.id}"/>
		</spring:url>
		<a href="${exportDataUrl}">
			<strong><spring:message code="export.data.user"/></strong>
		</a>
	</security:authorize>
	
	<security:authorize access="hasRole('VISITOR')">
		<spring:url var="editProfileUrl" value="authenticated/editProfile.do"/>
		<a href="${editProfileUrl}">
			<strong><spring:message code="actor.editProfile" /></strong>
		</a> / 
		<spring:url var="deleteUserUrl" value="authenticated/deleteUser.do"/>
		<a href="${deleteUserUrl}" onClick="return confirm('<spring:message code="delete.user.confirmation" />')">
			<strong><spring:message code="delete.user"/></strong>
		</a> / 
		<spring:url var="exportDataUrl" value="export/visitor.do">
			<spring:param name="id" value="${actor.id}"/>
		</spring:url>
		<a href="${exportDataUrl}">
			<strong><spring:message code="export.data.user"/></strong>
		</a>
	</security:authorize>
	
	
	<security:authorize access="hasAnyRole('GUARD')">
	
			<tr>
				<td><spring:message code="guard.phone"/>:</td>
				<td><jstl:out value="${guard.phone}" /></td>
			</tr>
			<tr>
				<td><spring:message code="guard.email"/>:</td>
				<td><jstl:out value="${guard.email}" /></td>
			</tr>
		
		<spring:url var="editProfileUrl" value="authenticated/editProfile.do"/>
		<a href="${editProfileUrl}">
			<strong><spring:message code="actor.editProfile" /></strong>
		</a> / 
		<spring:url var="deleteUserUrl" value="authenticated/deleteUser.do"/>
		<a href="${deleteUserUrl}" onClick="return confirm('<spring:message code="delete.user.confirmation" />')">
			<strong><spring:message code="delete.user"/></strong>
		</a> / 
		<spring:url var="exportDataUrl" value="export/guard.do">
			<spring:param name="id" value="${actor.id}"/>
		</spring:url>
		<a href="${exportDataUrl}">
			<strong><spring:message code="export.data.user"/></strong>
		</a>
	</security:authorize>
	
	
	</table>

