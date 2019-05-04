package co.markhed.demo.messaging.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

public class Constants {

    private Constants() {}

    public static final String DEFAULT_JSON = APPLICATION_JSON_UTF8_VALUE;

    public class ApiPaths {

        private ApiPaths() {}

        public static final String MESSAGES_PATH = "/api/messages";
        public static final String SENT_PATH = "/sent";
        public static final String RECEIVED_PATH = "/received";

    }

    public class VariablePaths {

        private VariablePaths() {}

        public static final String MESSAGE_ID_VARNAME = "messageId";
        public static final String MESSAGE_ID_VARPATH = "/{" + MESSAGE_ID_VARNAME + "}";

        public static final String USER_ID_VARNAME = "userId";
        public static final String USER_ID_VARPATH = "/{" + USER_ID_VARNAME + "}";

    }

}
