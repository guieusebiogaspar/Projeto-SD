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
  <h1>Consola de administração</h1>
  <p>Bem-vindo ${session.username}</p>
  <a href="<s:url action="registar" />">Registar pessoas</a><br/>
  <a href="<s:url action="criareleicao" />">Criar eleição</a><br/>
  <a href="<s:url action="editareleicao" />">Editar eleição</a><br/>
  <a href="<s:url action="detalhespessoas" />">Detalhes de pessoas</a><br/>
  <a href="<s:url action="detalheseleicoes" />">Detalhes de eleições</a><br/>
  <a href="<s:url action="estadomesas" />">Estado das mesas de voto ON/OFF</a><br/>
  <a href="<s:url action="logout" />">Sair</a><br/>
</body>
</html>
