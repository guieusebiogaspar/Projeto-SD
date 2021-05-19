package RMI;

import java.rmi.*;
import java.util.ArrayList;

public interface RMIServerInterface extends Remote {

	String contaVotos(String mesa, String eleição) throws RemoteException;
	boolean verificaUsername(String username) throws RemoteException;
	ArrayList<Eleição> getPorComecar() throws  RemoteException;
	ArrayList<Eleição> getTerminadas() throws  RemoteException;
	ArrayList<Eleição> getAtivas() throws  RemoteException;
	String loginUserFrontEnd(String username, String password) throws RemoteException;
	//void olaAdmin(AdminConsoleInterface adm, String localaddress) throws RemoteException, NotBoundException;
	void adeusAdmin() throws RemoteException;
	int obterValor() throws RemoteException;
	int olaMesaVoto(String mesa) throws RemoteException;
	void registar(Pessoa pessoa) throws RemoteException;
	int rmvLista(Eleição eleição, String lista) throws RemoteException;
	boolean addLista(Eleição eleição, String lista) throws RemoteException;
	void criarEleição(Eleição eleição) throws RemoteException;
	Eleição getEleição(String titulo) throws RemoteException;
	boolean verificaCC(int cc) throws RemoteException;
	boolean verificaEleicao(String nome) throws RemoteException;
	void mudaNomeLista(Eleição eleição, String nome, String novoNome) throws RemoteException;
	boolean verificaLista(Eleição eleição, String nome) throws RemoteException;
	ArrayList<Pessoa> getPessoas() throws RemoteException;
	Pessoa verificaEleitor(int cc) throws RemoteException;
	boolean loginUser(String username, String password, int cc) throws RemoteException;
	void atualizaDescricao(Eleição eleição, String newDescri) throws RemoteException;
	void atualizaDataInicio(Eleição eleição, DataEleição newInicio) throws RemoteException;
	void atualizaDataFim(Eleição eleição, DataEleição newFim) throws RemoteException;
	void printOnServer(String departamento, int terminais) throws RemoteException;
	void addGrupo(Eleição eleição, String grupo) throws RemoteException;
	int rmvGrupo(Eleição eleição, String grupo) throws RemoteException;
	int rmvMesa(Eleição eleição, String mesa) throws RemoteException;
	void addMesa(Eleição eleição, String mesa) throws RemoteException;
	void adicionaPessoaLista(Eleição eleição, String lista, Pessoa pessoa) throws RemoteException;
	void removePessoaLista(Eleição eleição, String lista, Pessoa pessoa) throws RemoteException;
	void writeBD(String name) throws RemoteException;
	void atualizaTitulo(Eleição eleição, String newTitle) throws RemoteException;
	void readBD(String name) throws RemoteException;
	ArrayList<Eleição> getEleições() throws  RemoteException;
	Pessoa getPessoa(String cc) throws RemoteException;
	ArrayList<Eleição> filterEleições(String departamento, int cc) throws RemoteException;
	int adicionaVoto(Eleição eleição, String lista, int cc, String departamento, String momento) throws RemoteException;
	void pessoaAVotar(int cc, boolean estado) throws RemoteException;
	void verificaOnServer(String departamento) throws RemoteException;
}