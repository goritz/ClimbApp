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


public class MarkerDialog extends Dialog {


    TextView tvName, tvDetails;


    ImageView imgType;
    ImageButton imageButton;


    TaggedElement element;
    MarkerDialogListener listener;

    public MarkerDialog(Context context, TaggedElement marker, MarkerDialogListener listener) {
        super(context);
        this.element = marker;
        this.listener = listener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.marker_dialog);

        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        tvName = findViewById(R.id.dialog_name);
        tvName.setText("");
//        tvName.setHint(R.string.dialog_save_name_prompt);


        imgType = findViewById(R.id.dialog_imgType);

        if (element.isIndoor() && element.isOutdoor()) {
            imgType.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.ic_inout, null));
        } else if (element.isIndoor()) {
            imgType.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.ic_indoor, null));
        } else {
            imgType.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), R.drawable.ic_outdoor, null));
        }

        tvDetails = findViewById(R.id.dialog_details);
        tvDetails.setText("");
//        tvHours.setHint(R.string.dialog_save_details_prompt);


        imageButton = findViewById(R.id.dialog_btn_routing);
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

        StringBuilder details = new StringBuilder("");

        details.append(buildLine(element.getOperator()));

        details.append(joinLine(element.getStreet(), element.getHouseNumber()));
        details.append(joinLine(element.getPostcode(), element.getCity(),element.getSubUrb()));

        details.append(buildLine(element.getOpeningHours()));
        details.append(buildLine(element.getWebsite()));
        details.append(buildLine(element.getPhone()));

        details.append(buildLine(element.getClimbingGradeUIAA())); //TODO UIAA: value anstatt nur value
        details.append(buildLine(element.getClimbingGradeUIAAMax()));
        details.append(buildLine(element.getClimbingGradeUIAAMean()));
        details.append(buildLine(element.getClimbingGradeUIAAMin()));

        details.append(buildLine(element.getRock())); //TODO Felsenart?!: value
        details.append(buildLine(element.getLength(), "m")); //TODO Länge: value
        details.append(buildLine(element.getElevation(), "m")); //TODO Elevation: value

        this.tvDetails.setText(details);


//        element.getStreet()


    }

    private String joinLine(String... detail) {
        String line = "";
        for (String s : detail) {
            if (s != null && !s.isEmpty()) {
                line += s + ' ';

            }
        }
        return line + '\n';

    }

    private String buildLine(String detail) {
        if (detail != null && !detail.isEmpty()) {
            return detail + '\n';

        } else {
            return "";
        }
    }

    private String buildLine(int value, String suffix) {
        if (value != -1) {
            return value + ' ' + suffix + '\n';
        } else {
            return "";
        }
    }

    private String buildLine(float value, String suffix) {
        if (value != -1f) {
            return value + ' ' + suffix + '\n';
        } else {
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
