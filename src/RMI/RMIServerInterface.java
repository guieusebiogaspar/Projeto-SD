package RMI;

import java.rmi.*;

public interface RMIServerInterface extends Remote {

    public void olaAdmin(AdminConsoleInterface adm) throws RemoteException;

    public void showMenu() throws RemoteException;

    public void readCommand(String command) throws RemoteException;

    // isto tudo depois é substituiido por um objeto client
    public void register(String name, String nickname, String password, String phone, String morada, String cc, String ncc) throws java.rmi.RemoteException;
    // Aqui será tambem objeto (?) e falta pensar na maneira de restringir as eleições
    public void createElection(String startDate, String endDate, String title, String description) throws RemoteException;
    // objeto
    public void editElection(String startDate, String endDate, String title, String description) throws RemoteException;
    // objeto
    public void endElection(String startDate, String endDate, String title, String description) throws RemoteException;

    public void writeBD() throws RemoteException;

    public void readBD() throws RemoteException;

    public void check_results() throws RemoteException;
}