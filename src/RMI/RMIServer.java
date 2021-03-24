package RMI;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

import static java.lang.Thread.sleep;


public class RMIServer extends UnicastRemoteObject implements RMIServerInterface{
    static AdminConsoleInterface admin;
    private static ArrayList<Pessoa> pessoas;
    private static ArrayList<Eleição> eleições;

    public RMIServer() throws RemoteException {
        super();
    }

    public void olaAdmin(AdminConsoleInterface adm) throws RemoteException {
        System.out.println("Admin entrou no server");
        admin = adm;
    }

    public void registar(Pessoa pessoa) throws RemoteException {
        pessoas.add(pessoa);
        System.out.println("----- Pessoas inscritas -----");
        for(int i = 0; i < pessoas.size(); i++) {
            System.out.println(pessoa.getNome());
        }
    }

    public void criarEleição(Eleição eleição) throws RemoteException {
        eleições.add(eleição);
        System.out.println("----- Pessoas inscritas -----");
        for(int i = 0; i < eleições.size(); i++) {
            System.out.println(eleição.getTitulo());
        }
    }

    public void check_results() throws RemoteException {
    }

    public void writeBD() throws RemoteException {

    }

    public void readBD() throws RemoteException {

    }

    public static void main(String args[]) {
        String command;
        pessoas = new ArrayList<>();

        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());

        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        try {
            RMIServer server = new RMIServer();

            Registry r = LocateRegistry.createRegistry(7001);
            r.rebind("Server", server);
            System.out.println("RMI Server ready.");
            while (true) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
