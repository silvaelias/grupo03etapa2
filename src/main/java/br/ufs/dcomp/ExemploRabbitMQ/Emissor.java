package br.ufs.dcomp.ExemploRabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Emissor implements Runnable {
    private final static String EXCHANGE_NAME = "chat_exchange";
    private final String userName;
    private String currentRecipient;
    private Receptor receptor;

    public Emissor(String userName, Receptor receptor) {
        this.userName = userName;
        this.currentRecipient = "";
        this.receptor = receptor;
    }

    @Override
    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.252");
        factory.setUsername("admin");
        factory.setPassword("password");
        factory.setVirtualHost("/");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            Scanner scanner = new Scanner(System.in);

            System.out.print(">> ");
            while (true) {
                String input = scanner.nextLine();

                if (input.startsWith("@")) {
                    // Troca o destinatário atual para o nome especificado após o símbolo @
                    currentRecipient = input.substring(1).trim();
                    receptor.setCurrentRecipient(currentRecipient);
                    System.out.print("@" + currentRecipient + ">> ");
                } else {
                    if (currentRecipient.isEmpty()) {
                        System.out.println("Por favor, especifique um destinatário usando @username.");
                        System.out.print(">> ");
                    } else {
                        // Cria uma nova mensagem usando Protocol Buffers
                        String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                        String currentTime = new SimpleDateFormat("HH:mm").format(new Date());
                        MensagemProto.Mensagem mensagem = MensagemProto.Mensagem.newBuilder()
                            .setEmissor(userName)
                            .setData(currentDate)
                            .setHora(currentTime)
                            .setConteudo(MensagemProto.Conteudo.newBuilder()
                                .setTipo("text/plain")
                                .setCorpo(ByteString.copyFrom(input.getBytes("UTF-8")))
                                .build())
                            .build();

                        // Converte a mensagem para bytes e envia através do RabbitMQ
                        byte[] messageBytes = mensagem.toByteArray();
                        channel.basicPublish(EXCHANGE_NAME, currentRecipient, MessageProperties.PERSISTENT_TEXT_PLAIN, messageBytes);
                        System.out.print("@" + currentRecipient + ">> ");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
