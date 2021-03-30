package Multicast;

import RMI.RMIServer;
import RMI.RMIServerInterface;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;
import java.net.StandardSocketOptions;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import static java.net.SocketOptions.IP_MULTICAST_LOOP;

public class MesaVoto extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
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

    public void run() {
        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());

        MulticastSocket socket = null;
        System.out.println(this.getName() + " running...");

        try {
            RMIServerInterface serverRMI = (RMIServerInterface) LocateRegistry.getRegistry(7001).lookup("Server");
            serverRMI.olaMesaVoto(this.getName());
            System.out.println("Mesa de voto informou server que está ligado");

            socket = new MulticastSocket(PORT);  // create socket without binding it (only for sending)
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group); //join the multicast group

            InputStreamReader input = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(input);

            while (true) {

                System.out.printf("Introduza o seu número de Cartão de Cidadão: ");
                Integer cc = null;
                while(cc == null) cc = tryParse(reader.readLine());

                if(serverRMI.verificaEleitor(cc) != null) {
                    System.out.println("Cartão de cidadão válido!");
                    new HandleTerminal(socket, serverRMI, group, cc);
                } else {
                    System.out.println("O cc introduzido não se encontra na nossa DB.");
                }


                try { sleep((long) (Math.random() * SLEEP_TIME)); } catch (InterruptedException e) { }
            }

        } catch (RemoteException | NotBoundException ex) {
            System.out.println("Servidor não está online");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("A fechar socket Mesa de voto");
            socket.close();
        }
    }
}

class HandleTerminal extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;
    private MulticastSocket terminalSocket;
    private RMIServerInterface serverRMI;
    private InetAddress group;
    private int cc;

    public HandleTerminal(MulticastSocket socket, RMIServerInterface server, InetAddress grupo, int cartão) {
        this.terminalSocket = socket;
        this.serverRMI = server;
        this.group = grupo;
        this.cc = cartão;
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
        try {
            String message = "$ Terminal disponível";

            System.out.println("Redirecionando-o para um terminal de voto");

            // Vê que terminais estão à espera da mensagem "Terminal Disponível"
            enviaServer(terminalSocket, message, group);
            // Os que estiverem disponíveis enviam mensagem com o seu id. A mesa de voto capta um dos terminais.
            String terminal = filterMessage(terminalSocket, "@ Terminal");
            System.out.println("1 - " + terminal);
            terminal = terminal.replaceFirst("@", "\\$");
            // Avisa os terminais qual dos terminais captou
            enviaServer(terminalSocket, terminal, group);
            // O terminal informa que está pronto a ser utilizado
            message = filterMessage(terminalSocket, "@ Confirmação: ");
            System.out.println("2 - " + message);

            if(message.equals("@ Confirmação: Terminal encontrado")) {
                System.out.println("Será dirigido para " + terminal.replaceFirst("\\$", "o"));

                message = "$ Bem-vindo eleitor com o cc " + cc;
                enviaServer(terminalSocket, message, group);

                int entrou = 0;
                while(entrou == 0) {
                    message = filterMessage(terminalSocket, "@ type | login");

                    String[] nickPassword = message.strip().split(";");
                    String nick = nickPassword[1].substring(nickPassword[1].lastIndexOf(" ") + 1);
                    String password = nickPassword[2].substring(nickPassword[2].lastIndexOf(" ") + 1);

                    if(serverRMI.loginUser(nick, password, cc)) {
                        //message = "$ Logged in!";
                        message = "$ type | status; logged | on; msg | Bem-vindo ao eVoting";
                        entrou = 1;
                    } else {
                        message = "$ type | status; logged | off; msg | Username ou Password incorretos!";
                    }

                    enviaServer(terminalSocket, message, group);
                }
            }
            else {
                System.out.println("Não froam encontrados terminais disponíveis, tente novamente mais tarde");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("A sair de handleTerminal");
        }
    }
}