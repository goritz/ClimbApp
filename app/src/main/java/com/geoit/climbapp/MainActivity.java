package com.geoit.climbapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import com.geoit.climbapp.overpass.ElementFactory;
import com.geoit.climbapp.overpass.OverpassException;
import com.geoit.climbapp.overpass.OverpassTask;
import com.geoit.climbapp.overpass.TaggedElement;
import com.geoit.climbapp.overpass.TaskExecutor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.base.route.RouterCallback;
import com.mapbox.navigation.base.route.RouterFailure;
import com.mapbox.navigation.base.route.RouterOrigin;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.MapboxNavigationProvider;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView;
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements LocationListener {

    LocationManager locManager;

    MapboxNavigation navigation;


    View layout;
    private static final int PERMISSION_REQUEST_CODE = 1234;
    private static final String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    public static final int DEFAULT_RADIUS = 10; //km


    private FloatingActionButton btnSearch, btnSettings, btnCenter;
    private ProgressBar loadingBar;

    private MapView mapView;
    private MapboxMap mapboxMap;
    private Style mapboxStyle;
    private NavigationMapRoute route;
    private SymbolManager symbolManager;

    private HashMap<Symbol, TaggedElement> elementHashMap;
    private Location lastLocation;
    private final LatLng DEFAULT_LOCATION = new LatLng(52.13, 13.25);

    TaskExecutor executor;


    MapboxRouteLineApi lineApi;
    MapboxRouteLineView lineView;
    MapboxRouteLineOptions lineOptions;

    boolean isMapboxLocationActivated=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_main);


        layout = findViewById(R.id.main_layout);

        btnSearch = findViewById(R.id.btn_sendRequest);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkNetworkStatus()) {
                    UIUtils.showToast(MainActivity.this, getString(R.string.internet_disabled_overpass));
                    return;
                }
                sendRequest();
            }
        });
        btnSettings = findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
//                intent.addFlags(FLAG)
                startActivity(intent);


            }
        });
        btnCenter = findViewById(R.id.btn_center);

        loadingBar = findViewById(R.id.progressBar);
        loadingBar.setVisibility(View.INVISIBLE);

        elementHashMap = new HashMap<>();
        executor = new TaskExecutor();

        if (!checkNetworkStatus()) {
            UIUtils.showToast(MainActivity.this, getString(R.string.internet_disabled_map));
        }
        lineOptions = new MapboxRouteLineOptions.Builder(this).build();

        lineApi = new MapboxRouteLineApi(lineOptions);
        lineView = new MapboxRouteLineView(lineOptions);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                MainActivity.this.mapboxMap = mapboxMap;


                mapboxMap.addOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                    @Override
                    public boolean onMapLongClick(@NonNull com.mapbox.mapboxsdk.geometry.LatLng point) {
                        if (!checkNetworkStatus()) {
                            UIUtils.showToast(MainActivity.this, getString(R.string.internet_disabled_overpass));
                            return true;
                        }
                        sendRequest(new LatLng(point.getLatitude(), point.getLongitude()));
                        return true;
                    }
                });





                Bitmap bitmap_outdoor = UIUtils.drawableToBitmap(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_outdoor));
                Bitmap bitmap_indoor = UIUtils.drawableToBitmap(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_indoor));
                Bitmap bitmap_both = UIUtils.drawableToBitmap(AppCompatResources.getDrawable(MainActivity.this, R.drawable.ic_inout));



                mapboxMap.setStyle(new Style.Builder().fromUri(Style.MAPBOX_STREETS)

                                // Add the SymbolLayer icon image to the map style
                                .withImage("marker_outdoor", bitmap_outdoor)
                                .withImage("marker_indoor", bitmap_indoor)
                                .withImage("marker_both", bitmap_both)


                        , new Style.OnStyleLoaded() {
                            @Override
                            public void onStyleLoaded(@NonNull Style style) {
                                MainActivity.this.mapboxStyle=style;

                                // Map is set up and the style has loaded. Now you can add additional data or make other map adjustments.
                                // Set up a SymbolManager instance
                                if (MapboxNavigationProvider.isCreated()) {
                                    navigation = MapboxNavigationProvider.retrieve();
                                } else {
                                    navigation = MapboxNavigationProvider.create(new NavigationOptions.Builder(MainActivity.this).accessToken(getString(R.string.mapbox_access_token)).build());
                                }

                                route = new NavigationMapRoute(null, mapView, mapboxMap, R.style.ClimbAppNavigationMapRoute);
                                enableLocationMarker();



//                                MainActivity.this.mapStyle=style;
                                symbolManager = new SymbolManager(mapView, mapboxMap, style);

                                symbolManager.setIconAllowOverlap(true);
                                symbolManager.setTextAllowOverlap(true);
                                symbolManager.addClickListener(new OnSymbolClickListener() {
                                    @Override
                                    public void onAnnotationClick(Symbol symbol) {
                                        TaggedElement clicked = elementHashMap.get(symbol);
                                        if (clicked != null) {
                                            Log.d("Annotation Click", "symbol was clicked: " + clicked.toString());


                                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                                            boolean debug = sharedPreferences.getBoolean("debug", true);
                                            if (debug) {
                                                UIUtils.showToast(MainActivity.this, clicked.toFormattedString(), Toast.LENGTH_LONG);
                                            }

                                            MarkerDialog dialog = new MarkerDialog(MainActivity.this, clicked);
                                            dialog.setListener(new MarkerDialogListener() {
                                                @Override
                                                public void onStartNavigationClick(LatLng markerPosition) {
                                                    startNavigation(markerPosition);
                                                    dialog.dismiss();

                                                }
                                            });
                                            dialog.show();
                                        } else {
                                            Log.e("Annotation Click", "symbol was not found in hashmap: " + symbol.getLatLng().toString());
                                        }
                                    }
                                });

                                CameraPosition position = new CameraPosition.Builder()
                                        .target(new com.mapbox.mapboxsdk.geometry.LatLng(49.0, 11.0)) // Sets the new camera position
                                        .zoom(4) // Sets the zoom
//                                        .bearing(180) // Rotate the camera
//                                        .tilt(30) // Set the camera tilt
                                        .build(); // Creates a CameraPosition from the builder

                                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000);


                                btnCenter.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {


                                        if (locManager == null || !locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                            UIUtils.showToast(MainActivity.this, getString(R.string.gps_disabled));
                                            return;
                                        }

                                        if (lastLocation != null) {
                                            // Sets the new camera position and zoom
                                            CameraPosition position = new CameraPosition.Builder()
                                                    .target(new com.mapbox.mapboxsdk.geometry.LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                                                    .zoom(15)
                                                    .build();

                                            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 4000);
                                            enableLocationMarker();
                                        } else {
                                            UIUtils.showToast(MainActivity.this, getString(R.string.gps_no_position));

                                        }

                                    }
                                });
                            }
                        });

            }
        });


        requestPermission();
        UIUtils.showToast(this, getString(R.string.search_prompt), Toast.LENGTH_LONG);


    }

    private void startNavigation(LatLng b) {


        if (lastLocation == null) {
            UIUtils.showToast(MainActivity.this, getString(R.string.gps_no_position));
            return;
        }

        Point location = Point.fromLngLat(lastLocation.getLongitude(), lastLocation.getLatitude());
        Point target = Point.fromLngLat(b.getLongitude(), b.getLatitude());


        ArrayList<Point> list = new ArrayList<>();
        list.add(location);
        list.add(target);


        RouteOptions options = RouteOptions.builder()

                .coordinatesList(list)
                .profile(DirectionsCriteria.PROFILE_DRIVING)

                .alternatives(false)
                .steps(true)
                .build();

        navigation.requestRoutes(options, new RouterCallback() {

            @Override
            public void onRoutesReady(@NonNull List<? extends DirectionsRoute> list, @NonNull RouterOrigin routerOrigin) {
                System.out.println("ready");
                System.out.println(routerOrigin.toString());
                System.out.println(list.toString());

                if (list.size() == 0) {
                    return;
                }
                System.out.println(list.get(0).geometry());


                route.addRoute(list.get(0)); //so ist capture of ? egal!
                //List<DirectionsRoute> list2 = new ArrayList<>(list);
                //route.addRoutes(list2);

                CameraPosition position = new CameraPosition.Builder()
                        .target(new com.mapbox.mapboxsdk.geometry.LatLng(location.latitude(), location.longitude()))
                        .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2500);

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
    }

    private void sendRequest() {


        if (lastLocation != null) {
            Log.d("[Request]", "sending Overpass-Request");
            LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            sendRequest(latLng);
        } else {
            UIUtils.showToast(MainActivity.this, getString(R.string.gps_no_position));
        }

    }

    private void sendRequest(LatLng latLng) {

        System.out.println(executor.isRunning());
        if (executor.isRunning()) {
            UIUtils.showToast(this, getString(R.string.already_running_request));
            return;
        }


        loadingBar.setVisibility(View.VISIBLE);

        elementHashMap.clear();
        symbolManager.deleteAll();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String api = sharedPreferences.getString("overpass", getString(R.string.default_overpass_mirror));
        int radius = sharedPreferences.getInt("radius", DEFAULT_RADIUS);

        boolean isFilteringDeactivated = sharedPreferences.getBoolean("filter", true);
        boolean showIndoor = sharedPreferences.getBoolean("indoor", true);
        boolean showOutdoor = sharedPreferences.getBoolean("outdoor", true);
        boolean showFee = sharedPreferences.getBoolean("fee", true);

        Log.d("Overpass Request", "starting request for: " + api + ", radius: " + radius + latLng.toString());


        double deg = (radius * 1000) / (111320 * Math.cos(Math.toRadians(latLng.getLatitude())));
        final LatLngBounds bounds = LatLngBounds.from(latLng.getLatitude() + deg, latLng.getLongitude() + deg, latLng.getLatitude() - deg, latLng.getLongitude() - deg);


        executor.executeAsync(new OverpassTask(api, radius, latLng), new TaskExecutor.Callback<ArrayList<TaggedElement>>() {
            @Override
            public void onComplete(ArrayList<TaggedElement> result) {
                int additions = 0;

                loadingBar.setVisibility(View.INVISIBLE);
//                System.out.println(result.toString());
                for (TaggedElement t : result) {
//                    System.out.println(t.toString());
//                    Log.d("[Request]","adding symbol for "+t.toString());

                    if (!isFilteringDeactivated) {

                        if (t.isIndoor() && t.isOutdoor() && !showIndoor && !showOutdoor) {
                            continue;
                        } else {
                            if (t.isIndoor() && !showIndoor) {
                                continue;
                            } else if (t.isOutdoor() && !showOutdoor) {
                                continue;
                            }
                        }

                        if (t.isFee() && !showFee) {
                            continue;
                        }
                    }
                    addSymbolForElement(t);
                    additions++;

                }
                UIUtils.showToast(MainActivity.this, getString(R.string.request_found_elements, result.size(), result.size() - additions), Toast.LENGTH_SHORT);
                Log.d("[Request]", "result size " + result.size());
                Log.d("[Request]", "added " + additions + " symbols!");


            }

            @Override
            public void onTimeout() {
                UIUtils.showToast(MainActivity.this, getString(R.string.overpass_timeout), Toast.LENGTH_LONG);
                loadingBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError() {
                UIUtils.showToast(MainActivity.this, getString(R.string.overpass_error), Toast.LENGTH_SHORT);
                loadingBar.setVisibility(View.INVISIBLE);
            }
        });
        //disable user input while initial zoom animation is running
        CameraUpdate fitToBounds = CameraUpdateFactory.newLatLngBounds(bounds, 100);

        mapboxMap.getUiSettings().setAllGesturesEnabled(false);
        mapboxMap.animateCamera(fitToBounds, 2500, new MapboxMap.CancelableCallback() {
            @Override
            public void onCancel() {
                mapboxMap.getUiSettings().setAllGesturesEnabled(true);
            }

            @Override
            public void onFinish() {
                mapboxMap.getUiSettings().setAllGesturesEnabled(true);
            }
        });
    }

    private void addSymbolForElement(TaggedElement element) {
        // Add symbol at specified lat/lon
        try {
            Symbol symbol = symbolManager.create(new SymbolOptions()
                            .withLatLng(new com.mapbox.mapboxsdk.geometry.LatLng(element.getLatLng().getLatitude(), element.getLatLng().getLongitude()))
                            .withIconImage((element.isIndoor() ? (element.isOutdoor() ? "marker_both" : "marker_indoor") : "marker_outdoor"))
                            .withIconAnchor(Property.ICON_ANCHOR_BOTTOM)
//                            .withIconSize(0.8f)

//                                        .withDraggable(true)
            );
            elementHashMap.put(symbol, element);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }


    }


    private boolean checkNetworkStatus() {
        ConnectivityManager cm = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null;
    }

    @Deprecated
    private void testLocalXML(Context context, String filename) {
        try {
            System.out.println("TESTING XML: " + filename);
            Document doc = XMLUtils.loadDocument(context.getAssets().open(filename));
//            Overpasser.parseResponse(doc);
            ElementFactory.parseResponse(doc);

        } catch (IOException | SAXException | ParserConfigurationException | OverpassException e) {
            e.printStackTrace();
        }

    }
    @SuppressLint("MissingPermission")
    private void enableLocationMarker(){
        if(hasPermissions()&&!isMapboxLocationActivated){
            LocationComponentOptions locationComponentOptions =
                    LocationComponentOptions.builder(MainActivity.this)
                            .pulseEnabled(true)
                            .pulseColor(getColor(R.color.accent))
                            .pulseAlpha(.4f)
                            .pulseInterpolator(new DecelerateInterpolator())
                            .build();

            LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions
                    .builder(MainActivity.this, mapboxStyle)
                    .locationComponentOptions(locationComponentOptions)
                    .build();

//                mapboxLocation = mapboxMap.getLocationComponent();




            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Activate with a built LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
                locationComponent.setCameraMode(CameraMode.NONE);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
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
            Snackbar.make(layout, getString(R.string.permission_request),
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.permission_request_accept, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    System.out.println("requesting permission...");
                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSION_REQUEST_CODE);
                }
            }).show();


        } else {
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
                Snackbar.make(layout, getString(R.string.permissions_granted), Snackbar.LENGTH_SHORT).show();

                initLocator();
                if(mapboxStyle!=null){
                    enableLocationMarker();
                }
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
                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L,
                        10.0f, this);
                Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);







//                updateLocation(location);
                if (location != null) {
                    System.out.println("last known location: " + location.toString());
                    this.lastLocation = location;
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
        this.lastLocation = location;
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
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