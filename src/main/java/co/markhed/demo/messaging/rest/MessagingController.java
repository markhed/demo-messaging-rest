package co.markhed.demo.messaging.rest;

import co.markhed.demo.messaging.model.Message;
import co.markhed.demo.messaging.rest.request.MessageRequest;
import co.markhed.demo.messaging.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import static co.markhed.demo.messaging.rest.util.Constants.ApiPaths.MESSAGES_FULL_PATH;
import static co.markhed.demo.messaging.rest.util.Constants.ApiPaths.RECEIVER_SUBPATH;
import static co.markhed.demo.messaging.rest.util.Constants.ApiPaths.SENDER_SUBPATH;
import static co.markhed.demo.messaging.rest.util.Constants.JSON;
import static co.markhed.demo.messaging.rest.util.Constants.VariablePaths.MESSAGE_ID_VARNAME;
import static co.markhed.demo.messaging.rest.util.Constants.VariablePaths.MESSAGE_ID_VARPATH;
import static co.markhed.demo.messaging.rest.util.Constants.VariablePaths.RECEIVER_ID_VARNAME;
import static co.markhed.demo.messaging.rest.util.Constants.VariablePaths.RECEIVER_ID_VARPATH;
import static co.markhed.demo.messaging.rest.util.Constants.VariablePaths.SENDER_ID_VARNAME;
import static co.markhed.demo.messaging.rest.util.Constants.VariablePaths.SENDER_ID_VARPATH;
import static co.markhed.demo.messaging.rest.util.ResponseUtil.badRequestResponse;
import static co.markhed.demo.messaging.rest.util.ResponseUtil.createdResponse;
import static co.markhed.demo.messaging.rest.util.ResponseUtil.notFoundResponse;
import static co.markhed.demo.messaging.rest.util.ResponseUtil.okResponse;

@Api
@RestController
@RequestMapping(path = MESSAGES_FULL_PATH, produces = JSON)
public class MessagingController {

    @Autowired
    private MessageService messageService;

    @ApiOperation(value = "Create a message", response = Message.class)
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Successfully created message"),
        @ApiResponse(code = 400, message = "Message is not created because request is not correct or has missing values")})
    @PostMapping
    public ResponseEntity<Message> postMessage(@RequestBody @Valid MessageRequest messageRequest,
            BindingResult bindingResult, UriComponentsBuilder uriBuilder) {
        if (bindingResult.hasErrors()) {
            return badRequestResponse(bindingResult.getFieldErrors());
        }

        Message message = messageService.sendMessage(
                messageRequest.getSenderId(), messageRequest.getReceiverId(),
                messageRequest.getSubject(), messageRequest.getBody());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(formMessageLocation(uriBuilder, message));

        return createdResponse(message, headers);
    }

    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved message"),
        @ApiResponse(code = 404, message = "Message requested does not exist")})
    @ApiOperation(value = "Get a message", response = Message.class)
    @GetMapping(path = MESSAGE_ID_VARPATH)
    public ResponseEntity<Message> getMessage(@PathVariable(MESSAGE_ID_VARNAME) int messageId) {
        Optional<Message> message = messageService.readMessage(messageId);
        return message.isPresent() ? okResponse(message.get()) : notFoundResponse();
    }

    @ApiOperation(value = "Get all messages", response = Message.class, responseContainer="List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved messages")})
    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        return okResponse(messages);
    }

    @ApiOperation(value = "Get all messages by sender", response = Message.class, responseContainer="List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved messages")})
    @GetMapping(path = SENDER_SUBPATH + SENDER_ID_VARPATH)
    public ResponseEntity<List<Message>> getMessagesBySenderId(@PathVariable(SENDER_ID_VARNAME) int userId) {
        List<Message> messages = messageService.getSentMessagesOfUser(userId);
        return okResponse(messages);
    }

    @ApiOperation(value = "Get all messages by receiver", response = Message.class, responseContainer="List")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved messages")})
    @GetMapping(path = RECEIVER_SUBPATH + RECEIVER_ID_VARPATH)
    public ResponseEntity<List<Message>> getMessagesByReceiverId(@PathVariable(RECEIVER_ID_VARNAME) int userId) {
        List<Message> messages = messageService.getReceivedMessagesOfUser(userId);
        return okResponse(messages);
    }

    private static URI formMessageLocation(UriComponentsBuilder uriBuilder, Message message) {
        return uriBuilder.path(MESSAGES_FULL_PATH+ MESSAGE_ID_VARPATH).
            buildAndExpand(message.getId()).toUri();
    }

}
