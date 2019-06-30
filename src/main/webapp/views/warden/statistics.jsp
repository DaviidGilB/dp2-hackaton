<%--
 * action-1.jsp
 *
 * Copyright (C) 2019 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>
 
 

<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<security:authorize access="hasRole('WARDEN')">

<table style="width: 100%">
		<tr>
			<td><b><spring:message code="statistics.couplesMostVisits" />:</b></td>
		</tr>
		<jstl:choose>
			<jstl:when test="${couplesWithMostVisits.keySet().isEmpty()}">
			
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
				
			</jstl:when><jstl:otherwise>
			
				<jstl:forEach items="${couplesWithMostVisits.keySet()}" var="cosa">
					<tr>
						<td><jstl:out value="[${cosa}] ---------> ${couplesWithMostVisits.get(cosa)}" /></td>
					</tr>
				</jstl:forEach>	
				
			</jstl:otherwise>
		</jstl:choose>
</table>
<br />

<table style="width: 100%">
		<tr>
			<td><b><spring:message code="statistics.prisonersMostVisitorsVisited" />:</b></td>
		</tr>
		<jstl:choose>
			<jstl:when test="${prisonersWithVisitsToMostDifferentVisitors.isEmpty()}">
			
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
				
			</jstl:when><jstl:otherwise>
			
				<jstl:forEach items="${prisonersWithVisitsToMostDifferentVisitors}" var="cosa">
					<tr>
						<td><jstl:out value="${cosa}" /></td>
					</tr>
				</jstl:forEach>	
				
			</jstl:otherwise>
		</jstl:choose>
</table>
<br />

<table style="width: 100%">
		<tr>
			<td><b><spring:message code="statistics.visitorsMostPrisonersVisited" />:</b></td>
		</tr>
		<jstl:choose>
			<jstl:when test="${visitorsWithVisitsToMostDifferentPrisoners.isEmpty()}">
			
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
				
			</jstl:when><jstl:otherwise>
			
				<jstl:forEach items="${visitorsWithVisitsToMostDifferentPrisoners}" var="cosa">
					<tr>
						<td><jstl:out value="${cosa}" /></td>
					</tr>
				</jstl:forEach>	
				
			</jstl:otherwise>
		</jstl:choose>
</table>
<br />

<b><spring:message code="statistics.visitsWithAndWithoutReports" />:</b>	
<table style="width: 100%">

		<jstl:choose>
			<jstl:when test="${statistics.get(0) == null}">
			
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
				
			</jstl:when><jstl:otherwise>
			
				<tr>
					<td><b><spring:message code="statistics.withReport"/>:</b></td>
				</tr>
				<tr>
					<td><jstl:out value="${statistics.get(0)}%" /> </td>
				</tr>
				<tr>
					<td><b><spring:message code="statistics.withoutReport"/>:</b></td>
				</tr>
				<tr>
					<td><jstl:out value="${100.0 - statistics.get(0)}%" /></td>
				</tr>
				
			</jstl:otherwise>
		</jstl:choose>
		

</table>
<br />

<table style="width: 100%">
		<tr>
			<td><b><spring:message code="statistics.guardsWithMostReportsWritten" />:</b></td>
		</tr>
		<jstl:choose>
			<jstl:when test="${guardsWithTheLargestNumberOfReportsWritten.isEmpty()}">
			
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
				
			</jstl:when><jstl:otherwise>
			
				<jstl:forEach items="${guardsWithTheLargestNumberOfReportsWritten}" var="cosa">
					<tr>
						<td><jstl:out value="${cosa}" /></td>
					</tr>
				</jstl:forEach>	
				
			</jstl:otherwise>
		</jstl:choose>
</table>
<br />

<table style="width: 100%">
		<tr>
			<td><b><spring:message code="statistics.ratioOfGuards50PercentOfVisitsWithReport" />:</b></td>
		</tr>		

		<jstl:choose>
			<jstl:when test="${statistics.get(1) == null}">
			
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
				
			</jstl:when><jstl:otherwise>
			
				<tr>
					<td><jstl:out value="${statistics.get(1)}%" /></td>
				</tr>
				
			</jstl:otherwise>
		</jstl:choose>
</table>
<br />

<table style="width: 100%">
		<tr>
			<td><b><spring:message code="statistics.ratioOfPrisonersWithoutVisitsLastMonth" />:</b></td>
		</tr>		
				
		<jstl:choose>
			<jstl:when test="${statistics.get(2) == null}">
			
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
				
			</jstl:when><jstl:otherwise>
			
				<tr>
					<td><jstl:out value="${statistics.get(2)}%" /></td>
				</tr>
				
			</jstl:otherwise>
		</jstl:choose>
</table>
<br />

<table style="width: 100%">
		<tr>
			<td><b><spring:message code="statistics.ratioRegularVisitorsToAtLeastOnePrisoner" />:</b></td>
		</tr>		

		<jstl:choose>
			<jstl:when test="${statistics.get(3) == null}">
			
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
				
			</jstl:when><jstl:otherwise>
			
				<tr>
					<td><jstl:out value="${statistics.get(3)}%" /></td>
				</tr>
				
			</jstl:otherwise>
		</jstl:choose>
</table>
<br />

<table style="width: 100%">
		<tr>
			<td><b><spring:message code="statistics.ratioOfAvaliableGuardsVsFutureNotPermittedVisits" />:</b></td>
		</tr>		

		<jstl:choose>
			<jstl:when test="${statistics.get(4) == null}">
			
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
				
			</jstl:when><jstl:otherwise>
			
				<tr>
					<td><jstl:out value="${statistics.get(4)}" /></td>
				</tr>
				
			</jstl:otherwise>
		</jstl:choose>
</table>
<br />

<table style="width: 100%">
		<tr>
			<td><b><spring:message code="statistics.top3PrisonersLowestCrimeRate" />:</b></td>
		</tr>

		<jstl:choose>
			<jstl:when test="${top3PrisonersLowestCrimeRate.isEmpty()}">
			
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
				
			</jstl:when><jstl:otherwise>
			
				<jstl:forEach items="${top3PrisonersLowestCrimeRate}" var="cosa">
					<tr>
						<td><jstl:out value="${cosa}" /></td>
					</tr>
				</jstl:forEach>
				
			</jstl:otherwise>
		</jstl:choose>
		
</table>
<br />

<table style="width: 100%">
		<tr>
			<td><b><spring:message code="statistics.ratioOfNonIsolatedPrisonersVsIsolatedPrisoners" />:</b></td>
		</tr>		

		<jstl:choose>
			<jstl:when test="${statistics.get(5) == null}">
			
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
				
			</jstl:when><jstl:otherwise>
			
				<tr>
					<td><jstl:out value="${statistics.get(5)}" /></td>
				</tr>
				
			</jstl:otherwise>
		</jstl:choose>
</table>
<br />

<b><spring:message code="statistics.crimeRate" />:</b>	
<table style="width: 100%">

	<jstl:choose>
		<jstl:when test="${statistics.get(6) == null}">
		
			<tr> 
				<td><b><spring:message code="statistics.average" />:</b></td> 
		
			</tr>
			<tr>
				<td><spring:message code="statistics.NA" /></td>
			</tr>
			<tr>
				<td><b><spring:message code="statistics.minimum"/>:</b></td> 
			</tr>
			<tr>
				<td><spring:message code="statistics.NA" /></td>
			</tr>
			<tr>
				<td><b><spring:message code="statistics.maximum"/>:</b></td> 
			</tr>
			<tr>
				<td><spring:message code="statistics.NA" /></td>
			</tr>
			<tr>
				<td><b><spring:message code="statistics.standardDeviation"/>:</b></td> 
			</tr>
			<tr>
				<td><spring:message code="statistics.NA" /></td>
			</tr>
			
		</jstl:when><jstl:otherwise>
		
			<tr> 
				<td><b><spring:message code="statistics.average" />:</b></td> 
		
			</tr>
			<tr>
				<td><jstl:out value="${statistics.get(6)}" /> </td>
			</tr>
			<tr>
				<td><b><spring:message code="statistics.minimum"/>:</b></td> 
			</tr>
			<tr>
				<td><jstl:out value="${statistics.get(7)}" /> </td>
			</tr>
			<tr>
				<td><b><spring:message code="statistics.maximum"/>:</b></td> 
			</tr>
			<tr>
				<td><jstl:out value="${statistics.get(8)}" /> </td>
			</tr>
			<tr>
				<td><b><spring:message code="statistics.standardDeviation"/>:</b></td> 
			</tr>
			<tr>
				<td><jstl:out value="${statistics.get(9)}" /> </td>
			</tr>
			
		</jstl:otherwise>
	</jstl:choose>

</table>
<br />

<table style="width: 100%">
		<tr>
			<td><b><spring:message code="statistics.socialWorkersMostActivitiesForumComplete" />:</b></td>
		</tr>

		<jstl:choose>
			<jstl:when test="${socialWorkerMostActivitiesFull.isEmpty()}">
			
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
				
			</jstl:when><jstl:otherwise>
			
				<jstl:forEach items="${socialWorkerMostActivitiesFull}" var="cosa">
					<tr>
						<td><jstl:out value="${cosa}" /></td>
					</tr>
				</jstl:forEach>
				
			</jstl:otherwise>
		</jstl:choose>
</table>
<br />

<table style="width: 100%">
		<tr>
			<td><b><spring:message code="statistics.prisonersMostRejectedRequestToDifferentActivitiesAndNoApprovedOnThoseActivities" />:</b></td>
		</tr>

		<jstl:choose>
			<jstl:when test="${prisonersMostRejectedRequestToDifferentActivitiesAndNoApprovedOnThoseActivities.isEmpty()}">
			
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
				
			</jstl:when><jstl:otherwise>
			
				<jstl:forEach items="${prisonersMostRejectedRequestToDifferentActivitiesAndNoApprovedOnThoseActivities}" var="cosa">
					<tr>
						<td><jstl:out value="${cosa}" /></td>
					</tr>
				</jstl:forEach>
				
			</jstl:otherwise>
		</jstl:choose>
</table>
<br />

<b><spring:message code="statistics.ratioOfSocialWorkersWithAndWithoutCurriculum" />:</b>	
<table style="width: 100%">

		<jstl:choose>
			<jstl:when test="${statistics.get(10) == null}">
			
				<tr>
					<td><b><spring:message code="statistics.withCurriculum"/>:</b></td>
				</tr>
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
				<tr>
					<td><b><spring:message code="statistics.withoutCurriculum"/>:</b></td>
				</tr>
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
			</jstl:when><jstl:otherwise>
			
				<tr>
					<td><b><spring:message code="statistics.withCurriculum"/>:</b></td>
				</tr>
				<tr>
					<td><jstl:out value="${statistics.get(10)}%" /> </td>
				</tr>
				<tr>
					<td><b><spring:message code="statistics.withoutCurriculum"/>:</b></td>
				</tr>
				<tr>
					<td><jstl:out value="${100.0 - statistics.get(10)}%" /></td>
				</tr>
				
			</jstl:otherwise>
		</jstl:choose>
</table>
<br />

<table style="width: 100%">
		<tr>
			<td><b><spring:message code="statistics.activitiesLargestNumberPrisoners" />:</b></td>
		</tr>

		<jstl:choose>
			<jstl:when test="${activitiesLargestNumberPrisoners.isEmpty()}">
			
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
				
			</jstl:when><jstl:otherwise>
			
				<jstl:forEach items="${activitiesLargestNumberPrisoners}" var="cosa">
					<tr>
						<td><jstl:out value="${cosa}" /></td>
					</tr>
				</jstl:forEach>
				
			</jstl:otherwise>
		</jstl:choose>
</table>
<br />

<table style="width: 100%">
		<tr>
			<td><b><spring:message code="statistics.activitiesLargestAvgCrimeRate" />:</b></td>
		</tr>

		<jstl:choose>
			<jstl:when test="${activitiesLargestAvgCrimeRate.isEmpty()}">
			
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
				
			</jstl:when><jstl:otherwise>
			
				<jstl:forEach items="${activitiesLargestAvgCrimeRate}" var="cosa">
					<tr>
						<td><jstl:out value="${cosa}" /></td>
					</tr>
				</jstl:forEach>
				
			</jstl:otherwise>
		</jstl:choose>
</table>
<br />

<table style="width: 100%">
		<tr>
			<td><b><spring:message code="statistics.activitiesSmallestAvgCrimeRate" />:</b></td>
		</tr>

		<jstl:choose>
			<jstl:when test="${activitiesSmallestAvgCrimeRate.isEmpty()}">
			
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
				
			</jstl:when><jstl:otherwise>
			
				<jstl:forEach items="${activitiesSmallestAvgCrimeRate}" var="cosa">
					<tr>
						<td><jstl:out value="${cosa}" /></td>
					</tr>
				</jstl:forEach>
				
			</jstl:otherwise>
		</jstl:choose>
</table>
<br />

<table style="width: 100%">
		<tr>
			<td><b><spring:message code="statistics.socialWorkersLowestRatioPrisonersPerActivity" />:</b></td>
		</tr>

		<jstl:choose>
			<jstl:when test="${socialWorkersLowestRatioPrisonersPerActivity.isEmpty()}">
			
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
				
			</jstl:when><jstl:otherwise>
			
				<jstl:forEach items="${socialWorkersLowestRatioPrisonersPerActivity}" var="cosa">
					<tr>
						<td><jstl:out value="${cosa}" /></td>
					</tr>
				</jstl:forEach>
				
			</jstl:otherwise>
		</jstl:choose>
</table>
<br />

<table style="width: 100%">
		<tr>
			<td><b><spring:message code="statistics.activitiesMostSearched" />:</b></td>
		</tr>

		<jstl:choose>
			<jstl:when test="${activitiesMostSearched.isEmpty()}">
			
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
				
			</jstl:when><jstl:otherwise>
			
				<jstl:forEach items="${activitiesMostSearched}" var="cosa">
					<tr>
						<td><jstl:out value="${cosa}" /></td>
					</tr>
				</jstl:forEach>
				
			</jstl:otherwise>
		</jstl:choose>
</table>
<br />

<table style="width: 100%">
		<tr>
			<td><b><spring:message code="statistics.top5PrisonersParticipatedMostActivitiesLastMonth" />:</b></td>
		</tr>

		<jstl:choose>
			<jstl:when test="${top5PrisonersParticipatedMostActivitiesLastMonth.isEmpty()}">
			
				<tr>
					<td><spring:message code="statistics.NA" /></td>
				</tr>
				
			</jstl:when><jstl:otherwise>
			
				<jstl:forEach items="${top5PrisonersParticipatedMostActivitiesLastMonth}" var="cosa">
					<tr>
						<td><jstl:out value="${cosa}" /></td>
					</tr>
				</jstl:forEach>
				
			</jstl:otherwise>
		</jstl:choose>
</table>
<br />
</security:authorize>
