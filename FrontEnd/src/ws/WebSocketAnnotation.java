package ws;

import RMI.RMIServerInterface;
import RMI.WebSocketInterface;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnError;
import javax.websocket.Session;

@ServerEndpoint(value = "/ws")
public class WebSocketAnnotation extends UnicastRemoteObject implements WebSocketInterface {
    private Session session;
    private RMIServerInterface server;

    public static final Set<WebSocketAnnotation> users = new CopyOnWriteArraySet<>();

    public WebSocketAnnotation() throws RemoteException {
        super();
    }

    @OnOpen
    public void start(Session session) throws RemoteException, NotBoundException {
        try{
            server = (RMIServerInterface) LocateRegistry.getRegistry(7001).lookup("Server");
            WebSocketAnnotation ws = new WebSocketAnnotation();
            server.subscribe(ws);
        } catch (NotBoundException | RemoteException e){
            e.printStackTrace();
        }

        users.add(this);
        this.session = session;
    }

    @OnClose
    public void end() {
        // clean up once the WebSocket connection is closed
        users.remove(this);
    }

    @OnMessage
    public void receiveMessage(String message) {
        if(message.contains("logado")) {
            String nome[] = message.split(" ");
            sendMessage(nome[1]);
        }// else if (message.equals("users online")){

    }

    @OnError
    public void handleError(Throwable t) {
        t.printStackTrace();
    }

    public void sendMessage(String text) {
        System.out.println(text);
        // uses *this* object's session to call sendText()
        for(WebSocketAnnotation u: users) {
            try {
                u.session.getBasicRemote().sendText(text);
            } catch (IOException e) {
                // clean up once the WebSocket connection is closed
                try {
                    this.session.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
