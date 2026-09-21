package com.loanmanagement.controller;
import com.loanmanagement.dao.UserDao;
import com.loanmanagement.dao.impl.UserDaoImpl;
import com.loanmanagement.model.User;
import com.loanmanagement.service.AuthService;
import com.loanmanagement.service.impl.AuthServiceImpl;
import com.loanmanagement.util.PasswordUtil;

public class AppController {

    public static void main(String[] args) {
        AuthService auth = new AuthServiceImpl(new UserDaoImpl());

        System.out.println(auth.login("admin", "Admin@123"));

        try {
            auth.login("customer3", "Cust@1234");
        } catch (Exception e) {
            System.out.println("Refused: " + e.getMessage());
        }

        try {
            auth.login("admin", "wrong");
        } catch (Exception e) {
            System.out.println("Refused: " + e.getMessage());
        }
    }
}
