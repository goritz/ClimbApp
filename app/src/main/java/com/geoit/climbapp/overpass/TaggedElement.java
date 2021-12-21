package com.geoit.climbapp.overpass;

//import org.w3c.dom.Element;
//import org.w3c.dom.Node;

import android.util.Log;

import com.geoit.climbapp.LatLng;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;

public class TaggedElement extends BaseElement {


    final ElementType type;

    final ArrayList<Long> referenceIDs;

    final HashMap<String, String> otherTags = new HashMap<>();

    private LatLng latLng;


    //TODO access=no auslesen und anzeigen!
    String name = ""; //TODO oldName auslesen und kleiner unter name anzeigen?
    String street = "";
    String houseNumber = "";
    String postcode = "";
    String city="";
    String subUrb="";
    String openingHours = "";
    String website = "";
    String phone="";

    String operator="";

    boolean isSportsCenter = false;

    boolean hasIndoor = false;
    boolean hasOutdoor = false;
    boolean hasFee = false;
    String natural = "";

    ArrayList<ClimbingStyles> styles = new ArrayList<>();

    String climbingGradeUIAA = "";
    String climbingGradeUIAAMax = "";
    String climbingGradeUIAAMean = "";
    String climbingGradeUIAAMin = "";

//    String climbingGradeFrenchMax;
//    String climbingGradeFrenchMin;
//
//    String climbingGradeSaxonMax;
//    String climbingGradeSaxonMin;


    String rock = "";

    int length = -1;
    float elevation = -1;


    public TaggedElement(long id, ElementType type, Element osmElement) throws OverpassException {
        super(id);
        this.type = type;


        //Todo für alle ways der response am ende calcLatLng(refNodes)`?!

        NodeList tagList = osmElement.getElementsByTagName("tag");

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
                case "addr:suburb":
                    this.subUrb=value;
                    break;
                case "addr:city":
                    this.city=value;
                    break;
                case "contact:website":
                case "website":
                    this.website = value;
                    break;
                case "phone":
                    this.phone=value;
                    break;
                case "operator":
                    this.operator=value;
                    break;
                case "opening_hours":
                    this.openingHours = value;
                    break;
                case "leisure":
                    if (value.equals("sports_centre")) {
                        this.isSportsCenter = true;
                        this.hasIndoor = true;
                    }
                    break;
                case "fee":
                    if (value.equals("yes")) {
                        this.hasFee = true;
                    }
                    break;
                case "indoor":
                    if (value.equals("yes"))
                        this.hasIndoor = true;
                    break;
                case "natural":
                    if(!value.isEmpty()){
                        this.hasOutdoor = true;
                        this.natural = value;
                    }
                    break;
                case "ele":
                    try {
                        this.elevation = Float.parseFloat(value);
                    } catch (NumberFormatException nfe) {
                        Timber.tag("tag ele").w("could not parse elevation: %s", value);
                        nfe.printStackTrace();
                    }
                    break;
                case "climbing:rock":
                    this.rock = value;
                    break;
                case "climbing:grade:uiaa":
                    this.climbingGradeUIAA = value;
                    break;
                case "climbing:grade:uiaa:max":
                    this.climbingGradeUIAAMax = value;
                    break;
                case "climbing:grade:uiaa:mean":
                    this.climbingGradeUIAAMean = value;
                    break;
                case "climbing:grade:uiaa:min":
                    this.climbingGradeUIAAMin = value;
                    break;
                case "climbing:length":
                    try {
                        this.length = Integer.parseInt(value);
                    } catch (NumberFormatException nfe) {
//                        Timber.tag("tag climbing:length").w("could not parse length: %s", value);
                        Log.w("[NFE]", "could not parse length: " + value);
//                        nfe.printStackTrace();
                    }
                    break;

                //STYLES
                case "climbing:boulder":
                    if (!value.equals("no"))
                        styles.add(ClimbingStyles.BOULDER);
                    break;
                case "climbing:sport":
                    if (!value.equals("no"))
                        styles.add(ClimbingStyles.SPORT);
                    break;
                case "climbing:speed":
                    if (!value.equals("no"))
                        styles.add(ClimbingStyles.SPEED);
                    break;
                case "climbing:toprope":
                    if (!value.equals("no"))
                        styles.add(ClimbingStyles.TOPROPE);
                    break;
                case "climbing:trad":
                    if (!value.equals("no"))
                        styles.add(ClimbingStyles.TRAD);
                    break;
                case "climbing:multipitch":
                    if (!value.equals("no"))
                        styles.add(ClimbingStyles.MULTIPITCH);
                    break;
                case "climbing:ice":
                    if (!value.equals("no"))
                        styles.add(ClimbingStyles.ICE);
                    break;
                case "climbing:mixed":
                    if (!value.equals("no"))
                        styles.add(ClimbingStyles.MIXED);
                    break;
                case "climbing:deepwater":
                    if (!value.equals("no"))
                        styles.add(ClimbingStyles.DEEPWATER);
                    break;
                default:
                    otherTags.put(key, value);
                    break;
            }

            //TODO am ende prüfen und vervöllstädnigen


        }

        // Falls dieses Element ein WAY ist, referenzierte Nodes finden und parsen
        referenceIDs = new ArrayList<>();
        if (this.type == ElementType.WAY) {

            NodeList refList = osmElement.getElementsByTagName("nd");

            for (int i = 0; i < refList.getLength(); i++) {
                Element n = (Element) refList.item(i);

                try {
                    long refID = Long.parseLong(n.getAttribute("ref"));
                    referenceIDs.add(refID);
                } catch (NumberFormatException nfe) {
                    Log.w("[Warning]", "could not parse id reference (" + i + ") of way " + this.getId());
                    nfe.printStackTrace();
                }

            }
        }


    }


    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public ArrayList<Long> getReferenceIDs() {
        return this.referenceIDs;
    }

    //    public String toFormattedString(){
//        StringBuilder sb=new StringBuilder("ClimbingElement{");
//
//        sb.append(buildLine("type",this.type));
//        sb.append(buildLine("id",this.getId()));
//        sb.append(buildLine("pos",this.latLng));
//        sb.append(buildLine("name",this.name));
//        sb.append(buildLine("street",this.street));
//        sb.append(buildLine("houseNumber",this.houseNumber));
//        sb.append(buildLine("postcode",this.postcode));
//
//        return sb.toString();
//    }
    private String buildLine(String name, Object o) {
        if (o != null) {
            String os = o.toString();
            if (!os.isEmpty())
                return name + ": " + os + '\n';
        }
        return "";
    }


    public String toFormattedString() {
        return "TaggedElement: " + buildLine("type", type) +
                buildLine("id", getId()) +
                (type == ElementType.WAY ? buildLine("refs", referenceIDs.size()) : "") +
                buildLine("latLng", latLng) +
                buildLine("name", name) +
                buildLine("street", street) +
                buildLine("houseNumber", houseNumber) +
                buildLine("postcode", postcode) +
                buildLine("city", city) +
                buildLine("suburb", subUrb) +
                buildLine("openingHours", openingHours) +
                buildLine("website", website) +
                buildLine("phone", phone) +
                buildLine("operator", operator) +
                buildLine("isSportsCenter", isSportsCenter) +
                buildLine("hasIndoor", hasIndoor) +
                buildLine("hasOutdoor", hasOutdoor) +
                buildLine("hasFee", hasFee) +
                buildLine("natural", natural) +
                buildLine("styles", styles) +
                buildLine("climbingGradeUIAA", climbingGradeUIAA) +
                buildLine("climbingGradeUIAAMax", climbingGradeUIAAMax) +
                buildLine("climbingGradeUIAAMean", climbingGradeUIAAMean) +
                buildLine("climbingGradeUIAAMin", climbingGradeUIAAMin) +
                buildLine("rock", rock) +
                (length != -1 ? buildLine("length", length) : "") +
                (elevation != -1 ? buildLine("elevation", elevation) : "") +
                buildLine("TAGS", otherTags.toString())

                ;

    }

    public ElementType getType() {
        return type;
    }

    public HashMap<String, String> getOtherTags() {
        return otherTags;
    }

    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getCity() {
        return city;
    }

    public String getSubUrb() {
        return subUrb;
    }


    public String getOpeningHours() {
        return openingHours;
    }

    public String getWebsite() {
        return website;
    }

    public String getPhone() {
        return phone;
    }

    public String getOperator() {
        return operator;
    }

    public boolean isSportsCenter() {
        return isSportsCenter;
    }

    public boolean isIndoor() {
        return hasIndoor;
    }

    public boolean isOutdoor() {
        return hasOutdoor;
    }

    public boolean isFee() {
        return hasFee;
    }

    public String getNatural() {
        return natural;
    }

    public ArrayList<ClimbingStyles> getStyles() {
        return styles;
    }

    public String getClimbingGradeUIAA() {
        return climbingGradeUIAA;
    }

    public String getClimbingGradeUIAAMax() {
        return climbingGradeUIAAMax;
    }

    public String getClimbingGradeUIAAMean() {
        return climbingGradeUIAAMean;
    }

    public String getClimbingGradeUIAAMin() {
        return climbingGradeUIAAMin;
    }

    public String getRock() {
        return rock;
    }

    public int getLength() {
        return length;
    }

    public float getElevation() {
        return elevation;
    }

    @Override
    public String toString() {
        return "TaggedElement{" + "type=" + type +
                ", otherTags=" + otherTags +
                ", latLng=" + latLng +
                ", name='" + name + '\'' +
                ", street='" + street + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", postcode='" + postcode + '\'' +
                ", city='" + city + '\'' +
                ", openingHours='" + openingHours + '\'' +
                ", website='" + website + '\'' +
                ", phone='" + phone + '\'' +
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