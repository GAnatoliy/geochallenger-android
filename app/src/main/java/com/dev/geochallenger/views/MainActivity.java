package com.dev.geochallenger.views;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.dev.geochallenger.models.entities.login.LoginResponce;
import com.dev.geochallenger.models.entities.login.UserResponce;
import com.dev.geochallenger.models.repositories.TokenRepository;
import com.dev.geochallenger.presenters.MainPresenter;
import com.dev.geochallenger.views.adapters.RecyclerRelatedPhotosAdapter;
import com.dev.geochallenger.views.controlers.SearchControler;
import com.dev.geochallenger.views.interfaces.ABaseActivityView;
import com.dev.geochallenger.views.interfaces.IMainView;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.lapism.searchview.adapter.SearchAdapter;
import com.lapism.searchview.adapter.SearchItem;
import com.lapism.searchview.view.SearchView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends ABaseActivityView<MainPresenter> implements IMainView, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MENU_MY_ROUTES_INDEX = 0;
    private static final int MENU_LOG_OUT_INDEX = 1;

    private static final int REQUEST_AUTHORIZATION = 1;
    private static final int REQUEST_CODE_PICK_ACCOUNT = 2;
    private static final int REQUEST_CREATE_POI = 3;

    private MapView mapView;
    private GoogleMap map;
    private SearchView searchView;
    //    private String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile,https://www.googleapis.com/auth/userinfo.email";
    String SCOPE = "oauth2: https://www.googleapis.com/auth/userinfo.profile " +
            "https://www.googleapis.com/auth/userinfo.email " +
            "https://www.googleapis.com/auth/plus.login";
    private PlacesEntity placesEntity;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private Marker customMarker;
    private TextView tvSelectedPlaceCity;
    private TextView tvSelectedPlace;
    private SearchControler searchControler;
    private PlaceDetailedEntity selectedLatLng;
    private Map<Marker, Poi> markers = new HashMap<>();
    private BottomSheetBehavior<View> bottomLargeSheetBehavior;
    private ViewGroup llGotoPoi;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this, RetrofitModel.getInstance(), new Geocoder(this), new TokenRepository(this));
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

        FloatingActionButton fabPaperplane = (FloatingActionButton) findViewById(R.id.fabPaperplane);
        fabPaperplane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.createPoiClicked();
            }
        });

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.nsvMainActivity));
        bottomLargeSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.nsvLargeMainActivity));
        tvSelectedPlaceCity = (TextView) findViewById(R.id.tvMainPlaceDetailsCity);
        tvSelectedPlace = (TextView) findViewById(R.id.tvMainPlaceDetails);
        llGotoPoi = (ViewGroup) findViewById(R.id.ll_detailed_poi_gotto);
        llGotoPoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.createRouteClicked();
            }
        });

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
                presenter.onLocationSelected(latLng, false);
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.equals(customMarker)) {
                    bottomLargeSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    final int state = bottomSheetBehavior.getState();
                    if (state == BottomSheetBehavior.STATE_COLLAPSED ||
                            state == BottomSheetBehavior.STATE_SETTLING) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    final int state = bottomLargeSheetBehavior.getState();
                    if (state == BottomSheetBehavior.STATE_COLLAPSED || state == BottomSheetBehavior.STATE_SETTLING) {
                        presenter.getPoiDetails(markers.get(marker));
                        presenter.onLocationSelected(marker.getPosition(), true);
                    }
                }
                return true;
            }
        });

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this);

        initSearchView();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        ImageView userAvatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageViewAvatar);
        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(new TokenRepository(getApplicationContext()).getToken())) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                    getAccount();
                }
            }
        });

        final MenuItem myRoutesButton = navigationView.getMenu().getItem(MENU_MY_ROUTES_INDEX);
        myRoutesButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(MainActivity.this, MyRoutesActivity.class);
                startActivity(intent);
                return true;
            }
        });

        updateNavigationDrawerInfo();

        RecyclerView relatedPhotosRecyclerView = (RecyclerView) findViewById(R.id.relatedPhotosRecycler);
        final LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        relatedPhotosRecyclerView.setLayoutManager(layout);

        RecyclerRelatedPhotosAdapter recyclerRelatedPhotosAdapter = new RecyclerRelatedPhotosAdapter(MainActivity.this);
        relatedPhotosRecyclerView.setAdapter(recyclerRelatedPhotosAdapter);
    }

    public void updateNavigationDrawerInfo() {
        final MenuItem logOutButton = navigationView.getMenu().getItem(MENU_LOG_OUT_INDEX);

        final View nameContainer = navigationView.getHeaderView(0).findViewById(R.id.nameContainer);

        final TokenRepository tokenRepository = new TokenRepository(getApplicationContext());
        logOutButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                logOutButton.setVisible(false);
                nameContainer.setVisibility(View.GONE);
                tokenRepository.clearToken();
                return true;
            }
        });
        if (!TextUtils.isEmpty(tokenRepository.getToken())) {
            logOutButton.setVisible(true);
            nameContainer.setVisibility(View.VISIBLE);
        } else {
            logOutButton.setVisible(false);
            nameContainer.setVisibility(View.GONE);
        }
    }

    public void setDetailedPoiInfo(final Poi poi) {

        findViewById(R.id.fabChallenge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChallengeActivity.class);
                intent.putExtra(ExtraConstants.POI, new Gson().toJson(poi));
                startActivity(intent);
            }
        });
        TextView tvMainPlaceDetailsTitle = (TextView) findViewById(R.id.tvMainPlaceDetailsTitle);
        TextView tvMainPlaceDetailsAddress = (TextView) findViewById(R.id.tvMainPlaceDetailsAddress);
        final TextView detailedText = (TextView) findViewById(R.id.detailedText);

        tvMainPlaceDetailsTitle.setText(poi.getTitle());
        tvMainPlaceDetailsAddress.setText(poi.getAddress());

        final Spannable wordtoSpan = new SpannableString(poi.getContentPreview());
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 0, poi.getContentPreview().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        detailedText.setText(wordtoSpan);

        String readMore = " SHOW MORE";
        Spannable readMoreSpan = new SpannableString(readMore);
        readMoreSpan.setSpan(new ForegroundColorSpan(Color.BLUE), 0, readMore.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        detailedText.append(readMoreSpan);

        detailedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailedText.setText(poi.getContent());
            }
        });

        bottomLargeSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        View editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreatePoiActivity.class);
                intent.putExtra(ExtraConstants.SELECTED_LOCATION, new LatLng(poi.getLatitude(), poi.getLongitude()));
                intent.putExtra(ExtraConstants.TITLE, poi.getTitle());
                intent.putExtra(ExtraConstants.DESCRIPTION, poi.getContent());
                intent.putExtra(ExtraConstants.ID, poi.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    public void showCreatePoiScreen(LatLng selectedPlaceLocation) {
        Intent intent = new Intent(MainActivity.this, CreatePoiActivity.class);
        intent.putExtra(ExtraConstants.SELECTED_LOCATION, selectedPlaceLocation);
        startActivityForResult(intent, REQUEST_CREATE_POI);
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
        searchControler.setOnMenuClickListener(new SearchView.SearchMenuListener() {
            @Override
            public void onMenuClick() {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        searchControler.init();
    }

    String mEmail;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == RESULT_OK) {
                showProgress();

                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String token = "";
                        try {
                            token = GoogleAuthUtil.getToken(MainActivity.this, mEmail, SCOPE);
                            Log.d(TAG, "REQUEST_CODE_PICK_ACCOUNT: token: " + token + " mEmail: " + mEmail);
                            presenter.login(mEmail, token);
                        } catch (UserRecoverableAuthException e) {
                            startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
        if (requestCode == REQUEST_AUTHORIZATION) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extra = data.getExtras();
                String oneTimeToken = extra.getString("authtoken");
                Log.d(TAG, "REQUEST_AUTHORIZATION: token: " + oneTimeToken + " mEmail: " + mEmail);
                presenter.login(mEmail, oneTimeToken);
            } else {
                hideProgress();
            }
        }

        if (requestCode == REQUEST_CREATE_POI) {
            if (resultCode == Activity.RESULT_OK) {

            } else {
                hideProgress();
            }
        }
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
        markers.clear();
        searchView.hide(true);
        map.clear();
        if (pois != null) {
            for (Poi poi : pois) {
                final MarkerOptions snippet = new MarkerOptions()
                        .position(new LatLng(poi.getLatitude(), poi.getLongitude()))
                        .title(poi.getTitle())
                        .snippet(poi.getAddress())
                        .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(R.drawable.poi,
                                getResources().getDimensionPixelSize(R.dimen.marker_width),
                                getResources().getDimensionPixelSize(R.dimen.marker_height))));
                final Marker marker = map.addMarker(snippet);
                markers.put(marker, poi);
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

                bottomLargeSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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
    public void updateUserToken(LoginResponce loginResponce) {
        TokenRepository tokenRepository = new TokenRepository(getApplicationContext());
        tokenRepository.setToken(loginResponce.getAccess_token());
        presenter.getUser(loginResponce.getAccess_token());
    }

    @Override
    public void updateUserData(UserResponce loginResponce) {
        TextView points = (TextView) navigationView.getHeaderView(0).findViewById(R.id.points);
        TextView name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.name);
        View nameContainer = navigationView.getHeaderView(0).findViewById(R.id.nameContainer);

        if (loginResponce != null) {
            nameContainer.setVisibility(View.VISIBLE);
            points.setText(loginResponce.getPoints() + "\nPOINTS");
            name.setText(loginResponce.getName());
        }
        updateNavigationDrawerInfo();

        hideProgress();
    }

    @Override
    public void invalidToken(final String token) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GoogleAuthUtil.clearToken(getApplicationContext(), token);
                } catch (GoogleAuthException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

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
        hideProgress();
    }

    @Override
    public void onBackPressed() {

        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (customMarker != null) {
                customMarker.remove();
            }
            return;
        }

        if (bottomLargeSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomLargeSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }

        if (navigationView.isShown()) {
            drawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }

////        if (searchView != null && searchView.isSearchOpen()) {
//            searchView.hide(false);
////        } else {
        super.onBackPressed();
//        }
    }

    public Bitmap resizeMapIcons(int drawableId, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), drawableId);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

}
