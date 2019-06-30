<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme"  tagdir="/WEB-INF/tags"%>

<security:authorize access="permitAll">


	<jstl:if test="${prisoner}">

		<h3><spring:message code="product.currentPoints"/> 
		<span style="color:red; display:inline;"><jstl:out value="${points}" /></span> 
		<spring:message code="product.points"/></h3>
			
	</jstl:if>
			
	<display:table pagesize="5" name="products" id="row" requestURI="${requestURI}" >
		
		<display:column titleKey="product.name" > 
		<jstl:out value="${row.name}"></jstl:out>
		</display:column>
		<display:column titleKey="product.description" > 
		<jstl:out value="${row.description}"></jstl:out>
		</display:column>
		
			<jstl:choose>
				<jstl:when test="${locale == 'EN'}">
					<display:column  titleKey="product.type" >
						<jstl:out value="${row.type.typeProductEN}"></jstl:out>
					</display:column>
				</jstl:when><jstl:otherwise>
					<display:column  titleKey="product.type" >
						<jstl:out value="${row.type.typeProductES}"></jstl:out>
					</display:column>
				</jstl:otherwise>
			</jstl:choose>
		
		<display:column titleKey="product.price" > 
			<jstl:out value="${row.price}"></jstl:out>
		</display:column>
		<display:column titleKey="product.stock" > 
			<jstl:out value="${row.stock}"></jstl:out>
		</display:column>
		
		<jstl:if test="${prisoner}">
			<display:column>	
				<spring:url var="purchaseUrl" value="/product/prisoner/buy.do?productId={productId}">
					<spring:param name="productId" value="${row.id}" />
				</spring:url>
				<a href="${purchaseUrl}">
					<spring:message code="product.purchase" />
				</a>
			</display:column>
		</jstl:if>
		
		<jstl:if test="${salesman}">
		
			<display:column titleKey="product.status">
			
			<jstl:choose>
				<jstl:when test="${row.isDraftMode}">
					<spring:message code="product.draft"/>	
				</jstl:when><jstl:otherwise>
					<spring:message code="product.final"/>
				</jstl:otherwise>
			</jstl:choose>
			
			</display:column>
		
			<display:column>
			<jstl:choose>
				<jstl:when test="${row.isDraftMode}">
					<spring:url var="editUrl" value="/product/salesman/edit.do?productId={productId}">
						<spring:param name="productId" value="${row.id}" />
					</spring:url>
					
					<a href="${editUrl}">
						<spring:message code="button.edit" />
					</a>
				</jstl:when><jstl:otherwise>
				
					<spring:url var="editUrl" value="/product/salesman/restock.do?productId={productId}">
						<spring:param name="productId" value="${row.id}" />
					</spring:url>
					
					<a href="${editUrl}">
						<spring:message code="product.restock" />
					</a>

				</jstl:otherwise>
			</jstl:choose>
				
			</display:column>
		</jstl:if>
	</display:table>
	
	<jstl:if test="${prisoner && store == null}">
			<spring:url var="backUrl" value="/product/salesman/prisoner/list.do">
			</spring:url>
					<a href="${backUrl}">
						<spring:message code="product.back" />
					</a>
	</jstl:if>
	
	<jstl:if test="${salesman}">
			<spring:url var="createUrl" value="/product/salesman/create.do">
			</spring:url>
					<a href="${createUrl}">
						<spring:message code="product.create" />
					</a>
	</jstl:if>
</security:authorize>
