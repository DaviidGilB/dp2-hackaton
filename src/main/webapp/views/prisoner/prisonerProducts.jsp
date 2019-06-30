<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme"  tagdir="/WEB-INF/tags"%>

<security:authorize access="hasRole('PRISONER')">
			
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
		<display:column titleKey="product.quantity" > 
			<jstl:out value="${row.quantity}"></jstl:out>
		</display:column>
		<display:column titleKey="product.purchaseMoment" > 
			<jstl:out value="${row.purchaseMoment}"></jstl:out>
		</display:column>
	
	</display:table>
	
</security:authorize>
