package co.markhed.demo.messaging.rest.response;

import java.util.Date;

public class ErrorResponse {

    private String message;
    private String details;
    private Date date;

    public ErrorResponse(String message, String details, Date date) {
        this.message = message;
        this.details = details;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public Date getDate() {
        return date;
    }
}
