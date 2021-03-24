package RMI;

import java.io.Serializable;

public class Pessoa implements Serializable {
    private String tipo;
    private String nome;
    private String nickname;
    private String password;
    private String phone;
    private String morada;
    private String cc;
    private String validade;

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
     */

    public Pessoa(String tipo, String nome, String nickname, String password, String phone, String morada, String cc, String validade) {
        this.tipo = tipo;
        this.nome = nome;
        this.nickname = nickname;
        this.password = password;
        this.phone = phone;
        this.morada = morada;
        this.cc = cc;
        this.validade = validade;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getValidade() {
        return validade;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }
}
