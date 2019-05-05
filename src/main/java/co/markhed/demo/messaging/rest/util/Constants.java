package co.markhed.demo.messaging.rest.util;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

public class Constants {

    private Constants() {}

    public static final String JSON = APPLICATION_JSON_UTF8_VALUE;

    public class ApiPaths {

        private ApiPaths() {}

        public static final String API_ROOT_PATH = "/api";
        public static final String MESSAGES_SUB_PATH = "/messages";
        public static final String MESSAGES_FULL_PATH = API_ROOT_PATH + MESSAGES_SUB_PATH;

    }

    public class VariablePaths {

        private VariablePaths() {}

        public static final String MESSAGE_ID_VARNAME = "messageId";
        public static final String MESSAGE_ID_VARPATH = "/{" + MESSAGE_ID_VARNAME + "}";

        public static final String SENDER_ID_VARNAME = "senderId";
        public static final String RECEIVER_ID_VARNAME = "receiverId";


    }

}
