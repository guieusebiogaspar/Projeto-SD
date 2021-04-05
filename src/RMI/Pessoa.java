package RMI;

import java.io.Serializable;
import java.util.HashMap;

public class Pessoa implements Serializable {
    private String tipo;
    private String nome;
    private String username;
    private String password;
    private int phone;
    private String morada;
    private int cc;
    private String validade;
    private String grupo;
    private HashMap<String, String> votou;
    private boolean aVotar;

    /**
     * Construtor vazio do objeto Pessoa
     */
    public Pessoa(){
    }

    /**
     * Construtor do objeto Pessoa
     *
     * @param tipo - tipo da pessoa
     * @param nome - nome da pessoa
     * @param username - username da pessoa
     * @param password - password da pessoa
     * @param phone - numeor de contacto telefonico
     * @param morada - moarda da pessoa
     * @param cc - numero do cc
     * @param validade - validade cc
     * @param grupo - grupo a que pertence
     *
     */

    public Pessoa(String tipo, String nome, String username, String password, int phone, String morada, int cc, String validade, String grupo) {
        this.tipo = tipo;
        this.nome = nome;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.morada = morada;
        this.cc = cc;
        this.validade = validade;
        this.grupo = grupo;
        this.aVotar = false; // Fica a true se a pessoa estiver num voting terminal
        this.votou = new HashMap<>(); // HasMap com uma eleição como key e uma mesa de voto como value
    }

    public String getTipo() {
        return tipo;
    }

    public String getNome() {
        return nome;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPhone() {
        return phone;
    }

    public String getMorada() {
        return morada;
    }

    public int getCc() {
        return cc;
    }

    public String getValidade() {
        return validade;
    }

    public String getGrupo() {
        return grupo;
    }

    public HashMap<String, String> getVotou() {
        return votou;
    }

    public void setVotou(HashMap<String, String> votou) {
        this.votou = votou;
    }

    public boolean getAVotar() {
        return aVotar;
    }

    public void setAVotar(boolean aVotar) {
        this.aVotar = aVotar;
    }
}

