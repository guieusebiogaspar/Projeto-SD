package Multicast;

import RMI.RMIServerInterface;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VotingTerminal extends Thread {
    //private String MULTICAST_ADDRESS = "224.0.224.0";
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

            // Se a mensagem não começar por @
            if(message.charAt(0) != '@') {
                return message;
            }
        }
    }

    public String filterMessage(MulticastSocket socket, String expression) throws IOException {
        String message = null;
        while(message == null) {
            message = recebeCliente(socket);
            if(!message.contains(expression)) {
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
            if(last < 255) {
                last = last + 1;
            } else {
                last = last - 1;
            }

            String newAddress = MULTICAST_ADDRESS.substring(0, MULTICAST_ADDRESS.length()-1);
            newAddress = newAddress + last;

            while(true) {

                boolean disponível = true;
                int enviou = 0;
                String message;
                System.out.println("À espera de um votante...");
                while (disponível) {
                    if(enviou == 0) {
                        message = recebeCliente(socket);
                        //System.out.println("1 - " + message);
                        if (message.contains("type | search; available | no")) {
                            enviaCliente(socket, "@ type | search; available | yes; terminal | " + this.getName(), group);
                            enviou = 1;
                            message = recebeCliente(socket);
                            //System.out.println("2 - " + message);
                            if (message.contains("type | ack; terminal | " + this.getName())) {
                                disponível = false;
                                enviou = 0;
                            }
                        }
                    } else {
                        message = recebeCliente(socket);
                        //System.out.println("2 - " + message);
                        if (message.contains("type | ack; terminal | " + this.getName())) {
                            disponível = false;
                            enviou = 0;
                        }
                    }
                }

                // Vai iniciar uma sessão para o votante votar
                Session sessao = new Session(newAddress);
                Timer timer = new Timer();

                // Após 120 segundos a thread fecha
                timer.schedule(new ControlaTempoSessão(sessao, timer), 120000);
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
    private String MULTICAST_ADDRESS_SESSIONS;
    private int PORT = 4321;

    Session(String address){
        this.MULTICAST_ADDRESS_SESSIONS = address;
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
        MulticastSocket socketSession = null;

        try {

            socketSession = new MulticastSocket(PORT);  // create socket without binding it (only for sending)
            InetAddress groupSession = InetAddress.getByName(MULTICAST_ADDRESS_SESSIONS);
            socketSession.joinGroup(groupSession); //join the multicast group

            String message = null;
            message = filterMessage(socketSession, "type | welcome", null);
            String[] info = message.trim().split(";");
            for(int i = 0; i < info.length; i++) {
                if(info[i].contains("user")) {
                    System.out.println("Bem-vindo user com o cartão de cidadão " + info[i].substring(info[i].lastIndexOf(" ") + 1));
                }
            }

            String[] obterCC = message.trim().split(";");
            String cc = obterCC[1].substring(obterCC[1].lastIndexOf(" ") + 1);

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
                    message = filterMessage(socketSession, "type | status", "cc | " + cc);
                    if (message.contains("logged | on")) {
                        info = message.trim().split(";");
                        System.out.println();
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
                        }
                    }
                }

            } catch (IOException | InterruptedException e) {
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ControlaTempoSessão extends TimerTask {
    private Thread sessao;
    private Timer timer;

    ControlaTempoSessão(Thread sessao, Timer timer) {
        this.sessao = sessao;
        this.timer = timer;
    }

    public void run() {
        if(sessao != null && sessao.isAlive()) {
            System.out.println("\nO seu tempo de sessão expirou. Dirija-se de novo à Mesa de Voto.");
            sessao.interrupt();
            timer.cancel();
        }
    }
}