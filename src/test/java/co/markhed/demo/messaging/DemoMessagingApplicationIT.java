package co.markhed.demo.messaging;

import co.markhed.demo.messaging.model.Message;
import co.markhed.demo.messaging.repository.MessageRepository;
import co.markhed.demo.messaging.rest.request.MessageRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.List;
import static co.markhed.demo.messaging.rest.util.Constants.ApiPaths.MESSAGES_FULL_PATH;
import static co.markhed.demo.messaging.rest.util.Constants.ApiPaths.RECEIVER_SUBPATH;
import static co.markhed.demo.messaging.rest.util.Constants.ApiPaths.SENDER_SUBPATH;
import static co.markhed.demo.messaging.util.GeneralUtil.path;
import static co.markhed.demo.messaging.util.TestMessageUtil.ANY_VALUE;
import static co.markhed.demo.messaging.util.TestMessageUtil.newTestMessageRequest;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.RequestEntity.get;
import static org.springframework.http.RequestEntity.post;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = ApplicationTestConfig.class)
@ActiveProfiles("jpa, hsqldb")
public class DemoMessagingApplicationIT {

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private MessageRepository messageRepository;

    @LocalServerPort
    private int port;

    @Test
    public void testPostMessage_success() {
        // given
        int refIndex = 0;
        MessageRequest requestBody = newTestMessageRequest(refIndex);

        // when
        ResponseEntity<Message> response = restTemplate.exchange(
            post(messageUrl()).accept(APPLICATION_JSON).body(requestBody), Message.class);

        // then
        assertThat(response.getStatusCode(), is(CREATED));

        Message responseBody = response.getBody();
        assertThat(responseBody.getId(), is(notNullValue()));
        assertThat(responseBody.getSenderId(), is(requestBody.getSenderId()));
        assertThat(responseBody.getReceiverId(), is(requestBody.getReceiverId()));
        assertThat(responseBody.getSubject(), is(requestBody.getSubject()));
        assertThat(responseBody.getBody(), is(requestBody.getBody()));
        assertThat(responseBody.getSentDate(), is(notNullValue()));
    }

    @Test
    public void testPostMessage_location() {
        // given
        int refIndex = 1;
        MessageRequest requestBody = newTestMessageRequest(refIndex);
        ResponseEntity<Message> response = restTemplate.exchange(
            post(messageUrl()).accept(APPLICATION_JSON).body(requestBody), Message.class);
        Message createdMessage = response.getBody();

        // when
        Message messageAtLocation = restTemplate.exchange(get(response.getHeaders().getLocation()).
            accept(APPLICATION_JSON).build(), Message.class).getBody();

        // then
        assertThat(messageAtLocation, is(createdMessage));
    }

    @Test
    public void testPostMessage_failed() {
        // given empty object
        MessageRequest requestBody = new MessageRequest();

        // when
        ResponseEntity<Message> response = restTemplate.exchange(post(messageUrl()).
            accept(APPLICATION_JSON).body(requestBody), Message.class);

        // then
        assertThat(response.getStatusCode(), is(BAD_REQUEST));
        assertThat(response.hasBody(), is(false));
    }

    @Test
    public void testGetMessage_nonExisting() {
        // given no message has been posted

        // when
        ResponseEntity<Message> response = restTemplate.exchange(get(messageUrl(path(ANY_VALUE))).
            accept(APPLICATION_JSON).build(), Message.class);

        // then
        assertThat(response.getStatusCode(), is(NOT_FOUND));
        assertThat(response.hasBody(), is(false));
    }

    @Test
    public void testGetMessage_existing() {
        // given
        int refIndex = 2;
        Message expectedMessage = restTemplate.exchange(post(messageUrl()).
            accept(APPLICATION_JSON).body(newTestMessageRequest(refIndex)), Message.class).getBody();

        // when
        ResponseEntity<Message> response = restTemplate.exchange(get(messageUrl(path(expectedMessage.getId()))).
            accept(APPLICATION_JSON).build(), Message.class);

        // then
        assertThat(response.getStatusCode(), is(OK));

        Message responseBody = response.getBody();
        assertThat(responseBody.getId(), is(expectedMessage.getId()));
        assertThat(responseBody.getSenderId(), is(expectedMessage.getSenderId()));
        assertThat(responseBody.getReceiverId(), is(expectedMessage.getReceiverId()));
        assertThat(responseBody.getSubject(), is(expectedMessage.getSubject()));
        assertThat(responseBody.getBody(), is(expectedMessage.getBody()));
        assertThat(responseBody.getSentDate(), is(expectedMessage.getSentDate()));
    }

    @Test
    public void testGetMessagesBySenderId() {
        // given
        int refIndex = 3;
        MessageRequest messageRequest = newTestMessageRequest(refIndex);
        int senderId = messageRequest.getSenderId();
        restTemplate.exchange(post(messageUrl()).
            accept(APPLICATION_JSON).body(messageRequest), Message.class);
        restTemplate.exchange(post(messageUrl()).
            accept(APPLICATION_JSON).body(messageRequest), Message.class);

        restTemplate.exchange(post(messageUrl()).
            accept(APPLICATION_JSON).body(newTestMessageRequest(0)), Message.class);

        // when
        ResponseEntity<List<Message>> response = restTemplate.exchange(
            get(messageUrl(SENDER_SUBPATH + path(senderId))).accept(APPLICATION_JSON).build(),
            new ParameterizedTypeReference<List<Message>>() {});

        // then
        assertThat(response.getStatusCode(), is(OK));

        List<Message> responseBody = response.getBody();
        assertThat(responseBody.size(), is(2));
        assertThat(responseBody.get(0).getSenderId(), is(senderId));
        assertThat(responseBody.get(1).getSenderId(), is(senderId));
    }

    @Test
    public void testGetMessagesByReceiverId() {
        // given
        int index = 4;
        MessageRequest messageRequest = newTestMessageRequest(index);
        int receiverId = messageRequest.getReceiverId();
        restTemplate.exchange(post(messageUrl()).
            accept(APPLICATION_JSON).body(messageRequest), Message.class);
        restTemplate.exchange(post(messageUrl()).
            accept(APPLICATION_JSON).body(messageRequest), Message.class);

        restTemplate.exchange(post(messageUrl()).
            accept(APPLICATION_JSON).body(newTestMessageRequest(0)), Message.class);

        // when
        ResponseEntity<List<Message>> response = restTemplate.exchange(
            get(messageUrl(RECEIVER_SUBPATH + path(receiverId))).accept(APPLICATION_JSON).build(),
            new ParameterizedTypeReference<List<Message>>() {});

        // then
        assertThat(response.getStatusCode(), is(OK));

        List<Message> responseBody = response.getBody();
        assertThat(responseBody.size(), is(2));
        assertThat(responseBody.get(0).getReceiverId(), is(receiverId));
        assertThat(responseBody.get(1).getReceiverId(), is(receiverId));
    }

    @Test
    public void testGetAllMessages() {
        // given
        int index = 5;
        Message expected1 = restTemplate.exchange(post(messageUrl()).
            accept(APPLICATION_JSON).body(newTestMessageRequest(index++)), Message.class).getBody();
        Message expected2 = restTemplate.exchange(post(messageUrl()).
            accept(APPLICATION_JSON).body(newTestMessageRequest(index++)), Message.class).getBody();
        Message expected3 = restTemplate.exchange(post(messageUrl()).
            accept(APPLICATION_JSON).body(newTestMessageRequest(index++)), Message.class).getBody();

        // when
        ResponseEntity<List<Message>> response = restTemplate.exchange(
            get(messageUrl()).accept(APPLICATION_JSON).build(),
            new ParameterizedTypeReference<List<Message>>() {});

        // then
        assertThat(response.getStatusCode(), is(OK));

        List<Message> responseBody = response.getBody();
        assertThat(responseBody, hasItems(expected1, expected2, expected3));
    }

    private URI messageUrl() {
        return messageUrl("");
    }

    private URI messageUrl(String subPath) {
        return URI.create("http://localhost:" + port + MESSAGES_FULL_PATH + subPath);
    }


}