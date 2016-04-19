package com.dev.geochallenger.views;

import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.dev.geochallenger.R;
import com.dev.geochallenger.models.ExtraConstants;
import com.dev.geochallenger.models.RetrofitModel;
import com.dev.geochallenger.presenters.CreateRoutePresenter;
import com.dev.geochallenger.views.interfaces.ABaseActivityView;
import com.dev.geochallenger.views.interfaces.ICreateRouteView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class CreateRouteActivity extends ABaseActivityView<CreateRoutePresenter> implements ICreateRouteView {

    private MapView mapView;
    private GoogleMap map;
    private Polyline currentRoute;
    private Location myLocation;
    private LatLng selectedLocation;
    private Address selectedAddress;

    @Override
    protected CreateRoutePresenter createPresenter() {
        return new CreateRoutePresenter(this, RetrofitModel.getInstance(), selectedLocation, selectedAddress, myLocation);
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);


        myLocation = (Location) getIntent().getParcelableExtra(ExtraConstants.MY_LOCATION);
        selectedLocation = (LatLng) getIntent().getParcelableExtra(ExtraConstants.SELECTED_LOCATION);
        selectedAddress = (Address)getIntent().getParcelableExtra(ExtraConstants.SELECTED_ADDRESS);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) findViewById(R.id.mvCreatePath);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this);

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_path;
    }

    @Override
    public void initMap() {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(49.0935026, 33.299107));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
        map.addMarker(markerOptions);

        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(new LatLng(50.2679361, 28.6386982));
        markerOptions2.icon(BitmapDescriptorFactory.defaultMarker());
        map.addMarker(markerOptions2);

        MarkerOptions markerOptions3 = new MarkerOptions();
        markerOptions3.position(new LatLng(49.2118017,31.8512216));
        markerOptions3.icon(BitmapDescriptorFactory.defaultMarker());
        map.addMarker(markerOptions3);

        MarkerOptions markerOptions4 = new MarkerOptions();
        markerOptions4.position(new LatLng(50.0162109,32.9536455));
        markerOptions4.icon(BitmapDescriptorFactory.defaultMarker());
        map.addMarker(markerOptions4);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                presenter.toggleWaypoints(marker.getPosition());
                return true;
            }
        });
    }

    @Override
    public String getGoogleWebApiKey() {
        return getString(R.string.google_directions_key);
    }

    @Override
    public void showRouteCalculationErrorMessage() {

    }

    @Override
    public int getRouteColor() {
        return Color.BLUE;
    }

    @Override
    public void drawRouteInUiThread(final PolylineOptions lineOptions) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (currentRoute != null) {
                    currentRoute.remove();
                }
                lineOptions.width(4);
                lineOptions.color(getRouteColor());
                currentRoute = map.addPolyline(lineOptions);
                moveToBounds(lineOptions.getPoints());
                hideProgress();
            }
        });
    }

    private void moveToBounds(List<LatLng> points){

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0; i < points.size();i++){
            builder.include(points.get(i));
        }
        LatLngBounds bounds = builder.build();
        int padding = getResources().getDimensionPixelSize(R.dimen.route_offset); // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.animateCamera(cu);
    }

    @Override
    public void hideProgressInUiThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgress();
            }
        });
    }
}
