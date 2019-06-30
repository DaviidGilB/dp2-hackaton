<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme"  tagdir="/WEB-INF/tags"%>

<security:authorize access="hasAnyRole('WARDEN', 'VISITOR', 'GUARD', 'SALESMAN', 'SOCIALWORKER')">
			
	<display:table pagesize="5" name="charges" id="row" requestURI="${requestURI}" >
		
		<display:column titleKey="charge.title" >
				
			<jstl:set var="title" value="${row.titleEnglish}"/>
				<jstl:if test="${locale=='ES'}">
					<jstl:set var="title" value="${row.titleSpanish}"/>
				</jstl:if>
			
			<jstl:out value="${title}"/>
		</display:column>
		
		<display:column titleKey="charge.year" > 
			<jstl:choose>
	    
		      	<jstl:when test="${locale=='EN'}">
			    	<jstl:out value="${row.year} years and ${row.month} months " /> 
			    </jstl:when>
			      
			    <jstl:otherwise>
			    	<jstl:out value="${row.year} años y ${row.month} meses " />
			    </jstl:otherwise>
		    
		    </jstl:choose>
			
		</display:column> 

	</display:table>
	
	<jstl:if test="${!warden}">
	
			<jstl:choose>
			
			<jstl:when test="${finder}">
			
			<spring:url var="backUrl" value="/finder/visitor/list.do">
			</spring:url>
			<a href="${backUrl}">
				<spring:message code="charge.back" />
			</a>
			
			</jstl:when>
			<jstl:otherwise>
			
			<spring:url var="backUrl" value="/anonymous/prisoner/list.do">
			</spring:url>
			<a href="${backUrl}">
				<spring:message code="charge.back" />
			</a>
			
			</jstl:otherwise>
			
			</jstl:choose>

	</jstl:if>
	
<jstl:if test="${warden}">
	<jstl:choose>
		<jstl:when test="${suspect}">
			<spring:url var="backUrl" value="/prisoner/warden/listSuspects.do"/>
			<a href="${backUrl}">
				<spring:message code="charge.back" />
			</a>
		</jstl:when><jstl:otherwise>
			<spring:url var="backUrl2" value="/warden/freePrisoners/list.do">
			</spring:url>
			<a href="${backUrl2}">
				<spring:message code="charge.back" />
			</a>
			
		</jstl:otherwise>
	</jstl:choose>
</jstl:if>
	
	
</security:authorize>


<security:authorize access="isAnonymous()">
			
	<display:table pagesize="5" name="charges" id="row" requestURI="${requestURI}" >
		
		<display:column titleKey="charge.title" >
				
			<jstl:set var="title" value="${row.titleEnglish}"/>
				<jstl:if test="${locale=='ES'}">
					<jstl:set var="title" value="${row.titleSpanish}"/>
				</jstl:if>
			
			<jstl:out value="${title}"/>
		</display:column>
		
		<display:column titleKey="charge.year" > 
			<jstl:choose>
	    
		      	<jstl:when test="${locale=='EN'}">
			    	<jstl:out value="${row.year} years and ${row.month} months " /> 
			    </jstl:when>
			      
			    <jstl:otherwise>
			    	<jstl:out value="${row.year} años y ${row.month} meses " />
			    </jstl:otherwise>
		    
		    </jstl:choose>
			
		</display:column> 

	</display:table>
	
	
			<spring:url var="backUrl" value="/anonymous/prisoner/list.do"/>
			<a href="${backUrl}">
				<spring:message code="charge.back" />
			</a>
	
	
</security:authorize>
