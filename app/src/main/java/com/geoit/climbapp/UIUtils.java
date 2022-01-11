package com.geoit.climbapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
//        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 200);


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
    /**
     * Draws the contents of a (vector) drawable into a new bitmap
     * @param drawable the source drawable to be copied
     * @return 8-bit RGB bitmap
     */
    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
