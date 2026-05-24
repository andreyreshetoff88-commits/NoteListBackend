package ru.reshetoff.notelistbackend.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.reshetoff.notelistbackend")
public class NoteListBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(NoteListBackendApplication.class, args);
    }
}
