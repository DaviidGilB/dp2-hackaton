<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme"  tagdir="/WEB-INF/tags"%>

<script type="text/javascript">

function show(id){
	
	let form = document.getElementById('charges_' + id);
	
	if(form.style.display  == 'none'){
		form.style.display ='inline';
	}else{
		form.style.display ='none';	
		}
	
}

</script>

<security:authorize access="hasRole('WARDEN')">
			
	<display:table pagesize="5" name="prisoners" id="row" requestURI="${requestURI}" >
		
		<display:column titleKey="prisoner.name" > 
			<jstl:out value="${row.name}" />
		</display:column>
		<display:column titleKey="prisoner.middleName" > 	
			<jstl:out value="${row.middleName}" />
		</display:column>
		<display:column titleKey="prisoner.surname" > 
			<jstl:out value="${row.surname}" />
		</display:column>
		<display:column titleKey="prisoner.photo">
			<a href="${row.photo}">
				<spring:message code="prisoner.viewPhoto"/>
			</a>
		</display:column> 
			
		<display:column titleKey="prisoner.crimeRate" > 
			<jstl:out value="${row.crimeRate}" />
		</display:column>
		<display:column titleKey="prisoner.ticker" > 	
			<jstl:out value="${row.ticker}" />
		</display:column>	
		<display:column titleKey="prisoner.incomeDate" > 
			<jstl:out value="${row.incomeDate}" />
		</display:column>
		
		<display:column titleKey="prisoner.exitDate" > 
			<jstl:out value="${row.exitDate}" />
		</display:column>
		
		<display:column titleKey="prisoner.isSuspect" >
			<jstl:out value="${row.isSuspect}" />
		</display:column>
		
		

				
		<display:column  titleKey="prisoner.charges" > 
				<spring:url var="chargesUrl"
					value="/prisoner/warden/listSuspectCharges.do?prisonerId={prisonerId}">
					<spring:param name="prisonerId" value="${row.id}" />
				</spring:url>
				<a href="${chargesUrl}"><spring:message code="prisoner.viewCharges" /></a>
		</display:column>
		
		<display:column  > 
		<jstl:if test="${row.isSuspect && row.isIsolated == false}">
				<spring:url var="chargesUrl"
					value="/prisoner/warden/isolate.do?prisonerId={prisonerId}">
					<spring:param name="prisonerId" value="${row.id}" />
				</spring:url>
				<a href="${chargesUrl}"><spring:message code="prisoner.isolate" /></a>
		</jstl:if>
		</display:column>
		
		
		
		
		<display:column titleKey="prisoner.addChargesColum" > 
		
		<jstl:if test="${row.charges.size() < possibleCharges.size()-1}">	
		
		<div>
		<security:authorize access="hasAnyRole('WARDEN')" >
		<table style="border:none; display:inline">
			<tr>
				<td>
					<button onclick="show(${row.id})"><spring:message code="warden.showButton" /></button>
				</td>
				<td>
					<div id="charges_${row.id}" style="display:none">
						<form id="charge" action="prisoner/warden/addCharge.do?prisonerId=${row.id}" method="post">
							<table style="border:none; display:inline">
								<tr>
									<td >
										<jstl:if test="${locale =='EN'}">
											<jstl:set var="chargesLanguage" value ="titleEnglish"/>	
										</jstl:if>
						
										<jstl:if test="${locale =='ES'}">
											<jstl:set var="chargesLanguage" value ="titleSpanish"/>	
										</jstl:if>
										<select id="id" name="id">
											<jstl:forEach items="${possibleCharges}" var="i">
												<jstl:if test="${!row.charges.contains(i) && i.isDraftMode == false}">
													<option value="${i.id}">
														${i.titleEnglish}
													</option>
												</jstl:if>
											</jstl:forEach>
										</select>
									</td>
									<td>
										<input type="submit" name="save" value="<spring:message code="warden.addCharge" />"/> 
									</td>
								</tr>
							</table>
						</form>
					</div>
				</td>
			</tr>
			
		</table>
			
			
		</security:authorize>
		</div>
		</jstl:if>
		</display:column>
		
		<security:authorize access="hasRole('WARDEN')">
			<display:column titleKey="actor.profiles">
				<spring:url var="prisonerUrl"
					value="/prisoner/warden/edit.do">
					<spring:param name="prisonerId" value="${row.id}" />
				</spring:url>
				<a href="${prisonerUrl}"><spring:message code="actor.editProfile" /></a>
			</display:column>
		</security:authorize>
		


		
        
	</display:table>
	
</security:authorize>
