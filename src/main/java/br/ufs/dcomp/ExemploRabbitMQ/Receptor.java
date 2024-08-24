package br.ufs.dcomp.ExemploRabbitMQ;

import com.rabbitmq.client.*;
import com.google.protobuf.InvalidProtocolBufferException;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Receptor {
    private final String username;
    private final Connection connection;
    private final Channel channel;

    public Receptor(String username) throws IOException, TimeoutException {
        this.username = username;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.252"); //
        factory.setUsername("admin"); //
        factory.setPassword("password");
        factory.setVirtualHost("/"); 
        this.connection = factory.newConnection();
        this.channel = connection.createChannel();

        // Declara a fila do usuário
        channel.queueDeclare(username, false, false, false, null);
    }

    public void start() throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            try {
                // Desserializa a mensagem recebida
                MensagemOuterClass.Mensagem mensagem = MensagemOuterClass.Mensagem.parseFrom(delivery.getBody());

                // Exibe a mensagem no formato desejado
                System.out.println("(" + mensagem.getData() + " às " + mensagem.getHora() + ") "
                        + mensagem.getEmissor() + " diz: " + mensagem.getConteudo().getCorpo().toStringUtf8());

                // Reexibe o prompt atual
                System.out.print("@" + mensagem.getEmissor() + ">> ");
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        };

        // Consome mensagens da fila do usuário
        channel.basicConsume(username, true, deliverCallback, consumerTag -> { });
    }

    public void close() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }
}
