package com.example.mytasknote;

import com.example.mytasknote.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile({"local","aws"})
@RequiredArgsConstructor
public class BootstrapRunner implements CommandLineRunner {

    private final UserRepository users;

    @Override
    public void run(String... args) {
        long count = users.count();
        log.info("Users in DB: {}", count);
    }
}
