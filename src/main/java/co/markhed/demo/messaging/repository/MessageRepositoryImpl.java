package co.markhed.demo.messaging.repository;

import co.markhed.demo.messaging.model.Message;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Repository
@Profile("jpa")
public class MessageRepositoryImpl implements MessageRepository {

    private static final String MESSAGE = "message";
    private static final String MESSAGE_ID = "id";
    private static final String SENDER_ID = "senderId";
    private static final String RECEIVER_ID = "receiverId";

    private static final String SELECT_MESSAGE_QUERY = "SELECT " + MESSAGE + " FROM Message " + MESSAGE;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void save(Message message) throws DataAccessException {
        if (message.getId() == null) {
            entityManager.persist(message);
        } else {
            entityManager.merge(message);
        }
    }

    @Override
    public void delete(Message message) throws DataAccessException {
        entityManager.remove(entityManager.contains(message) ? message : entityManager.merge(message));
    }

    @Override
    public Message findById(int id) throws DataAccessException {
        Query query = createSelectQuery(messageColumn(MESSAGE_ID) + "=:" + MESSAGE_ID);
        query.setParameter(MESSAGE_ID, id);
        return (Message) query.getSingleResult();
    }

    @Override
    public List<Message> findBySenderId(int userId) throws DataAccessException {
        Query query = createSelectQuery(messageColumn(SENDER_ID) + "=:" + SENDER_ID);
        query.setParameter(SENDER_ID, userId);
        return query.getResultList();
    }

    @Override
    public List<Message> findByReceiverId(int userId) throws DataAccessException {
        Query query = createSelectQuery(messageColumn(RECEIVER_ID) + "=:" + RECEIVER_ID);
        query.setParameter(RECEIVER_ID, userId);
        return query.getResultList();
    }

    @Override
    public List<Message> findAll() throws DataAccessException {
        return createSelectQuery(null).getResultList();
    }

    private Query createSelectQuery(String criteria) {
        String queryString = isNotBlank(criteria) ? (SELECT_MESSAGE_QUERY + " WHERE " + criteria) : SELECT_MESSAGE_QUERY;
        return entityManager.createQuery(queryString);
    }

    private static String messageColumn(String fieldName) {
        return MESSAGE + "." + fieldName;
    }

}
