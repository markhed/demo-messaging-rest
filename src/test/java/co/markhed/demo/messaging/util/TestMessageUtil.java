package co.markhed.demo.messaging.util;

import co.markhed.demo.messaging.model.Message;
import co.markhed.demo.messaging.rest.request.MessageRequest;

import java.util.Date;

public class TestMessageUtil {

    public static final Integer ANY_VALUE = 99999; // don't-care value

    public static Message newTestMessageWithNoId(int index) {
        Message message = new Message();
        message.setSenderId(asTestSenderId(index));
        message.setReceiverId(asTestReceiverId(index));
        message.setSubject(asTestSubject(index));
        message.setBody(asTestBody(index));
        message.setSentDate(new Date());
        return message;
    }

    public static Message newTestMessageWithId(int index) {
        Message message = newTestMessageWithNoId(index);
        message.setId(index);
        return message;
    }

    public static MessageRequest newTestMessageRequest(int index) {
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setSenderId(asTestSenderId(index));
        messageRequest.setReceiverId(asTestReceiverId(index));
        messageRequest.setSubject(asTestSubject(index));
        messageRequest.setBody(asTestBody(index));
        return messageRequest;
    }

    public static int asTestReceiverId(int index) {
        return index * 11;
    }

    public static int asTestSenderId(int index) {
        return index * 13;
    }

    public static String asTestBody(int index) {
        return "body_" + index;
    }

    public static String asTestSubject(int index) {
        return "subject_" + index;
    }

}
