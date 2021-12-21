package com.geoit.climbapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.core.content.res.ResourcesCompat;

import com.geoit.climbapp.overpass.TaggedElement;



public class MarkerDialog extends Dialog{




    TextView tvName, tvDetails;






    ImageView imgType;
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


        imgType=findViewById(R.id.dialog_imgType);

        if(element.isIndoor() && element.isOutdoor()){
            imgType.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(),R.drawable.ic_inout,null));
        }else if(element.isIndoor()){
            imgType.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(),R.drawable.ic_indoor,null));
        }else {
            imgType.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(),R.drawable.ic_outdoor,null));
        }

        tvDetails = findViewById(R.id.dialog_details);
        tvDetails.setText("");
//        tvHours.setHint(R.string.dialog_save_details_prompt);


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
        this.tvDetails.setText(element.getOpeningHours());

        StringBuilder details=new StringBuilder("");



//        element.getStreet()







    }
    private String buildLine(String detail){
        if(detail!=null){
            return detail+'\n';

        }else{
            return "";
        }
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
