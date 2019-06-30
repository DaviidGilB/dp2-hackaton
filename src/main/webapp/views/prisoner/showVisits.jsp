<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<%@ page import="java.util.Date" %>
<%@ page import="java.sql.Timestamp"%>
  
<%
java.util.Date utilDate = new java.util.Date();
java.sql.Timestamp now = new java.sql.Timestamp(utilDate.getTime());
%>
<jstl:set var="now" value="<%=now%>"/>

<security:authorize access="hasAnyRole('PRISONER', 'VISITOR', 'GUARD')">

<p><spring:message code="position.audits.list" /></p>

<security:authorize access="hasRole('PRISONER')" > 
	<form name="filter" id="filter" action="visit/prisoner/filter.do" method="post">
		<label for="filter"><spring:message code="request.filter"/></label>

		<br/>
	
		<select name="fselect">
  			<option value="ALL">-</option>
  			<option value="PENDING"><spring:message code="status.pending"/></option>
 			<option value="ACCEPTED"><spring:message code="status.accepted"/></option>
  			<option value="REJECTED"><spring:message code="status.rejected"/></option>
  			<option value="PERMITTED"><spring:message code="status.permitted"/></option>
		</select>
		
		<input type="submit" name="refresh" id="refresh" value="<spring:message code ="request.filter.button"/>"/>
	
	</form>
</security:authorize>

<security:authorize access="hasRole('VISITOR')" > 
	<form name="filter" id="filter" action="visit/visitor/filter.do" method="post">
		<label for="filter"><spring:message code="request.filter"/></label>

		<br/>
	
		<select name="fselect">
  			<option value="ALL">-</option>
  			<option value="PENDING"><spring:message code="status.pending"/></option>
 			<option value="ACCEPTED"><spring:message code="status.accepted"/></option>
  			<option value="REJECTED"><spring:message code="status.rejected"/></option>
  			<option value="PERMITTED"><spring:message code="status.permitted"/></option>
		</select>
		
		<input type="submit" name="refresh" id="refresh" value="<spring:message code ="request.filter.button"/>"/>
	
	</form>
</security:authorize>

	
	<br/>

	<display:table pagesize="5" name="visits" id="row" requestURI="${requestURI}" >
	
	<jstl:choose>
			<jstl:when test="${row.visitStatus.toString()=='PENDING'}">
				<jstl:set var="color" value="#DBD51A" />
				
				<jstl:if test="${row.date < now}">
					<jstl:set var="color" value="grey" />
				</jstl:if>
				
			</jstl:when>
			
			<jstl:when test="${row.visitStatus.toString()=='ACCEPTED'}">
				<jstl:set var="color" value="green" />
			</jstl:when>
			
			<jstl:when test="${row.visitStatus.toString()=='REJECTED'}">
				<jstl:set var="color" value="red" />
			</jstl:when>
			
			<jstl:when test="${row.visitStatus.toString()=='PERMITTED'}">
				<jstl:set var="color" value="blue" />
			</jstl:when>
			
		</jstl:choose>
	
	<display:column titleKey="visit.visitStatus" style="color:${color}" sortable="true">
	
		<jstl:if test="${row.visitStatus=='PERMITTED'}">
				<jstl:set var="status" value="PERMITTED"/>
				<jstl:if test="${locale=='ES'}">
					<jstl:set var="status" value="PERMITIDA"/>
				</jstl:if>
			</jstl:if>
			<jstl:if test="${row.visitStatus=='ACCEPTED'}">
				<jstl:set var="status" value="ACCEPTED"/>
				<jstl:if test="${locale=='ES'}">
					<jstl:set var="status" value="ACEPTADA"/>
				</jstl:if>
			</jstl:if>
			<jstl:if test="${row.visitStatus=='REJECTED'}">
				<jstl:set var="status" value="REJECTED"/>
				<jstl:if test="${locale=='ES'}">
					<jstl:set var="status" value="RECHAZADA"/>
				</jstl:if>
			</jstl:if>
			<jstl:if test="${row.visitStatus=='PENDING'}">
				<jstl:set var="status" value="PENDING"/>
				<jstl:if test="${locale=='ES'}">
					<jstl:set var="status" value="PENDIENTE"/>
				</jstl:if>
			</jstl:if>
			<jstl:out value="${status}"/>
	
	</display:column>
	
	<display:column titleKey="visit.description" style="color:${color}">
		<jstl:out value="${row.description}"/>
	</display:column>
	
	<display:column titleKey="visit.date" style="color:${color}">
		<jstl:out value="${row.date}"/>
	</display:column>
	
	<display:column titleKey="visit.reason" style="color:${color}">
		<jstl:if test="${row.reason=='BUSSINESS'}">
				<jstl:set var="reason" value="BUSSINESS"/>
				<jstl:if test="${locale=='ES'}">
					<jstl:set var="reason" value="NEGOCIOS"/>
				</jstl:if>
			</jstl:if>
			<jstl:if test="${row.reason=='PERSONAL'}">
				<jstl:set var="reason" value="PERSONAL"/>
				<jstl:if test="${locale=='ES'}">
					<jstl:set var="reason" value="PERSONAL"/>
				</jstl:if>
			</jstl:if>
			<jstl:if test="${row.reason=='LEGAL'}">
				<jstl:set var="reason" value="LEGAL"/>
				<jstl:if test="${locale=='ES'}">
					<jstl:set var="reason" value="LEGAL"/>
				</jstl:if>
			</jstl:if>
			<jstl:if test="${row.reason=='MEDICAL'}">
				<jstl:set var="reason" value="MEDICAL"/>
				<jstl:if test="${locale=='ES'}">
					<jstl:set var="reason" value="MEDICA"/>
				</jstl:if>
			</jstl:if>
			<jstl:out value="${reason}"/>
	</display:column>
	
	<security:authorize access="hasAnyRole('GUARD', 'PRISONER')" >
			<display:column titleKey="visit.visitor" style="color:${color}" >
				<jstl:out value="${row.visitor.name} ${row.visitor.middleName} ${row.visitor.surname}" />
			</display:column>	
	</security:authorize>
	
	<security:authorize access="hasAnyRole('GUARD','VISITOR')">
		<display:column titleKey="visit.prisoner" style="color:${color}">
			<jstl:out value="${row.prisoner.name} ${row.prisoner.middleName} ${row.prisoner.surname}" />
		</display:column>	
	</security:authorize>



	<display:column>
		<jstl:if test="${row.report!=null}">
			<a href="visit/report/list.do?visitId=${row.id}">
				<spring:message code="visit.report" />
			</a>
		</jstl:if>
		
		<security:authorize access="hasRole('GUARD')" >
			<jstl:if test="${row.visitStatus == 'PERMITTED' && row.report == null && row.date < now}">
				<a href="visit/report/create.do?visitId=${row.id}" >
						<spring:message code="visit.createReport" />
					</a>
			</jstl:if>

		</security:authorize>

	</display:column>
	
	<display:column>
	
		<jstl:if test="${row.visitStatus == 'PENDING'}">
			
			<security:authorize access="hasRole('PRISONER')" >
				<jstl:if test="${!row.createdByPrisoner && (row.date > now) }">
					<a href="visit/prisoner/accept.do?visitId=${row.id}" onclick="return confirm('<spring:message code="visit.confirmChangeStatus" />')">
						<spring:message code="visit.accept" />
					</a>
					<br />
					<a href="visit/prisoner/reject.do?visitId=${row.id}"  onclick="return confirm('<spring:message code="visit.confirmChangeStatus" />')">
						<spring:message code="visit.reject" />
					</a>
				</jstl:if>
			</security:authorize>
			
			<security:authorize access="hasRole('VISITOR')" >
				<jstl:if test="${row.createdByPrisoner && (row.date > now)}">
					<a href="visit/visitor/accept.do?visitId=${row.id}" onclick="return confirm('<spring:message code="visit.confirmChangeStatus" />')">
						<spring:message code="visit.accept" />
					</a>
					<br />
					<a href="visit/visitor/reject.do?visitId=${row.id}"  onclick="return confirm('<spring:message code="visit.confirmChangeStatus" />')">
						<spring:message code="visit.reject" />
					</a>
				</jstl:if>
			</security:authorize>
			
		</jstl:if>
		
		<jstl:if test="${row.visitStatus == 'ACCEPTED'}">
		
			<security:authorize access="hasRole('GUARD')" >
					<a href="visit/guard/permit.do?visitId=${row.id}" onclick="return confirm('<spring:message code="visit.confirmChangeStatus" />')">
						<spring:message code="visit.permit" />
					</a>
					<br />
					<a href="visit/guard/reject.do?visitId=${row.id}"  onclick="return confirm('<spring:message code="visit.confirmChangeStatus" />')">
						<spring:message code="visit.reject" />
					</a>
			</security:authorize>
			
		</jstl:if>
	
	</display:column>
	
	</display:table>


</security:authorize>