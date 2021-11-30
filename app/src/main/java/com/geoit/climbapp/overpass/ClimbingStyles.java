package com.geoit.climbapp.overpass;

public enum ClimbingStyles {

    BOULDER("boulder"),
    SPORT("sport"),
    SPEED("speed"),
    TOPROPE("toprope"),
    TRAD("trad"),
    MULTIPITCH("multipitch"),
    ICE("ice"),
    MIXED("mixed"),
    DEEPWATER("deepwater"),

    ;

    String value; //osm tag

    ClimbingStyles(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
