package co.markhed.demo.messaging.rest;

import co.markhed.demo.messaging.model.Message;
import co.markhed.demo.messaging.rest.request.MessageRequest;
import co.markhed.demo.messaging.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import static co.markhed.demo.messaging.rest.util.Constants.ApiPaths.MESSAGES_PATH;
import static co.markhed.demo.messaging.rest.util.Constants.ApiPaths.RECEIVED_PATH;
import static co.markhed.demo.messaging.rest.util.Constants.ApiPaths.SENT_PATH;
import static co.markhed.demo.messaging.rest.util.Constants.JSON;
import static co.markhed.demo.messaging.rest.util.Constants.VariablePaths.MESSAGE_ID_VARNAME;
import static co.markhed.demo.messaging.rest.util.Constants.VariablePaths.MESSAGE_ID_VARPATH;
import static co.markhed.demo.messaging.rest.util.Constants.VariablePaths.USER_ID_VARNAME;
import static co.markhed.demo.messaging.rest.util.Constants.VariablePaths.USER_ID_VARPATH;
import static co.markhed.demo.messaging.rest.util.ResponseUtil.badRequestResponse;
import static co.markhed.demo.messaging.rest.util.ResponseUtil.createdResponse;
import static co.markhed.demo.messaging.rest.util.ResponseUtil.notFoundResponse;
import static co.markhed.demo.messaging.rest.util.ResponseUtil.okResponse;

@RestController
@RequestMapping(MESSAGES_PATH)
public class MessagingController {

    @Autowired
    private MessageService messageService;

    @PostMapping(path = "", produces = JSON)
    public ResponseEntity<Message> postMessage(@RequestBody @Valid MessageRequest messageRequest,
            BindingResult bindingResult, UriComponentsBuilder uriBuilder) {
        if (bindingResult.hasErrors()) {
            return badRequestResponse(bindingResult.getFieldErrors());
        }

        Message message = messageService.sendMessage(
                messageRequest.getSenderId(), messageRequest.getReceiverId(),
                messageRequest.getSubject(), messageRequest.getBody());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(formLocation(uriBuilder, message));

        return createdResponse(message, headers);
    }

    @GetMapping(path = MESSAGE_ID_VARPATH, produces = JSON)
    public ResponseEntity<Message> getMessage(@PathVariable(MESSAGE_ID_VARNAME) int messageId) {
        Optional<Message> message = messageService.readMessage(messageId);
        return message.isPresent() ? okResponse(message.get()) : notFoundResponse();
    }

    @GetMapping(path = SENT_PATH + USER_ID_VARPATH, produces = JSON)
    public ResponseEntity<List<Message>> getSentMessages(@PathVariable(USER_ID_VARNAME) int userId) {
        List<Message> messages = messageService.getSentMessagesOfUser(userId);
        return okResponse(messages);
    }

    @GetMapping(path = RECEIVED_PATH + USER_ID_VARPATH, produces = JSON)
    public ResponseEntity<List<Message>> getReceivedMessages(@PathVariable(USER_ID_VARNAME) int userId) {
        List<Message> messages = messageService.getReceivedMessagesOfUser(userId);
        return okResponse(messages);
    }

    private static URI formLocation(UriComponentsBuilder uriBuilder, Message message) {
        return uriBuilder.path(MESSAGES_PATH + MESSAGE_ID_VARPATH).buildAndExpand(message.getId()).toUri();
    }

}
