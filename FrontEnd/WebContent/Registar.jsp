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
    <h1>Registar pessoa</h1>

    <h3>Introduza os dados da pessoa que pretende registar</h3>

    <s:form action="registarpessoa" method="post">
      <s:text name="Tipo (Estudante, Docente, Funcionário): " />
      <s:textfield name="tipo" required="required"/><br/>
      <s:text name="Nome: " />
      <s:textfield name="nome" required="required"/><br/>
      <s:text name="Username: " />
      <s:textfield name="username" required="required"/><br/>
      <s:text name="Password: " />
      <s:textfield name="password" required="required"/><br/>
      <s:text name="Phone: " />
      <s:textfield name="phone" required="required"/><br/>
      <s:text name="Morada: " />
      <s:textfield name="morada" required="required"/><br/>
      <s:text name="Cartão de cidadão: " />
      <s:textfield name="cc" required="required"/><br/>
      <s:text name="Validade do cartão de cidadão (MM/AA): " />
      <s:textfield name="validade" required="required"/><br/>
      <s:text name="Departamento a que pertence: " />
      <s:textfield name="grupo" required="required"/><br/>
      <s:submit type = "button"><s:text name="Ver"/></s:submit>
      <button><a href="<s:url action="voltar"/>">Voltar</a></button>
    </s:form>

</body>
</html>
