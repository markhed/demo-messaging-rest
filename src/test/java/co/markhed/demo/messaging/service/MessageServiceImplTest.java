package co.markhed.demo.messaging.service;

import co.markhed.demo.messaging.model.Message;
import co.markhed.demo.messaging.repository.MessageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import static org.assertj.core.util.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    public void sendMessage() throws Exception {
        // given
        int expectedSenderId = 1;
        int expectedReceiverId = 2;
        String expectedSubject = "subject";
        String expectedBody = "body";

        // when
        Message actualMessage = messageService.sendMessage(expectedSenderId, expectedReceiverId,
            expectedSubject, expectedBody);

        //then
        assertThat(actualMessage.getSenderId(), is(expectedSenderId));
        assertThat(actualMessage.getReceiverId(), is(expectedReceiverId));
        assertThat(actualMessage.getSubject(), is(expectedSubject));
        assertThat(actualMessage.getBody(), is(expectedBody));
    }

    @Test
    public void readMessage() throws Exception {
        // given
        int messageId = 1;
        Message message = mock(Message.class);
        when(message.getId()).thenReturn(messageId);
        when(messageRepository.findById(messageId)).thenReturn(message);

        // when
        Optional<Message> actualMessage = messageService.readMessage(messageId);

        // then
        assertThat(actualMessage.get().getId(), is(messageId));
    }

    @Test
    public void getSentMessagesOfUser() throws Exception {
        // given
        int userId = 1;
        List<Message> messages = newArrayList(new Message(), new Message(), new Message());
        when(messageRepository.findBySenderId(userId)).thenReturn(messages);

        // when
        List<Message> actualMessages = messageService.getSentMessagesOfUser(userId);

        // then
        assertThat(actualMessages, containsInAnyOrder(messages.toArray()));
    }

    @Test
    public void getReceivedMessagesOfUser() throws Exception {
        // given
        int userId = 1;
        List<Message> messages = newArrayList(new Message(), new Message(), new Message());
        when(messageRepository.findByReceiverId(userId)).thenReturn(messages);

        // when
        List<Message> actualMessages = messageService.getReceivedMessagesOfUser(userId);

        // then
        assertThat(actualMessages, containsInAnyOrder(messages.toArray()));
    }

}