package co.markhed.demo.messaging.service;

import co.markhed.demo.messaging.model.Message;

import java.util.List;
import java.util.Optional;

public interface MessageService {

    Message sendMessage(int senderId, int receiverId, String subject, String body);

    Optional<Message> readMessage(int messageId);

    List<Message> getSentMessagesOfUser(int userId);

    List<Message> getReceivedMessagesOfUser(int userId);

    List<Message> getAllMessages();

}
