package Multicast;

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
        MesaVotoRecebe recebe = new MesaVotoRecebe();
        recebe.start();
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
                    String message = "$ Bem-vindo eleitor com o cc " + cc;
                    byte[] buffer = message.getBytes();

                    group = InetAddress.getByName(MULTICAST_ADDRESS);
                    //send package to the group
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);

                    System.out.println("Redirecionando-o para uma mesa de voto");
                    socket.send(packet);
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
            socket.close();
        }
    }
}

class MesaVotoRecebe extends Thread {
    private String MULTICAST_ADDRESS = "224.0.224.0";
    private int PORT = 4321;

    public void run() {
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(PORT);  // create socket and bind it
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, true);
                socket.receive(packet);

                //System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + " with message:");
                String message = new String(packet.getData(), 0, packet.getLength());
                if(message.charAt(0) != '$') System.out.println(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}