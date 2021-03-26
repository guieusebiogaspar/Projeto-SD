package RMI;

import java.io.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.LocateRegistry;

public class AdminConsole extends UnicastRemoteObject implements AdminConsoleInterface {

    AdminConsole() throws RemoteException {
        super();
    }

    public void menu() {
        System.out.println("--------- MENU ---------");
        System.out.println("[1] - Registar pessoa");
        System.out.println("[2] - Criar eleição");
        System.out.println("[3] - Editar eleição");
        System.out.println("[4] - Terminar eleição");
        System.out.println("[5] - Consultar resultados de eleições passadas");
        System.out.println("[0] - Sair");
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
                terminaEleiçao(server);
                break;
            case "5":
                //consultaResultados(server);
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

    public void registar(RMIServerInterface server) throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        System.out.println("--------- Registar Pessoa ---------");
        String tipo, nome, nickname, password,morada, validade;
        Integer phone = null, cc = null;
        System.out.print("Tipo (Estudante, Docente, Funcionário): ");
        tipo = reader.readLine();
        System.out.print("Nome: ");
        nome = reader.readLine();
        System.out.print("Nickname: ");
        nickname = reader.readLine();
        System.out.println("Password: ");
        password = reader.readLine();
        System.out.print("Phone: ");
        while(phone == null) phone = tryParse(reader.readLine());
        System.out.println("Morada: ");
        morada = reader.readLine();
        System.out.print("Cartão de cidadão: ");
        while(cc == null) cc = tryParse(reader.readLine());
        System.out.print("Validade cartão de cidadão: ");
        validade = reader.readLine();

        Pessoa pessoa = new Pessoa(tipo, nome, nickname, password, phone, morada, cc, validade);

        server.registar(pessoa);
        System.out.println("Pessoa registada no servidor!");
    }

    public void criarEleiçao(RMIServerInterface server) throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        System.out.println("--------- Criar Eleição ---------");
        Integer diaInicio = null, mesInicio = null, anoInicio = null, horaInicio = null, minutoInicio = null, diaFim = null, mesFim = null, anoFim = null, horaFim = null, minutoFim = null;
        String titulo, descrição;
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

        DataEleição inicio = new DataEleição(diaInicio, mesInicio, anoInicio, horaInicio, minutoInicio);
        DataEleição fim = new DataEleição(diaFim, mesFim, anoFim, horaFim, minutoFim);

        Eleição eleição = new Eleição(inicio, fim, titulo, descrição, true);

        server.criarEleição(eleição);
        System.out.println("Pessoa registada no servidor!");
    }

    public void editarEleiçao(RMIServerInterface server) throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        String inputzito;
        Eleição eleição = null;
        System.out.println("--------- Editar Eleição ---------");

        while(eleição == null) {
            System.out.println("Introduza o titulo da eleição que pretende alterar (Prima 0 para sair):");
            inputzito = reader.readLine();

            if (inputzito.equals("0")) menu();

            eleição = server.getEleição(inputzito);
            if(eleição == null) {
                System.out.println("Não existe nenhuma eleição com esse título. Tente novamente.");
            }

            if(eleição.getAtiva() == true) {
                System.out.println("A eleição " + eleição.getTitulo() + " está a decorrer, como tal, não pode ser editada");
                eleição = null;
            }
        }

        System.out.println("-- Dados da eleição --");
        System.out.println("Titulo: " + eleição.getTitulo());
        System.out.println("Descrição: " + eleição.getDescrição());
        System.out.println("Data início: " + eleição.getInicio());
        System.out.println("Data Fim: " + eleição.getFim());
        System.out.println("Qual o campo da eleição que pretende alterar?");
        System.out.println("[1] - Título");
        System.out.println("[2] - Descrição");
        System.out.println("[3] - Data início");
        System.out.println("[4] - Data fim");
        System.out.println("[0] - Sair");
        inputzito = reader.readLine();
        if (inputzito.equals("0")) menu();

        int check = 0;
        while(check == 0) {
            switch (inputzito) {
                case "1":
                    System.out.println("Titulo atual: " + eleição.getTitulo());
                    System.out.print("Novo título: ");
                    inputzito = reader.readLine();

                    eleição.setTitulo(inputzito);

                    System.out.println("Titulo atualizado com sucesso!");
                    check = 1;
                    break;
                case "2":
                    System.out.println("Descrição atual: " + eleição.getDescrição());
                    System.out.print("Nova descrição: ");
                    inputzito = reader.readLine();

                    eleição.setDescrição(inputzito);

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

                    eleição.getInicio().setDia(dia);
                    eleição.getInicio().setDia(mes);
                    eleição.getInicio().setDia(ano);

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

                    eleição.getFim().setDia(dia);
                    eleição.getFim().setDia(mes);
                    eleição.getFim().setDia(ano);

                    System.out.println("Data de fim atualizada com sucesso!");
                    check = 1;
                    break;
                case "0":
                    check = 1;
                    menu();
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }

        //server.editarEleição(eleição);
        System.out.println("Pessoa registada no servidor!");
    }

    public void terminaEleiçao(RMIServerInterface server) throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        String inputzito;
        Eleição eleição = null;
        System.out.println("--------- Terminar Eleição ---------");

        while(eleição == null) {
            System.out.println("Introduza o titulo da eleição que pretende terminar (Prima 0 para sair):");
            inputzito = reader.readLine();

            if (inputzito.equals("0")) menu();

            eleição = server.getEleição(inputzito);
            if(eleição == null) {
                System.out.println("Não existe nenhuma eleição com esse título. Tente novamente.");
            }

            eleição.setAtiva(false);
            System.out.println("Eleição " + eleição.getTitulo() + " terminada!");
        }
    }

    public void init(AdminConsole adminConsole) {
        String command;

        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());

        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        try {
            RMIServerInterface server = (RMIServerInterface) LocateRegistry.getRegistry(7001).lookup("Server");
            server.olaAdmin(adminConsole);
            System.out.println("Admin informou server que está ligado");
            while (true) {
                menu();
                System.out.print("> ");
                command = reader.readLine();
                readCommand(server, command);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws RemoteException {
        AdminConsole adminConsole = new AdminConsole();
        adminConsole.init(adminConsole);
    }
}
