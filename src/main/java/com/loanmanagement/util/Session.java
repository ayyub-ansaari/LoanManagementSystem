package com.loanmanagement.util;

import com.loanmanagement.model.Role;
import com.loanmanagement.model.User;

public class Session {
    public static User currentUser;
    private Session(){
    }
    public static void login(User user){
        currentUser = user;
    }
    public static void logout(){
        currentUser = null;
    }
    public static boolean isLoggedIn(){
        return currentUser !=null;
    }
    public static User getCurrentUser(){
        if(currentUser == null){
            throw new IllegalStateException("No user is logged in");
        }
        return currentUser;
    }
    public static int getCurrentUserId(){
        return getCurrentUser().getUserId();
    }
    public static Role getRole(){
        return getCurrentUser().getRole();
    }
}
