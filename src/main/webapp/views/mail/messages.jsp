<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<p><jstl:out value="${boxName}" /></p>

<security:authorize access="isAuthenticated()">


<display:table
	pagesize="4" name="messages" id="row"
	requestURI="message/actor/list.do" >
	
	<!-- Date -->
	<display:column	titleKey="mail.message.moment">
		<jstl:out value="${row.moment}" />
	</display:column>
	
	<display:column	titleKey="mail.message.subject">
		<jstl:out value="${row.subject}" />
	</display:column>
	
	<display:column	titleKey="mail.message.body">
		<jstl:out value="${row.body}" />
	</display:column>
	
	
	<display:column	titleKey="mail.message.tags">
		<jstl:out value="${row.tags}" />
	</display:column>
							
	<display:column	titleKey="mail.message.sender">
		<jstl:out value="${row.sender}" />
	</display:column>	
	
	<display:column	titleKey="mail.message.receiver">
		<jstl:out value="${row.recipient}" />
	</display:column>
	
	
	<display:column titleKey="mail.message.move">
	
<jstl:forEach items="${boxes}" var="box">
 
 	<spring:url var="moveMessage" value="/message/actor/move.do?messageId=${row.id}&boxId=${box.id}">
			
	</spring:url>
	
 
 		<a href="${moveMessage}">
			<jstl:out value="${box.name} " />
			<br />
		</a>
		
	</jstl:forEach>
 

	</display:column>	
	
	<display:column titleKey="mail.message.copy">
	
<jstl:forEach items="${boxes}" var="box">
 
 	<spring:url var="copyMessage" value="/message/actor/copy.do?messageId=${row.id}&boxId=${box.id}">
			
	</spring:url>
	
 
 		<a href="${copyMessage}">
			<jstl:out value="${box.name} " />
			<br />
		</a>
		
	</jstl:forEach>
 

	</display:column>	
	
	<display:column>
	
	<jstl:choose>
  <jstl:when test="${boxName == 'TRASHBOX'}">
   <spring:url var="deletemessage" value="/message/actor/delete.do?rowId={rowId}&boxId=${boxId}">
				<spring:param name="rowId" value="${row.id}"/>
			</spring:url>
		
				<a href="${deletemessage}" onclick="return confirm('<spring:message code="mail.delete" />')">
				 <spring:message code="message.delete"/>
				 </a>
  </jstl:when>
  <jstl:otherwise>
    <spring:url var="deletemessage" value="/message/actor/delete.do?rowId={rowId}&boxId=${boxId}">
				<spring:param name="rowId" value="${row.id}"/>
			</spring:url>
		
				<a href="${deletemessage}" onclick="return confirm('<spring:message code="mail.delete" />')">
				 <spring:message code="message.deleteToTrash"/>
				 </a>
  </jstl:otherwise>
</jstl:choose>
		
		</display:column>
	
	
	

															
</display:table>

<!-- Enlaces parte inferior -->
<jstl:choose>
<jstl:when test="${crimRate == true}">
  	<h3 style="color:Red;"><spring:message code="mail.message.cannotSend" /></h3>
  </jstl:when>
  <jstl:otherwise>
    <spring:url var="newMessage" value="/message/actor/create.do"/>
	<p><a href="${newMessage}"><spring:message code="mail.message.new" /></a></p>
    
  </jstl:otherwise>
</jstl:choose>



<spring:url var="mail" value="/box/actor/list.do"/>

<p><a href="${mail}"><spring:message code="mail.back" /></a></p>

</security:authorize>