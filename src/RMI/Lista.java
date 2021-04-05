package RMI;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Lista implements Serializable {
    private String nome;
    private String tipo;
    private int numVotos;
    private ArrayList<Pessoa> membros;

    /**
     * Construtor vazio do objeto Lista
     */
    public Lista()
    {
    }

    /**
     * Construtor do objeto Lista
     *
     * @param nome - nome da lista
     * @param tipo - tipo dos membros da lista
     *
     */
    public Lista(String nome, String tipo)
    {
        this.nome = nome;
        this.tipo = tipo;
        this.numVotos = 0; // numero de votos da lista
        this.membros = new ArrayList<>(); // membros da lista
    }

    public String getNome() {
        return nome;
    }

    public int getNumVotos() {
        return numVotos;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setNumVotos(int numVotos) {
        this.numVotos = numVotos;
    }

    public ArrayList<Pessoa> getMembros() {
        return membros;
    }

}
