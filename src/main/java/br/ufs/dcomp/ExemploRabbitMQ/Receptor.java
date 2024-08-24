package br.ufs.dcomp.ExemploRabbitMQ;

import com.rabbitmq.client.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Receptor implements Runnable {
    private final static String EXCHANGE_NAME = "chat_exchange";
    private final String userName;
    private String currentRecipient;

    public Receptor(String userName) {
        this.userName = userName;
        this.currentRecipient = "";
    }

    @Override
    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.252");
        factory.setUsername("admin");
        factory.setPassword("password");
        factory.setVirtualHost("/");

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            String queueName = channel.queueDeclare(userName, false, false, false, null).getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, userName);

            // Callback para processar mensagens recebidas
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                // Decodifica a mensagem usando Protocol Buffers
                MensagemProto.Mensagem mensagem = MensagemProto.Mensagem.parseFrom(delivery.getBody());
                String emissor = mensagem.getEmissor();
                String conteudo = new String(mensagem.getConteudo().getCorpo().toByteArray(), "UTF-8");

                String timeStamp = mensagem.getData() + " às " + mensagem.getHora();
                System.out.println("\n(" + timeStamp + ") " + emissor + " diz: " + conteudo);

                // Restaura o prompt para o destinatário atual
                System.out.print("@" + currentRecipient + ">> ");
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrentRecipient(String recipient) {
        this.currentRecipient = recipient;
    }
}
