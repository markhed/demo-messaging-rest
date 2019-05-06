package co.markhed.demo.messaging.rest;

import co.markhed.demo.messaging.model.Message;
import co.markhed.demo.messaging.rest.request.MessageRequest;
import co.markhed.demo.messaging.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static co.markhed.demo.messaging.rest.util.Constants.ApiPaths.MESSAGES_FULL_PATH;
import static co.markhed.demo.messaging.rest.util.Constants.ApiPaths.RECEIVER_SUBPATH;
import static co.markhed.demo.messaging.rest.util.Constants.ApiPaths.SENDER_SUBPATH;
import static co.markhed.demo.messaging.rest.util.Constants.JSON;
import static co.markhed.demo.messaging.util.GeneralUtil.format;
import static co.markhed.demo.messaging.util.GeneralUtil.path;
import static co.markhed.demo.messaging.util.TestMessageUtil.ANY_VALUE;
import static co.markhed.demo.messaging.util.TestMessageUtil.newTestMessageRequest;
import static co.markhed.demo.messaging.util.TestMessageUtil.newTestMessageWithId;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.util.Lists.list;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith(SpringRunner.class)
public class MessagingControllerTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessagingController messagingController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = standaloneSetup(messagingController).build();
    }

    @Test
    public void postMessage() throws Exception {
        //given
        int index = 1;
        MessageRequest messageRequest = newTestMessageRequest(index);
        Message message = newTestMessageWithId(index);
        when(messageService.sendMessage(messageRequest.getSenderId(), messageRequest.getReceiverId(),
            messageRequest.getSubject(), messageRequest.getBody())).thenReturn(message);

        //when and then
        ResultActions resultActions = mockMvc.perform(
            post(MESSAGES_FULL_PATH).content(asJson(messageRequest)).accept(JSON).contentType(JSON));
        resultActions.andExpect(status().isCreated()).andExpect(content().contentType(JSON));
        assertExpectedMessage(resultActions, message);
    }

    @Test
    public void getMessage_existing() throws Exception {
        // given
        Message message = newTestMessageWithId(1);
        when(messageService.readMessage(message.getId())).thenReturn(of(message));

        // when and then
        ResultActions resultActions = mockMvc.perform(
            get(MESSAGES_FULL_PATH + path(message.getId())).contentType(JSON));
        resultActions.andExpect(status().isOk()).andExpect(content().contentType(JSON));
        assertExpectedMessage(resultActions, message);
    }

    @Test
    public void getMessage_nonExisting() throws Exception {
        // given
        when(messageService.readMessage(anyInt())).thenReturn(empty());

        // when and then
        ResultActions resultActions = mockMvc.perform(
            get(MESSAGES_FULL_PATH + path(ANY_VALUE)).contentType(JSON));
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void getMessagesBySenderId() throws Exception {
        // given
        Message message1 = newTestMessageWithId(1);
        Message message2 = newTestMessageWithId(2);
        when(messageService.getSentMessagesOfUser(anyInt())).thenReturn(list(message1, message2));

        // when and then
        ResultActions resultActions = mockMvc.perform(
            get(MESSAGES_FULL_PATH + SENDER_SUBPATH + path(ANY_VALUE)).contentType(JSON));
        resultActions.andExpect(status().isOk()).andExpect(content().contentType(JSON));
        assertExpectedMessages(resultActions, message1, message2);
    }

    @Test
    public void getMessagesByReceiverId() throws Exception {
        // given
        Message message1 = newTestMessageWithId(1);
        Message message2 = newTestMessageWithId(2);
        when(messageService.getReceivedMessagesOfUser(anyInt())).thenReturn(list(message1, message2));

        // when and then
        ResultActions resultActions = mockMvc.perform(
            get(MESSAGES_FULL_PATH + RECEIVER_SUBPATH + path(ANY_VALUE)).contentType(JSON));
        resultActions.andExpect(status().isOk()).andExpect(content().contentType(JSON));
        assertExpectedMessages(resultActions, message1, message2);
    }

    @Test
    public void getAllMessages() throws Exception {
        // given
        Message message1 = newTestMessageWithId(1);
        Message message2 = newTestMessageWithId(2);
        when(messageService.getAllMessages()).thenReturn(list(message1, message2));

        // when and then
        ResultActions resultActions = mockMvc.perform(get(MESSAGES_FULL_PATH).contentType(JSON));
        resultActions.andExpect(status().isOk()).andExpect(content().contentType(JSON));
        assertExpectedMessages(resultActions, message1, message2);
    }

    private static void assertExpectedMessage(ResultActions resultActions, Message expectedMessage) throws Exception {
        assertExpectedMessages(resultActions, expectedMessage);
    }

    private static void assertExpectedMessages(ResultActions resultActions, Message... expectedMessages) throws Exception {
        int arrayLength = expectedMessages.length;
        for (int i = 0; i < arrayLength; i++) {
            Message expected = expectedMessages[i];
            resultActions.andExpect(jsonPath(formExpression("id", i, arrayLength)).value(expected.getId()))
                .andExpect(jsonPath(formExpression("senderId", i, arrayLength)).value(expected.getSenderId()))
                .andExpect(jsonPath(formExpression("receiverId", i, arrayLength)).value(expected.getReceiverId()))
                .andExpect(jsonPath(formExpression("subject", i, arrayLength)).value(expected.getSubject()))
                .andExpect(jsonPath(formExpression("body", i, arrayLength)).value(expected.getBody()))
                .andExpect(jsonPath(formExpression("sentDate", i, arrayLength)).value(format(expected.getSentDate())));
        }
    }


    private static String formExpression(String key, int index, int arraySize) {
        return String.format("$%s%s", arraySize > 1 ? ".[" + index + "]." : ".", key);
    }

    private static String asJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

}