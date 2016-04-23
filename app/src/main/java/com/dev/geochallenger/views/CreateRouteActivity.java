package com.dev.geochallenger.views;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dev.geochallenger.R;
import com.dev.geochallenger.models.ExtraConstants;
import com.dev.geochallenger.models.RetrofitModel;
import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.cities.PlacesEntity;
import com.dev.geochallenger.models.entities.cities.Predictions;
import com.dev.geochallenger.models.repositories.TokenRepository;
import com.dev.geochallenger.presenters.CreateRoutePresenter;
import com.dev.geochallenger.views.adapters.CreateRouteSearchAdapter;
import com.dev.geochallenger.views.interfaces.ABaseActivityView;
import com.dev.geochallenger.views.interfaces.ICreateRouteView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateRouteActivity extends ABaseActivityView<CreateRoutePresenter> implements ICreateRouteView {

    private MapView mapView;
    private GoogleMap map;
    private Polyline currentRoute;
    private Location myLocation;
    private LatLng selectedLocation;
    private Address selectedAddress;
    private CreateRouteSearchAdapter autoCompleteFromAdapter;
    private List<Marker> markers = new ArrayList<>();
    private CreateRouteSearchAdapter autoCompleteToAdapter;
    private TextView tvDistance;
    private TextView tvPoisCount;
    private FloatingActionButton fabCreateRoute;
    private ViewGroup distanceBanner;
    private AutoCompleteTextView autoCompleteTextViewFrom;
    private AutoCompleteTextView autoCompleteTextViewTo;
    private Marker originMarker;
    private Marker destinationMarker;

    @Override
    protected CreateRoutePresenter createPresenter() {
        return new CreateRoutePresenter(this, RetrofitModel.getInstance(), selectedLocation, selectedAddress, myLocation, new TokenRepository(getApplicationContext()));
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);

        myLocation = (Location) getIntent().getParcelableExtra(ExtraConstants.MY_LOCATION);
        selectedLocation = (LatLng) getIntent().getParcelableExtra(ExtraConstants.SELECTED_LOCATION);
        selectedAddress = (Address) getIntent().getParcelableExtra(ExtraConstants.SELECTED_ADDRESS);

        tvDistance = (TextView)findViewById(R.id.tvCreateRouteDistance);
        tvPoisCount = (TextView) findViewById(R.id.tvCreateRouteItemsCount);
        fabCreateRoute = (FloatingActionButton)findViewById(R.id.fabCreateRoute);
        distanceBanner = (ViewGroup)findViewById(R.id.flCreateRouteBanner);

        fabCreateRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateRouteDialog();
            }
        });
        findViewById(R.id.iv_create_route_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) findViewById(R.id.mvCreatePath);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this);

        setSearchFrom();
    }

    private void showCreateRouteDialog() {

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        input.setLayoutParams(lp);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_create_route_title);
        builder.setMessage(R.string.dialog_create_route_message);
        builder.setView(input);
        builder.setPositiveButton(R.string.dialog_create_route_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!TextUtils.isEmpty(input.getText())) {
                    presenter.createRoute(input.getText().toString());
                }
            }
        });
        builder.setNegativeButton(R.string.dialog_create_route_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void setSearchFrom() {
        autoCompleteTextViewFrom = (AutoCompleteTextView) findViewById(R.id.svPathFrom);

        autoCompleteTextViewFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String newText = String.valueOf(s);
                if (newText.length() > 2) {
                    presenter.findPlaces(newText, getString(R.string.google_directions_key), true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        autoCompleteFromAdapter = new CreateRouteSearchAdapter(this, android.R.layout.simple_dropdown_item_1line,
                new ArrayList<Predictions>());
        autoCompleteTextViewFrom.setAdapter(autoCompleteFromAdapter);
        autoCompleteTextViewFrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CreateRouteSearchAdapter adapter = (CreateRouteSearchAdapter) parent.getAdapter();
                Predictions selectedPredictions = adapter.getItem(position);
                final String place_id = selectedPredictions.getPlace_id();
                presenter.getDetailedPlaceInfo(place_id, getString(R.string.google_directions_key), true);

            }
        });

        autoCompleteTextViewTo = (AutoCompleteTextView) findViewById(R.id.svPathTo);

        autoCompleteTextViewTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String newText = String.valueOf(s);
                if (newText.length() > 2) {
                    presenter.findPlaces(newText, getString(R.string.google_directions_key), false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        autoCompleteToAdapter = new CreateRouteSearchAdapter(this, android.R.layout.simple_dropdown_item_1line,
                new ArrayList<Predictions>());
        autoCompleteTextViewTo.setAdapter(autoCompleteToAdapter);
        autoCompleteTextViewTo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CreateRouteSearchAdapter adapter = (CreateRouteSearchAdapter) parent.getAdapter();
                Predictions selectedPredictions = adapter.getItem(position);
                final String place_id = selectedPredictions.getPlace_id();
                presenter.getDetailedPlaceInfo(place_id, getString(R.string.google_directions_key), false);

            }
        });
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
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int selectedPoisCount = presenter.toggleWaypoints(marker.getPosition());
                tvPoisCount.setText(getString(R.string.create_route_pois_count, selectedPoisCount));
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
    public void drawRouteInUiThread(final PolylineOptions lineOptions, final double distance) {
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

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LatLngBounds latLngBounds = map.getProjection().getVisibleRegion().latLngBounds;
                        presenter.getPoisByViewPort(latLngBounds.northeast.latitude, latLngBounds.southwest.longitude, latLngBounds.southwest.latitude, latLngBounds.northeast.longitude);
                    }
                }, 2000);

                tvDistance.setText(String.format("%.2f KM", distance));
                distanceBanner.setVisibility(View.VISIBLE);

                if (lineOptions != null) {
                    List<LatLng> points = lineOptions.getPoints();
                    if (points != null && points.size() > 1) {
                        setOriginMarker(points.get(0));
                        setDestinationMarker(points.get(points.size()-1));
                    }
                }
            }
        });
    }

    private void setOriginMarker(LatLng latLng) {
        if (originMarker != null) {
            originMarker.remove();
        }
        MarkerOptions snippet = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(R.drawable.my_pos_small, getDestinationMarkerSize(), getDestinationMarkerSize())));
        originMarker = map.addMarker(snippet);
    }

    private int getDestinationMarkerSize() {
        return getResources().getDimensionPixelSize(R.dimen.custom_marker_size);
    }

    public Bitmap resizeMapIcons(int drawableId,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),drawableId);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void setDestinationMarker(LatLng latLng) {
        if (destinationMarker != null) {
            destinationMarker.remove();
        }
        MarkerOptions snippet = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(R.drawable.dest_small, getDestinationMarkerSize(), getDestinationMarkerSize())));
        destinationMarker = map.addMarker(snippet);
    }

    private void moveToBounds(List<LatLng> points) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < points.size(); i++) {
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
    
    @Override
    public void populateAutocompeteList(boolean from, PlacesEntity placesEntity) {
        final List<Predictions> predictionses = Arrays.asList(placesEntity.getPredictions());
        if (from) {
            autoCompleteFromAdapter.setNewPredictions(predictionses);
            autoCompleteFromAdapter.notifyDataSetChanged();
        } else {
            autoCompleteToAdapter.setNewPredictions(predictionses);
            autoCompleteToAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showPois(List<Poi> pois) {
        removeOldMarkers();
        if (pois != null) {
            for (Poi poi : pois) {
                final MarkerOptions snippet = new MarkerOptions()
                        .position(new LatLng(poi.getLatitude(), poi.getLongitude()))
                        .title(poi.getTitle())
                        .snippet(poi.getAddress())
                        .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(poi.isWaypoint() ? R.drawable.added : R.drawable.poi,
                                getResources().getDimensionPixelSize(R.dimen.marker_width),
                                getResources().getDimensionPixelSize(R.dimen.marker_height))));
                markers.add(map.addMarker(snippet));
            }
        }
    }

    @Override
    public void setOrigin(String origin) {
        autoCompleteTextViewFrom.setText(origin);
    }

    @Override
    public void setDestination(String destination) {
        autoCompleteTextViewTo.setText(destination);
    }

    private void removeOldMarkers() {
        for(int i = 0; i < markers.size(); i++) {
            markers.get(i).remove();
        }
    }
}
