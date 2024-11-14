package steps;

import com.rabbitmq.client.*;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

public class RabbitMQStepsDefinitions {

    private Connection connection;
    private Channel channel;
    private String queueName;
    private String messageToSend;
    private String receivedMessage;

    // Configuración para conectarse al contenedor de RabbitMQ
    private void connectToRabbitMQ() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq"); // Cambiar si Docker usa otro host
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    @Given("un contenedor de RabbitMQ está corriendo en Docker")
    public void rabbitmq_running_in_docker() throws Exception {
        connectToRabbitMQ();
    }

    @Given("las colas {string} y {string} están disponibles en RabbitMQ")
    public void queues_available_in_rabbitmq(String queue1, String queue2) throws Exception {
        channel.queueDeclare(queue1, true, false, false, null);
        channel.queueDeclare(queue2, true, false, false, null);
    }

    @Given("quiero enviar un mensaje a la cola {string}")
    public void prepare_message_for_queue(String queue) {
        this.queueName = queue;
        this.messageToSend = null;
    }

    @When("envío el mensaje {string} a la cola {string}")
    public void send_message_to_queue(String message, String queue) throws Exception {
        this.messageToSend = message;
        channel.basicPublish("", queue, null, message.getBytes());
    }

    @Then("debería recibir el mensaje {string} desde la cola {string}")
    public void receive_message_from_queue(String expectedMessage, String queue) throws Exception {
        // Consumir el mensaje de la cola
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            receivedMessage = new String(delivery.getBody(), "UTF-8");
        };
        channel.basicConsume(queue, true, deliverCallback, consumerTag -> {});

        // Esperar y verificar el mensaje recibido
        Thread.sleep(1000); // Pausa breve para asegurar la recepción
        Assertions.assertEquals(expectedMessage, receivedMessage,
                "El mensaje recibido no coincide con el mensaje enviado.");

        // Cerrar conexión al final de la prueba
        channel.close();
        connection.close();
    }
}
