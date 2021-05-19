<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
		 pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>eVoting</title>
</head>
<body>
	<h1>Bem-vindo ao eVotimg!</h1>

	<s:form action="login" method="post">
		<s:text name="Username" /><br/>
		<s:textfield name="username" required="required"/><br/>
		<s:text name="Password" /><br/>
		<s:textfield name="password" required="required"/><br/>
		<s:submit type = "button"><s:text name="Login"/></s:submit>
	</s:form>

	<p>Caso não tenha conta, entre em contacto nos seguintes e-mails:</p>
	<p>uc2018278327@student.uc.pt</p>
	<p>mail do gaspar@student.uc.pt</p>
</body>
</html>