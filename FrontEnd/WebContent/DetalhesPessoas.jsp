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
      <h5>Nome - Cartão de cidadão</h5>
      <c:forEach items="${projectBean.pessoas}" var="value">
        <c:out value="${value.nome} - ${value.cc}" /><br>
      </c:forEach>

      <h3>Introduza o cartão de cidadão da pessoa que pretende ver informações</h3>

      <s:form action="detalhespessoa" method="post">
        <s:text name="Cartão de cidadão" /><br/>
        <s:textfield name="cc" required="required"/><br/>
        <s:submit type = "button"><s:text name="Ver"/></s:submit>
        <button><a href="<s:url action="voltar"/>">Voltar</a></button>
      </s:form>
    </c:when>
    <c:otherwise>
      <h3>Não existem pessoas registadas</h3>
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
      <c:out value="Cartão de Cidadão: ${session.searchPessoa.cc}" /><br/>
      <c:out value="Validade Cartão de Cidadão: ${session.searchPessoa.validade}" /><br/>
      <c:out value="Departamento a que pertence: ${session.searchPessoa.grupo}" /><br/>
      <c:out value="Eleições e respetivas mesas de voto em que votou: " /><br/>
      <c:forEach items="${session.searchPessoa.votou}" var="entry">
        <c:out value="Eleição: ${entry.key} - Mesa de voto: ${entry.value}"/> <br />
      </c:forEach>
    </c:when>
  </c:choose>


</body>
</html>
