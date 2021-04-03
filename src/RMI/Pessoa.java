package RMI;

import java.io.Serializable;

public class Pessoa implements Serializable {
    private String tipo;
    private String nome;
    private String nickname;
    private String password;
    private int phone;
    private String morada;
    private int cc;
    private String validade;
    private String grupo;

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
     * @param nickname - nickname da pessoa
     * @param password - password da pessoa
     * @param phone - numeor de contacto telefonico
     * @param morada - moarda da pessoa
     * @param cc - numero do cc
     * @param validade - validade cc
     * @param grupo - grupo a que pertence
     */

    public Pessoa(String tipo, String nome, String nickname, String password, int phone, String morada, int cc, String validade, String grupo) {
        this.tipo = tipo;
        this.nome = nome;
        this.nickname = nickname;
        this.password = password;
        this.phone = phone;
        this.morada = morada;
        this.cc = cc;
        this.validade = validade;
        this.grupo = grupo;
    }

    public String getTipo() {
        return tipo;
    }

    public String getNome() {
        return nome;
    }

    public String getNickname() {
        return nickname;
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

}
