<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <link rel="stylesheet" type="text/css" href="facil.css">
  <title>eVoting</title>
</head>
<body>
  <h1>Detalhes das pessoas</h1>

  <c:choose>
    <c:when test="${projectBean.pessoas.size() > 0}">
      <h5>Nome - Cart�o de cidad�o</h5>
      <c:forEach items="${projectBean.pessoas}" var="value">
        <c:out value="${value.nome} - ${value.cc}" /><br>
      </c:forEach>

      <h3>Introduza o cart�o de cidad�o da pessoa que pretende ver informa��es</h3>

      <s:form action="detalhespessoa" method="post">
        <s:text name="Cart�o de cidad�o" /><br/>
        <s:textfield name="cc" required="required"/><br/>
        <s:submit type = "button"><s:text name="Ver"/></s:submit>
        <button><a href="<s:url action="voltar"/>">Voltar</a></button>
      </s:form>
    </c:when>
    <c:otherwise>
      <h3>N�o existem pessoas registadas</h3>
      <button><a href="<s:url action="voltar"/>">Voltar</a></button>
    </c:otherwise>
  </c:choose>

  <c:choose>
    <c:when test="${session.searchPessoa != null}">
      <h3>Detalhes da pessoa</h3>
      <c:out value="Nome: ${session.searchPessoa.nome}" /><br/>
      <c:out value="Username: ${session.searchPessoa.username}" /><br/>
      <c:out value="Password: ${session.searchPessoa.password}" /><br/>
      <c:out value="Phone: ${session.searchPessoa.phone}" /><br/>
      <c:out value="Morada: ${session.searchPessoa.morada}" /><br/>
      <c:out value="Cart�o de Cidad�o: ${session.searchPessoa.cc}" /><br/>
      <c:out value="Validade Cart�o de Cidad�o: ${session.searchPessoa.validade}" /><br/>
      <c:out value="Departamento a que pertence: ${session.searchPessoa.grupo}" /><br/>
      <c:out value="Elei��es e respetivas mesas de voto em que votou: " /><br/>
      <c:forEach items="${session.searchPessoa.votou}" var="entry">
        <c:out value="Elei��o: ${entry.key} - Mesa de voto: ${entry.value}"/> <br />
      </c:forEach>
    </c:when>
  </c:choose>


</body>
</html>
