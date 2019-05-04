package co.markhed.demo.messaging.rest;

import co.markhed.demo.messaging.MessageService;
import co.markhed.demo.messaging.model.Message;
import co.markhed.demo.messaging.rest.request.MessageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import static co.markhed.demo.messaging.rest.Constants.ApiPaths.MESSAGES_PATH;
import static co.markhed.demo.messaging.rest.Constants.ApiPaths.RECEIVED_PATH;
import static co.markhed.demo.messaging.rest.Constants.ApiPaths.SENT_PATH;
import static co.markhed.demo.messaging.rest.Constants.DEFAULT_JSON;
import static co.markhed.demo.messaging.rest.Constants.VariablePaths.MESSAGE_ID_VARNAME;
import static co.markhed.demo.messaging.rest.Constants.VariablePaths.MESSAGE_ID_VARPATH;
import static co.markhed.demo.messaging.rest.Constants.VariablePaths.USER_ID_VARNAME;
import static co.markhed.demo.messaging.rest.Constants.VariablePaths.USER_ID_VARPATH;
import static co.markhed.demo.messaging.rest.Util.createdResponse;
import static co.markhed.demo.messaging.rest.Util.okResponse;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(MESSAGES_PATH)
public class MessagingController {

    @Autowired
    private MessageService messageService;

    @RequestMapping(path = "", method = POST, produces = DEFAULT_JSON)
    public ResponseEntity<Message> postMessage(@RequestBody MessageRequest messageRequest,
            UriComponentsBuilder uriBuilder) {
        Message message = messageService.sendMessage(
                messageRequest.getSenderId(), messageRequest.getReceiverId(),
                messageRequest.getSubject(), messageRequest.getBody());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(formLocation(uriBuilder, message));

        return createdResponse(message);
    }

    @RequestMapping(path = MESSAGE_ID_VARPATH, method = GET, produces = DEFAULT_JSON)
    public ResponseEntity<Message> getMessage(@PathVariable(MESSAGE_ID_VARNAME) String messageId) {
        Message message = messageService.readMessage(messageId);
        return okResponse(message);
    }

    @RequestMapping(path = SENT_PATH + USER_ID_VARPATH, method = GET, produces = DEFAULT_JSON)
    public ResponseEntity<List<Message>> getSentMessages(@PathVariable(USER_ID_VARNAME) String userId) {
        List<Message> messages = messageService.getSentMessagesOfUser(userId);
        return okResponse(messages);
    }

    @RequestMapping(path = RECEIVED_PATH + USER_ID_VARPATH, method = GET, produces = DEFAULT_JSON)
    public ResponseEntity<List<Message>> getReceivedMessages(@PathVariable(USER_ID_VARNAME) String userId) {
        List<Message> messages = messageService.getReceivedMessagesOfUser(userId);
        return okResponse(messages);
    }

    private static URI formLocation(UriComponentsBuilder uriBuilder, Message message) {
        return uriBuilder.path(MESSAGES_PATH + MESSAGE_ID_VARPATH).buildAndExpand(message.getId()).toUri();
    }

}
