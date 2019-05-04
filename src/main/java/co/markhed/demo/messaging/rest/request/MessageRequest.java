package co.markhed.demo.messaging.rest.request;

import javax.validation.constraints.NotNull;

public class MessageRequest {

    @NotNull
    private Integer senderId;

    @NotNull
    private Integer receiverId;

    @NotNull
    private String subject;

    @NotNull
    private String body;

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
