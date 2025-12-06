package com.example.mytasknote.domain.service;

public class DuplicateUsernameException extends RuntimeException {
    public DuplicateUsernameException(String username) {
        super("username already exists: " + username);
    }
}
