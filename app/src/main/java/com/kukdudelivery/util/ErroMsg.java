package com.kukdudelivery.util;

import java.util.ArrayList;

public class ErroMsg {

    static ArrayList<String> errors=new ArrayList<>();
    public static void SetErrorMsg(){
        errors.add("Please on mobile data from settings");
        errors.add("No Internet Connection");
    }
    public static String getErrorMsg(int i){
        SetErrorMsg();
        return errors.get(i);
    }
}
