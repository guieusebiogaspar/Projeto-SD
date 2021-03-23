package RMI;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.net.*;
import java.io.*;


public class RMIServer extends UnicastRemoteObject implements RMIServerInterface{
    static AdminConsoleInterface admin;

    public RMIServer() throws RemoteException {
        super();
    }

    public void olaAdmin(AdminConsoleInterface adm) throws RemoteException {
        System.out.println("Admin entrou no server");
        admin = adm;
    }


    public void showMenu() throws RemoteException {
        System.out.println("--------- MENU ---------");
        System.out.println("[1] - Registar pessoa");
        System.out.println("[2] - Criar eleição");
        System.out.println("[3] - Editar eleição");
        System.out.println("[4] - Terminar eleição");
    }

    public void readCommand(String command) throws RemoteException {
    }

    public void register(String name, String nickname, String password, String phone, String morada, String cc, String ncc) throws RemoteException {
    }

    public void createElection(String startDate, String endDate, String title, String description) throws RemoteException {

    }

    public void editElection(String startDate, String endDate, String title, String description) throws RemoteException {

    }

    public void endElection(String startDate, String endDate, String title, String description) throws RemoteException {

    }


    public void check_results() throws RemoteException {
    }

    public void writeBD() throws RemoteException {

    }

    public void readBD() throws RemoteException {

    }

    public static void main(String args[]) {
        String a;

        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());

        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        try {
            //User user = new User();
            RMIServer server = new RMIServer();

            Registry r = LocateRegistry.createRegistry(7001);
            r.rebind("Server", server);
            System.out.println("RMI Server ready.");
        } catch (Exception re) {
            System.out.println("Exception in HelloImpl.main: " + re);
        }
    }

}
