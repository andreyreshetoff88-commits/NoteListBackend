package ru.reshetoff.notelistbackend.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "ru.reshetoff.notelistbackend")
@EntityScan(basePackages = "ru.reshetoff.notelistbackend.domain.entity")
public class NoteListBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(NoteListBackendApplication.class, args);
    }
}
