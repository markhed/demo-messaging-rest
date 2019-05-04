package co.markhed.demo.messaging.repository;

import co.markhed.demo.messaging.model.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import static co.markhed.demo.messaging.util.TestMessageUtil.newTestMessageWithNoId;
import static co.markhed.demo.messaging.util.TestMessageUtil.asTestReceiverId;
import static co.markhed.demo.messaging.util.TestMessageUtil.asTestSenderId;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;


@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("jpa, hsqldb")
public class MessageRepositoryIT {

    @Autowired
    private MessageRepository messageRepository;

    @Test
    @Transactional
    public void save_when_new() throws Exception {
        // given
        Message message = newTestMessageWithNoId(1);
        assertNull(message.getId());

        // when
        messageRepository.save(message);

        // then
        assertThat(message.getId(), is(notNullValue()));
    }

    @Test
    @Transactional
    public void save_when_existing() throws Exception {
        // given
        Message message = newTestMessageWithNoId(1);
        messageRepository.save(message);
        Integer expectedMessageId = message.getId();
        assertNotNull(expectedMessageId);
        String expectedBody = "body_updated";
        assertNotEquals(expectedBody, message.getBody());

        // when
        message.setBody(expectedBody);
        messageRepository.save(message);

        // then
        assertThat(message.getId(), is(expectedMessageId));
        assertThat(message.getBody(), is(expectedBody));
    }

    @Test(expected = EmptyResultDataAccessException.class)
    @Transactional
    public void delete() throws Exception {
        // given
        Message message = saveMessage(1);
        Integer messageId = message.getId();

        // when
        messageRepository.delete(message);

        // then exception
        messageRepository.findById(messageId);
    }

    @Test
    @Transactional
    public void findById() throws Exception {
        // given
        Message expected1 = saveMessage(1);
        Message expected2 = saveMessage(2);
        Message expected3 = saveMessage(3);

        // when
        Message actual1 = messageRepository.findById(expected1.getId());
        Message actual2 = messageRepository.findById(expected2.getId());
        Message actual3 = messageRepository.findById(expected3.getId());

        // then
        assertThat(actual1, is(expected1));
        assertThat(actual2, is(expected2));
        assertThat(actual3, is(expected3));
    }

    @Test
    @Transactional
    public void findBySenderId() throws Exception {
        // given
        int refIndex = 1;
        List<Message> expected = new ArrayList<>();
        expected.add(saveMessage(refIndex));
        expected.add(saveMessage(refIndex));

        saveMessage(refIndex - 1); // not expected
        saveMessage(refIndex + 1); // not expected

        // when
        List<Message> actual = messageRepository.findBySenderId(asTestSenderId(refIndex));

        // then
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Transactional
    public void findByReceiverId() throws Exception {
        // given
        int refIndex = 1;
        List<Message> expected = new ArrayList<>();
        expected.add(saveMessage(refIndex));
        expected.add(saveMessage(refIndex));

        saveMessage(refIndex - 1); // not expected
        saveMessage(refIndex + 1); // not expected

        // when
        List<Message> actual = messageRepository.findByReceiverId(asTestReceiverId(refIndex));

        // then
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Transactional
    public void findAll() throws Exception {
        // given
        List<Message> expected = new ArrayList<>();
        expected.add(saveMessage(1));
        expected.add(saveMessage(2));
        expected.add(saveMessage(3));

        // when
        List<Message> actual = messageRepository.findAll();

        // then
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    private Message saveMessage(int index) {
        Message message = newTestMessageWithNoId(index);
        messageRepository.save(message);
        return message;
    }

}