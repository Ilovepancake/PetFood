package com.bry.petfood;

import com.bry.petfood.Models.CurrentUser;

public class Variables {
    public static String userName  = "";
    private static String password = "";
    public static CurrentUser user;

    public static void setPassword(String pass){
        password = pass;
    }
}
