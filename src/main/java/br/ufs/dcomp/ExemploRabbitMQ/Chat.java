//grupo 03:
// Isaias Elias da Silva
// Jardel Santos Nascimento
// Sergio Santana dos Santos

package br.ufs.dcomp.ExemploRabbitMQ;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Chat {
    private String username;
    private Emissor emissor;
    private Receptor receptor;
    private String currentRecipient = "";
    private Map<String, String> groups = new HashMap<>();

    public Chat(String username) throws IOException, TimeoutException {
        this.username = username;
        this.emissor = new Emissor(username);
        this.receptor = new Receptor(username);
        this.receptor.start();
    }

    public void startChat() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(currentRecipient.isEmpty() ? ">> " : "@" + currentRecipient + ">> ");
            String input = scanner.nextLine();

            if (input.startsWith("!addGroup")) {
                createGroup(input);
            } else if (input.startsWith("!addUser")) {
                addUserToGroup(input);
            } else if (input.startsWith("@")) {
                changeRecipient(input);
            } else {
                sendMessage(input);
            }
        }
    }

    private void createGroup(String input) {
        String[] parts = input.split(" ");
        if (parts.length == 2) {
            String groupName = parts[1];
            groups.put(groupName, groupName);
            System.out.println("Grupo " + groupName + " criado.");
        } else {
            System.out.println("Formato inválido para criação de grupo.");
        }
    }

    private void addUserToGroup(String input) {
        String[] parts = input.split(" ");
        if (parts.length == 3) {
            String userName = parts[1];
            String groupName = parts[2];
            // Em uma implementação completa, aqui você faria o bind da fila do usuário ao exchange do grupo
            System.out.println("Usuário " + userName + " adicionado ao grupo " + groupName + ".");
        } else {
            System.out.println("Formato inválido para adicionar usuário ao grupo.");
        }
    }

    private void changeRecipient(String input) {
        currentRecipient = input.substring(1); // remove '@'
    }

    private void sendMessage(String messageText) {
        if (currentRecipient.isEmpty()) {
            System.out.println("Selecione um destinatário primeiro usando @username.");
            return;
        }
        try {
            emissor.sendMessage(currentRecipient, messageText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        System.out.print("User: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        Chat chat = new Chat(username);
        chat.startChat();
    }
}
