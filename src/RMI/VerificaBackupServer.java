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
   public VerificaBackupServer()
   {
       super();
       this.start();
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
                System.out.println("entrei");
                byte[] buffer = new byte[1000];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.setSoTimeout(5000);
                try{
                    aSocket.receive(request);
                }
                catch (SocketTimeoutException t){
                    System.out.println("nao respondes olha vou dormir");
                    while(true)
                    {
                        Thread.sleep(200);
                    }
                }

                //System.out.println("oi");
                received = new String(request.getData(), 0, request.getLength());
                System.out.println("RECEBIDO\t" + received);
            }
        }
        catch (SocketException e){System.out.println("Socket: " + e.getMessage());
        }catch (IOException | InterruptedException e) {System.out.println("IO: " + e.getMessage());
        }finally {if(aSocket != null) aSocket.close();}
    }
}
