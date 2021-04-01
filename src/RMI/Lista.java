package RMI;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Lista implements Serializable {
    private String nome;
    private int numVotos;

    public Lista()
    {

    }
    public Lista(String nome)
    {
        this.nome = nome;
        this.numVotos = 0;
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


    @Override
    public String toString() {
        return "Lista{" +
                "nome='" + nome + '\'' +
                ", numVotos=" + numVotos +
                '}';
    }
}
