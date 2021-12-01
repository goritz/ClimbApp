package com.geoit.climbapp;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class UIUtils {
    /**
     * Generic function to show a toast in a custom design, using the resource color values.
     * The toasts text is set as the given string
     *
     * @param text
     */
    public static void showToast(Context context, String text,int toastLength) {

//        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);

        Toast toast = Toast.makeText(context, text, toastLength);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 200);


//        View view = toast.getView();
//        view.setBackgroundColor(context.getColor(R.color.accent));
////        view.getBackground().setColorFilter(context.getColor(R.color.accent), PorterDuff.Mode.SRC_IN);
//        TextView tv = toast.getView().findViewById(android.R.id.message);
//        tv.setTextColor(context.getColor(R.color.black));

        toast.show();


    }
    public static void showToast(Context context,String text){
        showToast(context,text,Toast.LENGTH_LONG);
    }
}
