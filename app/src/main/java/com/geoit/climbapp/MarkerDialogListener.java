package com.geoit.climbapp;

import com.geoit.climbapp.overpass.TaggedElement;
import com.mapbox.geojson.Point;

public interface MarkerDialogListener {
    void onStartNavigationClick(LatLng markerPosition);
}
