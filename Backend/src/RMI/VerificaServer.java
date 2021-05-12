package RMI;
import java.net.*;
import java.io.*;
import java.rmi.RMISecurityManager;

public class VerificaServer extends Thread
{
    private int souB;
    public VerificaServer()
    {
        super();
        this.souB = 0;
        this.start();
    }

    public int getSouB() {
        return this.souB;
    }

    public void run()
    {
        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());
        DatagramSocket aSocket = null;
        try{
            String received = null;
            DatagramSocket bSocket = new DatagramSocket(7070);
            System.out.println("Estou a espera no porto 7070");
            while(true)
            {
                //System.out.println("entrei");
                byte[] buffer = new byte[1000];
                DatagramPacket requestB = new DatagramPacket(buffer, buffer.length);
                bSocket.setSoTimeout(5000);
                try{
                    this.souB = 0;
                    bSocket.receive(requestB);
                    received = new String(requestB.getData(), 0, requestB.getLength());
                    //System.out.println("Recebido " + received);
                }
                catch (SocketTimeoutException t)
                {
                    System.out.println("Sou server primario");
                    this.souB = 1;
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
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
