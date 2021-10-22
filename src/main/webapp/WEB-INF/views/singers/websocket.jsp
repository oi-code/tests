<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3"
	crossorigin="anonymous">
<head>
    <meta charset="UTF-8">
    <title>WebSocket Tester</title>
    <script language="javascript" type="text/javascript" src="http://code.jquery.com/jquery-2.1.1.min.js"></script>
<script language="javascript" type="text/javascript">
        var ping;
        var websocket;

        jQuery(function ($) {
            function writePing(message) {
                $('#pingOutput').append(message + '\n');
            }

            function writeStatus(message) {
                $("#statusOutput").val($("#statusOutput").val() + message + '\n');
            }

            function writeMessage(message) {
                $('#messageOutput').append(message + '\n')
            }

            $('#connect')
                .click(function doConnect() {
                    websocket = new WebSocket($("#target").val());

                    websocket.onopen = function (evt) {
                        writeStatus("CONNECTED");

                        var ping = setInterval(function () {
                            if (websocket != "undefined") {
                                websocket.send("ping");
                            }
                        }, 3000);
                    };

                    websocket.onclose = function (evt) {
                        writeStatus("DISCONNECTED");
                    };

                    websocket.onmessage = function (evt) {
                        if (evt.data === "ping") {
                            writePing(evt.data);
                        } else {
                            writeMessage('ECHO: ' + evt.data);
                        }
                    };

                    websocket.onerror = function (evt) {
                        onError(writeStatus('ERROR:' + evt.data))
                    };
                });

            $('#disconnect')
                .click(function () {
                    if (typeof websocket != 'undefined') {
                        websocket.close();
                        websocket = undefined;
                    } else {
                        alert("Not connected.");
                    }
                });

            $('#send')
                .click(function () {
                    if (typeof websocket != 'undefined') {
                        websocket.send($('#message').val());
                    } else {
                        alert("Not connected.");
                    }
                });
        });
</script>
</head>

<body>
<table class="table table-dark table-striped align-middle">
<tr>
<td align="center">
<h2>WebSocket Tester</h2>
Target:
<input type="text" id="target" size="40" value="ws://localhost:8080/thisPathWillBeNestedInJspPage"/><!--ws://localhost:8080/ch-->
<br/>
<button id="connect">Connect</button>
<button id="disconnect">Disconnect</button>
<br/>
<br/>Message:
<input type="text" id="message" value=""/>
<button id="send">Send</button>
<br/>
<p>Status output:</p>
<pre><textarea id="statusOutput" rows="10" cols="50"></textarea></pre>
<p>Message output:</p>
<pre><textarea id="messageOutput" rows="10" cols="50"></textarea></pre>
<p>Ping output:</p>
<pre><textarea id="pingOutput" rows="10" cols="50"></textarea></pre>
</tr>
</table>
</body>
</html>
<!--html>
<head>
    <meta charset="UTF-8">
    <title>SockJS Tester</title>
    <script language="javascript" type="text/javascript" src="https://d1fxtkz8shb9d2.cloudfront.net/sockjs-0.3.4.min.js"></script>
    <script language="javascript" type="text/javascript" src="http://code.jquery.com/jquery-2.1.1.min.js"></script>
    <script language="javascript" type="text/javascript">
        var ping;
        var sockjs;

        jQuery(function ($) {
            function writePing(message) {
                $('#pingOutput').append(message + '\n');
            }

            function writeStatus(message) {
                $("#statusOutput").val($("#statusOutput").val() + message + '\n');
            }

            function writeMessage(message) {
                $('#messageOutput').append(message + '\n')
            }

            $('#connect')
                    .click(function doConnect() {
                        sockjs = new SockJS($("#target").val());

                        sockjs.onopen = function (evt) {
                            writeStatus("CONNECTED");

                            var ping = setInterval(function () {
                                if (sockjs != "undefined") {
                                    sockjs.send("ping");
                                }
                            }, 3000);
                        };

                        sockjs.onclose = function (evt) {
                            writeStatus("DISCONNECTED");
                        };

                        sockjs.onmessage = function (evt) {
                            if (evt.data === "ping") {
                                writePing(evt.data);
                            } else {
                                writeMessage('ECHO: ' + evt.data);
                            }
                        };

                        sockjs.onerror = function (evt) {
                            onError(writeStatus('ERROR:' + evt.data))
                        };
                    });

            $('#disconnect')
                    .click(function () {
                        if(typeof sockjs != 'undefined') {
                            sockjs.close();
                            sockjs = undefined;
                        } else {
                            alert("Not connected.");
                        }
                    });

            $('#send')
                    .click(function () {
                        if(typeof sockjs != 'undefined') {
                            sockjs.send($('#message').val());
                        } else {
                            alert("Not connected.");
                        }
                    });
        });
    </script>
</head>

<body>
<h2>SockJS Tester</h2>
    Target:
    <input id="target" size="40" value="http://localhost:8080/ch"/>
    <br/>
    <button id="connect">Connect</button>
    <button id="disconnect">Disconnect</button>
    <br/>
    <br/>Message:
    <input id="message" value=""/>
    <button id="send">Send</button>
    <br/>
    <p>Status output:</p>
    <pre><textarea id="statusOutput" rows="10" cols="50"></textarea></pre>
    <p>Message output:</p>
    <pre><textarea id="messageOutput" rows="10" cols="50"></textarea></pre>
    <p>Ping output:</p>
    <pre><textarea id="pingOutput" rows="10" cols="50"></textarea></pre>
</body>
</html-->