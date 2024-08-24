//grupo 03:
// Isaias Elias da Silva
// Jardel Santos Nascimento
// Sergio Santana dos Santos

package br.ufs.dcomp.ExemploRabbitMQ;

import java.util.Scanner;

public class Chat {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("User: ");
        String userName = scanner.nextLine();

        Receptor receptor = new Receptor(userName);
        Thread receptorThread = new Thread(receptor);

        Thread emissorThread = new Thread(new Emissor(userName, receptor));

        // Inicializa o Gerenciador de Grupos
        Connection connection = // Obter a conexão apropriada
        GerenciadorDeGrupos gerenciadorDeGrupos = new GerenciadorDeGrupos(connection);

        // Loop para capturar comandos do usuário
        while (true) {
            String input = scanner.nextLine();

            if (input.startsWith("!addGroup")) {
                String groupName = input.split(" ")[1];
                gerenciadorDeGrupos.addGroup(groupName);
                gerenciadorDeGrupos.addUserToGroup(userName, groupName); // Adiciona o usuário ao grupo criado
            } else if (input.startsWith("!addUser")) {
                String[] parts = input.split(" ");
                String user = parts[1];
                String group = parts[2];
                gerenciadorDeGrupos.addUserToGroup(user, group);
            } else if (input.startsWith("!delFromGroup")) {
                String[] parts = input.split(" ");
                String user = parts[1];
                String group = parts[2];
                gerenciadorDeGrupos.removeUserFromGroup(user, group);
            } else if (input.startsWith("!removeGroup")) {
                String groupName = input.split(" ")[1];
                gerenciadorDeGrupos.removeGroup(groupName);
            }
        }

        emissorThread.start();
        receptorThread.start();
    }
}
