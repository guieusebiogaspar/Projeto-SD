<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <title>eVoting</title>
</head>
<body>
  <c:choose>
    <c:when test="${session.loggedin.equals('eleitor')}">
      <h1>Terminal voto</h1>
      <p>Bem-vindo ${session.username}</p>
      <p>Em que elei��o pretende votar?</p>
      <c:choose>
        <c:when test="${projectBean.eleicoes.size() > 0}">
          <h2>Ativas</h2>
          <c:forEach items="${projectBean.ativasVoto}" var="value">
            <c:out value="${value.titulo}" /><br>
          </c:forEach>

          <s:form action="mostraListas" method="post">
            <s:text name="Elei��o" /><br/>
            <s:textfield name="eleicao" required="required"/><br/>
            <s:submit type = "button"><s:text name="Ver listas candidatas"/></s:submit>
            <button><a href="<s:url action="logout"/>">Sair</a></button>
          </s:form>
        </c:when>
        <c:otherwise>
          <h3>N�o existem elei��es ativas a que esteja habilitado a votar</h3>
          <button><a href="<s:url action="logout"/>">Sair</a></button>
        </c:otherwise>
      </c:choose>

      <c:choose>
        <c:when test="${session.searchEleicao != null}">
          <c:choose>
            <c:when test="${session.searchEleicao.listas.size() > 0}">
              <c:forEach items="${session.searchEleicao.listas}" var="value">
                <c:out value="${value.nome}" /><br>
              </c:forEach>

              <h3>Introduza a lista em que pretende votar</h3>

              <s:form action="votarLista" method="post">
                <s:text name="Lista" /><br/>
                <s:textfield name="lista" required="required"/><br/>
                <s:submit type = "button"><s:text name="Votar"/></s:submit>
              </s:form>
            </c:when>
            <c:otherwise>
              <h3>Esta elei��o n�o cont�m nenhuma lista candidata</h3>
            </c:otherwise>
          </c:choose>
        </c:when>
      </c:choose>
    </c:when>
    <c:otherwise>
      <jsp:include page="index.jsp"></jsp:include>
    </c:otherwise>
  </c:choose>
</body>
</html>