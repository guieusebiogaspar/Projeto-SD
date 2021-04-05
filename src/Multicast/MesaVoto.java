package Multicast;

import RMI.*;

import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.ConnectException;
import java.rmi.registry.LocateRegistry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class MesaVoto extends Thread {
    //private String MULTICAST_ADDRESS_TERMINALS = "224.0.224.0";
    private String MULTICAST_ADDRESS_TERMINALS;
    private int PORT = 4321;
    private String departamento;

    public static void main(String[] args) {
        if (args.length == 0 || args.length == 1) {
            System.out.println("java MesaVoto multicastAddress departamento");
            System.exit(0);
        }


        MesaVoto mesa = new MesaVoto(args[0], args[1]);
        mesa.start();
    }

    public MesaVoto(String address, String depart) {
        super("Mesa de Voto " + (long) (Math.random() * 1000));
        this.MULTICAST_ADDRESS_TERMINALS = address;
        this.departamento = depart;
    }


    public Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            System.out.println("Este campo só aceita números!");
            return null;
        }
    }

    /**
     * Método que vai enviar um packet por UDP para o voting terminal
     *
     * @param socket
     * @param message - mensagem a enviar no packet
     * @param group
     */
    public void enviaServer(MulticastSocket socket, String message, InetAddress group) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
        socket.send(packet);
    }

    /**
     * Método que vai receber um packet por UDP de um voting terminal
     *
     * @param socket
     * @return mensagem recebida
     */
    public String recebeServer(MulticastSocket socket) throws IOException {
        // Corre até receber uma mensagem do servidor, onde dá return
        while (true) {
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String message = new String(packet.getData(), 0, packet.getLength());

            // Se a mensagem não começar por @
            if (message.charAt(0) != '$') {
                return message;
            }
        }
    }

    public String filterMessage(MulticastSocket socket, String expression) throws IOException {
        String message = null;
        while (message == null) {
            message = recebeServer(socket);
            if (!message.contains(expression)) {
                message = null;
            }
        }

        return message;
    }

    public Eleição escolherEleição(RMIServerInterface serverRMI, String departamento, int cc) throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        ArrayList<Eleição> eleições = serverRMI.filterEleições(departamento, cc);
        if (eleições.size() == 0) {
            System.out.println("Não existem eleições disponíveis em que esteja autorizado a votar nesta mesa de voto");
            return null;
        }

        System.out.println("-------- Eleições nesta mesa de voto --------");
        for (int i = 0; i < eleições.size(); i++) {
            System.out.println("" + (i + 1) + " - " + eleições.get(i).getTitulo());
        }

        System.out.print("Introduza o número da eleição em que pretende votar: ");
        Integer escolha = null;

        int check = 0;
        while (check == 0) {
            while (escolha == null) escolha = tryParse(reader.readLine());
            if (escolha > 0 && escolha <= eleições.size()) {
                check = 1;
            } else {
                System.out.println("Número não corresponde a nenhuma lista");
                escolha = null;
            }
        }

        return eleições.get(escolha - 1);
    }

    public void Correr(RMIServerInterface serverRMI, MulticastSocket socketFindTerminal) throws IOException {
        socketFindTerminal = new MulticastSocket(PORT);  // create socket without binding it (only for sending)
        InetAddress groupTerminal = InetAddress.getByName(MULTICAST_ADDRESS_TERMINALS);
        socketFindTerminal.joinGroup(groupTerminal); //join the multicast group



        // Prepara o address para tratar das sessões dos eleitores
        int last = Integer.parseInt(MULTICAST_ADDRESS_TERMINALS.substring(MULTICAST_ADDRESS_TERMINALS.length() - 1));

        // caso seja o ultimo endereço ip permitido, usa o anterior no outro grupo mutlicast
        int last2 = 0;
        if(last < 255) {
            last = last + 1;
            last2 = last + 1;
        } else {
            last = last - 1;
            last2 = last - 2;
        }

        String newAddress = MULTICAST_ADDRESS_TERMINALS.substring(0, MULTICAST_ADDRESS_TERMINALS.length() - 1);
        newAddress = newAddress + last;
        String newAddress2 = MULTICAST_ADDRESS_TERMINALS.substring(0, MULTICAST_ADDRESS_TERMINALS.length()-1);
        newAddress2 = newAddress2 + last2;

        new AtualizaMesa(departamento, newAddress2, serverRMI);
        new verificaMesa(departamento, newAddress2, serverRMI);
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);



        while (true) {

            System.out.printf("Introduza o seu número de Cartão de Cidadão: ");
            Integer cc = null;
            while (cc == null) cc = tryParse(reader.readLine());

            if (serverRMI.verificaEleitor(cc) != null) {
                System.out.println("Cartão de cidadão válido!");

                Eleição eleição = escolherEleição(serverRMI, departamento, cc);

                if (eleição == null) continue;

                System.out.println("Redirecionando-o para um terminal de voto");
                String message = "$ type | search; available | no";

                // Vê que terminais estão à espera da mensagem "Terminal Disponível"
                enviaServer(socketFindTerminal, message, groupTerminal);

                // Os que estiverem disponíveis enviam mensagem com o seu id. A mesa de voto capta um dos terminais.
                String terminal = filterMessage(socketFindTerminal, "type | search;");

                String[] decompose = terminal.trim().split(";");
                //System.out.println("1 - " + terminal);

                String nr = null;
                for (int i = 0; i < decompose.length; i++) {
                    if (decompose[i].contains("terminal")) {
                        nr = decompose[i].substring(decompose[i].lastIndexOf(" ") + 1);
                        break;
                    }
                }

                // "@ type | search; available | yes; terminal | nr terminal"
                terminal = "$ type | ack; terminal | " + nr;

                // Avisa os terminais qual dos terminais captou
                enviaServer(socketFindTerminal, terminal, groupTerminal);
                System.out.println("Será dirigido para o terminal " + decompose[2].substring(decompose[2].lastIndexOf(" ") + 1));

                serverRMI.pessoaAVotar(cc, true);
                message = "$ type | welcome; user | " + cc;
                enviaServer(socketFindTerminal, message, groupTerminal);

                new HandleSession(serverRMI, newAddress, cc, eleição, departamento);

            } else {
                System.out.println("O cc introduzido não se encontra na nossa DB ou este utilizador já se encontra a votar.");
            }

            try {
                sleep(2000);
            } catch (InterruptedException e) {
            }
        }
    }

    public void run() {
        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());


        MulticastSocket socketFindTerminal = null;
        System.out.println("Mesa de voto " + departamento + " running...");
        while (true) {
            try {
                RMIServerInterface serverRMI = (RMIServerInterface) LocateRegistry.getRegistry(7001).lookup("Server");

                if (serverRMI.obterValor() == 1) {
                    try {
                        serverRMI = (RMIServerInterface) LocateRegistry.getRegistry(7002).lookup("Server");
                        if(serverRMI.olaMesaVoto(departamento)==1){
                            System.out.println("Já existe uma mesa de voto no "+departamento);
                            return;
                        }
                        Correr(serverRMI, socketFindTerminal);

                    } catch (RemoteException | NotBoundException ex) {
                        try {
                            RMIServerInterface serverRMI1 = (RMIServerInterface) LocateRegistry.getRegistry(7001).lookup("Server");
                            if(serverRMI1.olaMesaVoto(departamento)==1)
                            {
                                System.out.println("Já existe uma mesa de voto no "+departamento);
                                return;
                            }
                            Correr(serverRMI1, socketFindTerminal);
                        } catch (RemoteException | NotBoundException ex1) {
                            System.out.println("Servidor não está online");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                if (serverRMI.obterValor() == 0) {
                    try {
                        //serverRMI = (RMIServerInterface) LocateRegistry.getRegistry(7002).lookup("Server");
                        if(serverRMI.olaMesaVoto(departamento)==1) {
                            System.out.println("Já existe uma mesa de voto no " + departamento);
                            return;
                        }
                        Correr(serverRMI, socketFindTerminal);

                    } catch (RemoteException ex) {
                        System.out.println("Servidor não está online");
                        try {
                            RMIServerInterface serverRMI1 = (RMIServerInterface) LocateRegistry.getRegistry(7002).lookup("Server");
                            if(serverRMI1.olaMesaVoto(departamento)==1)
                            {
                                System.out.println("Já existe uma mesa de voto no "+departamento);
                                return;
                            }

                            Correr(serverRMI1, socketFindTerminal);

                        } catch (RemoteException | NotBoundException ex1) {
                            System.out.println("Servidor não está online");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (RemoteException | NotBoundException ex) {
                System.out.println("Servidor não está online");
                try {
                    RMIServerInterface serverRMI = (RMIServerInterface) LocateRegistry.getRegistry(7002).lookup("Server");
                    if(serverRMI.olaMesaVoto(departamento)==1){
                        System.out.println("Já existe uma mesa de voto no "+departamento);
                        return;
                    }
                    Correr(serverRMI, socketFindTerminal);
                } catch (RemoteException | NotBoundException ex1) {
                    System.out.println("Servidor não está online");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("A fechar socket Mesa de voto");
                    //socketFindTerminal.close();
                }
            }
        }
    }
}

class HandleSession extends Thread {
    private String MULTICAST_ADDRESS_SESSIONS;
    private int PORT = 4321;
    private RMIServerInterface serverRMI;
    private int cc;
    private Eleição eleição;
    private String departamento;

    HandleSession(RMIServerInterface server, String address, int cartao, Eleição eleição, String departamento) {
        this.serverRMI = server;
        this.MULTICAST_ADDRESS_SESSIONS = address;
        this.cc = cartao;
        this.eleição = eleição;
        this.departamento = departamento;
        this.start();
    }

    /**
     * Método que vai enviar um packet por UDP para o voting terminal
     *
     * @param socket
     * @param message - mensagem a enviar no packet
     * @param group
     */
    public void enviaServer(MulticastSocket socket, String message, InetAddress group) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
        socket.send(packet);
    }

    /**
     * Método que vai receber um packet por UDP de um voting terminal
     *
     * @param socket
     * @return mensagem recebida
     */
    public String recebeServer(MulticastSocket socket) throws IOException {
        // Corre até receber uma mensagem do servidor, onde dá return
        while (true) {
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String message = new String(packet.getData(), 0, packet.getLength());

            // Se a mensagem não começar por @
            if (message.charAt(0) != '$') {
                return message;
            }
        }
    }

    public String filterMessage(MulticastSocket socket, String expression, String id) throws IOException {
        String message = null;
        while (message == null) {
            message = recebeServer(socket);
            if(message.contains("type | timeout")) {
                String info[] = message.trim().split(";");
                for(int i = 0; i < info.length; i++) {
                    if(info[i].contains("user")) {
                        serverRMI.pessoaAVotar(Integer.parseInt(info[i].substring(info[i].lastIndexOf(" ") + 1)), false);
                        return "Interrupt";
                    }
                }
            }
            if (!message.contains(expression) || !message.contains(id)) {
                message = null;
            }
        }
        return message;
    }
    public void CorrerHandle(RMIServerInterface serverRMI, MulticastSocket socketSession, int serverPort) throws IOException, InterruptedException {
        socketSession = new MulticastSocket(PORT);  // create socket without binding it (only for sending)
        InetAddress groupSession = InetAddress.getByName(MULTICAST_ADDRESS_SESSIONS);
        socketSession.joinGroup(groupSession); //join the multicast group

        HandleSession.sleep(1000);

        String[] info = null;
        String message = null;
        int entrou = 0;
        while (entrou == 0) {
            message = filterMessage(socketSession, "type | login", "cc | " + cc);
            if(message.equals("Interrupt")) {
                return;
            }

            info = message.trim().split(";");
            int cartao = 0;
            String nick = null;
            String password = null;

            for(int i = 0; i < info.length; i++) {
                if(info[i].contains("cc")) {
                    cartao = Integer.parseInt(info[1].substring(info[1].lastIndexOf(" ") + 1));
                }
                else if (info[i].contains("username")){
                    nick = info[i].substring(info[i].lastIndexOf(" ") + 1);
                }
                else if (info[i].contains("password")) {
                    password = info[i].substring(info[i].lastIndexOf(" ") + 1);
                }
            }

            if (serverRMI.loginUser(nick, password, cc) && cc == cartao) {
                message = "$ type | status; cc | " + cc + "; logged | on; msg | Bem-vindo ao eVoting";
                entrou = 1;
            } else {
                message = "$ type | status; cc | " + cc + "; logged | off; msg | Username ou Password incorretos!";
            }

            enviaServer(socketSession, message, groupSession);
        }

        Thread.sleep(300);
        ArrayList<Lista> listas = eleição.getListas();
        message = "$ type | item_list; cc | " + cc + "; item_count | " + listas.size();
        for (int i = 0; i < listas.size(); i++) {
            message = message + "; item_" + i + "_name | " + listas.get(i).getNome();
        }

        // envia as listas
        enviaServer(socketSession, message, groupSession);

        // recebe a lista que o eleitor votou
        message = filterMessage(socketSession, "type | vote", "cc | " + cc);
        if(message.equals("Interrupt")) {
            return;
        }

        info = message.trim().split(";");
        String lista = null;
        for (int i = 0; i < info.length; i++) {
            if (info[i].contains("list")) {
                lista = info[i].substring(info[i].lastIndexOf(" ") + 1);
                break;
            }
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();
        while(true)
        {
            try{
                if (serverRMI.adicionaVoto(eleição, lista, cc, departamento, dtf.format(now)) == 1)
                    return;
            }
            catch(ConnectException c)
            {
                try{
                    RMIServerInterface server = (RMIServerInterface) LocateRegistry.getRegistry(serverPort).lookup("Server");
                    if(server.adicionaVoto(eleição, lista, cc, departamento, dtf.format(now))==1)
                        return;
                } catch (NotBoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void run() {
        MulticastSocket socketSession = null;
        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());
        while(true)
        {
            try {
                RMIServerInterface servidor = (RMIServerInterface) LocateRegistry.getRegistry(7001).lookup("Server");
                if(servidor.obterValor() == 1)
                {
                    try{
                        servidor = (RMIServerInterface) LocateRegistry.getRegistry(7002).lookup("Server");
                        CorrerHandle(servidor, socketSession, 7001);
                    }
                    catch(RemoteException | NotBoundException ex)
                    {
                        try{
                            RMIServerInterface serverRMI1 = (RMIServerInterface) LocateRegistry.getRegistry(7001).lookup("Server");
                            CorrerHandle(serverRMI1, socketSession, 7002);
                        }
                        catch(RemoteException | NotBoundException ex1)
                        {
                            System.out.println("Servidor não está onlineA");
                        }
                        catch(IOException | InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    catch(IOException | InterruptedException e){
                        e.printStackTrace();
                    }
                }
                if(servidor.obterValor() == 0)
                {
                    try{
                        CorrerHandle(servidor, socketSession, 7002);
                    }
                    catch(RemoteException ex)
                    {
                        try{
                            RMIServerInterface serverRMI1 = (RMIServerInterface) LocateRegistry.getRegistry(7002).lookup("Server");
                            CorrerHandle(serverRMI1, socketSession, 7001);
                        }
                        catch(RemoteException | NotBoundException ex1)
                        {
                            System.out.println("Servidor não está onlineB");
                        }
                        catch(IOException | InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    catch(IOException | InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }catch(RemoteException | NotBoundException ex)
            {
                System.out.println("Servidor não está onlineC");
                try{
                    RMIServerInterface serverRMI2 = (RMIServerInterface) LocateRegistry.getRegistry(7002).lookup("Server");
                    CorrerHandle(serverRMI2, socketSession, 7001);
                }
                catch(RemoteException | NotBoundException ex1){
                    System.out.println("Servidor não está onlineD");
                }
                catch(IOException | InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }

    }
}

class AtualizaMesa extends Thread {
    private String ATUALIZA_ADDRESS;
    private int PORT = 4321;
    private RMIServerInterface serverRMI;
    private String departamento;

    public AtualizaMesa(String departamento, String address, RMIServerInterface serverRMI) {
        this.ATUALIZA_ADDRESS = address;
        this.serverRMI = serverRMI;
        this.departamento = departamento;
        this.start();
    }

    /**
     * Método que vai receber um packet por UDP da mesa de voto
     *
     * @param socket
     *
     * @return mensagem recebida
     */
    public String recebeCliente(MulticastSocket socket) throws IOException {
        // Corre até receber uma mensagem do servidor, onde dá return
        while(true){
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String message = new String(packet.getData(), 0, packet.getLength());

            // Se a mensagem não começar por @
            if(message.charAt(0) != '$') {
                return message;
            }
        }
    }

    public String filterMessage(MulticastSocket socket, String expression) throws IOException {
        String message = null;
        while (message == null) {
            message = recebeCliente(socket);
            if (!message.contains(expression)) {
                message = null;
            }
        }

        return message;
    }

    public void run() {
        try {
            MulticastSocket socketAtualiza = null;  // create socket without binding it (only for sending)
            socketAtualiza = new MulticastSocket(PORT);
            InetAddress groupAtualiza = InetAddress.getByName(ATUALIZA_ADDRESS);
            socketAtualiza.joinGroup(groupAtualiza); //join the multicast group

            ArrayList<String> terminais = new ArrayList<>();

            String terminal = null;
            while(true) {
                String[] info = filterMessage(socketAtualiza, "type | update").split(";");
                for(int i = 0; i < info.length; i++) {
                    if(info[i].contains("terminal")) {
                        terminal = info[i].substring(info[i].lastIndexOf(" ") + 1);
                        break;
                    }
                }

                if(terminais.contains(terminal)) {
                    serverRMI.printOnServer(departamento, terminais.size());
                    terminais.clear();
                }
                terminais.add(terminal);
            }

        } catch (IOException e) {
            this.run();
        }

    }
}
class verificaMesa extends Thread{
    private String ATUALIZA_ADDRESS;
    private int PORT = 4321;
    private RMIServerInterface serverRMI;
    private String departamento;

    public verificaMesa(String departamento, String address, RMIServerInterface serverRMI) {
        this.ATUALIZA_ADDRESS = address;
        this.serverRMI = serverRMI;
        this.departamento = departamento;
        this.start();
    }

    /**
     * Método que vai receber um packet por UDP da mesa de voto
     *
     * @param socket
     *
     * @return mensagem recebida
     */
    public String recebeCliente(MulticastSocket socket) throws IOException {
        // Corre até receber uma mensagem do servidor, onde dá return
        while(true){
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            String message = new String(packet.getData(), 0, packet.getLength());

            // Se a mensagem não começar por @
            if(message.charAt(0) != '$') {
                return message;
            }
        }
    }

    public String filterMessage(MulticastSocket socket, String expression) throws IOException {
        String message = null;
        while (message == null) {
            message = recebeCliente(socket);
            if (!message.contains(expression)) {
                message = null;
            }
        }

        return message;
    }

    public void run() {
        try {
            while(true) {
                serverRMI.verificaOnServer(departamento);
            }
        } catch (IOException e) {
            this.run();
        }

    }
}