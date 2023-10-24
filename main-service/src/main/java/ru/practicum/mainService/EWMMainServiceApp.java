package ru.practicum.mainService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ru.practicum"})
public class EWMMainServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(EWMMainServiceApp.class, args);
    }
}