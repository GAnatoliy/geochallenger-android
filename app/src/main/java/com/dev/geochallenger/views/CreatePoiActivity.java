package com.dev.geochallenger.views;

import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.dev.geochallenger.R;
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
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by a_dibrivnyj on 4/23/16.
 */
public class CreatePoiActivity extends ABaseActivityView<CreatePoiPresenter> implements ICreatePoiView {

    private MapView mapView;
    private GoogleMap map;
    private EditText descriptionEditText;
    private EditText titleEditText;

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

        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        titleEditText = (EditText) findViewById(R.id.titleEditText);

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
                final Location myLocation = map.getMyLocation();

                Geocoder geocoder = new Geocoder(getApplicationContext());
                geocoder.getAddress(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), new IGeocoder.IGeocoderListener() {
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
                        poiRequest.setLatitude((float) myLocation.getLatitude());
                        poiRequest.setLongitude((float) myLocation.getLongitude());
                        presenter.createPoi(poiRequest);
                    }

                    @Override
                    public void onError(Exception e) {
                        hideProgress();
                    }
                });

                Toast.makeText(CreatePoiActivity.this, "create", Toast.LENGTH_SHORT).show();
                return true;
            }
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void poiCreated(Poi poi) {
        hideProgress();
        finish();
    }
}
