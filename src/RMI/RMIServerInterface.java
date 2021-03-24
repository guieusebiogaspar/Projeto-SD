package RMI;

import java.rmi.*;

public interface RMIServerInterface extends Remote {

    public void olaAdmin(AdminConsoleInterface adm) throws RemoteException;

    public void registar(Pessoa pessoa) throws RemoteException;

    public void criarEleição(Eleição eleição) throws RemoteException;

    public Eleição getEleição(String titulo) throws RemoteException;

    public void writeBD() throws RemoteException;

    public void readBD() throws RemoteException;

    public void check_results() throws RemoteException;
}