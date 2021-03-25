package Multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.Socket;

public class ReceiveServer extends  Thread {
    byte[] buffer1 ;
    DatagramPacket packet1 ;
    MulticastSocket pool_socket;
    private String proprio;

    public ReceiveServer(MulticastSocket socket, String proprio){
        buffer1= new byte[256];
        packet1 = new DatagramPacket(buffer1, buffer1.length);
        pool_socket=socket;
        this.proprio = proprio;
        this.start();
    }
    public void run(){
        try {
            pool_socket.receive(packet1);
        } catch (IOException e) {
            e.printStackTrace();
        }


        String message = new String(packet1.getData(), 0, packet1.getLength());
        String[] check = message.split(" ");
        String[] senhor = proprio.split(" ");

        if(!check[1].equals(senhor[1])) {
            System.out.println("mensagem que outro servidor mandou: "+message);
        }
    }

}