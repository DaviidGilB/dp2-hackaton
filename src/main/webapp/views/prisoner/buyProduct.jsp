<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<script>
function mult() {
	m1 = document.getElementById("price").value;
	m2 = document.getElementById("quantity").value;
	r = m1*m2;
	document.getElementById("totalCost").value = r;
}

function esEntero(numero){
    if (numero % 1 == 0) {
        alert ("Es un numero entero");
    } else {
        alert ("Es un numero decimal");
    }
}

function conf() {
	m1 = document.getElementById("price").value;
	m2 = document.getElementById("quantity").value;
	
	r = m1*m2;
	
	if (r % 1 == 0) {
		if(r>0) {
			return alert('<spring:message code="product.purchase.confirmation"/> ' + r + ' <spring:message code="product.points"/>');
		} else {
			return alert('<spring:message code="product.purchase.zeroAndNegativeError"/>');
		}
    } else {
        alert ('<spring:message code="product.purchase.decimalError"/>');
    }

}
</script>

<security:authorize access="hasRole('PRISONER')">

	<h3><spring:message code="product.currentPoints"/> 
	<span style="color:red; display:inline;"><jstl:out value="${points}" /></span> 
	<spring:message code="product.points"/></h3>
	
	<display:table name="product" id="row">
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
	</display:table>

	<form action="product/prisoner/buy.do" method="post">
		<input type="hidden" name="productId" value="${product.id}">
		<input type="hidden" id="price" value="${product.price}">
		
		<span><spring:message code="product.quantity"/>:</span>
		<input type="number" id="quantity" name="quantity" min="1" value="1" onchange="mult();">
		<br/>
		
		<span><spring:message code="product.total"/>:</span>
		<input type="text" id="totalCost" value="${product.price}" readonly/>
		<br/><br/>
	
		<input type="submit" name="save" value="<spring:message code ='product.buy'/>" onclick="conf();"/>
		<acme:cancel url="/product/prisoner/store.do" code="product.cancel"/>
	</form>
		
</security:authorize>