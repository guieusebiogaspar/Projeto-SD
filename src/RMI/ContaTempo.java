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
        this.start();
    }

    public int writeBD(String status, ArrayList<Eleição> eleicoes, Eleição el)
    {
        File f = new File("eleicoes.obj");
        try {
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(eleicoes);
            oos.close();
            System.out.println("Eleicao " + el.getTitulo() + status);
            return 1;
        } catch (FileNotFoundException ex) {
            System.out.println("Erro a criar ficheiro.");
        } catch (IOException ex) {
            System.out.println("Erro a escrever para o ficheiro.");
        }
        return 0;
    }
    public void run() {
        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());

        ArrayList<Eleição> eleicoes = new ArrayList<>();
        System.out.println("Estou a contar o tempo");
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
                LocalTime lt = LocalTime.now();

                if(ld.getYear() >= el.getFim().getAno() && ld.getMonthValue() >= el.getFim().getMes() && ld.getDayOfMonth() >= el.getFim().getDia())
                {
                    if(ld.getYear() > el.getFim().getAno())
                    {
                        if(el.getAtiva())
                        {
                            el.setAtiva(false);
                            el.setTerminada(true);
                            if(writeBD("terminada", eleicoes, el) == 1)
                                break;
                        }
                    }
                    if(ld.getYear() == el.getFim().getAno())
                    {
                        if(ld.getMonthValue() > el.getFim().getMes())
                        {
                            if(el.getAtiva())
                            {
                                el.setAtiva(false);
                                el.setTerminada(true);
                                if(writeBD("terminada", eleicoes, el) == 1)
                                    break;
                            }
                        }
                        if(ld.getMonthValue() == el.getFim().getMes())
                        {
                            if(ld.getDayOfMonth() > el.getFim().getDia())
                            {
                                if(el.getAtiva())
                                {
                                    el.setAtiva(false);
                                    el.setTerminada(true);

                                    if(writeBD("terminada", eleicoes, el) == 1)
                                        break;
                                }
                            }
                            if(ld.getDayOfMonth() == el.getFim().getDia())
                            {
                                if(lt.getHour() > el.getFim().getHora())
                                {
                                    if(el.getAtiva())
                                    {
                                        el.setAtiva(false);
                                        el.setTerminada(true);

                                        if(writeBD("terminada", eleicoes, el) == 1)
                                            break;
                                    }
                                }
                                if(lt.getHour() == el.getFim().getHora())
                                {
                                    if(lt.getMinute() >= el.getFim().getMinuto())
                                    {
                                        if(el.getAtiva())
                                        {
                                            el.setAtiva(false);
                                            el.setTerminada(true);

                                            if(writeBD("terminada", eleicoes, el) == 1)
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if(ld.getYear() >= el.getInicio().getAno() && ld.getMonthValue() >= el.getInicio().getMes() && ld.getDayOfMonth() >= el.getInicio().getDia())
                {
                    if(el.getInicio().getAno() == el.getFim().getAno() && el.getInicio().getMes() == el.getFim().getMes() && el.getInicio().getDia() == el.getFim().getDia())
                    {
                        if(el.getInicio().getHora() == el.getFim().getHora())
                        {
                            if(lt.getHour() >= el.getInicio().getHora())
                            {
                                if(lt.getMinute() >= el.getInicio().getMinuto() && lt.getMinute() < el.getFim().getMinuto())
                                {
                                    if(!el.getAtiva())
                                    {
                                        el.setAtiva(true);
                                        if(writeBD("iniciada", eleicoes, el) == 1)
                                            break;
                                    }
                                }
                            }

                        }
                        else{
                            if(lt.getHour() >= el.getInicio().getHora() && lt.getHour() < el.getFim().getHora())
                            {
                                if(!el.getAtiva())
                                {
                                    el.setAtiva(true);
                                    el.setTerminada(false);
                                    if(writeBD("iniciada", eleicoes, el) == 1)
                                        break;
                                }
                            }
                        }
                    }
                    else{
                        if(el.getInicio().getAno() != el.getFim().getAno())
                        {
                            if(ld.getYear() >= el.getInicio().getAno() && ld.getYear() < el.getFim().getAno())
                            {
                                if(!el.getAtiva())
                                {
                                    el.setAtiva(true);
                                    el.setTerminada(false);

                                    if(writeBD("iniciada", eleicoes, el) == 1)
                                        break;
                                }
                            }
                        }
                        else{
                            if(el.getInicio().getMes() != el.getFim().getMes())
                            {
                                if(ld.getMonthValue() >= el.getInicio().getMes() && ld.getMonthValue() < el.getFim().getMes())
                                {
                                    if(ld.getYear() >= el.getInicio().getAno() && ld.getYear() < el.getFim().getAno())
                                    {
                                        if(!el.getAtiva())
                                        {
                                            el.setAtiva(true);
                                            el.setTerminada(false);

                                            if(writeBD("iniciada", eleicoes, el) == 1)
                                                break;
                                        }
                                    }
                                }
                            }
                            else{
                                if(el.getInicio().getDia() != el.getFim().getDia())
                                {
                                    if(ld.getDayOfMonth() >= el.getInicio().getDia() && ld.getDayOfMonth() < el.getFim().getDia())
                                    {
                                        if(!el.getAtiva())
                                        {
                                            el.setAtiva(true);
                                            el.setTerminada(false);

                                            if(writeBD("iniciada", eleicoes, el) == 1)
                                                break;
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