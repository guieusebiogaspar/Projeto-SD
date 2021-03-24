package RMI;

import java.io.Serializable;

public class Eleição implements Serializable {
    private DataEleição inicio;
    private DataEleição fim;
    private String titulo;
    private String descrição;
    private Boolean ativa;

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
     * @param ativa - se a eleição esta ativa
     */
    public Eleição(DataEleição inicio, DataEleição fim, String titulo, String descrição, Boolean ativa) {
        this.inicio = inicio;
        this.fim = fim;
        this.titulo = titulo;
        this.descrição = descrição;
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

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }
}
