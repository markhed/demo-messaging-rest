package co.markhed.demo.messaging.repository;

import co.markhed.demo.messaging.model.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Integer> {

    List<Message> findBySenderId(int userId);

    List<Message> findByReceiverId(int userId);

}
