package com.rsdesign.wallpaper.util;

public class utils {
    public static final int AD_PER_PHOTO = 5;
    public static int categoryId = -1;
    public static boolean isLoginUser = false;
    public static String searchJobString = "";
    public static String searchJobCategory = "";

    public static String convertCount(int count){
        String returnCount= "";
        if (count< 1000){
            returnCount = String.valueOf(count);
        } else{
            int temp = count/1000;
            returnCount = String.valueOf(temp)+ "K";
        }

        return returnCount;
    }
}
