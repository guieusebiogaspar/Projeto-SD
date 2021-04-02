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
import java.util.Arrays;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VotingTerminal extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;

    public static void main(String[] args) {
        VotingTerminal client = new VotingTerminal();
        client.start();
    }

    public VotingTerminal() {
        super("" + (long) (Math.random() * 1000));
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



            while(true) {

                boolean disponível = true;
                int enviou = 0;
                String message;
                System.out.println("À espera de um votante...");
                while (disponível) {
                    if(enviou == 0) {
                        message = recebeCliente(socket);
                        System.out.println("1 - " + message);
                        if (message.contains("$ type | search; available | no")) {
                            enviaCliente(socket, "@ type | search; available | yes; terminal | " + this.getName(), group);
                            enviou = 1;
                            message = recebeCliente(socket);
                            System.out.println("2 - " + message);
                            if (message.contains("$ type | ack; terminal | " + this.getName())) {
                                disponível = false;
                                enviou = 0;
                            }
                        }
                    } else {
                        message = recebeCliente(socket);
                        System.out.println("2 - " + message);
                        if (message.contains("$ type | ack; terminal | " + this.getName())) {
                            disponível = false;
                            enviou = 0;
                        }
                    }
                }

                // Vai iniciar uma sessão para o votante votar
                //Scanner keyboardScanner = new Scanner(System.in);
                Session sessao = new Session();
                Timer timer = new Timer();

                // Após 120 segundos a thread fecha
                timer.schedule(new ControlaTempoSessão(sessao, timer), 10000);
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
    private String MULTICAST_ADDRESS_SESSIONS = "224.0.224.1";
    private int PORT = 4321;


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
        MulticastSocket socketSession = null;

        try {

            socketSession = new MulticastSocket(PORT);  // create socket without binding it (only for sending)
            InetAddress groupSession = InetAddress.getByName(MULTICAST_ADDRESS_SESSIONS);
            socketSession.joinGroup(groupSession); //join the multicast group

            String message = null;
            message = filterMessage(socketSession, "$ type | welcome;");
            System.out.println(message);

            String[] obterCC = message.split(";");
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
                    login = login + "username | " + keyboardScanner.readLine() + "; ";

                    System.out.print("Password: ");
                    while(!keyboardScanner.ready()) {
                        Thread.sleep(200);
                    }
                    login = login + "password | " + keyboardScanner.readLine();

                    enviaCliente(socketSession, login, groupSession);

                    message = filterMessage(socketSession, "$ type | status; cc | " + cc + "; logged");

                    System.out.println(message);

                    if (message.contains("logged | on")) {
                        tentativas = 0;
                        System.out.println("Aqui tens o boletim de voto");
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

    public resetTimer()

    public void run() {
        if(sessao != null && sessao.isAlive()) {
            System.out.println("\nO seu tempo de sessão expirou. Dirija-se de novo à Mesa de Voto.");
            sessao.interrupt();
            timer.cancel();
        }
    }
}