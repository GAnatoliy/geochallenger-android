package com.dev.geochallenger.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.dev.geochallenger.R;
import com.dev.geochallenger.models.ExtraConstants;
import com.dev.geochallenger.models.RetrofitModel;
import com.dev.geochallenger.models.api.Geocoder;
import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.cities.PlacesEntity;
import com.dev.geochallenger.models.entities.cities.Predictions;
import com.dev.geochallenger.models.entities.routes.RouteResponse;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

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
    private RouteResponse routeResponse;
    private BottomSheetBehavior<View> bottomLargeSheetBehavior;
    private View bottomSheet;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected CreateRoutePresenter createPresenter() {
        return new CreateRoutePresenter(this, RetrofitModel.getInstance(), selectedLocation, selectedAddress,
                myLocation, new TokenRepository(getApplicationContext()), routeResponse, new Geocoder(this));
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);

        myLocation = (Location) getIntent().getParcelableExtra(ExtraConstants.MY_LOCATION);
        selectedLocation = (LatLng) getIntent().getParcelableExtra(ExtraConstants.SELECTED_LOCATION);
        selectedAddress = (Address) getIntent().getParcelableExtra(ExtraConstants.SELECTED_ADDRESS);
        String routeString = getIntent().getStringExtra(ExtraConstants.ROUTE);
        if (!TextUtils.isEmpty(routeString)) {
            routeResponse = new Gson().fromJson(routeString, RouteResponse.class);
        }

        tvDistance = (TextView) findViewById(R.id.tvCreateRouteDistance);
        tvPoisCount = (TextView) findViewById(R.id.tvCreateRouteItemsCount);
        fabCreateRoute = (FloatingActionButton) findViewById(R.id.fabCreateRoute);
        distanceBanner = (ViewGroup) findViewById(R.id.flCreateRouteBanner);
        bottomSheet = findViewById(R.id.nsvPoiDetails);
        bottomLargeSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.clCreatePath);
        fabCreateRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String origin = autoCompleteTextViewFrom.getText().toString();
                String destination = autoCompleteTextViewTo.getText().toString();
                if (!TextUtils.isEmpty(origin) && !TextUtils.isEmpty(destination)) {
                    presenter.createRoute(origin + "|" + destination);
                }
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
                final int state = bottomLargeSheetBehavior.getState();
                if (state == BottomSheetBehavior.STATE_COLLAPSED || state == BottomSheetBehavior.STATE_SETTLING) {
                    presenter.getPoiDetails(marker.getPosition());
                }

                return true;
            }
        });
    }

    public void setDetailedPoiInfo(final Poi poi) {
        TextView tvMainPlaceDetailsTitle = (TextView) findViewById(R.id.tvMainPlaceDetailsTitle);
        TextView tvMainPlaceDetailsAddress = (TextView) findViewById(R.id.tvMainPlaceDetailsAddress);
        final TextView detailedText = (TextView) findViewById(R.id.detailedText);
        findViewById(R.id.ll_detailed_poi_gotto).setVisibility(View.GONE);
        FloatingActionButton addWaypoint = (FloatingActionButton) findViewById(R.id.fabAddWaypoint);
        addWaypoint.setImageResource(android.R.drawable.ic_input_add);
        addWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPoisCount = presenter.toggleWaypoints(poi);
                setSelectedPoisCount(selectedPoisCount);
                if (bottomLargeSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomLargeSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });


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
        editButton.setVisibility(View.GONE);
    }

    public void setSelectedPoisCount(int count) {
        tvPoisCount.setText(getString(R.string.create_route_pois_count, count));
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
                new Handler().postDelayed(new Runnable() {
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
                                setDestinationMarker(points.get(points.size() - 1));
                            }
                        }
                    }
                }, 1000);
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

    public Bitmap resizeMapIcons(int drawableId, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), drawableId);
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

    @Override
    public void showMyRoutes() {
        finish();
        startActivity(new Intent(this, MyRoutesActivity.class));
    }

    private void removeOldMarkers() {
        for (int i = 0; i < markers.size(); i++) {
            markers.get(i).remove();
        }
    }

    @Override
    public void onBackPressed() {
        if (bottomLargeSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomLargeSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }
        super.onBackPressed();
    }
}
