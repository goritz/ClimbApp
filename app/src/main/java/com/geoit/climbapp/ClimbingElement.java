package com.geoit.climbapp;

//import org.w3c.dom.Element;
//import org.w3c.dom.Node;

import android.util.Log;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;

public class ClimbingElement extends BaseElement {


    String name="";
    String street="";
    String houseNumber="";
    String postcode="";
    String openingHours="";
    String website="";

    boolean isSportsCenter=false;

    boolean hasIndoor=false;
    boolean hasOutdoor=true;
    boolean hasFee=false;
    String natural="";

    ArrayList<ClimbingStyles> styles=new ArrayList<>();

    String climbingGradeUIAA ="";
    String climbingGradeUIAAMax="";
    String climbingGradeUIAAMean="";
    String climbingGradeUIAAMin="";

//    String climbingGradeFrenchMax;
//    String climbingGradeFrenchMin;
//
//    String climbingGradeSaxonMax;
//    String climbingGradeSaxonMin;


    String rock="";

    int length=0;
    int elevation=0;


    public ClimbingElement(Element osmNode) throws OverpassException {
        super(osmNode);


//        HashMap<String, String> tags = new HashMap<>();

        NodeList tagList = osmNode.getElementsByTagName("tag");

        for (int i = 0; i < tagList.getLength(); i++) {

            Element n = (Element) tagList.item(i);
            String key = n.getAttribute("k");
            String value = n.getAttribute("v");


            switch (key) {
                case "name":
                    this.name = value;
                    break;
                case "addr:street":
                    this.street = value;
                    break;
                case "addr:housenumber":
                    this.houseNumber = value;
                    break;
                case "addr:postcode":
                    this.postcode = value;
                    break;
                case "contact:website":
                    this.website = value;
                    break;
                case "opening_hours":
                    this.openingHours = value;
                    break;
                case "leisure":
                    if (value.equals("sports_centre"))
                        this.isSportsCenter = true;
                    break;
                case "fee":
                    if (value.equals("yes")) {
                        this.hasFee = true;
                    }
                case "indoor":
                    if (value.equals("yes"))
                        this.hasIndoor = true;
                    break;
                case "natural":
                    this.hasOutdoor = true;
                    this.natural = value;
                    break;
                case "ele":
                    try {
                        this.elevation = Integer.parseInt(value);
                    } catch (NumberFormatException nfe) {
                        Timber.tag("tag ele").w("could not parse elevation: %s", value);
                        nfe.printStackTrace();
                    }
                    break;
                case "climbing:rock":
                    this.rock=value;
                    break;
                case "climbing:grade:uiaa":
                    this.climbingGradeUIAA=value;
                    break;
                case "climbing:grade:uiaa:max":
                    this.climbingGradeUIAAMax=value;
                    break;
                case "climbing:grade:uiaa:mean":
                    this.climbingGradeUIAAMean=value;
                    break;
                case "climbing:grade:uiaa:min":
                    this.climbingGradeUIAAMin=value;
                    break;
                case "climbing:length":
                    try{
                        this.length=Integer.parseInt(value);
                    } catch (NumberFormatException nfe){
                        Timber.tag("tag climbing:length").w("could not parse length: %s", value);
                        nfe.printStackTrace();
                    }
                    break;

                    //STYLES
                case "climbing:boulder":
                    if(!value.equals("no"))
                        styles.add(ClimbingStyles.BOULDER);
                    break;
                case "climbing:sport":
                    if(!value.equals("no"))
                        styles.add(ClimbingStyles.SPORT);
                    break;
                case "climbing:speed":
                    if(!value.equals("no"))
                        styles.add(ClimbingStyles.SPEED);
                    break;
                case "climbing:toprope":
                    if(!value.equals("no"))
                        styles.add(ClimbingStyles.TOPROPE);
                    break;
                case "climbing:trad":
                    if(!value.equals("no"))
                        styles.add(ClimbingStyles.TRAD);
                    break;
                case "climbing:multipitch":
                    if(!value.equals("no"))
                        styles.add(ClimbingStyles.MULTIPITCH);
                    break;
                case "climbing:ice":
                    if(!value.equals("no"))
                        styles.add(ClimbingStyles.ICE);
                    break;
                case "climbing:mixed":
                    if(!value.equals("no"))
                        styles.add(ClimbingStyles.MIXED);
                    break;
                case "climbing:deepwater":
                    if(!value.equals("no"))
                        styles.add(ClimbingStyles.DEEPWATER);
                    break;

            }


        }
    }

    @Override
    public String toString() {
        return "ClimbingElement{" +
                "name='" + name + '\'' +
                ", street='" + street + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", postcode='" + postcode + '\'' +
                ", openingHours='" + openingHours + '\'' +
                ", website='" + website + '\'' +
                ", isSportsCenter=" + isSportsCenter +
                ", hasIndoor=" + hasIndoor +
                ", hasOutdoor=" + hasOutdoor +
                ", hasFee=" + hasFee +
                ", natural='" + natural + '\'' +
                ", styles=" + styles +
                ", climbingGradeUIAA='" + climbingGradeUIAA + '\'' +
                ", climbingGradeUIAAMax='" + climbingGradeUIAAMax + '\'' +
                ", climbingGradeUIAAMean='" + climbingGradeUIAAMean + '\'' +
                ", climbingGradeUIAAMin='" + climbingGradeUIAAMin + '\'' +
                ", rock='" + rock + '\'' +
                ", length=" + length +
                ", elevation=" + elevation +
                '}';
    }
}