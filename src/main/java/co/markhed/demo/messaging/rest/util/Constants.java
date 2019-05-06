package co.markhed.demo.messaging.rest.util;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

public class Constants {

    private Constants() {}

    public static final String JSON = APPLICATION_JSON_UTF8_VALUE;

    public class ApiPaths {

        private ApiPaths() {}

        private static final String API_ROOT_PATH = "/api";
        private static final String MESSAGES_SUBPATH = "/messages";
        public static final String RECEIVER_SUBPATH = "/receiver";
        public static final String SENDER_SUBPATH = "/sender";

        public static final String MESSAGES_FULL_PATH = API_ROOT_PATH + MESSAGES_SUBPATH;

    }

    public class VariablePaths {

        private VariablePaths() {}

        public static final String MESSAGE_ID_VARNAME = "messageId";
        public static final String MESSAGE_ID_VARPATH = "/{" + MESSAGE_ID_VARNAME + "}";

        public static final String SENDER_ID_VARNAME = "senderId";
        public static final String SENDER_ID_VARPATH = "/{" + SENDER_ID_VARNAME + "}";

        public static final String RECEIVER_ID_VARNAME = "receiverId";
        public static final String RECEIVER_ID_VARPATH = "/{" + RECEIVER_ID_VARNAME + "}";


    }

}
