package com.loanmanagement.controller;

import com.loanmanagement.model.Role;
import com.loanmanagement.model.User;
import com.loanmanagement.service.UserService;
import com.loanmanagement.util.ConsoleUtil;
import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdminController {

    public static void manageUsers(UserService userService) {

        while (true) {
            System.out.println("Manage Users");
            System.out.println("1. View all users");
            System.out.println("2. Search by username");
            System.out.println("3. Add user");
            System.out.println("4. Edit user");
            System.out.println("5. Delete user");
            System.out.println("0. Back");

            System.out.println("Choice: ");
            Scanner sc = new Scanner(System.in);
            int choice = sc.nextInt();

            try {
                switch (choice) {
                    case 1 -> print(userService.getAllUsers());
                    case 2 -> searchUsers(userService);
                    case 3 -> addUser(userService);
                    case 4 -> editUser(userService);
                    case 5 -> deleteUser(userService);
                    case 0 -> { return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void print(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        System.out.println();
        for (User u : users) {
            System.out.println(u);
        }
        System.out.println(users.size() + " user(s)");
    }

    private static void searchUsers(UserService userService) {
        Scanner searchuser = new Scanner(System.in);
        System.out.println("Enter the user that you want to find: ");
        String text = searchuser.nextLine().toLowerCase();

        List<User> found = new ArrayList<>();
        for (User u : userService.getAllUsers()) {
            if (u.getUsername().toLowerCase().contains(text)) {
                found.add(u);
            }
        }
        print(found);
    }

    private static void addUser(UserService userService) {
        User user = new User();
        user.setUsername(ConsoleUtil.readLine("New username: "));
        String password = ConsoleUtil.readLine("Password: ");
        user.setRole(askRole());

        userService.addUser(user, password);
        System.out.println("Created: " + user);
    }

    private static void editUser(UserService userService) {
        int id = ConsoleUtil.readInt("User id to edit: ");

        User user = userService.getUserById(id);
        System.out.println("Editing: " + user);

        user.setUsername(ConsoleUtil.readLine("New username: "));
        user.setRole(askRole());

        userService.updateUser(user);
        System.out.println("Updated: " + user);
    }

    private static void deleteUser(UserService userService) {
        int id = ConsoleUtil.readInt("User id to delete: ");

        String confirm = ConsoleUtil.readLine("Type YES to confirm: ");
        if (!confirm.equals("YES")) {
            System.out.println("Cancelled.");
            return;
        }

        userService.deleteUser(id);
        System.out.println("Deleted.");
    }

    private static Role askRole() {
        System.out.println("1. ADMIN   2. LOAN_OFFICER   3. CUSTOMER");
        return switch (ConsoleUtil.readInt("Role: ")) {
            case 1 -> Role.ADMIN;
            case 2 -> Role.LOAN_OFFICER;
            case 3 -> Role.CUSTOMER;
            default -> throw new IllegalArgumentException("Invalid role choice");
        };
    }
}