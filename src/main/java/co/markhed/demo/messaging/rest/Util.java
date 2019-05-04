package co.markhed.demo.messaging.rest;

import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class Util {

    private Util() {
    }

    public static <T> ResponseEntity<T> createdResponse(T body) {
        return new ResponseEntity<>(body, CREATED);
    }

    public static <T> ResponseEntity<T> okResponse(T body) {
        return new ResponseEntity<>(body, OK);
    }

}
