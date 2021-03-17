package RMI;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.net.*;
import java.io.*;


public class RMIServer extends UnicastRemoteObject implements RMIServerInterface{

    public RMIServer() throws RemoteException {
        super();
    }

    @Override
    public void register(String name, String nickname, String password, String phone, String morada, String cc, String ncc) throws RemoteException {

    }

    @Override
    public void create_election(String startDate, String endDate, String title, String description) throws RemoteException {

    }

    @Override
    public void edit_election(String startDate, String endDate, String title, String description) throws RemoteException {

    }

    @Override
    public void end_election(String startDate, String endDate, String title, String description) throws RemoteException {

    }

    @Override
    public void check_results() throws RemoteException {

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
            r.rebind("callback", server);
            System.out.println("RMI Server ready.");
        } catch (Exception re) {
            System.out.println("Exception in HelloImpl.main: " + re);
        }
    }

}
