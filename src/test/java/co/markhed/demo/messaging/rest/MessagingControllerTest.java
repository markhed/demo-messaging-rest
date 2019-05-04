package co.markhed.demo.messaging.rest;

import co.markhed.demo.messaging.MessageService;
import co.markhed.demo.messaging.model.Message;
import co.markhed.demo.messaging.rest.request.MessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Date;
import static co.markhed.demo.messaging.rest.Constants.ApiPaths.MESSAGES_PATH;
import static co.markhed.demo.messaging.rest.Constants.ApiPaths.RECEIVED_PATH;
import static co.markhed.demo.messaging.rest.Constants.ApiPaths.SENT_PATH;
import static co.markhed.demo.messaging.rest.Constants.DEFAULT_JSON;
import static org.assertj.core.util.Lists.list;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@SpringBootTest
@RunWith(SpringRunner.class)
public class MessagingControllerTest {

    @MockBean
    private MessageService messageService;

    @Autowired
    private MessagingController messagingController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = standaloneSetup(messagingController).build();
    }

    @Test
    public void postMessage() throws Exception {
        //given
        String id = "1";
        MessageRequest messageRequest = createTestMessageRequest(id);
        Message message = createTestMessage(id);
        when(messageService.sendMessage(messageRequest.getSenderId(), messageRequest.getReceiverId(),
            messageRequest.getSubject(), messageRequest.getBody())).thenReturn(message);

        //when and then
        ResultActions resultActions = mockMvc.perform(
            post(MESSAGES_PATH).content(asJson(messageRequest)).accept(DEFAULT_JSON).contentType(DEFAULT_JSON));
        resultActions.andExpect(status().isCreated()).andExpect(content().contentType(DEFAULT_JSON));
        assertExpectedMessage(resultActions, message);
    }

    @Test
    public void getMessage() throws Exception {
        // given
        Message message = createTestMessage("1");
        when(messageService.readMessage(message.getId())).thenReturn(message);

        // when and then
        ResultActions resultActions = mockMvc.perform(
            get(MESSAGES_PATH + "/" + message.getId()).contentType(DEFAULT_JSON));
        resultActions.andExpect(status().isOk()).andExpect(content().contentType(DEFAULT_JSON));
        assertExpectedMessage(resultActions, message);
    }

    @Test
    public void getSentMessages() throws Exception {
        // given
        Message message1 = createTestMessage("1");
        Message message2 = createTestMessage("2");
        when(messageService.getSentMessagesOfUser(any())).thenReturn(list(message1, message2));

        // when and then
        ResultActions resultActions = mockMvc.perform(
            get(MESSAGES_PATH + SENT_PATH + "/xxx").contentType(DEFAULT_JSON));
        resultActions.andExpect(status().isOk()).andExpect(content().contentType(DEFAULT_JSON));
        assertExpectedMessages(resultActions, message1, message2);
    }

    @Test
    public void getReceivedMessages() throws Exception {
        // given
        Message message1 = createTestMessage("1");
        Message message2 = createTestMessage("2");
        when(messageService.getReceivedMessagesOfUser(any())).thenReturn(list(message1, message2));

        // when and then
        ResultActions resultActions = mockMvc.perform(
            get(MESSAGES_PATH + RECEIVED_PATH + "/" + "/xxx").contentType(DEFAULT_JSON));
        resultActions.andExpect(status().isOk()).andExpect(content().contentType(DEFAULT_JSON));
        assertExpectedMessages(resultActions, message1, message2);
    }

    private static void assertExpectedMessage(ResultActions resultActions, Message expectedMessage) throws Exception {
        assertExpectedMessages(resultActions, expectedMessage);
    }

    private static void assertExpectedMessages(ResultActions resultActions, Message... expectedMessages) throws Exception {
        int arrayLength = expectedMessages.length;
        for (int i = 0; i < arrayLength; i++) {
            Message expectedMessage = expectedMessages[i];
            resultActions.andExpect(jsonPath(formExpression("id", i, arrayLength)).value(expectedMessage.getId()))
                .andExpect(jsonPath(formExpression("senderId", i, arrayLength)).value(expectedMessage.getSenderId()))
                .andExpect(jsonPath(formExpression("receiverId", i, arrayLength)).value(expectedMessage.getReceiverId()))
                .andExpect(jsonPath(formExpression("subject", i, arrayLength)).value(expectedMessage.getSubject()))
                .andExpect(jsonPath(formExpression("body", i, arrayLength)).value(expectedMessage.getBody()))
                .andExpect(jsonPath(formExpression("sentDate", i, arrayLength)).value(expectedMessage.getSentDate().getTime()));
        }
    }

    private static Message createTestMessage(String id) {
        Message message = new Message();
        message.setId(id);
        message.setSenderId("sender_" + id);
        message.setReceiverId("receiver_" + id);
        message.setSubject("subject_" + id);
        message.setBody("body_" + id);
        message.setSentDate(new Date());
        return message;
    }

    private static MessageRequest createTestMessageRequest(String id) {
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setSenderId("sender_" + id);
        messageRequest.setReceiverId("receiver_" + id);
        messageRequest.setSubject("subject_" + id);
        messageRequest.setBody("body_" + id);
        return messageRequest;
    }


    private static String formExpression(String key, int index, int arraySize) {
        return String.format("$%s%s", arraySize > 1 ? ".[" + index + "]" : ".", key);
    }

    private static String asJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

}