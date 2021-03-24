package RMI;

import java.io.Serializable;

public class Eleição implements Serializable {
    private String diaInicio;
    private String horaInicio;
    private String minutoInicio;
    private String diaFim;
    private String horaFim;
    private String minutoFim;
    private String titulo;
    private String descrição;

    /**
     * Construtor vazio do objeto Eleição
     */
    public Eleição() {
    }

    /**
     * Construtor do objeto Eleição
     *
     * @param diaInicio - nome da pessoa
     * @param horaInicio - nickname da pessoa
     * @param minutoInicio - password da pessoa
     * @param diaFim - dia de fim da eleição
     * @param horaFim - hora de fim da eleição
     * @param minutoFim - minuto de fim da eleição
     * @param titulo - titulo eleicao
     * @param descrição - descrição da eleição
     */
    public Eleição(String diaInicio, String horaInicio, String minutoInicio, String diaFim, String horaFim, String minutoFim, String titulo, String descrição) {
        this.diaInicio = diaInicio;
        this.horaInicio = horaInicio;
        this.minutoInicio = minutoInicio;
        this.diaFim = diaFim;
        this.horaFim = horaFim;
        this.minutoFim = minutoFim;
        this.titulo = titulo;
        this.descrição = descrição;
    }

    public String getDiaInicio() {
        return diaInicio;
    }

    public void setDiaInicio(String diaInicio) {
        this.diaInicio = diaInicio;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getMinutoInicio() {
        return minutoInicio;
    }

    public void setMinutoInicio(String minutoInicio) {
        this.minutoInicio = minutoInicio;
    }

    public String getDiaFim() {
        return diaFim;
    }

    public void setDiaFim(String diaFim) {
        this.diaFim = diaFim;
    }

    public String getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(String horaFim) {
        this.horaFim = horaFim;
    }

    public String getMinutoFim() {
        return minutoFim;
    }

    public void setMinutoFim(String minutoFim) {
        this.minutoFim = minutoFim;
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
}
