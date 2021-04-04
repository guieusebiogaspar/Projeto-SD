package Multicast;

import RMI.Eleição;
import RMI.Lista;
import RMI.RMIServer;
import RMI.RMIServerInterface;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;


public class MesaVoto extends Thread {
    //private String MULTICAST_ADDRESS_TERMINALS = "224.0.224.0";
    private String MULTICAST_ADDRESS_TERMINALS;
    private int PORT = 4321;
    private String departamento;

    public static void main(String[] args) {
        if(args.length == 0 || args.length == 1) {
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
     *
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
     *
     * @return mensagem recebida
     */
    public String recebeServer(MulticastSocket socket) throws IOException {
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
        while(message == null) {
            message = recebeServer(socket);
            if(!message.contains(expression)) {
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
            System.out.println("" + (i+1) + " - " + eleições.get(i).getTitulo());
        }

        System.out.print("Introduza o número da eleição em que pretende votar: ");
        Integer escolha = null;

        int check = 0;
        while(check == 0) {
            while(escolha == null) escolha = tryParse(reader.readLine());
            if(escolha > 0 && escolha <= eleições.size()) {
                check = 1;
            } else {
                System.out.println("Número não corresponde a nenhuma lista");
                escolha = null;
            }
        }

        return eleições.get(escolha-1);
    }

    public void run() {
        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());

        MulticastSocket socketFindTerminal = null;
        System.out.println("Mesa de voto " + departamento +  " running...");

        try {
            RMIServerInterface serverRMI = (RMIServerInterface) LocateRegistry.getRegistry(7001).lookup("Server");
            serverRMI.olaMesaVoto(this.getName());

            socketFindTerminal = new MulticastSocket(PORT);  // create socket without binding it (only for sending)
            InetAddress groupTerminal = InetAddress.getByName(MULTICAST_ADDRESS_TERMINALS);
            socketFindTerminal.joinGroup(groupTerminal); //join the multicast group

            // Prepara o address para tratar das sessões dos eleitores
            int last = Integer.parseInt(MULTICAST_ADDRESS_TERMINALS.substring(MULTICAST_ADDRESS_TERMINALS.length() -1));
            if(last < 255) {
                last = last + 1;
            } else {
                last = last - 1;
            }

            String newAddress = MULTICAST_ADDRESS_TERMINALS.substring(0, MULTICAST_ADDRESS_TERMINALS.length()-1);
            newAddress = newAddress + last;

            InputStreamReader input = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(input);

            while (true) {

                System.out.printf("Introduza o seu número de Cartão de Cidadão: ");
                Integer cc = null;
                while(cc == null) cc = tryParse(reader.readLine());

                if(serverRMI.verificaEleitor(cc) != null) {
                    System.out.println("Cartão de cidadão válido!");

                    Eleição eleição = escolherEleição(serverRMI, departamento, cc);

                    if(eleição == null) continue;

                    System.out.println("Redirecionando-o para um terminal de voto");
                    String message = "$ type | search; available | no";

                    // Vê que terminais estão à espera da mensagem "Terminal Disponível"
                    enviaServer(socketFindTerminal, message, groupTerminal);

                    // Os que estiverem disponíveis enviam mensagem com o seu id. A mesa de voto capta um dos terminais.
                    String terminal = filterMessage(socketFindTerminal, "type | search;");

                    String[] decompose = terminal.trim().split(";");
                    //System.out.println("1 - " + terminal);

                    String nr = null;
                    for(int i = 0; i < decompose.length; i++) {
                        if(decompose[i].contains("terminal")) {
                            nr = decompose[i].substring(decompose[i].lastIndexOf(" ") + 1);
                            break;
                        }
                    }

                    // "@ type | search; available | yes; terminal | nr terminal"
                    terminal = "$ type | ack; terminal | " + nr;

                    // Avisa os terminais qual dos terminais captou
                    enviaServer(socketFindTerminal, terminal, groupTerminal);
                    System.out.println("Será dirigido para o terminal " + decompose[2].substring(decompose[2].lastIndexOf(" ") + 1));
                    new HandleSession(serverRMI, newAddress, cc, eleição, departamento);

                } else {
                    System.out.println("O cc introduzido não se encontra na nossa DB.");
                }


                try { sleep(2000); } catch (InterruptedException e) { }
            }

        } catch (RemoteException | NotBoundException ex) {
            System.out.println("Servidor não está online");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("A fechar socket Mesa de voto");
            socketFindTerminal.close();
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
     *
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
     *
     * @return mensagem recebida
     */
    public String recebeServer(MulticastSocket socket) throws IOException {
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

    public String filterMessage(MulticastSocket socket, String expression, String id) throws IOException {
        String message = null;
        while(message == null) {
            message = recebeServer(socket);
            if(!message.contains(expression) || !message.contains(id)) {
                message = null;
            }
        }
        return message;
    }

    public void run() {
        MulticastSocket socketSession = null;

        try {
            socketSession = new MulticastSocket(PORT);  // create socket without binding it (only for sending)
            InetAddress groupSession = InetAddress.getByName(MULTICAST_ADDRESS_SESSIONS);
            socketSession.joinGroup(groupSession); //join the multicast group

            HandleSession.sleep(1000);

            String message = "$ type | welcome; user | " + cc;
            enviaServer(socketSession, message, groupSession);
            String[] info = null;
            int entrou = 0;
            while(entrou == 0) {
                message = filterMessage(socketSession, "type | login", "cc | " + cc);
                info = message.trim().split(";");
                int cartao = Integer.parseInt(info[1].substring(info[1].lastIndexOf(" ") + 1));
                String nick = info[2].substring(info[2].lastIndexOf(" ") + 1);
                String password = info[3].substring(info[3].lastIndexOf(" ") + 1);
                if(serverRMI.loginUser(nick, password, cc) && cc == cartao) {
                    message = "$ type | status; cc | " + cc + "; logged | on; msg | Bem-vindo ao eVoting";
                    entrou = 1;
                } else {
                    message = "$ type | status; cc | " + cc + "; logged | off; msg | Username ou Password incorretos!";
                }

                enviaServer(socketSession, message, groupSession);
            }

            Thread.sleep(500);
            ArrayList<Lista> listas = eleição.getListas();
            message = "$ type | item_list; cc | " + cc + "; item_count | " + listas.size();
            for(int i = 0; i < listas.size(); i++) {
                message = message + "; item_" + i + "_name | " + listas.get(i).getNome();
            }

            // envia as listas
            enviaServer(socketSession, message, groupSession);

            // recebe a lista que o eleitor votou
            message = filterMessage(socketSession, "type | vote", "cc | " + cc);

            info = message.trim().split(";");
            String lista = null;
            for(int i = 0; i < info.length; i++) {
                if(info[i].contains("list")) {
                    lista = info[i].substring(info[i].lastIndexOf(" ") + 1);
                    break;
                }
            }

            serverRMI.adicionaVoto(eleição, lista, cc, departamento);
            //System.out.println("\nVoto enviado na eleição " + eleição.getTitulo() + " na lista " + lista);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}