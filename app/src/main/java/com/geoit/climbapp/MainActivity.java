package com.geoit.climbapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
//import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.base.route.RouterCallback;
import com.mapbox.navigation.base.route.RouterFailure;
import com.mapbox.navigation.base.route.RouterOrigin;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.MapboxNavigationProvider;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements LocationListener {

    LocationManager locManager;

    MapboxNavigation navigation;


    View layout;
    private static final int PERMISSION_REQUEST_CODE = 1234;
    private static final String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};


    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.main_layout);


        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {

                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments


                    }
                });

            }
        });


        if(MapboxNavigationProvider.isCreated()){
            navigation=MapboxNavigationProvider.retrieve();
        }else{
            navigation=MapboxNavigationProvider.create(new NavigationOptions.Builder(this).accessToken(getString(R.string.mapbox_access_token)).build());
        }

        Point a=Point.fromLngLat(13.25,52.3);
        Point b=Point.fromLngLat(15.75,52);

        ArrayList<Point>list=new ArrayList<>();
        list.add(a);
        list.add(b);


        RouteOptions options= RouteOptions.builder().coordinatesList(list).profile(DirectionsCriteria.PROFILE_DRIVING).alternatives(false).build();

        navigation.requestRoutes(options, new RouterCallback() {
            @Override
            public void onRoutesReady(@NonNull List<? extends DirectionsRoute> list, @NonNull RouterOrigin routerOrigin) {
                System.out.println("ready");
                System.out.println(routerOrigin.toString());
                System.out.println(list.toString());

            }

            @Override
            public void onFailure(@NonNull List<RouterFailure> list, @NonNull RouteOptions routeOptions) {
                System.out.println("failure");
                System.out.println(routeOptions.toString());
                System.out.println(list.toString());

            }

            @Override
            public void onCanceled(@NonNull RouteOptions routeOptions, @NonNull RouterOrigin routerOrigin) {
                System.out.println("canceled");
                System.out.println(routerOrigin.toString());
                System.out.println(list.toString());
            }
        });






        requestPermission();


        testLocalXML(this.getApplicationContext(),"testabfrage2.xml");
        testLocalXML(this.getApplicationContext(),"australia.xml");
        testLocalXML(this.getApplicationContext(),"pyrenaen.xml");
        testLocalXML(this.getApplicationContext(),"portugal.xml");
        testLocalXML(this.getApplicationContext(),"southamerica.xml");


    }




    private void testLocalXML(Context context,String filename){
        try {
            System.out.println("TESTING XML: "+filename);
            Document doc=XMLUtils.loadDocument(context.getAssets().open(filename));
            Overpasser.parseResponse(doc);

        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }

    }

    /**
     * checks whether or not the app has all permissions granted to be fully functional
     *
     * @return boolean (hasPermissions)
     */
    private boolean hasPermissions() {

        boolean p0 = ActivityCompat.checkSelfPermission(this, PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED;
        boolean p1 = ActivityCompat.checkSelfPermission(this, PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED;

        System.out.println(PERMISSIONS[0] + " " + p0);
        System.out.println(PERMISSIONS[1] + " " + p1);

        return (p0 && p1);


//        return (ActivityCompat.checkSelfPermission(this, PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, PERMISSIONS[2]) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Requests permissions via a snackbar with a button, if the user has not granted the permissions already
     * Only if the permissions are given the map can be opened
     */
    private void requestPermission() {
        // Permission has not been granted and must be requested.
        // Display a SnackBar with cda button to request the missing permission.
        if (!hasPermissions()) {
            System.out.println("haspermission is false");
            Snackbar.make(layout, "permissions_required",
                    Snackbar.LENGTH_INDEFINITE).setAction("permission_request_accept", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    System.out.println("requesting permission...");
                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSION_REQUEST_CODE);
                }
            }).show();


        }else{
            initLocator();
        }


    }

    /**
     * Is called when the user has exited the permissions dialog
     * As long as not every required permission has been granted, the user will be continuously asked to grant permissions via requestPermission()
     *
     * @param requestCode  (identifying this request)
     * @param permissions  (required permissions in String[])
     * @param grantResults (resultCodes for each permission in a int[])
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length == 2) {
            // Request for location permission.
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start location Activity.
                System.out.println("permission_granted");
                Snackbar.make(layout, "permissions_granted", Snackbar.LENGTH_SHORT).show();

                initLocator();
            } else {
                // Permission request was denied.
                System.out.println("permission was denied");
                requestPermission();
            }
        }
    }

    /**
     * Initializes the location manager and gets the last known location
     * shows a snackbar if the provider is disabled
     */
    private void initLocator() {


        try {
            locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locManager != null) {
                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100L,
                        10.0f, this);
                Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                updateLocation(location);
                if (location != null) {
                    System.out.println("last known location: " + location.toString());
                } else {
                    System.out.println("no last known location found!");
                }

                if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                    showEnableLocationServicesSnackbar();
                    System.out.println("gps is off");
                } else {
//                    showToast(getString(R.string.locating));
                    System.out.println("locating");
                }
            }

        } catch (SecurityException | NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Called when the location has changed.
     *
     * @param location the updated location
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        System.out.println("location changed: " + location.toString());
    }

    /**
     * Called when the provider is enabled by the user.
     *
     * @param provider the name of the location provider that has become enabled
     */
    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }
    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates
     * is called on an already disabled provider, this method is called
     * immediately.
     *
     * @param provider the name of the location provider that has become disabled
     */
    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        navigation.onDestroy();
    }

}