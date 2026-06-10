package ru.reshetoff.notelistbackend.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "ru.reshetoff.notelistbackend")
@EntityScan(basePackages = "ru.reshetoff.notelistbackend.domain.entity")
@EnableScheduling
public class NoteListBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(NoteListBackendApplication.class, args);
    }
}
