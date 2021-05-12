package RMI;

import java.io.Serializable;

public class DataEleição implements Serializable {
    private int dia;
    private int mes;
    private int ano;
    private int hora;
    private int minuto;

    /**
     * Construtor vazio do objeto DataEleição
     */
    public DataEleição(){
    }

    public DataEleição(int dia, int mes, int ano, int hora, int minuto) {
        this.dia = dia;
        this.mes = mes;
        this.ano = ano;
        this.hora = hora;
        this.minuto = minuto;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public int getMinuto() {
        return minuto;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }

    @Override
    public String toString() {
        return "Dia " + dia + "/" + mes + "/" + ano + ", às " + hora + "h" + minuto;
    }
}
