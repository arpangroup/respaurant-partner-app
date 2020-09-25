package com.example.mainactivity.util;

public class CommonUtils {
    public static String getFormattedTime(int hourIn24HourFormat, int minute){
        int selectedHour = hourIn24HourFormat;
        int selectedMinute = minute;
        String AM_PM = " AM";
        String mmPreCode = "";
        if(selectedHour >= 12){
            AM_PM =  " PM";
            if(selectedHour >= 13 && selectedHour <24){
                selectedHour -= 12;
            }else{
                selectedHour = 12;
            }
        }else if(selectedHour == 0){
            selectedHour = 12;
        }
        if(minute < 10){
            mmPreCode = "0";
        }

        return selectedHour + ":"+mmPreCode+selectedMinute +" "+ AM_PM;
    }
}
