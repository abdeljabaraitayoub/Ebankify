package org.hidxop.ebankify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EbankifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankifyApplication.class, args);
    }

}
