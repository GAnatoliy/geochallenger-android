package com.dev.geochallenger.views;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.dev.geochallenger.R;
import com.dev.geochallenger.models.ExtraConstants;
import com.dev.geochallenger.models.RetrofitModel;
import com.dev.geochallenger.models.api.Geocoder;
import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.PoiRequest;
import com.dev.geochallenger.models.interfaces.IGeocoder;
import com.dev.geochallenger.models.repositories.TokenRepository;
import com.dev.geochallenger.presenters.CreatePoiPresenter;
import com.dev.geochallenger.views.interfaces.ABaseActivityView;
import com.dev.geochallenger.views.interfaces.ICreatePoiView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

/**
 * Created by a_dibrivnyj on 4/23/16.
 */
public class CreatePoiActivity extends ABaseActivityView<CreatePoiPresenter> implements ICreatePoiView {

    private MapView mapView;
    private GoogleMap map;
    private EditText descriptionEditText;
    private EditText titleEditText;
    private LatLng selectedLocation;
    private Marker selectedMarker;

    private long id = -1;

    @Override
    protected CreatePoiPresenter createPresenter() {
        return new CreatePoiPresenter(this, RetrofitModel.getInstance(), new TokenRepository(getApplicationContext()));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.create_poi_layout;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);

        final ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#111133")));

        selectedLocation = (LatLng) getIntent().getParcelableExtra(ExtraConstants.SELECTED_LOCATION);
        final String title = getIntent().getExtras().getString(ExtraConstants.TITLE);
        final String description = getIntent().getExtras().getString(ExtraConstants.DESCRIPTION);

        id = getIntent().getExtras().getLong(ExtraConstants.ID, -1);

        mapView = (MapView) findViewById(R.id.mvFeed);
        mapView.onCreate(savedInstanceState);
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 13));
                map.setOnMyLocationChangeListener(null);
            }
        });


        MapsInitializer.initialize(this);

        titleEditText = (EditText) findViewById(R.id.titleEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);

        titleEditText.setText(title);
        descriptionEditText.setText(description);

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                selectedLocation = latLng;
                if (selectedMarker != null) {
                    selectedMarker.remove();
                }
                drawMarker(latLng);
            }
        });
        drawMarker(selectedLocation);
    }

    public void drawMarker(LatLng location) {
        final MarkerOptions snippet = new MarkerOptions()
                .position(location)
                .title("")
                .snippet("");

        selectedMarker = map.addMarker(snippet);
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
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Create").setIcon(R.drawable.paperplane).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showProgress();

                Geocoder geocoder = new Geocoder(getApplicationContext());
                geocoder.getAddress(selectedLocation, new IGeocoder.IGeocoderListener() {
                    @Override
                    public void onAddressFetched(Address address) {
                        PoiRequest poiRequest = new PoiRequest();
                        poiRequest.setTitle(titleEditText.getText().toString());
                        poiRequest.setContent(descriptionEditText.getText().toString());
                        if (address != null && address.getMaxAddressLineIndex() >= 0) {
                            String addressString = "";
                            int maxAddressLineIndex = address.getMaxAddressLineIndex();

                            addressString += maxAddressLineIndex >= 0 ? address.getAddressLine(0) : "";
                            if (!addressString.equals("")) {
                                addressString += maxAddressLineIndex >= 1 ? ", " + address.getAddressLine(1) : "";
                            } else {
                                addressString += maxAddressLineIndex >= 1 ? address.getAddressLine(1) : "";
                            }
                            poiRequest.setAddress(addressString);
                        } else {
                            poiRequest.setAddress("");
                        }
                        poiRequest.setLatitude((float) selectedLocation.latitude);
                        poiRequest.setLongitude((float) selectedLocation.longitude);
                        if (id != -1) {
                            presenter.updatePoi(poiRequest, id);
                        } else {
                            presenter.createPoi(poiRequest);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        hideProgress();
                    }
                });

                return true;
            }
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void poiCreated(Poi poi) {
        hideProgress();

        Toast.makeText(CreatePoiActivity.this, "Poi created", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        final Gson gson = new Gson();
        intent.putExtra("poi", gson.toJson(poi));
        setResult(RESULT_OK, intent);

        finish();
    }

    @Override
    public void poiUpdated(Poi poi) {
        hideProgress();

        Toast.makeText(CreatePoiActivity.this, "Poi updated", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        final Gson gson = new Gson();
        intent.putExtra("poi", gson.toJson(poi));
        setResult(RESULT_OK, intent);

        finish();
    }
}
