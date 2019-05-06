package co.markhed.demo.messaging.rest.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel(description = "Details about a message to be created")
public class MessageRequest {

    @ApiModelProperty(notes = "The ID of the sender")
    @NotNull
    private Integer senderId;

    @ApiModelProperty(notes = "The ID of the receiver")
    @NotNull
    private Integer receiverId;

    @ApiModelProperty(notes = "The subject of the message")
    @NotNull
    private String subject;

    @ApiModelProperty(notes = "The body of the message")
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
