package RMI;

import java.io.*;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.time.*;
import java.util.Date;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
        //System.out.println("Estou a contar o tempo");
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
                    //System.out.println("Erro a ler ficheiro.A");
                }
                catch(ClassNotFoundException ex){
                    System.out.println("Erro a converter objeto.");
                }
            }
            for(Eleição el: eleicoes) {
                LocalDate ld = LocalDate.now();
                SimpleDateFormat sdfo = new SimpleDateFormat("yyyy-MM-dd");
                String dataInicio = new String(el.getInicio().getAno() + "-" + el.getInicio().getMes() + "-" + el.getInicio().getDia());
                String dataFim = new String(el.getFim().getAno() + "-" + el.getFim().getMes() + "-" + el.getFim().getDia());
                String atualDate = new String(ld.getYear()+"-"+ld.getMonthValue()+"-"+ld.getDayOfMonth());
                try {
                    Date i = sdfo.parse(dataInicio);
                    Date fim = sdfo.parse(dataFim);
                    Date atual = sdfo.parse(atualDate);
                    //System.out.println(i);
                    if(atual.after(i))
                    {
                        if(atual.before(fim))
                        {
                            if(!el.getAtiva())
                            {
                                el.setAtiva(true);
                                el.setTerminada(false);
                                if(writeBD(" iniciada", eleicoes, el) == 1)
                                    break;
                            }
                        }
                        if(atual.after(fim))
                        {
                            if(el.getAtiva())
                            {
                                el.setAtiva(false);
                                el.setTerminada(true);
                                if(writeBD(" terminada", eleicoes, el) == 1)
                                    break;
                            }
                        }
                    }
                    if(atual.equals(i))
                    {
                        if(atual.equals(fim))
                        {
                            LocalTime lt = LocalTime.now();
                            if(lt.getHour() == el.getInicio().getHora())
                            {
                                if(el.getInicio().getHora() == el.getFim().getHora())
                                {
                                    if(lt.getMinute() >= el.getInicio().getMinuto())
                                    {
                                        if(lt.getMinute() < el.getFim().getMinuto())
                                        {
                                            if(!el.getAtiva())
                                            {
                                                el.setAtiva(true);
                                                el.setTerminada(false);
                                                if(writeBD(" iniciada", eleicoes, el) == 1)
                                                    break;
                                            }
                                        }
                                        else
                                        {
                                            if(el.getAtiva())
                                            {
                                                el.setAtiva(false);
                                                el.setTerminada(true);
                                                if(writeBD(" terminada", eleicoes, el) == 1)
                                                    break;
                                            }
                                        }
                                    }
                                }
                                else{
                                    if(lt.getHour() < el.getFim().getHora())
                                    {
                                        if(lt.getMinute() >= el.getInicio().getMinuto())
                                        {
                                            if(!el.getAtiva())
                                            {
                                                el.setAtiva(true);
                                                el.setTerminada(false);
                                                if(writeBD(" iniciada", eleicoes, el) == 1)
                                                    break;
                                            }
                                        }
                                    }
                                    else{
                                        if(lt.getHour() == el.getFim().getHora())
                                        {
                                            if(lt.getMinute() >= el.getFim().getMinuto())
                                            {
                                                if(el.getAtiva())
                                                {
                                                    el.setAtiva(false);
                                                    el.setTerminada(true);
                                                    if(writeBD(" terminada", eleicoes, el) == 1)
                                                        break;
                                                }
                                            }
                                            else{
                                                if(lt.getMinute() >= el.getInicio().getMinuto())
                                                {
                                                    if(!el.getAtiva())
                                                    {
                                                        el.setAtiva(true);
                                                        el.setTerminada(false);
                                                        if(writeBD(" iniciada", eleicoes, el) == 1)
                                                            break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if(lt.getHour() > el.getInicio().getHora())
                            {
                                if(lt.getHour() >= el.getFim().getHora())
                                {
                                    if(el.getAtiva())
                                    {
                                        el.setAtiva(false);
                                        el.setTerminada(true);
                                        if(writeBD(" terminada", eleicoes, el) == 1)
                                            break;
                                    }

                                }
                                else
                                {
                                    if(!el.getAtiva())
                                    {
                                        el.setAtiva(true);
                                        el.setTerminada(false);
                                        if(writeBD(" iniciada", eleicoes, el) == 1)
                                            break;
                                    }
                                }

                            }

                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}