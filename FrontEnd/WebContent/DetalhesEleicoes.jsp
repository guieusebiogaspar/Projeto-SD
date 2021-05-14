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
    <h1>Detalhes de eleições</h1>
    <c:choose>
        <c:when test="${projectBean.eleicoes.size() > 0}">
            <h2>Ativas</h2>
            <c:forEach items="${projectBean.ativas}" var="value">
                <c:out value="${value.titulo}" /><br>
            </c:forEach>
            <h2>Terminadas</h2>
            <c:forEach items="${projectBean.terminadas}" var="value">
                <c:out value="${value.titulo}" /><br>
            </c:forEach>
            <h2>Por começar</h2>
            <c:forEach items="${projectBean.porComecar}" var="value">
                <c:out value="${value.titulo}" /><br>
            </c:forEach>

            <h3>Introduza o título da eleição que pretende ver informações</h3>

            <s:form action="detalheseleicao" method="post">
                <s:text name="Eleição" /><br/>
                <s:textfield name="eleicao" required="required"/><br/>
                <s:submit type = "button"><s:text name="Ver"/></s:submit>
                <button><a href="<s:url action="voltar"/>">Voltar</a></button>
            </s:form>
        </c:when>
        <c:otherwise>
            <h3>Não existem eleições</h3>
            <button><a href="<s:url action="voltar"/>">Voltar</a></button>
        </c:otherwise>
    </c:choose>

    <c:choose>
        <c:when test="${session.searchEleicao != null && !session.searchEleicao.ativa}">
            <h3>Detalhes da eleição</h3>
            <c:out value="Título: ${session.searchEleicao.titulo}" /><br/>
            <c:out value="Descrição: ${session.searchEleicao.descrição}" /><br/>
            <c:out value="Data de início: ${session.searchEleicao.inicio.toString()}" /><br/>
            <c:out value="Data fim: ${session.searchEleicao.fim.toString()}" /><br/>
            <c:out value="Grupos: " /><br/>
            <c:forEach items="${session.searchEleicao.grupos}" var="value">
                <c:out value="${value}"/>
            </c:forEach>
            <br/>
            <c:out value="Dados Listas: " /><br/>
            <c:forEach items="${session.searchEleicao.listas}" var="value">
                <c:choose>
                    <c:when test="${!(value.nome.equals(\"Nulo\") && value.nome.equals(\"Branco\"))}">
                        <c:out value="\tVotos ${value.nome}: ${value.numVotos}"/><br/>
                    </c:when>
                    <c:otherwise>
                        <c:out value="\tVotos Lista ${value.nome}: ${value.numVotos}"/><br/>
                    </c:otherwise>
                </c:choose>
                <c:when test="${value.membros.size() > 0}">
                    <c:out value="\tMembros:"></c:out><br/>
                    <c:forEach items="${value.membros}" var="value">
                        <c:out value="\t- ${value.nome}"></c:out><br/>
                    </c:forEach>
                </c:when>
            </c:forEach>
            <c:out value="Mesas de voto: " /><br/>
            <c:forEach items="${session.mesasVoto}" var="value">
                <c:out value="Mesa voto ${value}: "></c:out><br/>
            </c:forEach>
        </c:when>
    </c:choose>
</body>
</html>