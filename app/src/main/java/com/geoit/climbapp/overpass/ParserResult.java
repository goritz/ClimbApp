package com.geoit.climbapp.overpass;

import java.util.ArrayList;

@Deprecated
public class ParserResult {

    ArrayList<TaggedElement> nodeElements;
    ArrayList<TaggedElement> wayElements;

    public ParserResult(ArrayList<TaggedElement> nodeElements, ArrayList<TaggedElement> wayElements) {
        this.nodeElements = nodeElements;
        this.wayElements = wayElements;
    }

    public ArrayList<TaggedElement> getNodeElements() {
        return nodeElements;
    }

    public ArrayList<TaggedElement> getWayElements() {
        return wayElements;
    }
}
