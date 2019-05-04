package co.markhed.demo.messaging.rest.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;

import java.util.List;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class ResponseUtil {

    private ResponseUtil() {}

    public static <T> ResponseEntity<T> createdResponse(T body, HttpHeaders headers) {
        return new ResponseEntity<>(body, headers, CREATED);
    }

    public static <T> ResponseEntity<T> okResponse(T body) {
        return new ResponseEntity<>(body, OK);
    }

    public static ResponseEntity notFoundResponse() {
        return new ResponseEntity<>(NOT_FOUND);
    }

    public static ResponseEntity badRequestResponse(List<FieldError> allErrors) {
        String errorText = allErrors.stream().map(e ->
            format("[%s - %s]", e.getField(), e.getDefaultMessage())).
            collect(joining(", "));
        HttpHeaders headers = new HttpHeaders();
        headers.add("error", errorText);

        return new ResponseEntity<>(headers, BAD_REQUEST);
    }

}
