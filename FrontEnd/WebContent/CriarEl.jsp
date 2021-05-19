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
        <h1>Criar Eleição</h1>

        <h3>Introduza os dados da eleição que pretende criar</h3>

        <s:form action="criarEleicao" method="post">
            <s:text name="Título: " />
            <s:textfield name="titulo" required="required"/><br/>
            <s:text name="Descrição: " />
            <s:textfield name="descricao" required="required"/><br/>
            <s:text name="Data de início: " /><br/>
            <s:text name="Dia: " />
            <s:textfield type="number" name="diaInicio" required="required"/>
            <s:text name="Mês: " />
            <s:textfield type="number" name="mesInicio" required="required"/>
            <s:text name="Ano: " />
            <s:textfield type="number" name="anoInicio" required="required"/>
            <s:text name="Hora: " />
            <s:textfield type="number" name="horaInicio" required="required"/>
            <s:text name="Minuto: " />
            <s:textfield type="number" name="minutoInicio" required="required"/><br/>
            <s:text name="Data de fim: " /><br/>
            <s:text name="Dia: " />
            <s:textfield type="number" name="diaFim" required="required"/>
            <s:text name="Mês: " />
            <s:textfield type="number" name="mesFim" required="required"/>
            <s:text name="Ano: " />
            <s:textfield type="number" name="anoFim" required="required"/>
            <s:text name="Hora: " />
            <s:textfield type="number" name="horaFim" required="required"/>
            <s:text name="Minuto: " />
            <s:textfield type="number" name="minutoFim" required="required"/><br/>
            <s:text name="Departamento que pode votar nesta eleição: "/>
            <s:select list="{'DEI','DEEC','DEM','DEC','DF','DQ','DM'}" name="grupoVotar"></s:select><br/>
            <s:text name="Local da mesa de voto desta eleição: "/>
            <s:select list="{'DEI','DEEC','DEM','DEC','DF','DQ','DM'}" name="mesa"></s:select><br/>
            <s:text name="Grupo de pessoas que pode votar: " />
            <s:select list="{'Estudante','Funcionário','Docente'}" name="opcao"></s:select><br/>
            <s:submit type = "button"><s:text name="Criar"/></s:submit>
            <button><a href="<s:url action="voltar"/>">Voltar</a></button>
        </s:form>
    </c:when>
    <c:otherwise>
        <jsp:include page="index.jsp"></jsp:include>
    </c:otherwise>
</c:choose>

</body>
</html>