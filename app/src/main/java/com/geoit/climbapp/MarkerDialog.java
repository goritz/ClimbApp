package com.geoit.climbapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.geoit.climbapp.overpass.ClimbingStyles;
import com.geoit.climbapp.overpass.TaggedElement;


public class MarkerDialog extends Dialog {


    TextView tvName, tvDetails;


    ImageView imgType;
    ImageButton imageButton;


    TaggedElement element;
    MarkerDialogListener listener;

    public MarkerDialog(Context context, TaggedElement marker) {
        super(context);
        this.element = marker;
    }
    public void setListener(MarkerDialogListener listener){
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
                if(listener!=null)
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

        if (element.getName().isEmpty()) {
            if (element.isSportsCenter()) {
                this.tvName.setText(getContext().getResources().getString(R.string.element_no_name_center));
            } else {
                this.tvName.setText(getContext().getResources().getString(R.string.element_no_name_boulder));
            }
        } else {
            this.tvName.setText(element.getName());
        }

        StringBuilder details = new StringBuilder("");

        if (!element.getStreet().isEmpty()) {
            StringBuilder address = new StringBuilder();

            address.append(joinLine(element.getStreet(), element.getHouseNumber()));
            if (!element.getPostcode().isEmpty() || !element.getCity().isEmpty() || !element.getSubUrb().isEmpty() || !element.getCountry().isEmpty()) {
                address.append('\n');
                address.append(joinLine(element.getPostcode(), element.getCity(), element.getSubUrb(), element.getCountry()));
            }
            details.append(address).append('\n');

        }

        details.append(buildLine(element.getOperator()));
        details.append(buildLine(element.getOpeningHours()));
        details.append(buildLine(element.getWebsite()));
        details.append(buildLine(element.getPhone()));

        details.append('\n');

//        details.append(buildLine(getContext().getString((element.isFee()?R.string.dialog_has_fee:R.string.dialog_no_fee))));
        if(element.isFee()){
            details.append(buildLine(getContext().getString(R.string.dialog_has_fee)));

        }

        if(!element.isAccess()){
            details.append(buildLine(getContext().getString(R.string.dialog_no_access)));
        }


        if (!element.getClimbingGradeUIAA().isEmpty() || !element.getClimbingGradeUIAAMax().isEmpty() || !element.getClimbingGradeUIAAMean().isEmpty() || !element.getClimbingGradeUIAAMin().isEmpty())
            details.append("UIAA:\n");

        if (!element.getClimbingGradeUIAA().isEmpty())
            details.append(buildLine("Grade: " + element.getClimbingGradeUIAA()));

        if (!element.getClimbingGradeUIAAMin().isEmpty())
            details.append(buildLine("Min: " + element.getClimbingGradeUIAAMin()));

        if (!element.getClimbingGradeUIAAMean().isEmpty())
            details.append(buildLine("Mean: " + element.getClimbingGradeUIAA()));

        if (!element.getClimbingGradeUIAAMax().isEmpty())
            details.append(buildLine("Max: " + element.getClimbingGradeUIAA()));

        if (!element.getRock().isEmpty())
            details.append(buildLine(getContext().getString(R.string.dialog_rock, element.getRock())));

        if (element.getLength() != -1)
            details.append(buildLine(getContext().getString(R.string.dialog_length, element.getLength())));

        if (element.getElevation() != -1f)
            details.append(buildLine(getContext().getString(R.string.dialog_elevation, element.getElevation())));



        if(element.getStyles().size()>0){
            details.append(buildLine(getContext().getString(R.string.dialog_climbing_styles)));

            for(ClimbingStyles style:element.getStyles()){
                details.append('\t').append(buildLine(style.getValue()));
            }
        }


        if(details.toString().trim().isEmpty()){
            details.append(getContext().getString(R.string.dialog_no_info));
        }

        this.tvDetails.setText(details);

    }

    private String joinLine(String... detail) {
        String line = "";
        for (String s : detail) {
            if (s != null && !s.isEmpty()) {
                line += s + ' ';

            }
        }
        return line;

    }

    private String buildLine(String detail) {
        if (detail != null && !detail.isEmpty()) {
            return detail + '\n';

        } else {
            return "";
        }
    }

}
