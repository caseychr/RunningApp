package com.runningapp.runningapp;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment implements PermissionsListener {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";

    @Inject PreferencesHelper mPreferencesHelper;

    ImageButton mVoiceBtn;
    ImageButton mMusicBtn;

    TextView mMyRunsBtn;

    String mDistance;
    String mDuration;

    MapView mMapView;
    MapboxMap mMapbox;
    MapboxDirections client;
    private DirectionsRoute currentRoute;
    Point origin;
    Point destination;

    //Button mButton;
    Style mStyle;


    PermissionsManager mPermissionsManager;

    LocationComponent mLocationComponent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_api_key));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                //Load SettingsFragment Fragment
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment fragment = fm.findFragmentById(R.id.fragment_container);
                fragment = SettingsFragment.newInstance();
                fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                return true;
                default:return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        MainActivity.sActivityComponent.inject(this);
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mVoiceBtn = view.findViewById(R.id.img_btn_voice);
        //mVoiceBtn.setImageDrawable(getContext().getDrawable(R.drawable.ic_voice_on));
        mMusicBtn = view.findViewById(R.id.img_btn_music);
        //mMusicBtn.setImageDrawable(getContext().getDrawable(R.drawable.ic_music_off));
        mMapView = view.findViewById(R.id.view_map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mMapbox = mapboxMap;
                mMapbox.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        mStyle = style;
                        getCurrentLocation();
                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                        //origin = Point.fromLngLat(-84.276938, 33.898869);
                        destination = Point.fromLngLat(-84.276938, 33.898869);
                        mMapbox.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                            @Override
                            public boolean onMapClick(@NonNull LatLng point) {
                                String s = String.format(Locale.US, "User clicked at: %s", point.toString());
                                destination = Point.fromLngLat(point.getLongitude(), point.getLatitude());
                                initSource(mStyle);
                                initLayers(mStyle);
                                getRoute(mStyle, origin, destination);
                                Log.i("AREA CLICKED", s);
                                Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
                                return true;
                            }
                        });
                    }
                });
            }
        });

        mMyRunsBtn = view.findViewById(R.id.img_btn_past);
        mMyRunsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), mPreferencesHelper.isWorking(), Toast.LENGTH_LONG).show();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment fragment = fm.findFragmentById(R.id.fragment_container);
                fragment = MyRunsFragment.newInstance();
                fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        });

        /*mButton = view.findViewById(R.id.constraint_bottom);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initSource(mStyle);
                initLayers(mStyle);
                getRoute(mStyle, origin, destination);
            }
        });*/
        return view;
    }

    private boolean hasLocationPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), LOCATION_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (hasLocationPermission()) {
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getRoute(@NonNull final Style style, Point origin, Point destination) {

        final LatLng dest = new LatLng();
        dest.setLongitude(destination.longitude());
        dest.setLatitude(destination.latitude());

        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_WALKING)
                .accessToken(getString(R.string.mapbox_api_key))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                System.out.println(call.request().url().toString());

// You can get the generic HTTP info about the response
                Log.d("Response code: ", response.body().toString());
                if (response.body() == null) {
                    Log.e("No routes fou you set", " the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Log.e("No routes found", "");
                    return;
                }

// Get the directions route
                currentRoute = response.body().routes().get(0);
                //mDistance = String.valueOf(currentRoute.distance());
                //mDuration = String.valueOf(currentRoute.duration());
                mDistance = convertDistance(currentRoute);
                mDuration = convertDuration(currentRoute);

// Make a toast which displays the route's distance
                Log.i("CurrentRoute", currentRoute.toString());
                Log.i("D&D", "DIST "+mDistance+", DUR "+mDuration);
                Toast.makeText(getActivity(), "DIST "+mDistance+", DUR "+mDuration, Toast.LENGTH_SHORT).show();

                if (style.isFullyLoaded()) {
// Retrieve and update the source designated for showing the directions route
                    GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

// Create a LineString with the directions route's geometry and
// reset the GeoJSON source for the route LineLayer source
                    if (source != null) {
                        Log.d("onResponse: ", "source != null");
                        source.setGeoJson(FeatureCollection.fromFeature(
                                Feature.fromGeometry(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6))));
                        shiftMapView(dest);
                    }
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Log.e("Error: ", throwable.getMessage());
                Toast.makeText(getContext(), "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initLayers(@NonNull Style loadedMapStyle) {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

// Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#009688"))
        );
        loadedMapStyle.addLayer(routeLayer);

// Add the red marker icon image to the map
        loadedMapStyle.addImage(RED_PIN_ICON_ID, BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.ic_arrow_d)));

// Add the red marker icon SymbolLayer to the map
        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                iconImage(RED_PIN_ICON_ID),
                iconIgnorePlacement(true),
                iconIgnorePlacement(true),
                iconOffset(new Float[]{0f, -4f})));
    }

    private void initSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID,
                FeatureCollection.fromFeatures(new Feature[]{})));

        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID,
                FeatureCollection.fromFeatures(new Feature[]{
                        Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude())),
                        Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude()))}));
        loadedMapStyle.addSource(iconGeoJsonSource);
    }

    @SuppressWarnings( {"MissingPermission"})
    private void getCurrentLocation() {

        if(PermissionsManager.areLocationPermissionsGranted(getContext())) {


            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(getContext(), mStyle)
                            .useDefaultLocationEngine(true).build();

            mLocationComponent = mMapbox.getLocationComponent();
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLocationComponent.activateLocationComponent(locationComponentActivationOptions);
            mLocationComponent.setLocationComponentEnabled(true);
            mLocationComponent.setRenderMode(RenderMode.GPS);
            mLocationComponent.setCameraMode(CameraMode.TRACKING);
            origin = Point.fromLngLat(mLocationComponent.getLastKnownLocation().getLongitude(), mLocationComponent.getLastKnownLocation().getLatitude());
        } else {
            mPermissionsManager = new PermissionsManager(this);
            mPermissionsManager.requestLocationPermissions(getActivity());
        }
    }


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        //Nothing to show here
    }

    @Override
    public void onPermissionResult(boolean granted) {
        //Nothing to show here
    }

    private void shiftMapView(LatLng dest) {
        LatLng origin = new LatLng();
        origin.setLatitude(mLocationComponent.getLastKnownLocation().getLatitude());
        origin.setLongitude(mLocationComponent.getLastKnownLocation().getLongitude());
        LatLngBounds latLngBounds = new LatLngBounds.Builder().include(origin).include(dest).build();

        mMapbox.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 200), 1000);
    }

    private String convertDistance(DirectionsRoute route) {
        Double km = route.distance()/1000;
        Double miles = km * 0.621371;
        return String.valueOf(miles);
    }

    private String convertDuration(DirectionsRoute route) {
        Double minutes = route.duration()/60;
        return String.valueOf(minutes);
    }
}
