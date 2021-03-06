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
            //writeToHistory('Connected to ' + window.location.host + '.');
        }

        function onClose(event) {
            writeToHistory('WebSocket closed (code ' + event.code + ').');
        }

        function onMessage(message) { // print the received message
            if(message.data.includes("Voto")) {
                writeToHistory(message.data);
            }
        }

        function onError(event) {
            writeToHistory('WebSocket error.');
        }

        function doSend(message) {
            websocket.send(message); // send the message to the server
        }

        function writeToHistory(text) {
            var history = document.getElementById('history');
            var line = document.createElement('p');
            line.style.wordWrap = 'break-word';
            line.innerHTML = text;
            history.appendChild(line);
            history.scrollTop = history.scrollHeight;
        }

        window.onbeforeunload = function() {
            websocket.onclose = function () {}; // disable onclose handler first
            websocket.close();
        };

    </script>
</head>
<body>
<jsp:useBean id="p" class="FrontEnd.model.ProjectBean" />
<c:choose>
    <c:when test="${session.loggedin.equals('admin')}">
        <h1>Detalhes de elei??es</h1>
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
                <h2>Por come?ar</h2>
                <c:forEach items="${projectBean.porComecar}" var="value">
                    <c:out value="${value.titulo}" /><br>
                </c:forEach>

                <h3>Introduza o t?tulo da elei??o que pretende ver informa??es</h3>

                <s:form action="detalheseleicao" method="post">
                    <s:text name="Elei??o" /><br/>
                    <s:textfield name="eleicao" required="required"/><br/>
                    <s:submit type = "button"><s:text name="Ver"/></s:submit>
                    <button><a href="<s:url action="voltar"/>">Voltar</a></button>
                </s:form>
            </c:when>
            <c:otherwise>
                <h3>N?o existem elei??es</h3>
                <button><a href="<s:url action="voltar"/>">Voltar</a></button>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${session.searchEleicao != null && session.searchEleicao.terminada}">
                <h3>Detalhes da elei??o</h3>
                <c:out value="T?tulo: ${session.searchEleicao.titulo}" /><br/>
                <c:out value="Descri??o: ${session.searchEleicao.descri??o}" /><br/>
                <c:out value="Data de in?cio: ${session.searchEleicao.inicio.toString()}" /><br/>
                <c:out value="Data fim: ${session.searchEleicao.fim.toString()}" /><br/>
                <c:out value="Departamentos: " /><br/>
                <c:out value="--- "></c:out>
                <c:forEach items="${session.searchEleicao.grupos}" var="value">
                    <c:out value="${value} "/>
                </c:forEach>
                <br/>

                <c:out value="Total de votos: ${projectBean.totalVotos}"/><br/>
                <c:out value="Dados Listas: " /><br/>
                <c:forEach items="${session.searchEleicao.listas}" var="value">
                    <c:choose>
                        <c:when test="${projectBean.totalVotos > 0}">
                            <c:out value=" --- Votos Lista ${value.nome}: ${value.numVotos} -> ${p.percentagemVotos(value.numVotos, session.searchEleicao)}%"/><br/>
                        </c:when>
                        <c:otherwise>
                            <c:out value=" --- Votos Lista ${value.nome}: ${value.numVotos} -> 0%"/><br/>
                        </c:otherwise>
                    </c:choose>
                    <c:out value="----- Membros:"></c:out><br/>
                    <c:forEach items="${value.membros}" var="value">
                        <c:out value="------- ${value.nome}"></c:out><br/>
                    </c:forEach>
                </c:forEach>
                <c:out value="Mesas de voto: " /><br/>
                <c:forEach items="${session.searchEleicao.mesasVoto}" var="value">
                    <c:out value="--- Mesa voto ${value}: ${p.contaVotos(value, session.searchEleicao.titulo)} eleitores"></c:out><br/>
                </c:forEach>

                <c:out value="Resultado: ${session.vencedora}"/><br/>
            </c:when>

            <c:when test="${session.searchEleicao != null && !session.searchEleicao.terminada}">
                <h3>Detalhes da elei??o</h3>
                <c:out value="T?tulo: ${session.searchEleicao.titulo}" /><br/>
                <c:out value="Descri??o: ${session.searchEleicao.descri??o}" /><br/>
                <c:out value="Data de in?cio: ${session.searchEleicao.inicio.toString()}" /><br/>
                <c:out value="Data fim: ${session.searchEleicao.fim.toString()}" /><br/>
                <c:out value="Departamentos: " /><br/>
                <c:out value="--- "></c:out>
                <c:forEach items="${session.searchEleicao.grupos}" var="value">
                    <c:out value="${value} "/>
                </c:forEach>
                <br/>
                <c:out value="Dados Listas: " /><br/>
                <c:forEach items="${session.searchEleicao.listas}" var="value">
                    <c:choose>
                        <c:when test="${value.nome.equals('Nulo') || value.nome.equals('Branco')}">
                            <c:out value=" --- Votos ${value.nome}: ${value.numVotos}"/><br/>
                        </c:when>
                        <c:otherwise>
                            <c:out value=" --- Votos Lista ${value.nome}: ${value.numVotos}"/><br/>
                        </c:otherwise>
                    </c:choose>
                    <c:out value="----- Membros:"></c:out><br/>
                    <c:forEach items="${value.membros}" var="value">
                        <c:out value="------- ${value.nome}"></c:out><br/>
                    </c:forEach>
                </c:forEach>
                <c:out value="Mesas de voto: " /><br/>
                <c:forEach items="${session.searchEleicao.mesasVoto}" var="value">
                    <c:out value="--- Mesa voto ${value}: ${p.contaVotos(value, session.searchEleicao.titulo)} eleitores"></c:out><br/>
                </c:forEach>
            </c:when>
        </c:choose>
        <div>
            <h1>Votos em direto</h1>
            <div id="container"><div id="history"></div></div>
        </div>
    </c:when>
    <c:otherwise>
        <jsp:include page="index.jsp"></jsp:include>
    </c:otherwise>
</c:choose>

</body>
</html>