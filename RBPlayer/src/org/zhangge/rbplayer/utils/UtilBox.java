package org.zhangge.rbplayer.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Created by zhangge on 2014/10/24.
 */
public class UtilBox {
	
	public static int[] getScreenSize(Activity context) {
		int[] sizes = new int[2];
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		sizes[0] = (int) dm.widthPixels;
		sizes[1] = (int) dm.heightPixels;
		return sizes;
	}

    public static String formatTime(long millis) {
        String output = "";
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 24;

        String secondsD = String.valueOf(seconds);
        String minutesD = String.valueOf(minutes);
        String hoursD = String.valueOf(hours);

        if (seconds < 10)
            secondsD = "0" + seconds;
        if (minutes < 10)
            minutesD = "0" + minutes;
        if (hours < 10){
            hoursD = "0" + hours;
        }

        if( days > 0 ){
            output = days +"d ";
        }
        if(hours > 0) {
            output += hoursD + ":";
        }
        output += minutesD + ":" + secondsD;

        return output;
    }
}
