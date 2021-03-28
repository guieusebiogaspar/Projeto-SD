package Multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

public class VotingTerminal extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;

    public static void main(String[] args) {
        VotingTerminal client = new VotingTerminal();
        client.start();
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

    public void run() {
        MulticastSocket socket = null;
        System.out.println(this.getName() + " ready...");

        try {
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            while (true) {
                String message = recebeCliente(socket);
                System.out.println(message);

                Scanner keyboardScanner = new Scanner(System.in);
                int tentativas = 3;

                while(tentativas > 0) {
                    String login = "@ ";
                    System.out.print("Username: ");
                    login = login + keyboardScanner.nextLine() + " ";
                    System.out.print("Password: ");
                    login = login + keyboardScanner.nextLine();

                    enviaCliente(socket, login, group);

                    message = recebeCliente(socket);


                    if(message.equals("$ Logged in!")) {
                        System.out.println(message);
                        tentativas = 0;
                        System.out.println("Aqui tens o boletim de voto");
                    } else {
                        System.out.println(message);
                        tentativas -= 1;
                        if(tentativas > 0) {
                            System.out.println("A autenticação falhou, tem mais " + tentativas + " tentativas");
                        } else {
                            System.out.println("Falhou na autenticação 3 vezes... Dirija-se à mesa de voto outra vez se quiser votar");
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}
