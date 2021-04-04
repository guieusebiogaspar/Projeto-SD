package RMI;

import Multicast.MesaVoto;

import java.io.Serializable;
import java.util.*;

public class Eleição implements Serializable {
    private DataEleição inicio;
    private DataEleição fim;
    private String titulo;
    private String descrição;
    private ArrayList<String> grupos;
    private ArrayList<Lista> listas;
    private ArrayList<String> mesasVoto;
    private ArrayList<String> quemPodeVotar;
    private ArrayList<Integer> jaVotaram;
    private Boolean ativa;
    private Boolean terminada;


    /**
     * Construtor vazio do objeto Eleição
     */
    public Eleição() {
    }
    /**
     * Construtor do objeto Eleição
     *
     * @param inicio - data inicio da eleição
     * @param fim - data fim eleição
     * @param titulo - titulo eleicao
     * @param descrição - descrição da eleição
     * @param grupos - grupos que podem votar na eleição
     */
    public Eleição(DataEleição inicio, DataEleição fim, String titulo, String descrição, ArrayList<String> grupos, ArrayList<Lista> listas, ArrayList<String> mesasVoto, ArrayList<String> quemPodeVotar) {
        this.inicio = inicio;
        this.fim = fim;
        this.titulo = titulo;
        this.descrição = descrição;
        this.grupos = grupos;
        this.listas = listas;
        this.mesasVoto = mesasVoto;
        this.quemPodeVotar = quemPodeVotar;
        this.ativa = false;
        this.terminada = false;
        this.jaVotaram = new ArrayList<>();
    }

    public DataEleição getInicio() {
        return inicio;
    }

    public void setInicio(DataEleição inicio) {
        this.inicio = inicio;
    }

    public DataEleição getFim() {
        return fim;
    }

    public void setFim(DataEleição fim) {
        this.fim = fim;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescrição() {
        return descrição;
    }

    public void setDescrição(String descrição) {
        this.descrição = descrição;
    }

    public ArrayList<String> getGrupos() {
        return grupos;
    }

    public Boolean getAtiva() {
        return this.ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public ArrayList<Lista> getListas() {
        return this.listas;
    }

    public ArrayList<String> getMesasVoto() {
        return mesasVoto;
    }

    public ArrayList<String> getQuemPodeVotar() {
        return quemPodeVotar;
    }

    public ArrayList<Integer> getJaVotaram() {
        return jaVotaram;
    }

    public void setJaVotaram(ArrayList<Integer> jaVotaram) {
        this.jaVotaram = jaVotaram;
    }

    public Boolean getTerminada() {
        return terminada;
    }

    public void setTerminada(Boolean terminada) {
        this.terminada = terminada;
    }
}
