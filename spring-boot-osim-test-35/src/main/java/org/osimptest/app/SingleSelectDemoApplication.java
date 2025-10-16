package org.osimptest.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class SingleSelectDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SingleSelectDemoApplication.class, args);
    }

}
