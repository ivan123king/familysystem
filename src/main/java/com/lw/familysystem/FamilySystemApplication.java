package com.lw.familysystem;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.lw")
@EnableAutoConfiguration
public class FamilySystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FamilySystemApplication.class, args);
    }

}
