package FrontEnd.model;

import RMI.Pessoa;
import RMI.RMIServerInterface;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;


public class ProjectBean {
    private RMIServerInterface server;
    private String username; // username and password supplied by the user
    private String password;
    private String cc;

    public ProjectBean() {
        try {
            server = (RMIServerInterface) LocateRegistry.getRegistry(7001).lookup("Server");
        } catch (NotBoundException | RemoteException e){
            e.printStackTrace();
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getUserMatchesPassword() throws IOException {
        if(server.loginUserFrontEnd(username, password).equals("admin")) {
            return "admin";
        } else if(server.loginUserFrontEnd(username, password).equals("eleitor")){
            return "eleitor";
        }
        else
        {
            return "nada";
        }
    }

    public ArrayList<Pessoa> getPessoas() throws RemoteException {
        return server.getPessoas();
    }

    public Pessoa getPessoa(String cc) throws RemoteException {
        return server.getPessoa(cc);
    }


}
