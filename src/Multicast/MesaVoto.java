package Multicast;

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


public class MesaVoto extends Thread {
    private String MULTICAST_ADDRESS_TERMINALS = "224.0.224.0";
    private int PORT = 4321;
    private long SLEEP_TIME = 5000;

    public static void main(String[] args) {
        MesaVoto mesa = new MesaVoto();
        mesa.start();
    }

    public MesaVoto() {
        super("Mesa de Voto " + (long) (Math.random() * 1000));
    }

    public Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            System.out.println("Os números de cartão de cidadão só contêm números!");
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

    public void run() {
        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());

        MulticastSocket socketFindTerminal = null;
        System.out.println(this.getName() + " running...");

        try {
            RMIServerInterface serverRMI = (RMIServerInterface) LocateRegistry.getRegistry(7001).lookup("Server");
            serverRMI.olaMesaVoto(this.getName());
            System.out.println("Mesa de voto informou server que está ligado");

            socketFindTerminal = new MulticastSocket(PORT);  // create socket without binding it (only for sending)
            InetAddress groupTerminal = InetAddress.getByName(MULTICAST_ADDRESS_TERMINALS);
            socketFindTerminal.joinGroup(groupTerminal); //join the multicast group


            InputStreamReader input = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(input);

            while (true) {

                System.out.printf("Introduza o seu número de Cartão de Cidadão: ");
                Integer cc = null;
                while(cc == null) cc = tryParse(reader.readLine());

                if(serverRMI.verificaEleitor(cc) != null) {
                    System.out.println("Cartão de cidadão válido!");
                    System.out.println("Redirecionando-o para um terminal de voto");
                    String message = "$ type | search; available | no";

                    // Vê que terminais estão à espera da mensagem "Terminal Disponível"
                    enviaServer(socketFindTerminal, message, groupTerminal);

                    // Os que estiverem disponíveis enviam mensagem com o seu id. A mesa de voto capta um dos terminais.
                    String terminal = filterMessage(socketFindTerminal, "@ type | search;");

                    String[] decompose = terminal.split(";");
                    System.out.println("1 - " + terminal);

                    // "@ type | search; available | yes; terminal | nr terminal"
                    terminal = "$ type | ack; terminal | " + decompose[2].substring(decompose[2].lastIndexOf(" ") + 1);

                    // Avisa os terminais qual dos terminais captou
                    enviaServer(socketFindTerminal, terminal, groupTerminal);
                    System.out.println("Será dirigido para o terminal " + decompose[2].substring(decompose[2].lastIndexOf(" ") + 1));
                    new HandleSession(serverRMI, cc);

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
    private String MULTICAST_ADDRESS_SESSIONS = "224.0.224.1";
    private int PORT = 4321;
    private RMIServerInterface serverRMI;
    private int cc;

    HandleSession(RMIServerInterface server, int cartao) {
        this.serverRMI = server;
        this.cc = cartao;
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

    public void run() {
        MulticastSocket socketSession = null;

        try {
            socketSession = new MulticastSocket(PORT);  // create socket without binding it (only for sending)
            InetAddress groupSession = InetAddress.getByName(MULTICAST_ADDRESS_SESSIONS);
            socketSession.joinGroup(groupSession); //join the multicast group

            HandleSession.sleep(1000);

            String message = "$ type | welcome; user | " + cc;
            enviaServer(socketSession, message, groupSession);
            int entrou = 0;
            while(entrou == 0) {
                message = filterMessage(socketSession, "@ type | login; cc | " + cc);

                String[] ccNickPassword = message.split(";");
                int cartao = Integer.parseInt(ccNickPassword[1].substring(ccNickPassword[1].lastIndexOf(" ") + 1));
                String nick = ccNickPassword[2].substring(ccNickPassword[2].lastIndexOf(" ") + 1);
                String password = ccNickPassword[3].substring(ccNickPassword[3].lastIndexOf(" ") + 1);

                if(serverRMI.loginUser(nick, password, cc) && cc == cartao) {
                    message = "$ type | status; cc | " + cc + "; logged | on; msg | Bem-vindo ao eVoting";
                    entrou = 1;
                } else {
                    System.out.println("ACHOO EUUU");
                    message = "$ type | status; cc | " + cc + "; logged | off; msg | Username ou Password incorretos!";
                }

                enviaServer(socketSession, message, groupSession);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}