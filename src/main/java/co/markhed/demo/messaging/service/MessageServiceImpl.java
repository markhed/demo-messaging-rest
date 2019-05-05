package co.markhed.demo.messaging.service;

import co.markhed.demo.messaging.model.Message;
import co.markhed.demo.messaging.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import static org.apache.commons.collections4.IteratorUtils.toList;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    @Transactional
    public Message sendMessage(int senderId, int receiverId, String subject, String body) {
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setSubject(subject);
        message.setBody(body);
        message.setSentDate(new Date());

        messageRepository.save(message);

        return message;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Message> readMessage(int messageId) {
        return messageRepository.findById(messageId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getSentMessagesOfUser(int userId) {
        return messageRepository.findBySenderId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getReceivedMessagesOfUser(int userId) {
        return messageRepository.findByReceiverId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getAllMessages() {
        return toList(messageRepository.findAll().iterator());
    }

}
