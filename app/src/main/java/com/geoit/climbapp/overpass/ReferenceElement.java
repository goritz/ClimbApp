package com.geoit.climbapp.overpass;

import com.geoit.climbapp.LatLng;

public class ReferenceElement extends BaseElement {

    private final LatLng latLng;

    protected ReferenceElement(long id, LatLng latLng) {
        super(id);
        this.latLng=latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }


}
