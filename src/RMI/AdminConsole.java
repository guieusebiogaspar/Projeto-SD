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
                editarEleiçao();
                break;
            case "4":
                terminaEleiçao();
                break;
            default:
                System.out.println("Opção Inválida");
        }
    }

    public void registar(RMIServerInterface server) throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        System.out.println("--------- Registar Pessoa ---------");
        String nome, nickname, password, phone, morada, cc, validade;
        System.out.println("Nome: ");
        nome = reader.readLine();
        System.out.println("Nickname: ");
        nickname = reader.readLine();
        System.out.println("Password: ");
        password = reader.readLine();
        System.out.println("Phone: ");
        phone = reader.readLine();
        System.out.println("Morada: ");
        morada = reader.readLine();
        System.out.println("Cartão de cidadão: ");
        cc = reader.readLine();
        System.out.println("Validade cartão de cidadão: ");
        validade = reader.readLine();

        Pessoa pessoa = new Pessoa(nome, nickname, password, phone, morada, cc, validade);

        server.registar(pessoa);
        System.out.println("Pessoa registada no servidor!");
    }

    public void criarEleiçao(RMIServerInterface server) throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        System.out.println("--------- Criar Eleição ---------");
        String diaInicio, horaInicio, minutoInicio, diaFim, horaFim, minutoFim, titulo, descrição;
        System.out.println("Dia de início da eleição: ");
        diaInicio = reader.readLine();
        System.out.println("Hora de início da eleição: ");
        horaInicio = reader.readLine();
        System.out.println("Minuto de início da eleição: ");
        minutoInicio = reader.readLine();
        System.out.println("Dia de fim da eleição: ");
        diaFim = reader.readLine();
        System.out.println("Hora de fim da eleição: ");
        horaFim = reader.readLine();
        System.out.println("Minuto de fim da eleição: ");
        minutoFim = reader.readLine();
        System.out.println("Título da eleição: ");
        titulo = reader.readLine();
        System.out.println("Descrição da eleição: ");
        descrição = reader.readLine();

        Eleição eleição = new Eleição(diaInicio, horaInicio, minutoInicio, diaFim, horaFim, minutoFim, titulo, descrição);

        server.criarEleição(eleição);
        System.out.println("Pessoa registada no servidor!");
    }

    public void editarEleiçao() {
        // String startDate, String endDate, String title, String description
        System.out.println("Edit Election");
    }

    public void terminaEleiçao() {
        // String startDate, String endDate, String title, String description
        System.out.println("End election");
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
