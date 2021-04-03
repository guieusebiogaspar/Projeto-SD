package RMI;
import java.net.*;
import java.io.*;
import java.rmi.RMISecurityManager;

public class VerificaServer extends Thread
{
    public VerificaServer()
    {
        super();
        this.start();
    }

    public void run()
    {
        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());

        DatagramSocket aSocket = null;
        try{
            aSocket = new DatagramSocket();
            String message = "Enviei";
            while(true)
            {
                byte[] m = message.getBytes();
                InetAddress aHost = InetAddress.getLocalHost();
                DatagramPacket request = new DatagramPacket(m, m.length, aHost, 7069);
                aSocket.send(request);
            }
        }
        catch (SocketException | UnknownHostException e){System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {if(aSocket != null) aSocket.close();}
    }
}
