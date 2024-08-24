import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class GerenciadorDeGrupos {
    private Connection connection;
    private Channel channel;

    public GerenciadorDeGrupos(Connection connection) throws Exception {
        this.connection = connection;
        this.channel = connection.createChannel();
    }

    public void addGroup(String groupName) throws Exception {
        // Cria um exchange do tipo fanout para o grupo
        channel.exchangeDeclare(groupName, "fanout");
    }

    public void addUserToGroup(String userName, String groupName) throws Exception {
        // Associa uma fila de usuário a um grupo
        String queueName = channel.queueDeclare(userName, false, false, false, null).getQueue();
        channel.queueBind(queueName, groupName, "");
    }

    public void removeUserFromGroup(String userName, String groupName) throws Exception {
        // Desassocia uma fila de usuário de um grupo
        String queueName = channel.queueDeclare(userName, false, false, false, null).getQueue();
        channel.queueUnbind(queueName, groupName, "");
    }

    public void removeGroup(String groupName) throws Exception {
        // Exclui o exchange do grupo
        channel.exchangeDelete(groupName);
    }
}
