package lab.anoper.yetcache.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQUtils {

    private static RabbitTemplate staticRabbitTemplate;

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        RabbitMQUtils.staticRabbitTemplate = rabbitTemplate;
    }

    public static void sendFanoutMessage(String message, String exchange) {
        if (staticRabbitTemplate == null) {
            throw new IllegalStateException("RabbitTemplate not initialized");
        }

        MessageProperties props = new MessageProperties();
        props.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN);
        props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);

        Message amqpMessage = new Message(message.getBytes(), props);
        staticRabbitTemplate.send(exchange, "", amqpMessage);
    }
}