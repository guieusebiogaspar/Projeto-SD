package RMI;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


public class RMIServer extends UnicastRemoteObject implements RMIServerInterface, Serializable{
    static AdminConsoleInterface admin;
    private static ArrayList<Pessoa> pessoas = new ArrayList<>();
    private static ArrayList<Eleição> eleições = new ArrayList<>();
    private static ArrayList<String> mesas = new ArrayList<>();
    private static int auxServer;


    public RMIServer() throws RemoteException {
        super();
    }

    /**
     * Método que devolve o auxServer
     *
     */
    public static int getAuxServer(){
        return auxServer;
    }

    /**
     * Método que ve qual o valor da variavel auxServer
     *
     */
    public int obterValor() throws RemoteException
    {
        int valor = getAuxServer();
        return valor;
    }

    /**
     * Método que a adminConsole chama para informar que está ligada
     * Server retribuí a mensagem
     *
     * @param adm
     *
     */
    public void olaAdmin(AdminConsoleInterface adm) throws RemoteException {
        System.out.println("Admin entrou no server");
        admin = adm;
        admin.olaServidor();
    }

    /**
     * Método que a adminConsole chama para informar que saiu do sistema
     * Server retribuí a mensagem
     *
     */
    public void adeusAdmin() throws RemoteException {
        System.out.println("Admin saiu do server");
        admin.adeusServidor();
    }

    /**
     * Método que a mesaVoto chama para informar que está ligada
     *
     */
    public int olaMesaVoto(String mesa) throws RemoteException {
        System.out.println("Mesa de voto " + mesa + " entrou no server");
        if(mesas.contains(mesa)) {
            //System.out.println("Já existe uma mesa de voto no " + mesa);
            return 1;
        }
        mesas.add(mesa);
        return 0;
    }

    /**
     * Método que regista uma pessoa na base de dados
     *
     * @param pessoa
     */
    public void registar(Pessoa pessoa) throws RemoteException {
        pessoas.add(pessoa);
        System.out.println("----- Pessoas inscritas -----");
        System.out.println("Nome\t\tCC");
        for(int i = 0; i < pessoas.size(); i++) {
            System.out.println(pessoas.get(i).getNome() + " \t" + pessoas.get(i).getCc());
        }
        writeBD("pessoas.obj");
    }

    /**
     * Método que regista uma eleição na base de dados
     *
     * @param eleição
     */
    public void criarEleição(Eleição eleição) throws RemoteException {
        eleições.add(eleição);
        System.out.println("----- Eleições criadas -----");
        for(int i = 0; i < eleições.size(); i++) {
            System.out.println(eleições.get(i).getTitulo());
        }
        writeBD("eleicoes.obj");
    }

    /**
     * Método atualiza o título de uma eleição
     *
     * @param eleição
     * @param newTitle
     */
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

    /**
     * Método atualiza a descrição de uma eleição
     *
     * @param eleição
     * @param newDescri
     */

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

    /**
     * Método atualiza a data de inicio de uma eleição
     *
     * @param eleição
     * @param newInicio
     */
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

    /**
     * Método atualiza a data de fim de uma eleição
     *
     * @param eleição
     * @param newFim
     */
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

    /**
     * Método adiciona um departamento que pode votar na eleição
     *
     * @param eleição
     * @param grupo
     */
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

    /**
     * Método adiciona uma mesa que pode votar na eleição
     *
     * @param eleição
     * @param mesa
     */

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

    /**
     * Método remove uma mesa um departamento pode votar na eleição
     *
     * @param eleição
     * @param grupo
     */
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

    /**
     * Método remove uma mesa que pode votar na eleição
     *
     * @param eleição
     * @param mesa
     */
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

    /**
     * Método adiciona uma lista à eleição
     *
     * @param eleição
     * @param lista
     */
    public boolean addLista(Eleição eleição, String lista) throws RemoteException
    {
        for(int i = 0; i < eleições.size(); i++) {
            if(eleições.get(i).getTitulo().equals(eleição.getTitulo())) {
                for(int j = 0; j < eleições.get(i).getListas().size(); j++) {
                    if(eleições.get(i).getListas().get(j).getNome().equals(lista)) {
                        return true;
                    }
                }

                Lista l = new Lista(lista, eleição.getQuemPodeVotar());
                eleições.get(i).getListas().add(l);
            }
        }
        writeBD("eleicoes.obj");
        return false;
    }

    /**
     * Método remove uma lista da eleição
     *
     * @param eleição
     * @param lista
     */
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
     * Método verifica se o nome fornecido corresponde a um titulo de uma lista
     *
     * @param eleição
     * @param nome
     */
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

    /**
     * Método muda o nome de uma lista
     *
     * @param eleição
     * @param nome
     * @param novoNome
     *
     */
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
     * Método que adiciona uma pessoa a uma lista
     *
     * @param eleição
     * @param lista
     * @param pessoa
     *
     */
    public void adicionaPessoaLista(Eleição eleição, String lista, Pessoa pessoa) throws RemoteException {
        for(Eleição el : eleições)
        {
            if(el.getTitulo().equals(eleição.getTitulo()))
            {
                for(Lista l: el.getListas())
                {
                    if(l.getNome().equals(lista))
                    {
                        l.getMembros().add(pessoa);
                    }
                }
            }
        }
        writeBD("eleicoes.obj");
    }

    /**
     * Método que remove uma pessoa de uma lista
     *
     * @param eleição
     * @param lista
     * @param pessoa
     *
     */
    public void removePessoaLista(Eleição eleição, String lista, Pessoa pessoa) throws RemoteException {
        for(Eleição el : eleições)
        {
            if(el.getTitulo().equals(eleição.getTitulo()))
            {
                for(Lista l: el.getListas())
                {
                    if(l.getNome().equals(lista))
                    {
                        for (int i = 0; i < l.getMembros().size(); i++) {
                            if(l.getMembros().get(i).getNome().equals(pessoa.getNome())) {
                                l.getMembros().remove(i);
                            }
                        }
                        for (int i = 0; i < l.getMembros().size(); i++) {
                            System.out.println(l.getMembros().get(i).getNome());
                        }
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

    /**
     * Método que vai verificar se o cc introduzido já existe na base de dados e se sim devolve essa pessoa
     *
     * @param cc
     *
     *  @return eleitor
     */
    public Pessoa verificaEleitor(int cc) throws RemoteException {
        for(int i = 0; i < pessoas.size(); i++) {
            if(pessoas.get(i).getCc() == cc && !pessoas.get(i).getAVotar()) {
                return pessoas.get(i);
            }
        }
        return null;
    }

    /**
     * Método que vai verificar se o nome da eleição introduzido já existe na base de dados
     *
     * @param nome
     *
     *  @return true se o cc não existe e false se o cc ja existe
     */
    public boolean verificaEleicao(String nome) throws RemoteException {
        for(int i = 0; i < eleições.size(); i++) {
            if(eleições.get(i).getTitulo().equals(nome)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Método que atualiza se a pessoa está ou não num terminal de voto
     *
     * @param cc
     * @param estado
     */
    public void pessoaAVotar(int cc, boolean estado) throws RemoteException {
        for(int i = 0; i < pessoas.size(); i++) {
            if(pessoas.get(i).getCc() == cc) {
                pessoas.get(i).setAVotar(estado);
            }
        }
    }

    /**
     * Método que verifica se o username e password introduzidos correspondem ao user com o cartão de cidadão em questão
     *
     * @param username
     * @param password
     * @param cc
     *
     * @return true se correspondem e false se não correspondem
     */
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

    /**
     * Método que devolve ArrayList de eleições
     *
     *  @return eleições
     */
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
        //pessoas = null;
        readBD("pessoas.obj");
        for(Pessoa p: pessoas)
        {
            String cartao = String.valueOf(p.getCc());
            if(cartao.equals(cc))
                return p;
        }
        return null;
    }

    /**
     * Método que devolve ArrayList de pessoas
     *
     *  @return eleições
     */
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
                // Se a eleição tiver a mesa de voto em questão
                // Se a pessoa pertencer ao tipo de pessoas que pode votar (Estudantes, Docentes ou Funcionários)
                // Se a pessoa pertence a um dos departamentos que podem votar na eleição
                // Se a pessoa ainda não votou
                // Se a eleição estiver ativa
                if(eleicoes.get(i).getMesasVoto().contains(departamento) &&
                        eleicoes.get(i).getQuemPodeVotar().equals(tipo) &&
                        eleicoes.get(i).getGrupos().contains(dep) &&
                        !eleicoes.get(i).getJaVotaram().contains(cc) &&
                        eleicoes.get(i).getAtiva())
                {
                    filtradas.add(eleicoes.get(i));
                }
            }
        }

        return filtradas;
    }

    /**
     * Método que adiciona um voto a uma lista da eleição
     *
     * @param eleição
     * @param lista
     * @param cc
     * @param departamento
     * @param momento
     *
     * @return devolve 1 se correu tudo bem
     */
    public int adicionaVoto(Eleição eleição, String lista, int cc, String departamento, String momento) throws RemoteException {
        if (eleições != null) {

            for (int i = 0; i < eleições.size(); i++) {
                if (eleições.get(i).getTitulo().equals(eleição.getTitulo()) && eleições.get(i).getAtiva()) { // Ao encontrar a eleição com o titulo correspondente
                    for (int j = 0; j < eleições.get(i).getListas().size(); j++) { // Vai percorrer as listas da eleição
                        if (eleições.get(i).getListas().get(j).getNome().equals(lista)) { // Se encontrar uma lista com o nome igual introduzido
                            int votos = eleições.get(i).getListas().get(j).getNumVotos(); // vai buscar o nr de votos dessa lista
                            eleições.get(i).getListas().get(j).setNumVotos(votos + 1); // Adiciona o novo voto

                            for (int k = 0; k < pessoas.size(); k++) {
                                if (pessoas.get(k).getCc() == cc) {
                                    // Atualiza na pessoa que votou em x eleição e em y mesa de voto
                                    // Diz que a pessoa não está a votar
                                    HashMap<String, String> votou = pessoas.get(k).getVotou();
                                    String depTime = departamento + " - " + momento;
                                    votou.put(eleição.getTitulo(), depTime);
                                    pessoas.get(k).setVotou(votou);
                                    pessoas.get(k).setAVotar(false);
                                }
                                // Adiciona a pessoa ao arrayList das pessoas que ja votaram naquela dada eleição
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
        }
        return 0;
    }

            /**
             * Método que chama o método da admin console que dá print ao estado das mesas de voto e respetivos terminais
             *
             * @param departamento
             * @param terminais
             */
            public void printOnServer(String departamento, int terminais) throws RemoteException {
            if(admin != null) admin.printOnAdmin(departamento, terminais);
        }

            /**
             * Verifica se a mesa de voto esta no arrayList das mesas de voto
             *
             * @param departamento
             */
            public void verificaOnServer(String departamento) throws RemoteException
            {
                if(mesas.contains(departamento)) {
                    mesas.clear();
                }
                mesas.add(departamento);

            }

            /**
             * (R)Escreve na base de dados o ficheiro indicado
             *
             * @param name
             */
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

            /**
             * Lê da base de dados o ficheiro indicado
             *
             * @param name
             */
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
                while (true) {
                    if(v.getSouB() == 1)
                        auxServer++;

                }
            }
            public static void main(String args[]) throws RemoteException {

            System.getProperties().put("java.security.policy", "policy.all");
            System.setSecurityManager(new RMISecurityManager());
            //System.setProperty("java.rmi.server.hostname", "192.168.1.171");

            try {
                servidorAtivar();
            } catch (Exception e) {

                try{
                    RMIServer server = new RMIServer();
                    Registry r = LocateRegistry.createRegistry(7002);
                    r.rebind("Server", server);

                    // Carrega estruturas de dados
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