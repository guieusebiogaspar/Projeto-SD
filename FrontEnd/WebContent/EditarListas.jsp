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
<jsp:useBean id="p" class="FrontEnd.model.ProjectBean" />
<c:choose>
    <c:when test="${session.loggedin.equals('admin')}">
        <s:form action="addRmvLista" method="post">
            <s:text name="Adicionar lista: "/>
            <s:textfield name="adicionaLista" /><br/>
            <s:text name="Remover lista: "/>
            <s:textfield name="removeLista" /><br/>
            <s:submit type = "button"><s:text name="Editar listas"/></s:submit>
        </s:form>
        <h1>Listas da eleição ${session.searchEleicao.titulo}</h1>
        <c:choose>
            <c:when test="${session.searchEleicao.listas.size() > 0}">
                <c:forEach items="${session.searchEleicao.listas}" var="value">
                    <c:out value="${value.nome}" /><br>
                </c:forEach>


                <h3>Introduza o título da lista que pretende editar</h3>

                <s:form action="detalheslista" method="post">
                    <s:text name="Lista" /><br/>
                    <s:textfield name="lista" required="required"/><br/>
                    <s:submit type = "button"><s:text name="Ver"/></s:submit>
                    <button><a href="<s:url action="voltar"/>">Voltar</a></button>
                </s:form>
            </c:when>
            <c:otherwise>
                <h3>Não existem listas nesta eleicao</h3>
                <button><a href="<s:url action="voltar"/>">Voltar</a></button>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${session.lista != null}">
                <h3>Detalhes da lista</h3>
                <c:out value="Nome: ${session.lista.nome}" /><br/>
                <c:out value="Membros: " /><br/>
                <c:forEach items="${session.lista.membros}" var="value">
                    <c:out value="${value.nome} - ${value.cc}; "/><br/>
                </c:forEach>
                <br/>
                <br/>

                <c:out value="Pessoas que não pertencem à lista e podem pertencer: " /><br/>
                <c:forEach items="${p.pessoasValidas(session.searchEleicao)}" var="value">
                    <c:out value="${value.nome} - ${value.cc}; "/><br/>
                </c:forEach>

                <h3>Introduza os dados da lista que pretende editar</h3>

                <s:form action="editarlista" method="post">
                    <s:text name="Nome: " />
                    <s:textfield name="nomeLista"/><br/>
                    <s:text name="Adicionar pessoa (cc) : "/>
                    <s:textfield name="adicionaPess" /><br/>
                    <s:text name="Remover pessoa (cc): "/>
                    <s:textfield name="removePess" /><br/>
                    <s:submit type = "button"><s:text name="Editar lista"/></s:submit>
                </s:form>
            </c:when>
        </c:choose>
    </c:when>
    <c:otherwise>
        <jsp:include page="index.jsp"></jsp:include>
    </c:otherwise>
</c:choose>

</body>
</html>