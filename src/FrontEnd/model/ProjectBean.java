package FrontEnd.model;

import java.rmi.registry.LocateRegistry;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import RMI.RMIServerInterface;


public class ProjectBean {
    private RMIServerInterface server;

    public ProjectBean() {
        try {
            server = (RMIServerInterface) LocateRegistry.getRegistry(7000).lookup("server");
        } catch (NotBoundException | RemoteException e){
            e.printStackTrace();
        }
    }
}
