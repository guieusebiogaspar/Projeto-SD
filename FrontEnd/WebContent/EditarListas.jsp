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
<c:choose>
    <c:when test="${session.loggedin.equals('admin')}">
        <h1>Listas da eleição ${session.searchEleicao.titulo}</h1>
        <c:choose>
            <c:when test="${session.searchEleicao.listas.size() > 0}">
                <c:forEach items="${session.searchEleicao.listas}" var="value">
                    <c:out value="${value.nome}" /><br>
                </c:forEach>

                <s:form action="editarLista" method="post">
                    <s:text name="Adicionar lista: "/>
                    <s:textfield name="adicionaLista" /><br/>
                    <s:text name="Remover lista: "/>
                    <s:textfield name="removeLista" /><br/>
                    <s:submit type = "button"><s:text name="Editar lista"/></s:submit>
                </s:form>
                <s:form action="detalheseleicaoeditar" method="post">
                    <s:text name="Eleição" /><br/>
                    <s:textfield name="eleicao" required="required"/><br/>
                    <s:submit type = "button"><s:text name="Ver"/></s:submit>
                    <button><a href="<s:url action="voltar"/>">Voltar</a></button>
                </s:form>
            </c:when>
            <c:otherwise>
                <h3>Não existem eleições por começar</h3>
                <button><a href="<s:url action="voltar"/>">Voltar</a></button>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${session.searchEleicao != null && !session.searchEleicao.terminada && !session.searchEleicao.ativa}">
                <h3>Detalhes da eleição</h3>
                <c:out value="Título: ${session.searchEleicao.titulo}" /><br/>
                <c:out value="Descrição: ${session.searchEleicao.descrição}" /><br/>
                <c:out value="Data de início: ${session.searchEleicao.inicio.toString()}" /><br/>
                <c:out value="Data fim: ${session.searchEleicao.fim.toString()}" /><br/>
                <c:out value="Departamentos: " /><br/>
                <c:out value="--- "></c:out>
                <c:forEach items="${session.searchEleicao.grupos}" var="value">
                    <c:out value="${value} "/>
                </c:forEach>
                <br/>
                <c:out value="Listas: " /><br/>
                <c:out value="--- "></c:out>
                <c:forEach items="${session.searchEleicao.listas}" var="value">
                    <c:when test="${!(value.nome.equals('Nulo') || value.nome.equals('Branco'))}">
                        <c:out value="${value.nome} "/>
                    </c:when>
                </c:forEach>
                <br/>
                <c:out value="Mesas de voto: " /><br/>
                <c:out value="--- "></c:out>
                <c:forEach items="${session.searchEleicao.mesasVoto}" var="value">
                    <c:out value="${value} "/>
                </c:forEach>

                <h3>Introduza os dados da eleição que pretende editar</h3>

                <s:form action="editarEleicao" method="post">
                    <s:text name="Título: " />
                    <s:textfield name="titulo"/><br/>
                    <s:text name="Descrição: " />
                    <s:textfield name="descricao"/><br/>
                    <s:text name="Data de início: " /><br/>
                    <s:text name="Dia: " />
                    <s:textfield type="number" name="diaInicio" />
                    <s:text name="Mês: " />
                    <s:textfield type="number" name="mesInicio" />
                    <s:text name="Ano: " />
                    <s:textfield type="number" name="anoInicio" />
                    <s:text name="Hora: " />
                    <s:textfield type="number" name="horaInicio" />
                    <s:text name="Minuto: " />
                    <s:textfield type="number" name="minutoInicio" /><br/>
                    <s:text name="Data de fim: " /><br/>
                    <s:text name="Dia: " />
                    <s:textfield type="number" name="diaFim" />
                    <s:text name="Mês: " />
                    <s:textfield type="number" name="mesFim" />
                    <s:text name="Ano: " />
                    <s:textfield type="number" name="anoFim" />
                    <s:text name="Hora: " />
                    <s:textfield type="number" name="horaFim" />
                    <s:text name="Minuto: " />
                    <s:textfield type="number" name="minutoFim" /><br/>
                    <s:text name="Adicionar departamento: "/>
                    <s:textfield name="adicionaDep" /><br/>
                    <s:text name="Remover departamento: "/>
                    <s:textfield name="removeDep" /><br/>
                    <s:text name="Adicionar mesa: "/>
                    <s:textfield name="adicionaMesa" /><br/>
                    <s:text name="Remover mesa: "/>
                    <s:textfield name="removeMesa" /><br/>
                    <button><a href="<s:url action="editarlis"/>">Editar listas</a></button>
                    <s:submit type = "button"><s:text name="Editar"/></s:submit>
                    <button><a href="<s:url action="voltar"/>">Voltar</a></button>
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