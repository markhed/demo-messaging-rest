package co.markhed.demo.messaging;

import co.markhed.demo.messaging.model.Message;

import java.util.List;

public interface MessageService {

    Message sendMessage(String senderId, String receiverId, String subject, String body);

    Message readMessage(String messageId);

    List<Message> getSentMessagesOfUser(String userId);

    List<Message> getReceivedMessagesOfUser(String userId);

}
