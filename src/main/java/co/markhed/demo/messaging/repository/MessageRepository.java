package co.markhed.demo.messaging.repository;

import co.markhed.demo.messaging.model.Message;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface MessageRepository {

    void save(Message message) throws DataAccessException;

    void delete(Message message) throws DataAccessException;

    Message findById(int id) throws DataAccessException;

    List<Message> findBySenderId(int userId) throws DataAccessException;

    List<Message> findByReceiverId(int userId) throws DataAccessException;

    List<Message> findAll() throws DataAccessException;

}
