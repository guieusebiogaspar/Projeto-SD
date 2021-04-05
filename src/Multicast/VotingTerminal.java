package Multicast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.*;


/**
 * Terminal de voto. Local onde os votantes realizam o voto na eleição que selecionaram na Mesa de Voto.
 *
 * Primeiro os votantes fazem login e depois escolhem a lista em que pretendem votar.
 *
 */

public class VotingTerminal extends Thread {
    private String MULTICAST_ADDRESS;
    private int PORT = 4321;

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("java VotingTerminal multicastAddress");
            System.exit(0);
        }

        VotingTerminal client = new VotingTerminal(args[0]);
        client.start();
    }

    /**
     * Construtor do objeto VotingTerminal
     *
     * @param address - endereço que vai abrir o socket
     *
     */
    public VotingTerminal(String address) {
        super("" + (long) (Math.random() * 1000));
        this.MULTICAST_ADDRESS = address;
    }

    /**
     * Método que vai enviar um packet por UDP para a mesa de voto
     *
     * @param socket
     * @param message - mensagem a enviar no packet
     * @param group
     *
     */
    public void enviaCliente(MulticastSocket socket, String message, InetAddress group) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
        socket.send(packet);
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

            // Se a mensagem não começar por @ (tipo de mensagens enviadas pelo voting terminal)
            if(message.charAt(0) != '@') {
                return message;
            }
        }
    }

    /**
     * Método que vai filtrar a mensagem recebida da Mesa de Voto. Só devolve a mensagem quando esta tiver a expressão indicada
     *
     * @param socket
     * @param expression - expressao que a mensagem precisa de conter para nao ser ignorada
     *
     * @return message
     */
    public String filterMessage(MulticastSocket socket, String expression) throws IOException {
        String message = null;
        while(message == null) {
            message = recebeCliente(socket);
            if (!message.contains(expression)) {
                message = null;
            }
        }

        return message;
    }

    public void run() {
        MulticastSocket socket = null;
        System.out.println("Terminal " + this.getName() + " ready...");
        try {
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            // Prepara o address para tratar das sessões dos eleitores
            int last = Integer.parseInt(MULTICAST_ADDRESS.substring(MULTICAST_ADDRESS.length() -1));
            int last2 = 0;
            if(last < 255) {
                last = last + 1;
                last2 = last + 1;
            } else {
                last = last - 1;
                last2 = last - 2;
            }

            // endereço para a thread que vai tratar das sessões dos votantes
            String newAddress = MULTICAST_ADDRESS.substring(0, MULTICAST_ADDRESS.length()-1);
            newAddress = newAddress + last;

            // endereço para a thread que vai enviar pings à mesa de voto
            String newAddress2 = MULTICAST_ADDRESS.substring(0, MULTICAST_ADDRESS.length()-1);
            newAddress2 = newAddress2 + last2;

            MulticastSocket socketAtualiza = null;  // create socket without binding it (only for sending)
            socketAtualiza = new MulticastSocket(PORT);
            InetAddress groupAtualiza = InetAddress.getByName(newAddress2);
            socketAtualiza.joinGroup(groupAtualiza); //join the multicast group

            // Thread que vai enviar pings a mesa de voto
            new Atualiza(socketAtualiza, groupAtualiza, this.getName());

            while(true) {
                /*
                    Através de um sistema em que o voting terminal recebe, envia e recebe, este vai (ou não) receber um votante

                    Se enviar uma mensagem a dizer que está disponível, mas não for escolhido, fica simplesmente a espera de uma mensagem
                    em como mais tarde acabou por ser escolhido
                 */
                boolean disponível = true;
                int enviou = 0;
                String message;
                System.out.println("À espera de um votante...");
                while (disponível) {
                    if(enviou == 0) {
                        message = recebeCliente(socket);
                        if (message.contains("type | search; available | no")) {
                            enviaCliente(socket, "@ type | search; available | yes; terminal | " + this.getName(), group);
                            enviou = 1;
                            message = recebeCliente(socket);
                            if (message.contains("type | ack; terminal | " + this.getName())) {
                                disponível = false;
                                enviou = 0;
                            }
                        }
                    } else {
                        message = recebeCliente(socket);
                        if (message.contains("type | ack; terminal | " + this.getName())) {
                            disponível = false;
                            enviou = 0;
                        }
                    }
                }

                // Da as boas vindas ao user
                message = filterMessage(socket, "type | welcome");
                String[] info = message.trim().split(";");
                String cc = null;
                for(int i = 0; i < info.length; i++) {
                    if(info[i].contains("user")) {
                        System.out.println("Bem-vindo user com o cartão de cidadão " + info[i].substring(info[i].lastIndexOf(" ") + 1));
                        cc = info[i].substring(info[i].lastIndexOf(" ") + 1);
                    }
                }

                MulticastSocket socketSession = new MulticastSocket(PORT);  // create socket without binding it (only for sending)
                InetAddress groupSession = InetAddress.getByName(newAddress);
                socketSession.joinGroup(groupSession); //join the multicast group

                // Vai iniciar uma sessão para o votante votar
                Session sessao = new Session(socketSession, groupSession, cc);
                Timer timer = new Timer();

                // Após 120 segundos a thread fecha
                timer.schedule(new ControlaTempoSessão(sessao, timer, socketSession, groupSession, this.getName(), cc), 120000);
                sessao.start();
                System.out.println("Tem 120 de segundos de sessão ativa!");

                sessao.join();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("A fechar socket voting terminal");
            socket.close();
        }
    }

}

class Session extends Thread {
    private MulticastSocket socketSession;
    private InetAddress groupSession;
    private String cc;
    private int PORT = 4321;

    Session(MulticastSocket socket, InetAddress group, String cc){
        this.socketSession = socket;
        this.groupSession = group;
        this.cc = cc;
    }

    /**
     * Método que so avança quando receber um inteiro
     *
     * @param text
     *
     * @return Inteiro
     */
    public Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            System.out.println("Este campo só aceita números!");
            return null;
        }
    }

    /**
     * Método que vai enviar um packet por UDP para a mesa de voto
     *
     * @param socket
     * @param message - mensagem a enviar no packet
     * @param group
     *
     */
    public void enviaCliente(MulticastSocket socket, String message, InetAddress group) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
        socket.send(packet);
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
            if(message.charAt(0) != '@') {
                return message;
            }
        }
    }

    /**
     * Método que vai filtrar a mensagem recebida da Mesa de Voto. Só devolve a mensagem quando esta tiver a expressão indicada e o id indicado
     *
     * @param socket
     * @param expression - expressao que a mensagem precisa de conter para nao ser ignorada
     * @param id - cc do votante
     *
     * @return message
     */
    public String filterMessage(MulticastSocket socket, String expression, String id) throws IOException {
        String message = null;
        while(message == null) {
            message = recebeCliente(socket);
            if (id != null) {
                if (!message.contains(expression) || !message.contains(id)) {
                    message = null;
                }
            } else {
                if (!message.contains(expression)) {
                    message = null;
                }
            }
        }

        return message;
    }

    public void run() {

        BufferedReader keyboardScanner = new BufferedReader(new InputStreamReader(System.in));

        try {
            int tentativas = 3;

            while (tentativas > 0) {
                String login = "@ type | login; cc | " + cc + ";";
                System.out.print("Username: ");

                while(!keyboardScanner.ready()) {
                    Thread.sleep(200);
                }
                login = login + " username | " + keyboardScanner.readLine() + "; ";

                System.out.print("Password: ");
                while(!keyboardScanner.ready()) {
                    Thread.sleep(200);
                }
                login = login + "password | " + keyboardScanner.readLine();

                enviaCliente(socketSession, login, groupSession);
                String message = filterMessage(socketSession, "type | status", "cc | " + cc);
                String info[] = null;
                if (message.contains("logged | on")) {
                    info = message.trim().split(";");
                    for(int i = 0; i < info.length; i++) {
                        if(info[i].contains("msg")) {
                            String[] msg = info[i].trim().split("\\|");
                            System.out.println(msg[1]);
                            break;
                        }
                    }

                    tentativas = 0;
                    message = filterMessage(socketSession, "type | item_list", "cc | " + cc);

                    //System.out.println(message);

                    info = message.trim().split(";");
                    ArrayList<String> listas = new ArrayList<>();
                    for(int i = 0; i < info.length; i++) {
                        if(info[i].contains("name")) {
                            listas.add(info[i].substring(info[i].lastIndexOf(" ") + 1));
                        }
                    }

                    System.out.println("------- Listas --------");
                    for(int i = 0; i < listas.size(); i++) {
                        System.out.println("" + (i+1) + " - " + listas.get(i));
                    }

                    System.out.print("Introduza o número da lista em que pretende votar: ");
                    Integer escolha = null;
                    int check = 0;
                    while(check == 0) {
                        while(!keyboardScanner.ready()) {
                            Thread.sleep(200);
                        }
                        while(escolha == null) escolha = tryParse(keyboardScanner.readLine());
                        if(escolha > 0 && escolha <= (listas.size() + 1)) {
                            check = 1;
                        } else {
                            System.out.println("Número não corresponde a nenhuma lista");
                            escolha = null;
                        }
                    }

                    message = "@ type | vote; cc | " + cc + "; list | " + listas.get(escolha-1);

                    // ver se foi branco ou nulo
                    System.out.println("Votou " + listas.get(escolha-1) + "\nObrigado.\n");
                    enviaCliente(socketSession, message, groupSession);

                } else {
                    tentativas -= 1;
                    if (tentativas > 0) {
                        System.out.println("A autenticação falhou, tem mais " + tentativas + " tentativas");
                    } else {
                        System.out.println("Falhou na autenticação 3 vezes... Dirija-se à mesa de voto outra vez se quiser votar");
                        enviaCliente(socketSession, "@ type | timeout; user | " + cc + ";", groupSession);
                    }
                }
            }

        } catch (IOException | InterruptedException e) {
        }
    }
}

class ControlaTempoSessão extends TimerTask {
    private Thread sessao;
    private Timer timer;
    private MulticastSocket socketSession;
    private InetAddress groupSession;
    private String terminal;
    private String cc;
    private int PORT = 4321;

    ControlaTempoSessão(Thread sessao, Timer timer, MulticastSocket socket, InetAddress group, String terminal, String cc) {
        this.sessao = sessao;
        this.timer = timer;
        this.socketSession = socket;
        this.groupSession = group;
        this.terminal = terminal;
        this.cc = cc;
    }

    /**
     * Método que vai enviar um packet por UDP para a mesa de voto
     *
     * @param socket
     * @param message - mensagem a enviar no packet
     * @param group
     *
     */
    public void enviaCliente(MulticastSocket socket, String message, InetAddress group) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
        socket.send(packet);
    }


    public void run() {
        if(sessao != null && sessao.isAlive()) {
            System.out.println("\nO seu tempo de sessão expirou. Dirija-se de novo à Mesa de Voto.");
            try {
                enviaCliente(socketSession, "@ type | timeout; user | " + cc + "; terminal | " + terminal, groupSession);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sessao.interrupt();
            timer.cancel();
        }
    }
}

class Atualiza extends Thread {
    private MulticastSocket socketAtualiza;
    private InetAddress groupAtualiza;
    private int PORT = 4321;
    private String terminal;

    public Atualiza(MulticastSocket socket, InetAddress group, String terminal) {
        this.socketAtualiza = socket;
        this.terminal = terminal;
        this.groupAtualiza = group;
        this.start();
    }

    public void enviaCliente(MulticastSocket socket, String message, InetAddress group) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
        socket.send(packet);
    }

    public void run() {
        try {

            while(true) {
                enviaCliente(socketAtualiza, "@ type | update; terminal | " + terminal, groupAtualiza);
                Thread.sleep(3000);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}