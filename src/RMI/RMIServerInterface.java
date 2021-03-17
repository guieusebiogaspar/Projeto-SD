package RMI;

import java.rmi.*;

public interface RMIServerInterface extends Remote {
    // isto tudo depois é substituiido por um objeto client
    public void register(String name, String nickname, String password, String phone, String morada, String cc, String ncc) throws java.rmi.RemoteException;
    // Aqui será tambem objeto (?) e falta pensar na maneira de restringir as eleições
    public void create_election(String startDate, String endDate, String title, String description) throws RemoteException;
    // objeto
    public void edit_election(String startDate, String endDate, String title, String description) throws RemoteException;
    // objeto
    public void end_election(String startDate, String endDate, String title, String description) throws RemoteException;

    public void check_results() throws RemoteException;
}