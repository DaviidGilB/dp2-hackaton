<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme"  tagdir="/WEB-INF/tags"%>

<security:authorize access="hasAnyRole('WARDEN', 'VISITOR', 'GUARD', 'SALESMAN', 'SOCIALWORKER')">
			
	<display:table pagesize="5" name="prisoners" id="row" requestURI="${requestURI}" >
		
		<display:column titleKey="prisoner.name">
			<jstl:out value="${row.name}"/>
		</display:column> 
		
		<display:column titleKey="prisoner.middleName">
			<jstl:out value="${row.middleName}"/>
		</display:column> 
		
		<display:column titleKey="prisoner.surname">
			<jstl:out value="${row.surname}"/>
		</display:column> 
		
		<display:column titleKey="prisoner.photo">
			<a href="${row.photo}">
				<spring:message code="prisoner.viewPhoto"/>
			</a>
		</display:column> 
			
		<display:column titleKey="prisoner.ticker" >
			<jstl:out value="${row.ticker}"/>
		</display:column>
		 		
		<display:column titleKey="prisoner.incomeDate" >
			<jstl:out value="${row.incomeDate}"/>
		</display:column>
		 
		<display:column titleKey="prisoner.exitDate">
			<jstl:out value="${row.exitDate}"/>
		</display:column> 

		<display:column titleKey="prisoner.isIsolated">
	
					<jstl:if test="${row.isIsolated}">
						<jstl:set var="isIsolated" value="YES"/>
						<jstl:if test="${locale=='ES'}">
							<jstl:set var="isIsolated" value="SI"/>
						</jstl:if>
					</jstl:if>
					<jstl:if test="${!row.isIsolated}">
						<jstl:set var="isIsolated" value="NO"/>
						<jstl:if test="${locale=='ES'}">
							<jstl:set var="isIsolated" value="NO"/>
						</jstl:if>
					</jstl:if>

					<jstl:out value="${isIsolated}"/>
	
		</display:column>
				
		<display:column  titleKey="prisoner.charges" > 
			
			<jstl:if test="${!warden}">
				<spring:url var="chargesUrl"
					value="/anonymous/charge/list.do?prisonerId={prisonerId}">
					<spring:param name="prisonerId" value="${row.id}" />
				</spring:url>
				<a href="${chargesUrl}"><spring:message code="prisoner.viewCharges" /></a>
			</jstl:if>
			
			<jstl:if test="${warden}">
				<spring:url var="chargesUrl"
					value="/warden/charge/list.do?prisonerId={prisonerId}">
					<spring:param name="prisonerId" value="${row.id}" />
				</spring:url>
				<a href="${chargesUrl}"><spring:message code="prisoner.viewCharges" /></a>
			</jstl:if>
		</display:column>
		
		<!-- BOTON DE CREATE VISIT -->
		<security:authorize access="hasRole('VISITOR')">
		
				<display:column>
					<jstl:if test="${(!row.freedom) && (!row.isIsolated)}">
		       		<spring:url var="createVisitUrl" value="/visit/visitor/create.do?prisonerId={prisonerId}">
		            	<spring:param name="prisonerId" value="${row.id}"/>
		            	
		        	</spring:url>
		        	
		        	<a href="${createVisitUrl}">
		              <spring:message var ="createVisit" code="visit.CreateVisit" />
		             <jstl:out value="${createVisit}" />   
		        	</a>
		        	</jstl:if>
		        </display:column>
		        
        </security:authorize>
        
	</display:table>
	
</security:authorize>


<security:authorize access="isAnonymous()">
			
	<display:table pagesize="5" name="prisoners" id="row" requestURI="${requestURI}" >
		
		<display:column titleKey="prisoner.name">
			<jstl:out value="${row.name}"/>
		</display:column> 
		
		<display:column titleKey="prisoner.middleName">
			<jstl:out value="${row.middleName}"/>
		</display:column> 
		
		<display:column titleKey="prisoner.surname">
			<jstl:out value="${row.surname}"/>
		</display:column> 
		
		<display:column titleKey="prisoner.photo">
			<a href="${row.photo}">
				<spring:message code="prisoner.viewPhoto"/>
			</a>
		</display:column> 
			
		<display:column titleKey="prisoner.ticker">
			<jstl:out value="${row.ticker}"/>
		</display:column>
		 		
		<display:column titleKey="prisoner.incomeDate">
			<jstl:out value="${row.incomeDate}"/>
		</display:column> 
		
		<display:column titleKey="prisoner.exitDate">
			<jstl:out value="${row.exitDate}"/>
		</display:column> 

		<display:column titleKey="prisoner.isIsolated">
	
					<jstl:if test="${row.isIsolated}">
						<jstl:set var="isIsolated" value="YES"/>
						<jstl:if test="${locale=='ES'}">
							<jstl:set var="isIsolated" value="SI"/>
						</jstl:if>
					</jstl:if>
					<jstl:if test="${!row.isIsolated}">
						<jstl:set var="isIsolated" value="NO"/>
						<jstl:if test="${locale=='ES'}">
							<jstl:set var="isIsolated" value="NO"/>
						</jstl:if>
					</jstl:if>

					<jstl:out value="${isIsolated}"/>
	
		</display:column>
				
		<display:column  titleKey="prisoner.charges" > 
			<spring:url var="chargesUrl"
				value="/anonymous/charge/list.do?prisonerId={prisonerId}">
				<spring:param name="prisonerId" value="${row.id}" />
				
			</spring:url>
			<a href="${chargesUrl}"><spring:message
				code="prisoner.viewCharges" /></a>
		</display:column>
		
		
		<!-- BOTON DE CREATE VISIT -->
		<security:authorize access="hasRole('VISITOR')">
		
				<display:column>
					<jstl:if test="${(!row.freedom) && (!row.isIsolated)}">
		       		<spring:url var="createVisitUrl" value="/visit/visitor/create.do?prisonerId={prisonerId}">
		            	<spring:param name="prisonerId" value="${row.id}"/>
		            	
		        	</spring:url>
		        	
		        	<a href="${createVisitUrl}">
		              <spring:message var ="createVisit" code="visit.CreateVisit" />
		             <jstl:out value="${createVisit}" />   
		        	</a>
		        	</jstl:if>
		        </display:column>
		        
        </security:authorize>
        
	</display:table>
	
</security:authorize>
