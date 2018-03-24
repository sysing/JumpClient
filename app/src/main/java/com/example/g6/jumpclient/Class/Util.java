package com.example.g6.jumpclient.Class;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import com.example.g6.jumpclient.R;

import org.simmetrics.SetMetric;
import org.simmetrics.metrics.OverlapCoefficient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import static com.google.common.primitives.Ints.asList;

/**
 * Created by g6 on 10-Mar-18.
 */

public class Util {
    public static final String SGTIME = "GMT+8";
    private static final Integer TAG_CODE_PERMISSION_LOCATION = 1;

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
    public static void hello(){
        Set<Integer> scores1 = new HashSet<>(asList(1, 1, 2, 3, 5, 8, 11, 19));
        Set<Integer> scores2 = new HashSet<>(asList(1, 2, 4, 8, 16, 32, 64));
        SetMetric<Integer> metric = new OverlapCoefficient<>();
        float result = metric.compare(scores1, scores2); // 0.4285
        System.out.print(result);
    }
}
