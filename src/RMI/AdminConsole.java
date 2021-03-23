package RMI;

import java.io.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.LocateRegistry;

public class AdminConsole extends UnicastRemoteObject implements AdminConsoleInterface {

    AdminConsole() throws RemoteException {
        super();
    }

    public static void main(String[] args) {
        String command;

        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());

        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        try {
            RMIServerInterface server = (RMIServerInterface) LocateRegistry.getRegistry(7001).lookup("Server");
            AdminConsole admin = new AdminConsole();
            server.olaAdmin(admin);
            System.out.println("Admin informou server que está ligado");
            server.showMenu();
            while (true) {
                System.out.print("> ");
                command = reader.readLine();
                server.readCommand(command);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
