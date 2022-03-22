package com.app.bahokrider.utils;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.text.TextUtils;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarUtil {


    public static void openDatPickerForDOB(Context context, final TextView textView) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 8);

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        if (!TextUtils.isEmpty(textView.getText().toString().trim())) {
            String[] datePicked = textView.getText().toString().trim().split("-");
            mYear = Integer.parseInt(datePicked[0]);
            mMonth = Integer.parseInt(datePicked[1]) - 1;
            mDay = Integer.parseInt(datePicked[2]);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String DOM = String.valueOf(dayOfMonth);
                        if (dayOfMonth < 10)
                            DOM = "0" + dayOfMonth;

                        String MOY = String.valueOf(monthOfYear + 1);
                        if ((monthOfYear + 1) < 10)
                            MOY = "0" + (monthOfYear + 1);

                        textView.setText(year + "-" + MOY + "-" + DOM);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

    }

    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();

        String AMPM = "AM";
        String hr = calendar.get(Calendar.HOUR_OF_DAY) + "";
        String min = calendar.get(Calendar.MINUTE) + "";

        if (calendar.get(Calendar.HOUR_OF_DAY) > 12) {
            hr = (calendar.get(Calendar.HOUR_OF_DAY) - 12) + "";
            AMPM = "PM";
        }

        if (hr.length() < 2)
            hr = "0" + hr;

        if (min.length() < 2)
            min = "0" + min;

        return hr + ":" + min + " " + AMPM;
    }

    public static String getPostTimeDifference(String postTime) {

        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        try {

            Date date1 = inFormat.parse(postTime);

            long mills = currentTime - (date1 != null ? date1.getTime() : 0);

            int hours = (int) (mills / (1000 * 60 * 60));

            if (hours >= 24 * 30) {
                int months = hours / (24 * 30);
                return months + " month(s) ago";
            } else if (hours >= 24) {
                int days = hours / 24;
                return days + " day(s) ago";
            } else if (hours >= 1) {
                return hours + " hour(s) ago";
            } else {
                int mins = (int) (mills % (1000 * 60 * 60));
                return mins + " min(s) ago";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



}
