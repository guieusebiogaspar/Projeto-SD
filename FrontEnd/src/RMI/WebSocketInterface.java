package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WebSocketInterface extends Remote {
    void sendMessage(String text) throws RemoteException;
}
