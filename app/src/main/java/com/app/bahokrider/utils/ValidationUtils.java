package com.app.bahokrider.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ValidationUtils {

    public static boolean isValidMobile(String value) {
        return value.length() != 10;
    }

    @SuppressLint("SimpleDateFormat")
    public static Date stringToDate(String dtString) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(dtString);
            System.out.println(date);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public static String dateToString(Date dtString) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String dateTime = format.format(dtString);
            System.out.println(dateTime);
            return dateTime;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



}
