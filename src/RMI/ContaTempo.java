package RMI;

import java.io.*;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.time.*;

public class ContaTempo extends Thread{

    public ContaTempo(){
        super();
    }

    public static void main(String[] args) {
        ContaTempo tempo = new ContaTempo();
        tempo.start();
    }
    public void run() {
        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());

        ArrayList<Eleição> eleicoes = new ArrayList<>();

        while(true)
        {
            File f = new File("eleicoes.obj");
            if(f.exists() && f.isFile())
            {
                try{
                    FileInputStream fis = new FileInputStream(f);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    eleicoes = (ArrayList<Eleição>)ois.readObject();
                    ois.close();
                }
                catch(FileNotFoundException ex){
                    System.out.println("Erro a abrir ficheiro.");
                }
                catch(IOException ex){
                    System.out.println("Erro a ler ficheiro.A");
                }
                catch(ClassNotFoundException ex){
                    System.out.println("Erro a converter objeto.");
                }
            }
            for(Eleição el: eleicoes) {
                LocalDate ld = LocalDate.now();
                if (ld.getYear() == el.getFim().getAno()) {
                    if (ld.getMonthValue() == el.getFim().getMes()) {
                        if (ld.getDayOfMonth() == el.getFim().getDia()) {
                            LocalTime lt = LocalTime.now();
                            if (lt.getHour() == el.getFim().getHora()) {
                                if (lt.getMinute() == el.getFim().getMinuto()) {
                                    if(el.getAtiva())
                                    {
                                        el.setAtiva(false);
                                        try {
                                            FileOutputStream fos = new FileOutputStream(f);
                                            ObjectOutputStream oos = new ObjectOutputStream(fos);
                                            oos.writeObject(eleicoes);
                                            oos.close();
                                            System.out.println("Eleicao" + el.getTitulo() + "terminada");
                                            break;
                                        } catch (FileNotFoundException ex) {
                                            System.out.println("Erro a criar ficheiro.");
                                        } catch (IOException ex) {
                                            System.out.println("Erro a escrever para o ficheiro.");
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
