package FrontEnd.model;

import RMI.*;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class ProjectBean {
    private RMIServerInterface server;
    private String username; // username and password supplied by the user
    private String password;
    private String cc;
    private String eleicao;
    private String tipo, nome, usernameRegisto, passwordRegisto, morada, validade, grupo;
    private Integer phone, ccRegisto;
    private DataEleição dataInicio, dataFim;
    private String titulo, descricao, opcao, mesa, grupoVotar;
    private String lista, nomeLista, pessoaLista;
    private ArrayList<Lista> listas;
    private ArrayList<String> mesas;
    private ArrayList<String> grupos;

    /**
     * Contrutor do projectBEan. Vai ligar-se ao server RMI.
     */
    public ProjectBean() {
        try {
            server = (RMIServerInterface) LocateRegistry.getRegistry(7001).lookup("Server");
            listas = new ArrayList<>();
            mesas = new ArrayList<>();
            grupos = new ArrayList<>();
        } catch (NotBoundException | RemoteException e){
            e.printStackTrace();
        }
    }

    /**
     * Regista pessoa
     *
     * @throws RemoteException
     */
    public void registarPessoa() throws RemoteException {
        Pessoa pessoa = new Pessoa(tipo, nome, usernameRegisto, passwordRegisto, phone, morada, ccRegisto, validade, grupo);

        server.registar(pessoa);
    }

    /**
     * Verifica se o cartão de cidadão já existe na database
     * @param cartao
     * @return
     * @throws RemoteException
     */
    public boolean verificaCc(Integer cartao) throws RemoteException {
        if(server.verificaCC(cartao)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Verifica se o username já existe na database
     * @param user
     * @return
     * @throws RemoteException
     */
    public boolean verificaUsername(String user) throws RemoteException {
        if(server.verificaUsername(user)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Verifica se a eleição já existe na database
     * @return
     * @throws RemoteException
     */
    public boolean verificaEleicao() throws RemoteException {
        if(server.verificaEleicao(titulo)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Verifica se a lista já existe na eleição
     * @param eleicao
     * @param lista
     * @return
     * @throws RemoteException
     */
    public boolean verificaLista(Eleição eleicao, String lista) throws RemoteException {
        if(server.verificaLista(eleicao, lista)) {
            return true;
        } else {
            return false;
        }
    }

    public Lista getListaEleicao(Eleição eleicao, String lista) throws RemoteException {
        return server.getListaEleicao(eleicao, lista);
    }

    /**
     * Alter nome da lista
     * @param eleicao
     * @param lista
     * @return
     * @throws RemoteException
     */
    public boolean mudaNomeLista(Eleição eleicao, String lista) throws RemoteException {
        if(server.verificaLista(eleicao, lista)) {
            server.mudaNomeLista(eleicao, lista, nomeLista);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Ve que pessoas podem ser adicionadas a lista
     * @param el
     * @return
     * @throws RemoteException
     */
    public ArrayList<Pessoa> pessoasValidas(Eleição el) throws RemoteException {
        ArrayList<Pessoa> pessoas = server.getPessoas();
        ArrayList<Pessoa> pessoasValidas = new ArrayList<>();

        for(int i = 0; i < pessoas.size(); i++) {
            if(pessoas.get(i).getTipo().equals(el.getQuemPodeVotar())) {
                pessoasValidas.add(pessoas.get(i));
            }
        }

        return pessoasValidas;
    }

    /**
     * Membros de uma lista
     * @param el
     * @param lista
     * @return
     * @throws RemoteException
     */
    public ArrayList<Pessoa> pessoasValidasLista(Eleição el, String lista) throws RemoteException {
        ArrayList<Pessoa> pessoasValidas = null;
        for(int i = 0; i < el.getListas().size(); i++) {
            if(el.getListas().get(i).getNome().equals(lista)) {
                pessoasValidas = el.getListas().get(i).getMembros();
                break;
            }
        }

        return pessoasValidas;
    }

    /**
     * adiciona uma pessoa a uma lista
     * @param el
     * @param lista
     * @throws RemoteException
     */
    public void adicionaPessoaLista(Eleição el, String lista) throws RemoteException {
        int cartaoo;
        try{
            cartaoo = Integer.parseInt(pessoaLista);
        } catch (Exception ex){
            return;
        }

        boolean verifica = server.verificaCC(cartaoo);

        int entrou = 0;
        Pessoa p = null;
        for (int i = 0; i < pessoasValidas(el).size(); i++) {
            if (pessoasValidas(el).get(i).getCc() == cartaoo) {
                entrou = 1;
                p = pessoasValidas(el).get(i);
                break;
            }
        }

        if (verifica && entrou == 1) {
            server.adicionaPessoaLista(el, lista, p);
        }
    }

    /**
     * remove uma pessoa de uma lista
     * @param el
     * @param lista
     * @throws RemoteException
     */
    public void removePessoaLista(Eleição el, String lista) throws RemoteException {
        int cartaoo;
        try{
            cartaoo = Integer.parseInt(pessoaLista);
        } catch (Exception ex){
            return;
        }

        boolean verifica = server.verificaCC(cartaoo);

        ArrayList<Pessoa> pessoasValidas = pessoasValidasLista(el, lista);
        int entrou = 0;
        Pessoa p = null;
        for(int i = 0; i < pessoasValidas.size(); i++) {
            if(pessoasValidas.get(i).getCc() == cartaoo) {
                entrou = 1;
                p = pessoasValidas.get(i);
                break;
            }
        }
        if(verifica && entrou == 1) {
            server.removePessoaLista(el, lista, p);
        }
    }

    /**
     * cria uma eleição
     * @throws RemoteException
     */
    public void criarEleicao() throws RemoteException {

        Eleição eleição = new Eleição(dataInicio, dataFim, titulo, descricao, grupos, listas, mesas, opcao);

        server.criarEleição(eleição);
    }

    /**
     * adiciona uma mesa de voto a eleição
     */
    public void adicionaMesa() {
        mesas.add(mesa);
    }

    /**
     * adiciona um departamento que pode votar a eleição
     */
    public void adicionaGrupo() {
        grupos.add(grupoVotar);
    }

    /**
     * adiciona uma lista a eleição
     * @param elei
     * @throws RemoteException
     */
    public void adicionaListaEleicao(String elei) throws RemoteException {
        Eleição el = server.getEleição(elei);

        server.addLista(el, lista);
    }

    /**
     * remove uma lista da eleição
     * @param elei
     * @throws RemoteException
     */
    public void removeListaEleicao(String elei) throws RemoteException {
        Eleição el = server.getEleição(elei);

        server.rmvLista(el, lista);
    }

    /**
     * adiciona uma mesa de voto a eleição
     * @param elei
     * @throws RemoteException
     */
    public void adicionaMesaEleicao(String elei) throws RemoteException {
        Eleição el = server.getEleição(elei);
        if(mesa != null){
            server.addMesa(el, mesa);
        }
    }

    /**
     * remove uma mesa de voto da eleição
     * @param elei
     * @throws RemoteException
     */
    public void removeMesaEleicao(String elei) throws RemoteException {
        Eleição el = server.getEleição(elei);
        server.rmvMesa(el, mesa);
    }

    /**
     * adiciona um departamento elegivel a votar a eleição
     * @param elei
     * @throws RemoteException
     */
    public void adicionaDepartamentoEleicao(String elei) throws RemoteException {
        Eleição el = server.getEleição(elei);
        if(grupoVotar != null) {
            server.addGrupo(el, grupoVotar);
        }
    }

    /**
     * remove um departamento da eleição
     * @param elei
     * @throws RemoteException
     */
    public void removeDepartamentoEleicao(String elei) throws RemoteException {
        Eleição el = server.getEleição(elei);
        server.rmvGrupo(el, grupoVotar);
    }

    /**
     * atualiza o titulo da eleição
     * @param elei
     * @throws RemoteException
     */
    public void atualizaTitulo(String elei) throws RemoteException {
        Eleição el = server.getEleição(elei);
        if(titulo != null) {
            server.atualizaTitulo(el, titulo);
        }
    }

    /**
     * atualiza a descrição da eleição
     * @param elei
     * @throws RemoteException
     */
    public void atualizaDescricao(String elei) throws RemoteException {
        Eleição el = server.getEleição(elei);
        if(descricao != null){
            server.atualizaDescricao(el, descricao);
        }
    }

    /**
     * atualiza a data de inicio da eleição
     * @param elei
     * @throws RemoteException
     */
    public void atualizaDataInicio(String elei) throws RemoteException {
        Eleição el = server.getEleição(elei);
        if(dataInicio != null){
            server.atualizaDataInicio(el, dataInicio);
        }
    }

    /**
     * atualiza a data de fum da eleição
     * @param elei
     * @throws RemoteException
     */
    public void atualizaDataFim(String elei) throws RemoteException {
        Eleição el = server.getEleição(elei);
        if(dataFim != null){
            server.atualizaDataFim(el, dataFim);
        }
    }

    /**
     * adiciona voto a uma lista
     * @param el
     * @return
     * @throws RemoteException
     */
    public boolean adicionaVoto(Eleição el) throws RemoteException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();

        return server.adicionaVotoWEB(el, lista, username, dtf.format(now));
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public void setEleicao(String eleicao) {
        this.eleicao = eleicao;
    }

    public void setTipo(String tipo) { this.tipo = tipo; }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setUsernameRegisto(String usernameRegisto) {
        this.usernameRegisto = usernameRegisto;
    }

    public void setPasswordRegisto(String passwordRegisto) {
        this.passwordRegisto = passwordRegisto;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public void setPhone(Integer phone) {
        this.phone = phone;
    }

    public void setCcRegisto(Integer ccRegisto) {
        this.ccRegisto = ccRegisto;
    }

    public void setDataInicio(Integer dia, Integer mes, Integer ano, Integer hora, Integer minuto) {
        DataEleição dataInicio = new DataEleição(dia, mes, ano, hora, minuto);
        this.dataInicio = dataInicio;
    }

    public void setDataFim(Integer dia, Integer mes, Integer ano, Integer hora, Integer minuto) {
        DataEleição dataFim = new DataEleição(dia, mes, ano, hora, minuto);
        this.dataFim = dataFim;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setOpcao(String opcao) {
        this.opcao = opcao;
    }

    public void setMesa(String mesa) { this.mesa = mesa; }

    public void setGrupoVotar(String grupoVotar) { this.grupoVotar = grupoVotar; }

    public void setLista(String lista) {
        this.lista = lista;
    }

    public void setNomeLista(String nomeLista) {
        this.nomeLista = nomeLista;
    }

    public void setPessoaLista(String pessoaLista) {
        this.pessoaLista = pessoaLista;
    }

    /**
     * verifica que tipo de user fez login e se os dados do login estao corretos
     * @return
     * @throws IOException
     */
    public String getUserMatchesPassword() throws IOException {
        if(server.loginUserFrontEnd(username, password).equals("admin")) {
            return "admin";
        } else if(server.loginUserFrontEnd(username, password).equals("eleitor")){
            return "eleitor";
        }
        else
        {
            return "nada";
        }
    }

    public ArrayList<Pessoa> getPessoas() throws RemoteException {
        return server.getPessoas();
    }

    public Pessoa getPessoa() throws RemoteException {
        return server.getPessoa(cc);
    }

    public ArrayList<Eleição> getEleicoes() throws RemoteException {
        return server.getEleições();
    }

    public ArrayList<Eleição> getAtivas() throws RemoteException {
        return server.getAtivas();
    }

    public ArrayList<Eleição> getAtivasVoto() throws RemoteException {
        return server.filterEleiçõesWEB(username);
    }

    public ArrayList<Eleição> getTerminadas() throws RemoteException {
        return server.getTerminadas();
    }

    public ArrayList<Eleição> getPorComecar() throws RemoteException {
        return server.getPorComecar();
    }

    public Eleição getEleição() throws RemoteException {
        return server.getEleição(eleicao);
    }

    public int getTotalVotos() throws RemoteException {
        Eleição el = getEleição();
        int totalVotos = 0;
        for(int i = 0; i < el.getListas().size(); i++) {
            totalVotos += el.getListas().get(i).getNumVotos();
        }
        return totalVotos;
    }

    /**
     * percentagem de votos de uma lista
     * @param nVotos
     * @param el
     * @return
     * @throws RemoteException
     */
    public String percentagemVotos(int nVotos, Eleição el) throws RemoteException {
        this.eleicao = el.getTitulo();
        int vtotais= getTotalVotos();
        DecimalFormat df2 = new DecimalFormat("#.##");
        double vot = ((((double) nVotos) * 100.0) / vtotais);
        String result = df2.format(vot);
        return result;
    }

    /**
     * conta votos de uma determinada mesa de voto
     * @param mesa
     * @param el
     * @return
     * @throws RemoteException
     */
    public String contaVotos(String mesa, String el) throws RemoteException {
        return server.contaVotos(mesa,el);
    }

    public String getVencedora() throws RemoteException {
        Eleição el = getEleição();
        return server.getVencedora(el);
    }


}