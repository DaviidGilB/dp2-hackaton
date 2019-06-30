<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags" %>


<security:authorize access="hasRole('WARDEN')">
	<jstl:if test="${warden!=null}">
		<form:form modelAttribute="warden" action="authenticated/editProfile.do">
		
		<form:hidden path = "id"/>
		
		<!-- Actor Attributes -->
		<fieldset>
	    	<legend><spring:message code="warden.personalData" /></legend>
			
			<acme:textbox path="name" code="warden.name" />
			<br />
			
			<acme:textbox path="middleName" code="warden.middleName" />
			<br />
			
			<acme:textbox path="surname" code="warden.surname" />
			<br />
			
			<acme:textbox path="email" code="warden.email" />
			<br />
			
			<acme:textbox path="photo" code="warden.photo" />
			<br />
		</fieldset>
		<br />
		
		<!-- BOTONES -->	
		<input type="submit" name="saveWarden" value="<spring:message code="warden.edit" />"/> 
		<acme:cancel url="/authenticated/showProfile.do" code="warden.cancel" /> 
		
		</form:form>
	</jstl:if>
	
</security:authorize>

<security:authorize access="hasRole('GUARD')">
	<jstl:if test="${guard!=null}">
	
		<form:form modelAttribute="guard" action="authenticated/editProfile.do">
		
		<form:hidden path = "id"/>
		
		<!-- Actor Attributes -->
		<fieldset>
	    	<legend><spring:message code="warden.personalData" /></legend>
			
			<acme:textbox path="name" code="warden.name" />
			<br />
			
			<acme:textbox path="middleName" code="warden.middleName" />
			<br />
			
			<acme:textbox path="surname" code="warden.surname" />
			<br />
			
			<acme:textbox path="photo" code="warden.photo" />
			<br />
			
			<acme:textbox path="email" code="guard.email" />
			<br />
			
			<acme:textbox path="phone" code="guard.phone" />
			<br />
		</fieldset>
		<br />
		
		<!-- BOTONES -->	
		<input type="submit" name="saveGuard" value="<spring:message code="warden.edit" />"/> 
		<acme:cancel url="/authenticated/showProfile.do" code="warden.cancel" /> 
		
		</form:form>
	</jstl:if>
</security:authorize>

<security:authorize access="hasRole('SALESMAN')">
	<jstl:if test="${salesman!=null}">
		<form:form modelAttribute="salesman" action="authenticated/editProfile.do">
		
		<form:hidden path = "id"/>
		
		<!-- Actor Attributes -->
		<fieldset>
	    	<legend><spring:message code="warden.personalData" /></legend>
			
			<acme:textbox path="name" code="warden.name" />
			<br />
			
			<acme:textbox path="middleName" code="warden.middleName" />
			<br />
			
			<acme:textbox path="surname" code="warden.surname" />
			<br />
			
			<acme:textbox path="photo" code="warden.photo" />
			<br />
			
			<acme:textbox path="VATNumber" code="salesman.VATNumber" />
			<br />
			
			<acme:textbox path="storeName" code="salesman.storeName" />
			<br />
		</fieldset>
		<br />
		
		<!-- BOTONES -->	
		<input type="submit" name="saveSalesMan" value="<spring:message code="warden.edit" />"/> 
		<acme:cancel url="/authenticated/showProfile.do" code="warden.cancel" /> 
		
		</form:form>
	</jstl:if>
	
</security:authorize>


<security:authorize access="hasRole('VISITOR')">
	<jstl:if test="${visitor!=null}">
		<form:form modelAttribute="visitor" action="authenticated/editProfile.do">
		
		<form:hidden path = "id"/>
		
		<!-- Actor Attributes -->
		<fieldset>
	    	<legend><spring:message code="warden.personalData" /></legend>
			
			<acme:textbox path="name" code="warden.name" />
			<br />
			
			<acme:textbox path="middleName" code="warden.middleName" />
			<br />
			
			<acme:textbox path="surname" code="warden.surname" />
			<br />
			
			<acme:textbox path="photo" code="warden.photo" />
			<br />
			
			<acme:textbox path="phoneNumber" code="visitor.phoneNumber" />
			<br />
			
			<acme:textbox path="email" code="visitor.email" />
			<br />
			
			<acme:textbox path="emergencyEmail" code="visitor.emergencyEmail" />
			<br />
			
			<acme:textbox path="address" code="visitor.address" />
			<br />
		</fieldset>
		<br />
		
		<!-- BOTONES -->	

		<input type="submit" name="saveVisitor" value="<spring:message code="warden.edit" />"/> 
		<acme:cancel url="/authenticated/showProfile.do" code="warden.cancel" /> 
		
		</form:form>
	</jstl:if>
	
</security:authorize>

<security:authorize access="hasRole('SOCIALWORKER')">
	<jstl:if test="${socialWorker!=null}">
		<form:form modelAttribute="socialWorker" action="authenticated/editProfile.do">

		<form:hidden path = "id"/>
		
		<!-- Actor Attributes -->
		<fieldset>
	    	<legend><spring:message code="warden.personalData" /></legend>
			
			<acme:textbox path="name" code="warden.name" />
			<br />
			
			<acme:textbox path="middleName" code="warden.middleName" />
			<br />
			
			<acme:textbox path="surname" code="warden.surname" />
			<br />

			<acme:textbox path="photo" code="warden.photo" />
			<br />

		<!-- Social Worker Attributes -->	
			
			<acme:textbox path="title" code="socialWorker.title" />
			<br />

		</fieldset>
		<br />

		<!-- BOTONES -->	

		<input type="submit" name="saveSocialWorker" value="<spring:message code="warden.edit" />"/> 
		<acme:cancel url="/authenticated/showProfile.do" code="warden.cancel" /> 

			
		
		</form:form>
	</jstl:if>

		
</security:authorize>