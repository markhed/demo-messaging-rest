package co.markhed.demo.messaging;

import co.markhed.demo.messaging.model.Message;
import co.markhed.demo.messaging.rest.request.MessageRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.List;
import static co.markhed.demo.messaging.rest.util.Constants.ApiPaths.MESSAGES_PATH;
import static co.markhed.demo.messaging.rest.util.Constants.ApiPaths.RECEIVED_PATH;
import static co.markhed.demo.messaging.rest.util.Constants.ApiPaths.SENT_PATH;
import static co.markhed.demo.messaging.util.GeneralUtil.path;
import static co.markhed.demo.messaging.util.TestMessageUtil.ANY_VALUE;
import static co.markhed.demo.messaging.util.TestMessageUtil.newTestMessageRequest;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
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
@ActiveProfiles("jpa, hsqldb")
public class DemoMessagingApplicationIT {

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    @LocalServerPort
    private int port;

    @Test
    public void testPostMessage_success() {
        // given
        int refIndex = 1;
        MessageRequest requestBody = newTestMessageRequest(refIndex);

        // when
        ResponseEntity<Message> response = restTemplate.exchange(
            post(getTestUrl("")).accept(APPLICATION_JSON).body(requestBody), Message.class);

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
    public void testPostMessage_failed() {
        // given empty object
        MessageRequest requestBody = new MessageRequest();

        // when
        ResponseEntity<Message> response = restTemplate.exchange(
            post(getTestUrl("")).accept(APPLICATION_JSON).body(requestBody), Message.class);

        // then
        assertThat(response.getStatusCode(), is(BAD_REQUEST));
        assertThat(response.hasBody(), is(false));
    }

    @Test
    public void testGetMessage_nonExisting() {
        // given no message has been posted

        // when
        ResponseEntity<Message> response = restTemplate.exchange(
            get(getTestUrl(path(ANY_VALUE))).accept(APPLICATION_JSON).build(), Message.class);

        // then
        assertThat(response.getStatusCode(), is(NOT_FOUND));
        assertThat(response.hasBody(), is(false));
    }

    @Test
    public void testGetMessage_existing() {
        // given
        int refIndex = 2;
        Message expectedMessage = restTemplate.exchange(
            post(getTestUrl("")).accept(APPLICATION_JSON).body(
                newTestMessageRequest(refIndex)), Message.class).getBody();

        // when
        ResponseEntity<Message> response = restTemplate.exchange(
            get(getTestUrl(path(expectedMessage.getId()))).accept(APPLICATION_JSON).build(), Message.class);

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
    public void testGetSentMessages() {
        // given
        int refIndex = 3;
        MessageRequest messageRequest = newTestMessageRequest(refIndex);
        int senderId = messageRequest.getSenderId();
        restTemplate.exchange(
            post(getTestUrl("")).accept(APPLICATION_JSON).body(messageRequest), Message.class);
        restTemplate.exchange(
            post(getTestUrl("")).accept(APPLICATION_JSON).body(messageRequest), Message.class);

        restTemplate.exchange(
            post(getTestUrl("")).accept(APPLICATION_JSON).body(newTestMessageRequest(0)), Message.class);

        // when
        ResponseEntity<List<Message>> response = restTemplate.exchange(
            get(getTestUrl(SENT_PATH + path(senderId))).accept(APPLICATION_JSON).build(),
            new ParameterizedTypeReference<List<Message>>(){});

        // then
        assertThat(response.getStatusCode(), is(OK));

        List<Message> responseBody = response.getBody();
        assertThat(responseBody.size(), is(2));
        assertThat(responseBody.get(0).getSenderId(), is(senderId));
        assertThat(responseBody.get(1).getSenderId(), is(senderId));
    }

    @Test
    public void testGetReceivedMessages() {
        // given
        int index = 4;
        MessageRequest messageRequest = newTestMessageRequest(index);
        int receiverId = messageRequest.getReceiverId();
        restTemplate.exchange(
            post(getTestUrl("")).accept(APPLICATION_JSON).body(messageRequest), Message.class);
        restTemplate.exchange(
            post(getTestUrl("")).accept(APPLICATION_JSON).body(messageRequest), Message.class);

        restTemplate.exchange(
            post(getTestUrl("")).accept(APPLICATION_JSON).body(newTestMessageRequest(0)), Message.class);

        // when
        ResponseEntity<List<Message>> response = restTemplate.exchange(
            get(getTestUrl(RECEIVED_PATH + path(receiverId))).accept(APPLICATION_JSON).build(),
            new ParameterizedTypeReference<List<Message>>(){});

        // then
        assertThat(response.getStatusCode(), is(OK));

        List<Message> responseBody = response.getBody();
        assertThat(responseBody.size(), is(2));
        assertThat(responseBody.get(0).getReceiverId(), is(receiverId));
        assertThat(responseBody.get(1).getReceiverId(), is(receiverId));
    }

    private URI getTestUrl(String subPath) {
        return URI.create("http://localhost:" + port + MESSAGES_PATH + subPath);
    }

}