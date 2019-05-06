package co.markhed.demo.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket api() {
        return new Docket(SWAGGER_2).select()
            .apis(RequestHandlerSelectors.basePackage("co.markhed.demo.messaging"))
            .paths(PathSelectors.regex("/.*"))
            .build().apiInfo(getApiInfo());
    }

    private static ApiInfo getApiInfo() {
        return new ApiInfoBuilder().title("Demo Messaging Rest App")
            .contact(new Contact("Mark Harun",
                "https://github.com/markhed/demo-messaging-rest",
                "markharun@gmail.com"))
            .version("0.0.1-SNAPSHOT")
            .build();
    }

}
