package com.geoit.climbapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;


import com.geoit.climbapp.overpass.TaggedElement;



public class MarkerDialog extends Dialog{




    TextView tvName,tvHours,tvOpen;




    public static final int MAX_NAME_LENGTH = 32;
    public static final int MAX_DETAILS_LENGTH = 2048;
    private static final char[] ILLEGAL_CHARS = {'.', ';', ':', '/', '\n', '\r', '\t', '\0', '\f', '`', '\'', '?', '!', '*', '\\', '<', '>', '|', '\"',};
    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_,";



    ImageButton imageButton;



    TaggedElement element;
    MarkerDialogListener listener;

    public MarkerDialog(Context context, TaggedElement marker,MarkerDialogListener listener) {
        super(context);
        this.element=marker;
        this.listener=listener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.marker_dialog);

        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        tvName = findViewById(R.id.dialog_name);
        tvName.setText("");
//        tvName.setHint(R.string.dialog_save_name_prompt);



        tvHours = findViewById(R.id.dialog_hours);
        tvHours.setText("");
//        tvHours.setHint(R.string.dialog_save_details_prompt);

        tvOpen=findViewById(R.id.dialog_status);
        tvOpen.setText("");

        imageButton =findViewById(R.id.dialog_btn_routing);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onStartNavigationClick(element.getLatLng());
            }
        });


    }

    /**
     * Called when the dialog is starting.
     */
    @Override
    protected void onStart() {
        super.onStart();
        this.tvName.setText(element.getName());
        this.tvHours.setText(element.getOpeningHours());
        this.tvOpen.setText("Jetzt geöffnet!");
    }

    /**
     * Start the dialog and display it on screen.  The window is placed in the
     * application layer and opaque.  Note that you should not override this
     * method to do initialization when the dialog is shown, instead implement
     * that in {@link #onStart}.
     */
    @Override
    public void show() {
        super.show();
    }
}
