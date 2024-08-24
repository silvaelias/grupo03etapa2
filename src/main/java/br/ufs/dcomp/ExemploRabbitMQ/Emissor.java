package br.ufs.dcomp.ExemploRabbitMQ;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import com.google.protobuf.ByteString;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


public class Emissor {
    private final String username;
    private final Connection connection;
    private final Channel channel;

    public Emissor(String username) throws IOException, TimeoutException {
        this.username = username;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.252"); //
        factory.setUsername("admin"); //
        factory.setPassword("password");
        factory.setVirtualHost("/"); 
        this.connection = factory.newConnection();
        this.channel = connection.createChannel();
    }

    public void sendMessage(String recipient, String messageText) throws IOException {
        String queueName = recipient; // assume que o nome da fila é o nome do destinatário

        // Cria a mensagem usando Protocol Buffers
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

        MensagemOuterClass.Mensagem mensagem = MensagemOuterClass.Mensagem.newBuilder()
            .setEmissor(username)
            .setData(currentDate)
            .setHora(currentTime)
            .setConteudo(MensagemOuterClass.Conteudo.newBuilder()
                .setTipo("text/plain")
                .setCorpo(ByteString.copyFromUtf8(messageText))
                .build())
            .build();

        // Serializa a mensagem para bytes
        byte[] messageBytes = mensagem.toByteArray();

        // Envia a mensagem para a fila do destinatário
        channel.basicPublish("", queueName, null, messageBytes);
    }

    public void close() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }
}
