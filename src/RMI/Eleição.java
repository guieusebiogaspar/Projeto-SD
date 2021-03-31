package RMI;

import java.io.Serializable;
import java.util.*;

public class Eleição implements Serializable {
    private DataEleição inicio;
    private DataEleição fim;
    private String titulo;
    private String descrição;
    private ArrayList<String> grupos;
    private Boolean ativa;


    // type (conselho geral ou nucleo de estudantes)
    // arrayList com listas candidatas

    // conselho geral tem listas separadas? ou uma candidatura tem as 3 (estudantes, docentes, funcionarios?
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
     * @param ativa - se a eleição esta ativa
     */
    public Eleição(DataEleição inicio, DataEleição fim, String titulo, String descrição, ArrayList<String> grupos, Boolean ativa) {
        this.inicio = inicio;
        this.fim = fim;
        this.titulo = titulo;
        this.descrição = descrição;
        this.grupos = grupos;
        this.ativa = ativa;
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

    public void setGrupos(ArrayList<String> grupos) {
        this.grupos = grupos;
    }

    public Boolean getAtiva() {
        return this.ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }
}
