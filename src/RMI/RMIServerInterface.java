package RMI;

import Multicast.MesaVoto;

import java.rmi.*;
import java.util.ArrayList;

public interface RMIServerInterface extends Remote {


    public void olaAdmin(AdminConsoleInterface adm) throws RemoteException;

    public void adeusAdmin() throws RemoteException;

    public void olaMesaVoto(String mesa) throws RemoteException;

    public void registar(Pessoa pessoa) throws RemoteException;

    public void criarEleição(Eleição eleição) throws RemoteException;
    public Eleição getEleição(String titulo) throws RemoteException;
    public boolean verificaCC(int cc) throws RemoteException;
    public ArrayList<Pessoa> getPessoas() throws RemoteException;
    public Pessoa verificaEleitor(int cc) throws RemoteException;
    public boolean loginUser(String username, String password, int cc) throws RemoteException;
    public void atualizaDescricao(Eleição eleição, String newDescri) throws RemoteException;
    public void atualizaDataInicio(Eleição eleição, DataEleição newInicio) throws RemoteException;
    public void atualizaDataFim(Eleição eleição, DataEleição newFim) throws RemoteException;
    public void addGrupo(Eleição eleição, String grupo) throws RemoteException;
    public int rmvGrupo(Eleição eleição, String grupo) throws RemoteException;
    public void writeBD(String name) throws RemoteException;
    public void atualizaTitulo(Eleição eleição, String newTitle) throws RemoteException;
    public void readBD(String name) throws RemoteException;
    public void mostraEleicoesAtivas() throws RemoteException;
    public ArrayList<Eleição> getEleições() throws  RemoteException;
    public void terminarEleição(Eleição eleição) throws RemoteException;
    public void check_results() throws RemoteException;
    public ArrayList<Eleição> filterEleições(String departamento, int cc) throws RemoteException;
}