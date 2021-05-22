<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <link rel="stylesheet" type="text/css" href="facil.css">
  <title>eVoting</title>
  <script type="text/javascript">

    var websocket = null;

    window.onload = function() { // URI = ws://10.16.0.165:8080/WebSocket/ws
      connect('wss://' + window.location.host + '/eVoting/ws');
    }

    function connect(host) { // connect to the host websocket
      if ('WebSocket' in window)
        websocket = new WebSocket(host);
      else if ('MozWebSocket' in window)
        websocket = new MozWebSocket(host);
      else {
        writeToHistory('Get a real browser which supports WebSocket.');
        return;
      }

      websocket.onopen    = onOpen; // set the 4 event listeners below
      websocket.onclose   = onClose;
      websocket.onmessage = onMessage;
      websocket.onerror   = onError;
    }

    function onOpen(event) {
      let username = "<%=session.getAttribute("username")%>";
      doSend("logado " + username)
    }

    function onClose(event) {
      writeToHistory('WebSocket closed (code ' + event.code + ').');
    }

    function onMessage(message) { // print the received message
      console.log("aqui recebi");
    }

    function onError(event) {
      writeToHistory('WebSocket error.');
    }

    function doSend(message) {
        websocket.send(message); // send the message to the server
    }

  </script>
</head>
<body>
  <c:choose>
    <c:when test="${session.loggedin.equals('eleitor')}">
      <h1>Terminal voto</h1>
      <p>Bem-vindo ${session.username}</p>
      <c:choose>
        <c:when test="${projectBean.ativasVoto.size() > 0}">
          <p>Em que eleição pretende votar?</p>
          <c:forEach items="${projectBean.ativasVoto}" var="value">
            <c:out value="${value.titulo}" /><br>
          </c:forEach>

          <br/>
          <s:form action="mostraListas" method="post">
            <s:text name="Eleição: " />
            <s:textfield name="eleicao" required="required"/><br/>
            <s:submit type = "button"><s:text name="Ver listas candidatas"/></s:submit>
            <button><a href="<s:url action="logout"/>">Sair</a></button>
          </s:form>
        </c:when>
        <c:otherwise>
          <h3>Não existem eleições ativas a que esteja habilitado a votar</h3>
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
              <h3>Esta eleição não contém nenhuma lista candidata</h3>
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
