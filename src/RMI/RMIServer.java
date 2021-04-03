package RMI;

import Multicast.MesaVoto;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

import static java.lang.Thread.getDefaultUncaughtExceptionHandler;
import static java.lang.Thread.sleep;


public class RMIServer extends UnicastRemoteObject implements RMIServerInterface, Serializable{
    static AdminConsoleInterface admin;
    static MesaVoto mesaVoto;
    private static ArrayList<Pessoa> pessoas = new ArrayList<>();
    private static ArrayList<Eleição> eleições = new ArrayList<>();

    public RMIServer() throws RemoteException {
        super();
    }

    public void olaAdmin(AdminConsoleInterface adm) throws RemoteException {
        System.out.println("Admin entrou no server");
        admin = adm;
        admin.olaServidor();
    }

    public void adeusAdmin() throws RemoteException {
        System.out.println("Admin saiu do server");
        admin.adeusServidor();
    }

    public void olaMesaVoto(String mesa) throws RemoteException {
        System.out.println("Mesa de voto entrou no server");
    }

    public void registar(Pessoa pessoa) throws RemoteException {
        pessoas.add(pessoa);
        System.out.println("----- Pessoas inscritas -----");
        System.out.println("Nome\t\tCC");
        for(int i = 0; i < pessoas.size(); i++) {
            System.out.println(pessoas.get(i).getNome() + " \t" + pessoas.get(i).getCc());
        }
        writeBD("pessoas.obj");
    }

    public void criarEleição(Eleição eleição) throws RemoteException {
        eleições.add(eleição);
        System.out.println("----- Eleições criadas -----");
        for(int i = 0; i < eleições.size(); i++) {
            System.out.println(eleições.get(i).getTitulo());
        }
        writeBD("eleicoes.obj");

    }

    public void terminarEleição(Eleição eleição) throws RemoteException
    {
        for(Eleição el: eleições)
        {
            if(el.getTitulo().equals(eleição.getTitulo()))
            {
                el.setAtiva(false);
            }
        }
        writeBD("eleicoes.obj");

    }
    public void atualizaTitulo(Eleição eleição, String newTitle) throws RemoteException
    {
        for(Eleição el : eleições)
        {
            if(el.getTitulo().equals(eleição.getTitulo()))
            {
                el.setTitulo(newTitle);
            }
        }
        writeBD("eleicoes.obj");
    }
    public void atualizaDescricao(Eleição eleição, String newDescri) throws RemoteException
    {
        for(Eleição el: eleições)
        {
            if(el.getTitulo().equals(eleição.getTitulo()))
            {
                el.setDescrição(newDescri);
            }
        }
        writeBD("eleicoes.obj");
    }
    public void atualizaDataInicio(Eleição eleição, DataEleição newInicio) throws RemoteException
    {
        for(Eleição el : eleições)
        {
            if(el.getTitulo().equals(eleição.getTitulo()))
            {
                el.setInicio(newInicio);
            }
        }
        writeBD("eleicoes.obj");
    }
    public void atualizaDataFim(Eleição eleição, DataEleição newFim) throws RemoteException
    {
        for(Eleição el: eleições)
        {
            if(el.getTitulo().equals(eleição.getTitulo()))
            {
                el.setFim(newFim);
            }
        }
        writeBD("eleicoes.obj");
    }
    public void addGrupo(Eleição eleição, String grupo) throws RemoteException
    {
        for(Eleição el : eleições)
        {
            if(el.getTitulo().equals(eleição.getTitulo()))
            {
                el.getGrupos().add(grupo);
            }
        }
        writeBD("eleicoes.obj");
    }

    public int rmvGrupo(Eleição eleição, String grupo) throws RemoteException
    {
        for(Eleição el : eleições)
        {
            if(el.getTitulo().equals(eleição.getTitulo()))
            {
                for(String s : el.getGrupos())
                {
                    if(s.equals(grupo))
                    {
                        int i = el.getGrupos().indexOf(s);
                        el.getGrupos().remove((i));
                        writeBD("eleicoes.obj");
                        return 1;
                    }
                }
            }
        }
        return 0;
    }
    public void addLista(Eleição eleição, String lista) throws RemoteException
    {
        for(Eleição el : eleições)
        {
            if(el.getTitulo().equals(eleição.getTitulo()))
            {
                Lista l = new Lista(lista);
                el.getListas().add(l);
            }
        }
        writeBD("eleicoes.obj");
    }
    public int rmvLista(Eleição eleição, String lista) throws RemoteException
    {
        for(Eleição el : eleições)
        {
            if(el.getTitulo().equals(eleição.getTitulo()))
            {
                for(Lista s : el.getListas())
                {
                    if(s.getNome().equals(lista))
                    {
                        int i = el.getListas().indexOf(s);
                        el.getListas().remove((i));
                        writeBD("eleicoes.obj");
                        eleições = null;
                        readBD("eleicoes.obj");
                        return 1;
                    }
                }
            }
        }
        return 0;
    }
    /**
     * Método que vai verificar se o cc introduzido já existe na base de dados
     *
     * @param cc
     *
     *  @return true se o cc não existe e false se o cc ja existe
     */
    public boolean verificaCC(int cc) throws RemoteException {

        for(int i = 0; i < pessoas.size(); i++) {
            if(pessoas.get(i).getCc() == cc) {
                return true;
            }
        }
        return false;
    }

    public Pessoa verificaEleitor(int cc) throws RemoteException {
        for(int i = 0; i < pessoas.size(); i++) {
            if(pessoas.get(i).getCc() == cc) {
                return pessoas.get(i);
            }
        }
        return null;
    }

    public boolean loginUser(String username, String password, int cc) throws RemoteException {
        for(int i = 0; i < pessoas.size(); i++) {
            if(pessoas.get(i).getNickname().equals(username) && pessoas.get(i).getPassword().equals(password) && pessoas.get(i).getCc() == cc) {
                return true;
            }
        }
        return false;
    }

    /**
     * Método que vai receber do cliente o titulo de uma eleição.
     * Se a eleição existir na BD devolve o objeto da mesma, se não existir devolve null.
     *
     * @param titulo
     *
     *  @return eleição - objeto eleição com o título dado como input
     */
    public Eleição getEleição(String titulo) throws RemoteException {
        eleições = null;
        readBD("eleicoes.obj");
        for(Eleição el: eleições)
        {
            if(el.getTitulo().equals(titulo))
                return el;
        }
        return null;
    }

    public ArrayList<Eleição> getEleições() throws  RemoteException
    {
        readBD("eleicoes.obj");
        return eleições;
    }
    public void mostraEleicoesAtivas() throws RemoteException{
        eleições = null;
        readBD("eleicoes.obj");
        for(Eleição el : getEleições())
        {
            System.out.println(el.getTitulo());
        }
    }
    public ArrayList<Pessoa> getPessoas() throws RemoteException{
        return pessoas;
    }
    public void check_results() throws RemoteException {

    }

    /**
     * Método que vai enviar a uma mesa de voto as eleições em que o eleitor identificado está autorizado a votar
     *
     * @param departamento - departamento onde está localizada a Mesa de Voto
     * @param cc - Cartão de Cidadão do eleitor
     *
     * @return eleições que o eleitor pode votar
     */
    public ArrayList<Eleição> filterEleições(String departamento, int cc) throws RemoteException {

        ArrayList<Eleição> filtradas = new ArrayList<>();
        String tipo = null; // De que tipo é o Eleitor
        String dep = null; // Departamento a que pertence o eleitor
        for (int i = 0; i < pessoas.size(); i++) {
            if(pessoas.get(i).getCc() == cc) {
                tipo = pessoas.get(i).getTipo();
                dep = pessoas.get(i).getGrupo();
                break;
            }
        }

        if(tipo != null && dep != null) {
            for (int i = 0; i < eleições.size(); i++) {
                // Se a eleição tiver a mesa de voto em questão
                // Se a pessoa pertencer ao tipo de pessoas que pode votar (Estudantes, Docentes ou Funcionários)
                // Se a pessoa pertence a um dos departamentos que podem votar na eleição
                if(eleições.get(i).getMesasVoto().contains(departamento) &&
                    eleições.get(i).getQuemPodeVotar().contains(tipo) &&
                    eleições.get(i).getGrupos().contains(dep))
                {
                    filtradas.add(eleições.get(i));
                }
            }
        }

        return filtradas;
    }

    public void writeBD(String name) throws RemoteException {
        File f = new File(name);
        try{
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            if(name.equals("pessoas.obj"))
                oos.writeObject(pessoas);
            if(name.equals("eleicoes.obj"))
                oos.writeObject(eleições);
            oos.close();
        }
        catch (FileNotFoundException ex){
            System.out.println("Erro a criar ficheiro.");
        }
        catch(IOException ex){
            System.out.println("Erro a escrever para o ficheiro.");
        }
    }

    public void readBD(String name) throws RemoteException {
        File f = new File(name);
        if(f.exists() && f.isFile())
        {
            try{
                FileInputStream fis = new FileInputStream(f);
                ObjectInputStream ois = new ObjectInputStream(fis);
                if(name.equals("pessoas.obj"))
                    pessoas = (ArrayList<Pessoa>)ois.readObject();
                if(name.equals("eleicoes.obj"))
                    eleições = (ArrayList<Eleição>)ois.readObject();
                ois.close();

            }
            catch(FileNotFoundException ex){
                System.out.println("Erro a abrir ficheiro.");
            }
            catch(IOException ex){
                System.out.println("Erro a ler ficheiro.");
            }
            catch(ClassNotFoundException ex){
                System.out.println("Erro a converter objeto.");
            }
        }
    }

    public static void main(String args[]) throws RemoteException {
        String command;

        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());
        //System.setProperty("java.rmi.server.hostname", "192.168.1.171");


        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        try {

            RMIServer server = new RMIServer();

            Registry r = LocateRegistry.createRegistry(7001);
            r.rebind("Server", server);
            File f = new File("pessoas.obj");
            if(f.exists() && f.isFile())
            {
                try{
                    FileInputStream fis = new FileInputStream(f);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    pessoas = (ArrayList<Pessoa>)ois.readObject();
                    ois.close();

                }
                catch(FileNotFoundException ex){
                    System.out.println("Erro a abrir ficheiro.");
                }
                catch(IOException ex){
                    System.out.println("Erro a ler ficheiro. Ainda nao existe");
                }
                catch(ClassNotFoundException ex){
                    System.out.println("Erro a converter objeto.");
                }
            }
            f = new File("eleicoes.obj");
            if(f.exists() && f.isFile())
            {
                try{
                    FileInputStream fis = new FileInputStream(f);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    eleições = (ArrayList<Eleição>)ois.readObject();
                    ois.close();

                }
                catch(FileNotFoundException ex){
                    System.out.println("Erro a abrir ficheiro.");
                }
                catch(IOException ex){
                    System.out.println("Erro a ler ficheiro. Ainda nao existeA");
                }
                catch(ClassNotFoundException ex){
                    System.out.println("Erro a converter objeto.");
                }
            }
            System.out.println("RMI Server ready.");

            new VerificaServer();
            new ContaTempo();
            System.out.println("Passei");

            while (true) {
            }
        } catch (Exception e) {

            try{
                RMIServer server = new RMIServer();

                Registry r = LocateRegistry.createRegistry(7002);
                r.rebind("Server", server);
                File f = new File("pessoas.obj");
                if(f.exists() && f.isFile())
                {
                    try{
                        FileInputStream fis = new FileInputStream(f);
                        ObjectInputStream ois = new ObjectInputStream(fis);
                        pessoas = (ArrayList<Pessoa>)ois.readObject();
                        ois.close();

                    }
                    catch(FileNotFoundException ex){
                        System.out.println("Erro a abrir ficheiro.");
                    }
                    catch(IOException ex){
                        System.out.println("Erro a ler ficheiro. Ainda não existia");
                    }
                    catch(ClassNotFoundException ex){
                        System.out.println("Erro a converter objeto.");
                    }
                }
                f = new File("eleicoes.obj");
                if(f.exists() && f.isFile())
                {
                    try{
                        FileInputStream fis = new FileInputStream(f);
                        ObjectInputStream ois = new ObjectInputStream(fis);
                        eleições = (ArrayList<Eleição>)ois.readObject();
                        ois.close();

                    }
                    catch(FileNotFoundException ex){
                        System.out.println("Erro a abrir ficheiro.");
                    }
                    catch(IOException ex){
                        System.out.println("Erro a ler ficheiro. Ainda não existia");
                    }
                    catch(ClassNotFoundException ex){
                        System.out.println("Erro a converter objeto.");
                    }
                }
                System.out.println("RMI Backup Server ready.");
                new VerificaBackupServer();
                while (true) {
                }
            }
            catch(java.rmi.server.ExportException e2) {
                System.out.println("Erro");
            }
            catch(Exception e1)
            {
                e.printStackTrace();
            }
            e.printStackTrace();

        }
    }
}
