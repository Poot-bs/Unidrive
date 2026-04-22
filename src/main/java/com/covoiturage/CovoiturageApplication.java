package com.covoiturage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CovoiturageApplication {

    public static void main(String[] args) {
        SpringApplication.run(CovoiturageApplication.class, args);
        System.out.println("Application démarrée ! Allez sur http://localhost:8080");
    }
}
