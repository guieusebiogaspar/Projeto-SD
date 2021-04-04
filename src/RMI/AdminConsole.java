package RMI;

import java.io.*;
import java.lang.reflect.Array;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.LocateRegistry;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.time.*;

public class AdminConsole extends UnicastRemoteObject implements AdminConsoleInterface {

    AdminConsole() throws RemoteException {
        super();
    }

    public void menu() {
        System.out.println("--------- MENU ----------");
        System.out.println("[1] - Registar pessoa");
        System.out.println("[2] - Criar eleição");
        System.out.println("[3] - Editar eleição");
        System.out.println("[4] - Detalhes de eleições");
        System.out.println("[0] - Sair");
    }
    public void olaServidor() throws RemoteException
    {
        System.out.println("Servidor diz ola ao admin");
    }
    public void adeusServidor() throws RemoteException{
        System.out.println("Servidor diz adeus ao admin");
    }
    public void readCommand(RMIServerInterface server, String command) throws IOException {
        switch (command) {
            case "1":
                registar(server);
                break;
            case "2":
                criarEleiçao(server);
                break;
            case "3":
                editarEleiçao(server);
                break;
            case "4":
                showDetails(server);
                break;
            case "0":
                System.out.println("Encerrando admin console...");
                server.adeusAdmin();
                System.exit(0);
            default:
                System.out.println("Opção Inválida");
        }
    }

    public Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            System.out.println("Introduza um número inteiro!");
            return null;
        }
    }

    public void showDetails(RMIServerInterface server) throws IOException
    {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        System.out.println("----- Detalhes de Eleições ------");
        System.out.println("[1] - Ativas");
        System.out.println("[2] - Terminadas");
        System.out.println("[0] - Sair");

        String inputzao = reader.readLine();

        switch(inputzao){
            case "1":
                mostrarAtivas(server);
                break;
            case "2":
                mostrarTerminadas(server);
                break;
            case "0":
                return;
            default:
                System.out.println("Opção inválida");
        }
    }

    public void mostrarAtivas(RMIServerInterface server) throws IOException{

        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        ArrayList<Eleição> eleicoes = server.getEleições();

        if(eleicoes.size() > 0) {
            for(Eleição el: eleicoes)
            {
                if(el.getAtiva())
                    System.out.println(el.getTitulo());

            }
        } else {
            System.out.println("Não existem eleições");
            return;
        }

        Eleição el = null;
        while(el == null || !el.getAtiva())
        {
            System.out.println("De qual eleicao deseja ver as informações? (titulo) (0 para sair)");
            String tit = reader.readLine();
            if(tit.equals("0"))
                return;
            el = server.getEleição(tit);
            if(el == null || !el.getAtiva())
                System.out.println("Por favor selecione uma eleição ativa");
        }
        System.out.println("---- ELEIÇÃO " + el.getTitulo() + " -----");
        System.out.println("Descrição: " + el.getDescrição());
        System.out.println("Data inicio: " + el.getInicio().toString());
        System.out.println("Data fim: " + el.getFim().toString());
        System.out.println("Grupos: ");
        for(String s : el.getGrupos())
            System.out.print(s + "\t");
        System.out.println();
        System.out.println("Dados Listas: ");
        for(int i = 0; i < el.getListas().size(); i++) {
            if(i == (el.getListas().size() - 1) || i == (el.getListas().size() - 1)) {
                System.out.println("\tVotos " + el.getListas().get(i).getNome() + ": " + el.getListas().get(i).getNumVotos());
            } else {
                System.out.println("\tVotos Lista " + el.getListas().get(i).getNome() + ": " + el.getListas().get(i).getNumVotos());
            }
        }

        System.out.println("Mesas de Voto: ");
        for(String s : el.getMesasVoto())
            System.out.println("Mesa voto " + s);

    }

    public void mostrarTerminadas(RMIServerInterface server) throws IOException
    {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        ArrayList<Eleição> eleicoes = server.getEleições();

        for(Eleição el: eleicoes)
        {
            if(!el.getAtiva())
                System.out.println(el.getTitulo());

        }

        Eleição el = null;
        while(el == null || el.getAtiva())
        {
            System.out.println("De qual eleicao deseja ver as informações? (titulo) (0 para sair)");
            String tit = reader.readLine();
            if(tit.equals("0"))
                return;
            el = server.getEleição(tit);
            if(el == null || !el.getAtiva())
                System.out.println("Por favor selecione uma eleição terminada");

        }

        System.out.println("---- ELEIÇÃO " + el.getTitulo() + "-----");
        System.out.println("Descrição: " + el.getDescrição());
        System.out.println("Data inicio: " + el.getInicio().toString());
        System.out.println("Data fim: " + el.getFim().toString());
        System.out.println("Grupos: ");
        for(String s : el.getGrupos())
            System.out.print(s + "\t");
        System.out.println();

        double totalVotos = 0;
        for(int i = 0; i < el.getListas().size(); i++) {
            totalVotos += el.getListas().get(i).getNumVotos();
        }

        DecimalFormat df2 = new DecimalFormat("#.##");

        System.out.println("Total de votos: " + (int) totalVotos);
        System.out.println("Dados Listas: ");
        for(Lista l : el.getListas())
            if(totalVotos > 0) {
                System.out.println("\tVotos Lista " + l.getNome() + ": " + l.getNumVotos() + " -> " + df2.format((((double) l.getNumVotos()) * 100.0) / totalVotos) + "%");
            } else {
                System.out.println("\tVotos Lista " + l.getNome() + ": " + l.getNumVotos() + " -> " + "0%");
            }

        System.out.println("Mesas de Voto: ");
        for(String s : el.getMesasVoto())
            System.out.println("Mesa voto " + s);

        int max = 0;
        ArrayList<String> vencedora = new ArrayList<>();
        for(int i = 0; i < (el.getListas().size() - 2); i++) {
            if(el.getListas().get(i).getNumVotos() > max) { // se os votos for maior que o max limpa o arraylist e adiciona a lista
                max = el.getListas().get(i).getNumVotos();
                vencedora.clear();
                vencedora.add(el.getListas().get(i).getNome());
            }
            else if (el.getListas().get(i).getNumVotos() == max) {
                vencedora.add(el.getListas().get(i).getNome());
            }
        }

        System.out.println();
        if(vencedora.size() > 1) {
            System.out.print("Houve um empate nas listas: ");
            for(int i = 0; i < vencedora.size(); i++) {
                if(i != (vencedora.size() - 1)) {
                    System.out.print(vencedora.get(i) + ", ");
                } else {
                    System.out.println(vencedora.get(i) + ".");
                }
            }
        } else {
            System.out.println("A lista vencedora foi a lista " + vencedora.get(0));
        }

    }

    public void registar(RMIServerInterface server) throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        boolean check = true;

        System.out.println("--------- Registar Pessoa ---------");
        String tipo = "", nome, nickname, password,morada, validade, grupo;
        Integer phone = null, cc = null;
        while(check) {
            System.out.println("A que grupo pertence?");
            System.out.println("[1] - Estudante");
            System.out.println("[2] - Docente");
            System.out.println("[3] - Funcionário");
            tipo = reader.readLine();
            switch(tipo) {
                case "1":
                    tipo = "Estudante";
                    check = false;
                    break;
                case "2":
                    tipo = "Docente";
                    check = false;
                    break;
                case "3":
                    tipo = "Funcionário";
                    check = false;
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
        System.out.print("Nome: ");
        nome = reader.readLine();
        System.out.print("Nickname: ");
        nickname = reader.readLine();
        System.out.print("Password: ");
        password = reader.readLine();
        System.out.print("Phone: ");
        while(phone == null) phone = tryParse(reader.readLine());
        System.out.print("Morada: ");
        morada = reader.readLine();
        System.out.print("Cartão de cidadão: ");
        check = true;
        while(check) {
            while(cc == null) cc = tryParse(reader.readLine());
            check = server.verificaCC(cc);
            if(check) {
                System.out.println("Esse cartão de cidadão pertence a outro cidadão!");
                cc = null;
            }
        }

        System.out.print("Validade cartão de cidadão (MM/AA): ");
        validade = reader.readLine();
        System.out.print("Departamento a que pertence: ");
        grupo = reader.readLine();

        Pessoa pessoa = new Pessoa(tipo, nome, nickname, password, phone, morada, cc, validade, grupo);

        server.registar(pessoa);
        System.out.println("Pessoa registada no servidor!");
    }

    public void criarEleiçao(RMIServerInterface server) throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        System.out.println("--------- Criar Eleição ---------");
        Integer diaInicio = null, mesInicio = null, anoInicio = null, horaInicio = null, minutoInicio = null, diaFim = null, mesFim = null, anoFim = null, horaFim = null, minutoFim = null;
        String titulo, descrição;
        String[] gruposInput;
        ArrayList<String> grupos = new ArrayList<>();
        ArrayList<Lista> listas = new ArrayList<>();
        ArrayList<String> mesas = new ArrayList<>();
        ArrayList<String> opcoesVoto = new ArrayList<>();
        System.out.print("Dia de início da eleição: ");
        while(diaInicio == null) diaInicio = tryParse(reader.readLine());
        System.out.print("Mês de início da eleição: ");
        while(mesInicio == null) mesInicio = tryParse(reader.readLine());
        System.out.print("Ano de início da eleição: ");
        while(anoInicio == null) anoInicio = tryParse(reader.readLine());
        System.out.print("Hora de início da eleição: ");
        while(horaInicio == null) horaInicio = tryParse(reader.readLine());
        System.out.print("Minuto de início da eleição: ");
        while(minutoInicio == null) minutoInicio = tryParse(reader.readLine());
        System.out.print("Dia de fim da eleição: ");
        while(diaFim == null) diaFim = tryParse(reader.readLine());
        System.out.print("Mês de fim da eleição: ");
        while(mesFim == null) mesFim = tryParse(reader.readLine());
        System.out.print("Ano de fim da eleição: ");
        while(anoFim == null) anoFim = tryParse(reader.readLine());
        System.out.print("Hora de fim da eleição: ");
        while(horaFim == null) horaFim = tryParse(reader.readLine());
        System.out.print("Minuto de fim da eleição: ");
        while(minutoFim == null) minutoFim = tryParse(reader.readLine());
        System.out.print("Título da eleição: ");
        titulo = reader.readLine();
        System.out.print("Descrição da eleição: ");
        descrição = reader.readLine();
        System.out.print("Departamentos (sigla) que podem votar nesta eleição (separe por espaços o nome dos departamentos): ");
        gruposInput = reader.readLine().trim().split(" ");
        for(int i = 0; i < gruposInput.length; i++)
            grupos.add(gruposInput[i]);

        DataEleição inicio = new DataEleição(diaInicio, mesInicio, anoInicio, horaInicio, minutoInicio);
        DataEleição fim = new DataEleição(diaFim, mesFim, anoFim, horaFim, minutoFim);

        System.out.println("Quantas listas tem a eleição?");
        Integer nListas = tryParse(reader.readLine());
        for(int i = 0; i < nListas; i++)
        {
            int cond = 0;
            String nome = null;
            if(i == 0)
            {
                System.out.print("Lista: ");
                nome = reader.readLine();
                Lista l = new Lista(nome);
                listas.add(l);
            }
            else{
                while(cond == 0)
                {
                    int n_passou = 0;
                    System.out.print("Lista: ");
                    nome = reader.readLine();
                    for(Lista a : listas)
                    {
                        if(a.getNome().equals(nome))
                        {
                            System.out.println("Já Há uma lista com esse nome!!");
                            n_passou = 1;
                        }
                    }
                    if(n_passou == 0)
                        cond = 1;
                }

                Lista l = new Lista(nome);
                listas.add(l);
            }
        }

        Lista branco = new Lista("Branco");
        Lista nulo = new Lista("Nulo");
        listas.add(branco);
        listas.add(nulo);

        System.out.println("Quantas mesas de voto irá ter esta eleição?");
        nListas = tryParse(reader.readLine());
        for(int i = 0; i < nListas; i++)
        {
            int cond = 0;
            String nome = null;
            if(i == 0){
                System.out.print("Local da mesa de voto (ex. DEI): ");
                nome = reader.readLine();
                mesas.add(nome);
            }
            else {
                while (cond == 0) {
                    int n_passou = 0;
                    System.out.print("Local da mesa de voto (ex. DEI): ");
                    nome = reader.readLine();
                    for (String a : mesas) {
                        if (a.equals(nome)) {
                            System.out.println("Já há uma lista com esse nome!!");
                            n_passou = 1;
                        }
                    }
                    if (n_passou == 0)
                        cond = 1;
                }
                mesas.add(nome);
            }
        }
        System.out.println("Quais os grupos de pessoas que podem votar? (separe por espaços o número de opções que pretende)");
        System.out.println("1 - Estudantes");
        System.out.println("2 - Docentes");
        System.out.println("3 - Funcionários");
        String[] opcoes = reader.readLine().trim().split(" ");

        for(int i = 0; i < opcoes.length; i++)
        {
            if(opcoes[i].equals("1"))
                opcoesVoto.add("Estudante");
            if(opcoes[i].equals("2"))
                opcoesVoto.add("Docente");
            if(opcoes[i].equals("3"))
                opcoesVoto.add("Funcionário");
        }
        LocalDate ld = LocalDate.now();
        LocalTime lt = LocalTime.now();
        /*if(ld.getYear() >= inicio.getAno() && ld.getMonthValue() >= inicio.getMes() && ld.getDayOfMonth() >= inicio.getDia() && lt.getHour() >= inicio.getHora() && lt.getMinute() >= inicio.getMinuto()){
            if(ld.getYear() <= fim.getAno() && ld.getMonthValue() <= fim.getMes() && ld.getDayOfMonth() <= fim.getDia() && lt.getHour() <= fim.getHora() && lt.getMinute() <= fim.getMinuto())
            {
                Eleição eleição = new Eleição(inicio, fim, titulo, descrição, grupos, true, listas, mesas, opcoesVoto, false);
                server.criarEleição(eleição);
                System.out.println("1111111");
            }
            if(ld.getYear() >= fim.getAno() && ld.getMonthValue() >= fim.getMes() && ld.getDayOfMonth() >= fim.getDia() && lt.getHour() >= fim.getHora() && lt.getMinute() >= fim.getMinuto()){
                Eleição eleição = new Eleição(inicio, fim, titulo, descrição, grupos, false, listas, mesas, opcoesVoto, true);
                server.criarEleição(eleição);
                System.out.println("222222");
            }
        }
        if(ld.getYear() <= inicio.getAno() && ld.getMonthValue() <= inicio.getMes() && ld.getDayOfMonth() <= inicio.getDia() && lt.getHour() <= inicio.getHora() && lt.getMinute() <= inicio.getMinuto()){
            Eleição eleição = new Eleição(inicio, fim, titulo, descrição, grupos, false, listas, mesas, opcoesVoto, false);
            server.criarEleição(eleição);
            System.out.println("33333333");
        }*/
        Eleição eleição = new Eleição(inicio, fim, titulo, descrição, grupos, listas, mesas, opcoesVoto);
        server.criarEleição(eleição);
        System.out.println("Eleição registada no servidor!");
    }

    public void editarEleiçao(RMIServerInterface server) throws IOException {

        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        ArrayList<Eleição> eleicoes = server.getEleições();

        String inputzito;
        Eleição eleição = null;
        System.out.println("--------- Editar Eleição ---------");
        for(Eleição el: eleicoes)
        {
            if(!el.getAtiva() && !el.getTerminada())
                System.out.println(el.getTitulo());

        }

        while(eleição == null)
        {
            System.out.println("Introduza o titulo da eleição que pretende alterar (Prima 0 para sair):");
            inputzito = reader.readLine();

            if (inputzito.equals("0")){
                return;
            }
            else{
                eleição = server.getEleição(inputzito);
                if(eleição == null) {
                    System.out.println("Não existe nenhuma eleição com esse título. Tente novamente.");
                }

                else if(eleição.getAtiva() == true) {
                    System.out.println("A eleição " + eleição.getTitulo() + " está a decorrer, como tal, não pode ser editada");
                    eleição = null;
                }
                else if(eleição.getTerminada() == true) {
                    System.out.println("A eleição " + eleição.getTitulo() + " já terminou, como tal, não pode ser editada");
                    eleição = null;
                }
                else{
                    System.out.println("-- Dados da eleição --");
                    System.out.println("Titulo: " + eleição.getTitulo());
                    System.out.println("Descrição: " + eleição.getDescrição());
                    System.out.println("Data início: " + eleição.getInicio());
                    System.out.println("Data Fim: " + eleição.getFim());
                    System.out.println("Departamentos:");
                    for(int i = 0; i < eleição.getGrupos().size(); i++) {
                        System.out.println("- " + eleição.getGrupos().get(i));
                    }
                    System.out.println("Listas:");
                    for(Lista l : eleição.getListas())
                        System.out.println(l.getNome());
                    System.out.println("Qual o campo da eleição que pretende alterar?");
                    System.out.println("[1] - Título");
                    System.out.println("[2] - Descrição");
                    System.out.println("[3] - Data início");
                    System.out.println("[4] - Data fim");
                    System.out.println("[5] - Departamentos");
                    System.out.println("[6] - Listas");
                    System.out.println("[0] - Sair");
                    inputzito = reader.readLine();
                    if (inputzito.equals("0")) return;//menu();

                    int check = 0;
                    while(check == 0) {
                        switch (inputzito) {
                            case "1":
                                System.out.println("Titulo atual: " + eleição.getTitulo());
                                System.out.print("Novo título: ");
                                inputzito = reader.readLine();

                                server.atualizaTitulo(eleição, inputzito);

                                System.out.println("Titulo atualizado com sucesso!");
                                check = 1;
                                break;
                            case "2":
                                System.out.println("Descrição atual: " + eleição.getDescrição());
                                System.out.print("Nova descrição: ");
                                inputzito = reader.readLine();

                                //eleição.setDescrição(inputzito);
                                server.atualizaDescricao(eleição, inputzito);
                                System.out.println("Descrição atualizada com sucesso!");
                                check = 1;
                                break;
                            case "3":
                                Integer dia = null, mes = null, ano = null;
                                System.out.println("Data início atual: " + eleição.getInicio().toString());
                                System.out.print("Novo dia: ");
                                while(dia == null) dia = tryParse(reader.readLine());
                                System.out.print("Novo mes: ");
                                while(mes == null) mes = tryParse(reader.readLine());
                                System.out.println("Novo ano: ");
                                while(ano == null) ano = tryParse(reader.readLine());

                                //eleição.getInicio().setDia(dia);
                                //eleição.getInicio().setDia(mes);
                                //eleição.getInicio().setDia(ano);
                                DataEleição d = new DataEleição(dia, mes, ano, eleição.getInicio().getHora(), eleição.getInicio().getMinuto());
                                server.atualizaDataInicio(eleição, d);

                                System.out.println("Data de início atualizada com sucesso!");
                                check = 1;
                                break;
                            case "4":
                                dia = null;
                                mes = null;
                                ano = null;
                                System.out.println("Data final atual: " + eleição.getInicio().toString());
                                System.out.print("Novo dia: ");
                                while(dia == null) dia = tryParse(reader.readLine());
                                System.out.print("Novo mes: ");
                                while(mes == null) mes = tryParse(reader.readLine());
                                System.out.println("Novo ano: ");
                                while(ano == null) ano = tryParse(reader.readLine());

                                DataEleição df = new DataEleição(dia, mes, ano, eleição.getInicio().getHora(), eleição.getInicio().getMinuto());
                                server.atualizaDataFim(eleição, df);

                                System.out.println("Data de fim atualizada com sucesso!");
                                check = 1;
                                break;
                            case "5":
                                System.out.println("Que operação deseja fazer?");
                                System.out.println("[1] - Adicionar novo departamento");
                                System.out.println("[2] - Remover departamento");
                                System.out.println("[0] - Voltar");

                                inputzito = reader.readLine();
                                int check1 = 0;
                                while(check1 == 0) {
                                    switch(inputzito){
                                        case "1":
                                            System.out.print("Departamento grupo: ");
                                            inputzito = reader.readLine();

                                            //ArrayList<String> temp = eleição.getGrupos();
                                            //temp.add(inputzito);
                                            //eleição.setGrupos(temp);
                                            server.addGrupo(eleição, inputzito);
                                            System.out.println("Departamento adicionado com sucesso!");
                                            check1 = 1;
                                            break;
                                        case "2":
                                            System.out.print("Departamento a remover: ");
                                            inputzito = reader.readLine();

                                            if(server.rmvGrupo(eleição, inputzito)==1){

                                            System.out.println("Departamento removido com sucesso!");

                                            }

                                            else {
                                                System.out.println("O departamento indicado não existe na lista de grupos desta eleição.");
                                            }
                                            check1 = 1;
                                            break;
                                        case "0":
                                            check1 = 1;
                                            break;
                                        default:
                                            System.out.println("Opção inválida");
                                    }
                                }

                                check = 1;
                                break;
                            case "6":
                                System.out.println("Que operação deseja fazer?");
                                System.out.println("[1] - Adicionar nova lista");
                                System.out.println("[2] - Remover lista");
                                System.out.println("[0] - Voltar");
                                inputzito = reader.readLine();
                                int check2 = 0;
                                while(check2 == 0)
                                {
                                    switch(inputzito)
                                    {
                                        case "1":
                                            System.out.println("Qual a lista: ");
                                            inputzito = reader.readLine();
                                            server.addLista(eleição, inputzito);
                                            System.out.println("Lista adicionada com sucesso!");
                                            check2 = 1;
                                            break;
                                        case "2":
                                            System.out.println("Que lista deseja remover: ");
                                            inputzito = reader.readLine();
                                            if(server.rmvLista(eleição, inputzito) == 1)
                                            {
                                                System.out.println("Lista removida com sucesso");
                                            }
                                            else{
                                                System.out.println("A lista nao existe");
                                            }
                                            check2=1;
                                            break;
                                        case "0":
                                            check2 = 1;
                                            break;
                                        default:
                                            System.out.println("Opção inválida");

                                    }

                                }
                                check = 1;
                                break;
                            case "0":
                                check = 1;
                                //menu();
                                break;
                            default:
                                System.out.println("Opção inválida");
                        }
                    }
                }
            }

            System.out.println("Pessoa registada no servidor!");
        }


        //server.editarEleição(eleição);

    }


    public void init(AdminConsole adminConsole) {

        String command;
        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());
        //System.setProperty("java.rmi.server.hostname", "192.168.1.171");

        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        while(true)
        {
            try {
                //RMIServerInterface server = (RMIServerInterface) LocateRegistry.getRegistry("192.168.1.171", 7001).lookup("Server");
                RMIServerInterface server = (RMIServerInterface) LocateRegistry.getRegistry(7001).lookup("Server");


                if(server.obterValor() == 1)
                {
                    try{
                        server = (RMIServerInterface) LocateRegistry.getRegistry(7002).lookup("Server");
                        server.olaAdmin(adminConsole);
                        System.out.println("Admin informou server que está ligado");
                        while (true) {
                            menu();
                            System.out.print("> ");
                            command = reader.readLine();
                            readCommand(server, command);
                        }
                    }
                    catch (RemoteException | NotBoundException ex) {
                        System.out.println("Servidor não está online");
                        try{
                            RMIServerInterface server1 = (RMIServerInterface) LocateRegistry.getRegistry(7001).lookup("Server");

                            server.olaAdmin(adminConsole);
                            System.out.println("Admin informou server que está ligado");
                            while (true) {
                                menu();
                                System.out.print("> ");
                                command = reader.readLine();
                                readCommand(server1, command);
                            }
                        }
                        catch (RemoteException | NotBoundException ex1) {
                            System.out.println("Servidor não está online");
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        //System.exit(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(server.obterValor() == 0)
                {
                    try{
                        server.olaAdmin(adminConsole);
                        System.out.println("Admin informou server que está ligado");
                        while (true) {
                            menu();
                            System.out.print("> ");
                            command = reader.readLine();
                            readCommand(server, command);
                        }
                    }
                    catch (RemoteException ex) {
                        System.out.println("Servidor não está online");
                        try{
                            RMIServerInterface server1 = (RMIServerInterface) LocateRegistry.getRegistry(7002).lookup("Server");

                            server.olaAdmin(adminConsole);
                            System.out.println("Admin informou server que está ligado");
                            while (true) {
                                menu();
                                System.out.print("> ");
                                command = reader.readLine();
                                readCommand(server1, command);
                            }
                        }
                        catch (RemoteException | NotBoundException ex1) {
                            System.out.println("Servidor não está online");
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        //System.exit(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (RemoteException | NotBoundException ex) {
                System.out.println("Servidor não está online");
                try{
                    RMIServerInterface server = (RMIServerInterface) LocateRegistry.getRegistry(7002).lookup("Server");

                    server.olaAdmin(adminConsole);
                    System.out.println("Admin informou server que está ligado");
                    while (true) {
                        menu();
                        System.out.print("> ");
                        command = reader.readLine();
                        readCommand(server, command);
                    }
                }
                catch (RemoteException | NotBoundException ex1) {
                    System.out.println("Servidor não está online");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                //System.exit(0);
            }
        }

    }

    public static void main(String[] args) throws RemoteException {
        AdminConsole adminConsole = new AdminConsole();
        adminConsole.init(adminConsole);
    }
}
