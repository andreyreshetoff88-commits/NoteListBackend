package ru.reshetoff.notelistbackend.web.config;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.reshetoff.notelistbackend.domain.service.UserService;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private final UserService userService;

    @Scheduled(cron = "0 0 * * * *")
    public void deleteUnverifiedOlderThan() {
        userService.deleteUnverifiedOlderThan(24);
    }
}
