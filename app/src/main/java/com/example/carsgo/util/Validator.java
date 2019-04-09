package com.example.carsgo.util;

import android.text.TextUtils;
import android.util.Patterns;

public class Validator {
     private static final String NAME_REGEX="[A-Z][a-zA-Z]*";

    public static boolean isValidFirstName(String firstName){
        if(firstName.isEmpty() || !firstName.matches(NAME_REGEX)){
            return false;
        }
        return true;
    }

    public static boolean isValidLastName(String lastName){
        if(lastName.isEmpty() || !lastName.matches(NAME_REGEX)){
            return false;
        }
        return true;
    }

    public static boolean isValidMobile(String mobile){
        if(mobile.length() == 10){
            return  true;
        }
        return false;
    }

    public static boolean isValidAddress(String address){
        if(address.isEmpty()){
            return false;
        }
        return true;
    }

    public static boolean isValidConfirmPassword(String p1,String p2){
        return p1.equals(p2);
    }

    public static boolean isValidEmail(String email){
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password){
        if(password.isEmpty() || password.length() <= 5){
            return false;
        }
        return true;
    }


}
