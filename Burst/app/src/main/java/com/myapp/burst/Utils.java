package com.myapp.burst;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by SRamesh on 3/17/2017.
 */

public class Utils {
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    public static String getDateStringInFormat(int date_x,int month_x,int year_x) {
        String dateString = "";

        //Setting dayOfMonth format
        if (date_x < 10) {
            dateString = dateString + "0" + date_x + "-";
        } else {
            dateString = dateString + date_x + "-";
        }

        //Setting monthOfYear format
        int displayMonth = month_x + 1;
        if (month_x < 9) {
            dateString = dateString + "0" + displayMonth + "-";
        } else {
            dateString = dateString + displayMonth + "-";
        }

        //Setting year
        return dateString + year_x;
    }
}
