package com.dev.geochallenger.views;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.dev.geochallenger.R;
import com.dev.geochallenger.presenters.CreateRoutePresenter;
import com.dev.geochallenger.views.interfaces.ABaseActivityView;
import com.dev.geochallenger.views.interfaces.ICreateRouteView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

public class CreateRouteActivity extends ABaseActivityView<CreateRoutePresenter> implements ICreateRouteView {

    private MapView mapView;
    private GoogleMap map;

    @Override
    protected CreateRoutePresenter createPresenter() {
        return new CreateRoutePresenter(this);
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) findViewById(R.id.mvFeed);
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

    }
}
