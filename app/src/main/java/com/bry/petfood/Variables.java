package com.bry.petfood;

import com.bry.petfood.Models.CurrentUser;
import com.bry.petfood.Models.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class Variables {
    public static String userName  = "";
    private static String password = "";
    public static CurrentUser user;

    public static FoodItem itemToBeAddedToCart;
    public static List<FoodItem> cartItems = new ArrayList<>();

    public static FoodItem itemToBeAddedToCompare;
    public static List<FoodItem> compareItems = new ArrayList<>();

    public static void setPassword(String pass){
        password = pass;
    }

    public static String getPassword(){
        return password;
    }
}
