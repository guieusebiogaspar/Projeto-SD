package RMI;

import Multicast.MesaVoto;

import java.rmi.*;

public interface RMIServerInterface extends Remote {

    public void olaAdmin(AdminConsoleInterface adm) throws RemoteException;

    public void adeusAdmin() throws RemoteException;

    public void olaMesaVoto(String mesa) throws RemoteException;

    public void registar(Pessoa pessoa) throws RemoteException;

    public void criarEleição(Eleição eleição) throws RemoteException;

    public Eleição getEleição(String titulo) throws RemoteException;

    public boolean verificaCC(int cc) throws RemoteException;

    public Pessoa verificaEleitor(int cc) throws RemoteException;

    public boolean loginUser(String username, String password) throws RemoteException;

    public void writeBD() throws RemoteException;

    public void readBD() throws RemoteException;

    public void check_results() throws RemoteException;
}