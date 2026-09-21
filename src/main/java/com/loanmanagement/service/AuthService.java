package com.loanmanagement.service;

import com.loanmanagement.model.User;

public interface AuthService {
    User login(String username, String password);

    void logout( );
}
