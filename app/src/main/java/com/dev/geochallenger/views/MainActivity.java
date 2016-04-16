package com.dev.geochallenger.views;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.geochallenger.R;
import com.dev.geochallenger.models.RetrofitModel;
import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.cities.PlacesEntity;
import com.dev.geochallenger.models.entities.cities.Predictions;
import com.dev.geochallenger.presenters.MainPresenter;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.lapism.searchview.adapter.SearchAdapter;
import com.lapism.searchview.adapter.SearchItem;
import com.lapism.searchview.view.SearchCodes;
import com.lapism.searchview.view.SearchView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ABaseActivityView<MainPresenter> implements IMainView, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 1;
    private static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    private MapView mapView;
    private GoogleMap map;
    private SearchView searchView;
    private List<SearchItem> mSuggestionsList;
    private String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    private PlacesEntity placesEntity;

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter(this, RetrofitModel.getInstance());
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
                startActivity(new Intent(MainActivity.this, CreateRouteActivity.class));
            }
        });

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) findViewById(R.id.mvFeed);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

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
        searchView.setVersion(SearchCodes.VERSION_TOOLBAR);
        searchView.setStyle(SearchCodes.STYLE_TOOLBAR_CLASSIC);
        searchView.setTheme(SearchCodes.THEME_LIGHT);
        searchView.setHint("Search");
        searchView.setVoice(false);
        searchView.setOnSearchMenuListener(new SearchView.SearchMenuListener() {
            @Override
            public void onMenuClick() {
                Toast.makeText(getApplicationContext(), "menu", Toast.LENGTH_SHORT).show();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                presenter.findPlaces(newText, getString(R.string.google_directions_key));
                return true;
            }
        });

        mSuggestionsList = new ArrayList<>();

        List<SearchItem> mResultsList = new ArrayList<>();
        SearchAdapter mSearchAdapter = new SearchAdapter(this, mResultsList, mSuggestionsList, SearchCodes.THEME_LIGHT);
        mSearchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView textView = (TextView) view.findViewById(R.id.textView_item_text);
                final Predictions predictions = placesEntity.getPredictions()[position];
                final String place_id = predictions.getPlace_id();
                presenter.getDetailedPlaceInfo(place_id, getString(R.string.google_directions_key));

            }
        });

        searchView.setAdapter(mSearchAdapter);
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
    public String getErrorTitle(Exception exception) {
        //TODO
        return null;
    }

    @Override
    public void initMap(List<Poi> pois) {

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
        mSuggestionsList.clear();
        for (Predictions predictions : placesEntity.getPredictions()) {
            mSuggestionsList.add(new SearchItem(predictions.getDescription()));
        }

    }

    @Override
    public void setMapLocation(LatLng latLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
