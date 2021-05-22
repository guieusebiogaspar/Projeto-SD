<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>eVoting</title>
  <link rel="stylesheet" type="text/css" href="facil.css">
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
      // para receber logo os users online
      //doSend("users online");
      //writeToHistory('Connected to ' + window.location.host + '.');
    }

    function onClose(event) {
      writeToHistory('WebSocket closed (code ' + event.code + ').');
    }

    window.onbeforeunload = function() {
      websocket.onclose = function () {}; // disable onclose handler first
      websocket.close();
    };

    function onMessage(message) { // print the received message
        if(!message.data.includes("Voto")) {
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

  </script>
</head>
<body>
<noscript>JavaScript must be enabled for WebSockets to work.</noscript>
<div>
  <h1>Users online</h1>
  <div id="container"><div id="history"></div></div>
  <button><a href="<s:url action="voltar"/>">Voltar</a></button>
</div>
</body>
</html>
