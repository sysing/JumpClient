package com.example.g6.jumpclient.Class;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.g6.jumpclient.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by g6 on 10-Mar-18.
 */

public class Util {
    public static final String SGTIME = "GMT+8";
    public static String getTimeString(Long unixSeconds) {
        // convert seconds to milliseconds
        Date date = new Date(unixSeconds);
        // the format of your date
        SimpleDateFormat sdf = new SimpleDateFormat(" HH:mm:ss   dd-MM-yy");
        // give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(TimeZone.getTimeZone("Singapore"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
    public static void setBadgeCount(Context context, LayerDrawable icon, int count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.action_orders);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.action_orders, badge);
    }


}
