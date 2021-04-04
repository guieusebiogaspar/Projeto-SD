package RMI;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.rmi.RMISecurityManager;

public class VerificaBackupServer extends Thread
{
    private int souP;
    public VerificaBackupServer()
    {
        super();
        this.start();
    }

    public int getSouP() {
        return souP;
    }

    public void run()
    {
        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());

        DatagramSocket aSocket = null;
        String received = null;
        System.out.println("Veryfing server backup...");
        try{
            aSocket = new DatagramSocket(7069);
            System.out.println("Estou a espera no porto 7069");
            while(true)
            {
                this.souP = 0;
                System.out.println("entrei");
                byte[] buffer = new byte[1000];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.setSoTimeout(5000);
                try{
                    aSocket.receive(request);
                    received = new String(request.getData(), 0, request.getLength());
                    System.out.println("RECEBIDO\t" + received);
                }
                catch (SocketTimeoutException t){
                    System.out.println("Server de Backup agora como Server Principal");
                    this.souP = 1;
                    try {

                        DatagramSocket bSocket = new DatagramSocket();
                        String message = "Enviei";
                        while (true) {
                            byte[] m = message.getBytes();
                            InetAddress aHost = InetAddress.getLocalHost();
                            DatagramPacket requestB = new DatagramPacket(m, m.length, aHost, 7070);
                            bSocket.send(requestB);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (SocketException e){System.out.println("Socket: " + e.getMessage());
        }catch (IOException e) {System.out.println("IO: " + e.getMessage());
        }finally {if(aSocket != null) aSocket.close();}
    }
}
