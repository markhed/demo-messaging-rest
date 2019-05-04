package co.markhed.demo.messaging.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import java.util.Date;
import static co.markhed.demo.messaging.util.GeneralUtil.DATE_PATTERN;
import static co.markhed.demo.messaging.util.GeneralUtil.TIME_ZONE;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Table(name = "messages")
public class Message extends BaseEntity {

    @Column(name = "senderId", nullable = false)
    private Integer senderId;

    @Column(name = "receiverId", nullable = false)
    private Integer receiverId;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "sentDate", nullable = false)
    @Temporal(TIMESTAMP)
    @JsonFormat(shape = STRING, pattern = DATE_PATTERN, timezone = TIME_ZONE)
    private Date sentDate;

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

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Message message = (Message) o;

        if (id != null ? !id.equals(message.id) : message.id != null) { return false; }
        if (senderId != null ? !senderId.equals(message.senderId) : message.senderId != null) { return false; }
        if (receiverId != null ? !receiverId.equals(message.receiverId) : message.receiverId != null) { return false; }
        if (subject != null ? !subject.equals(message.subject) : message.subject != null) { return false; }
        if (body != null ? !body.equals(message.body) : message.body != null) { return false; }
        return sentDate != null ? sentDate.equals(message.sentDate) : message.sentDate == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (senderId != null ? senderId.hashCode() : 0);
        result = 31 * result + (receiverId != null ? receiverId.hashCode() : 0);
        result = 31 * result + (subject != null ? subject.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        result = 31 * result + (sentDate != null ? sentDate.hashCode() : 0);
        return result;
    }

}
