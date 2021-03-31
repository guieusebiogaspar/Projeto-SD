package Multicast;

import RMI.RMIServerInterface;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
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
        System.setProperty("java.net.preferIPv4Stack", "true");

        try {
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);



            while(true) {

                boolean disponível = true;
                String message;
                System.out.println("À espera de um votante...");
                while (disponível) {
                    message = recebeCliente(socket);
                    System.out.println("1 - " + message);
                    if (message.contains("$ type | search; available | no")) {
                        enviaCliente(socket, "@ type | search; available | yes; terminal | " + this.getName(), group);
                        message = recebeCliente(socket);
                        System.out.println("2 - " + message);
                        if (message.contains("$ type | ack; terminal | " + this.getName())) {
                            disponível = false;
                            message = "@ type | confirmation; found | yes";
                            enviaCliente(socket, message, group);
                        }
                    }
                }

                // Vai iniciar uma sessão para o votante votar
                Session sessao = new Session(socket, group);
                Timer timer = new Timer();

                // Após 120 segundos a thread fecha
                timer.schedule(new ControlaTempoSessão(sessao, timer), 120000);
                System.out.println("Tem 120 de segundos de sessão ativa!");
                sessao.start();

                long start = System.currentTimeMillis();
                long end = start + 122000;

                // espera nesta thread os 120 segundos
                while (System.currentTimeMillis() < end) {
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("A fechar socket voting terminal");
            socket.close();
        }
    }

}

class Session extends Thread {
    private InetAddress group;
    private MulticastSocket socket;
    private int PORT = 4321;

    public Session(MulticastSocket socket, InetAddress group) {
        this.socket = socket;
        this.group = group;
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

        try {

            String message = null;
            message = filterMessage(socket, "$ type | welcome;");
            System.out.println(message);


            Scanner keyboardScanner = new Scanner(System.in);
            int tentativas = 3;

            while (tentativas > 0) {
                String login = "@ type | login; ";
                System.out.print("Username: ");
                login = login + "username | " + keyboardScanner.nextLine() + "; ";
                System.out.print("Password: ");
                login = login + "password | " + keyboardScanner.nextLine();

                enviaCliente(socket, login, group);

                message = filterMessage(socket, "$ type | status; logged");

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
            System.out.println("\nO seu tempo de sesão expirou. Dirija-se de novo à Mesa de Voto.");
            sessao.interrupt();
            timer.cancel();
        }
    }
}
