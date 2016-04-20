package com.dev.geochallenger.views;

import android.accounts.AccountManager;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dev.geochallenger.R;
import com.dev.geochallenger.models.ExtraConstants;
import com.dev.geochallenger.models.RetrofitModel;
import com.dev.geochallenger.models.api.Geocoder;
import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.cities.PlacesEntity;
import com.dev.geochallenger.models.entities.cities.Predictions;
import com.dev.geochallenger.models.entities.cities.detailed.PlaceDetailedEntity;
import com.dev.geochallenger.models.entities.cities.detailed.Viewport;
import com.dev.geochallenger.presenters.MainPresenter;
import com.dev.geochallenger.views.controlers.SearchControler;
import com.dev.geochallenger.views.interfaces.ABaseActivityView;
import com.dev.geochallenger.views.interfaces.IMainView;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lapism.searchview.adapter.SearchAdapter;
import com.lapism.searchview.adapter.SearchItem;
import com.lapism.searchview.view.SearchView;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MainActivity extends ABaseActivityView<MainPresenter> implements IMainView, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    private MapView mapView;
    private GoogleMap map;
    private SearchView searchView;
    private String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    private PlacesEntity placesEntity;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private Marker customMarker;
    private TextView tvSelectedPlaceCity;
    private TextView tvSelectedPlace;
    private SearchControler searchControler;
    private PlaceDetailedEntity selectedLatLng;

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this, RetrofitModel.getInstance(), new Geocoder(this));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.createRouteClicked();
            }
        });

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.nsvMainActivity));
        tvSelectedPlaceCity = (TextView) findViewById(R.id.tvMainPlaceDetailsCity);
        tvSelectedPlace = (TextView) findViewById(R.id.tvMainPlaceDetails);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) findViewById(R.id.mvFeed);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Location location = map.getMyLocation();
                if (location != null) {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(), location.getLongitude()), 13));
                }

                return true;
            }
        });
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                presenter.setMyLocation(location);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 13));
                //show location only on start
                map.setOnMyLocationChangeListener(null);
            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                presenter.onMapLongClick(latLng);
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.equals(customMarker)) {
                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }
                return true;
            }
        });

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this);

        initSearchView();

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAccount();
            }
        });
    }

    public void getAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    public void initSearchView() {
        searchView = (SearchView) findViewById(R.id.search_view);
        searchControler = new SearchControler(getApplicationContext(), searchView);
        searchControler.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (selectedLatLng != null) {
                    final Viewport viewport = selectedLatLng.getResult().getGeometry().getViewport();
                    Double topLeftLatitude = Double.parseDouble(viewport.getNortheast().getLat());
                    Double topLeftLongitude = Double.parseDouble(viewport.getSouthwest().getLng());
                    Double bottomRightLatitude = Double.parseDouble(viewport.getSouthwest().getLat());
                    Double bottomRightLongitude = Double.parseDouble(viewport.getNortheast().getLng());
                    presenter.queryText(query, topLeftLatitude, topLeftLongitude, bottomRightLatitude, bottomRightLongitude);
                } else {
                    presenter.queryText(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.findPlaces(newText, getString(R.string.google_directions_key));
                return true;
            }
        });
        searchControler.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Predictions selectedPredictions = placesEntity.getPredictions()[position];
                final String place_id = selectedPredictions.getPlace_id();
                presenter.getDetailedPlaceInfo(place_id, getString(R.string.google_directions_key));
                searchControler.hide();


                final View searchLocationlayout = LayoutInflater.from(MainActivity.this).inflate(R.layout.search_location_layout, null);

                final TextView textView = (TextView) searchLocationlayout.findViewById(R.id.textView);
                textView.setText(selectedPredictions.getDescription().split(", ")[0]);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.parseColor("#332354"));

                final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llLocationContainer);
                linearLayout.removeAllViews();
                linearLayout.addView(textView);
            }
        });
        searchControler.init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == RESULT_OK) {
                final String mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String token = "";
                        try {
                            token = GoogleAuthUtil.getToken(MainActivity.this, mEmail, SCOPE);
                            tokenObtained(token);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (GoogleAuthException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }

    public void tokenObtained(String token) {
        Log.d(TAG, "handleSignInResult. token: " + token);
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
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public String getErrorTitle(Exception exception) {
        //TODO
        return null;
    }

    @Override
    public void initMap(List<Poi> pois) {
        searchView.hide(true);
        map.clear();
        if (pois != null) {
            for (Poi poi : pois) {
                final MarkerOptions snippet = new MarkerOptions()
                        .position(new LatLng(poi.getLatitude(), poi.getLongitude()))
                        .title(poi.getTitle())
                        .snippet(poi.getAddress());
                map.addMarker(snippet);
            }
        }
    }

    @Override
    public void populateAutocompeteList(PlacesEntity placesEntity) {
        this.placesEntity = placesEntity;
        searchControler.clearSuggestionList();
        for (Predictions predictions : placesEntity.getPredictions()) {
            searchControler.addItomToSuggestionList(new SearchItem(predictions.getDescription()));
        }

    }

    @Override
    public void setMapLocation(PlaceDetailedEntity latLng) {
        this.selectedLatLng = latLng;
        final Viewport viewport = latLng.getResult().getGeometry().getViewport();
        final LatLng southwest = new LatLng(Double.parseDouble(viewport.getSouthwest().getLat()), (Double.parseDouble(viewport.getSouthwest().getLng())));
        final LatLng northeast = new LatLng(Double.parseDouble(viewport.getNortheast().getLat()), (Double.parseDouble(viewport.getNortheast().getLng())));
        final LatLngBounds latLngBounds = new LatLngBounds(southwest, northeast);
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0));
    }

    @Override
    public void setCustomMarker(LatLng latLng) {
        if (customMarker != null) {
            customMarker.remove();
        }
        final MarkerOptions snippet = new MarkerOptions()
                .position(latLng);
        customMarker = map.addMarker(snippet);
    }

    @Override
    public void showPlaceDetails() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }, 100);
    }

    @Override
    public void updatePlaceDetailsWithAddress(Address address) {
        int maxAddressLineIndex = address.getMaxAddressLineIndex();

        tvSelectedPlaceCity.setText(maxAddressLineIndex >= 0 ? address.getAddressLine(0) : "");
        tvSelectedPlace.setText(maxAddressLineIndex >= 1 ? address.getAddressLine(1) : "");
    }

    @Override
    public void showCreateRouteScreen(LatLng selectedPlaceLocation, @Nullable Address selectedPlaceAddress, @Nullable Location myLocation) {
        Intent intent = new Intent(MainActivity.this, CreateRouteActivity.class);
        intent.putExtra(ExtraConstants.SELECTED_LOCATION, selectedPlaceLocation);
        intent.putExtra(ExtraConstants.SELECTED_ADDRESS, selectedPlaceAddress);
        intent.putExtra(ExtraConstants.MY_LOCATION, myLocation);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onBackPressed() {
        if (searchView != null && searchView.isSearchOpen()) {
            searchView.hide(true);
        } else {
            super.onBackPressed();
        }
    }
}
