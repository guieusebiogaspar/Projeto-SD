package RMI;

import Multicast.MesaVoto;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.getDefaultUncaughtExceptionHandler;
import static java.lang.Thread.sleep;


public class RMIServer extends UnicastRemoteObject implements RMIServerInterface, Serializable{
    static AdminConsoleInterface admin;
    private static ArrayList<Pessoa> pessoas = new ArrayList<>();
    private static ArrayList<Eleição> eleições = new ArrayList<>();
    private static ArrayList<String> mesas = new ArrayList<>();
    private static int auxServer;


    public RMIServer() throws RemoteException {
        super();
    }
    public static int getAuxServer(){
        return auxServer;
    }
    public int obterValor() throws RemoteException
    {
        int valor = getAuxServer();
        return valor;
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
        System.out.println("Mesa de voto " + mesa + " entrou no server");
        mesas.add(mesa);
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

    public void addMesa(Eleição eleição, String mesa) throws RemoteException
    {
        for(Eleição el : eleições)
        {
            if(el.getTitulo().equals(eleição.getTitulo()))
            {
                el.getMesasVoto().add(mesa);
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

    public int rmvMesa(Eleição eleição, String mesa) throws RemoteException
    {
        for(Eleição el : eleições)
        {
            if(el.getTitulo().equals(eleição.getTitulo()))
            {
                for(String s : el.getMesasVoto())
                {
                    if(s.equals(mesa))
                    {
                        int i = el.getMesasVoto().indexOf(s);
                        el.getMesasVoto().remove((i));
                        writeBD("eleicoes.obj");
                        return 1;
                    }
                }
            }
        }
        return 0;
    }

   /* public boolean addLista(Eleição eleição, String lista) throws RemoteException
    {
        for(int i = 0; i < eleições.size(); i++) {
            if(eleições.get(i).getTitulo().equals(eleição.getTitulo())) {
                for(int j = 0; j < eleições.get(i).getListas().size(); j++) {
                    if(eleições.get(i).getListas().get(j).getNome().equals(lista)) {
                        return true;
                    }
                }

                Lista l = new Lista(lista);
                eleições.get(i).getListas().add(l);
            }
        }
        writeBD("eleicoes.obj");
        return false;
    }*/

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

    public void addTipos(Eleição eleição, ArrayList<String> tipos) throws RemoteException
    {
        for(Eleição el : eleições)
        {
            if(el.getTitulo().equals(eleição.getTitulo()))
            {
                el.setQuemPodeVotar(tipos);
            }
        }
        writeBD("eleicoes.obj");
    }

    public boolean verificaLista(Eleição eleição, String nome) throws RemoteException
    {
        for(Eleição el : eleições)
        {
            if(el.getTitulo().equals(eleição.getTitulo()))
            {
                for(Lista l: el.getListas())
                {
                    if(l.getNome().equals(nome))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void mudaNomeLista(Eleição eleição, String nome, String novoNome) throws RemoteException {
        for(Eleição el : eleições)
        {
            if(el.getTitulo().equals(eleição.getTitulo()))
            {
                for(Lista l: el.getListas())
                {
                    if(l.getNome().equals(nome))
                    {
                        l.setNome(novoNome);
                    }
                }
            }
        }
        writeBD("eleicoes.obj");
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
            if(pessoas.get(i).getCc() == cc && !pessoas.get(i).getAVotar()) {
                return pessoas.get(i);
            }
        }
        return null;
    }

    public boolean verificaEleicao(String nome) throws RemoteException {
        for(int i = 0; i < eleições.size(); i++) {
            if(eleições.get(i).getTitulo().equals(nome)) {
                return true;
            }
        }
        return false;
    }

    public void pessoaAVotar(int cc, boolean estado) throws RemoteException {
        for(int i = 0; i < pessoas.size(); i++) {
            if(pessoas.get(i).getCc() == cc) {
                pessoas.get(i).setAVotar(estado);
            }
        }
    }

    public boolean loginUser(String username, String password, int cc) throws RemoteException {
        for(int i = 0; i < pessoas.size(); i++) {
            if(pessoas.get(i).getUsername().equals(username) && pessoas.get(i).getPassword().equals(password) && pessoas.get(i).getCc() == cc) {
                return true;
            }
        }
        return false;
    }

    /**
     * Método que vai receber o titulo de uma eleição.
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

    /**
     * Método que vai receber o cc de uma pessoa.
     * Se a pessoa existir na BD devolve o objeto da mesma, se não existir devolve null.
     *
     * @param cc
     *
     *  @return eleição - objeto eleição com o título dado como input
     */
    public Pessoa getPessoa(String cc) throws RemoteException {
        pessoas = null;
        readBD("pessoas.obj");
        for(Pessoa p: pessoas)
        {
            String cartao = String.valueOf(p.getCc());
            if(cartao.equals(cc))
                return p;
        }
        return null;
    }

    public ArrayList<Pessoa> getPessoas() throws RemoteException{
        readBD("pessoas.obj");
        return pessoas;
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

        ArrayList<Eleição> eleicoes = getEleições();

        if(tipo != null && dep != null) {
            for (int i = 0; i < eleicoes.size(); i++) {
                /*System.out.println("Aqui tambem");
                if(eleições.get(i).getAtiva()) {
                    System.out.println("Eleição - " + eleições.get(i).getTitulo());
                    System.out.println("Mesas de voto - " + eleições.get(i).getMesasVoto());
                    System.out.println("Quem pode votar - " + eleições.get(i).getQuemPodeVotar());
                    System.out.println("Grupo - " + eleições.get(i).getGrupos());
                }*/

                // Se a eleição tiver a mesa de voto em questão
                // Se a pessoa pertencer ao tipo de pessoas que pode votar (Estudantes, Docentes ou Funcionários)
                // Se a pessoa pertence a um dos departamentos que podem votar na eleição
                // Se a pessoa ainda não votou
                // Se a eleição estiver ativa
                if(eleicoes.get(i).getMesasVoto().contains(departamento) &&
                        eleicoes.get(i).getQuemPodeVotar().contains(tipo) &&
                        eleicoes.get(i).getGrupos().contains(dep) &&
                        !eleicoes.get(i).getJaVotaram().contains(cc) &&
                        eleicoes.get(i).getAtiva())
                {
                    //System.out.println("Adicionou as filtradas");
                    filtradas.add(eleicoes.get(i));
                }
            }
        }

        return filtradas;
    }

    public int adicionaVoto(Eleição eleição, String lista, int cc, String departamento, String momento) throws RemoteException {

        for(int i = 0; i < eleições.size(); i++) {
            if(eleições.get(i).getTitulo().equals(eleição.getTitulo()) && eleições.get(i).getAtiva()) { // Ao encontrar a eleição com o titulo correspondente
                for(int j = 0; j < eleições.get(i).getListas().size(); j++) { // Vai percorrer as listas da eleição
                    if(eleições.get(i).getListas().get(j).getNome().equals(lista)) {
                        int votos = eleições.get(i).getListas().get(j).getNumVotos();
                        eleições.get(i).getListas().get(j).setNumVotos(votos+1);

                        for(int k = 0; k < pessoas.size(); k++) {
                            if(pessoas.get(k).getCc() == cc) {
                                HashMap<String, String> votou = pessoas.get(k).getVotou();
                                String depTime = departamento + " - " + momento;
                                votou.put(eleição.getTitulo(), depTime);
                                pessoas.get(k).setVotou(votou);
                                pessoas.get(k).setAVotar(false);
                                System.out.println("estou a false" + pessoas.get(k).getNome());
                            }
                        }

                        ArrayList<Integer> jaVotaram = eleições.get(i).getJaVotaram();
                        jaVotaram.add(cc);
                        eleições.get(i).setJaVotaram(jaVotaram);
                        System.out.println("Voto registado na eleição " + eleição.getTitulo() + " na lista " + eleição.getListas().get(j).getNome());
                        writeBD("eleicoes.obj");
                        writeBD("pessoas.obj");
                        return 1;
                    }
                }
            }
        }
        return 0;

    }

    public void printOnServer(String departamento, int terminais) throws RemoteException {
        if(admin != null) admin.printOnAdmin(departamento, terminais);
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

    public static void servidorAtivar() throws RemoteException
    {
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

        VerificaServer v =new VerificaServer();
        //new ContaTempo();
        System.out.println("Passei");
        while (true) {
            if(v.getSouB() == 1)
                auxServer++;

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
            servidorAtivar();
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
                VerificaBackupServer v1 = new VerificaBackupServer();
                //new ContaTempo();
                while (true) {
                    if(v1.getSouP() == 1)
                        auxServer--;
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