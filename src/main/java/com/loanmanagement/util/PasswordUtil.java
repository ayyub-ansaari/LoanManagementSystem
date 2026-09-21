package com.loanmanagement.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {
    private static final int COST = 10;
    private PasswordUtil(){

    }
    public static String hash(String plainPassword){
        return BCrypt.hashpw(plainPassword,BCrypt.gensalt(COST));
    }
    public static boolean matches( String plainPassword , String storeHash){
        if(plainPassword == null || storeHash == null){
            return false;
        }
        return BCrypt.checkpw(plainPassword,storeHash);
    }
}
