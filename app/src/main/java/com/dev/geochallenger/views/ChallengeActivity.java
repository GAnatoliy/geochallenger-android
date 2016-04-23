package com.dev.geochallenger.views;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dev.geochallenger.R;
import com.dev.geochallenger.models.ExtraConstants;
import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.presenters.ChallengePresenter;
import com.dev.geochallenger.views.interfaces.ABaseActivityView;
import com.dev.geochallenger.views.interfaces.IChallengeView;
import com.google.gson.Gson;

public class ChallengeActivity extends ABaseActivityView<ChallengePresenter> implements IChallengeView {

    public static final int ACCEPT_RANGE = 5000;
    private Poi poi;

    private ViewGroup checkinCell;
    private ViewGroup checkinComplete;
    private TextView poiTitle;
    private TextView poiAddress;

    @Override
    protected ChallengePresenter createPresenter() {
        return new ChallengePresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_challenge;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        String poiExtra = getIntent().getStringExtra(ExtraConstants.POI);
        if (!TextUtils.isEmpty(poiExtra)) {
            poi = new Gson().fromJson(poiExtra, Poi.class);
        }
        if (poi == null) {
            finish();
        }
        findViewById(R.id.btn_checkin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocation();
            }
        });

        checkinCell = (ViewGroup) findViewById(R.id.rl_challenge_checkin);
        checkinComplete = (ViewGroup) findViewById(R.id.fl_challenge_checkin_complete);

        poiTitle = (TextView) findViewById(R.id.tv_challenge_title);
        poiAddress = (TextView) findViewById(R.id.tv_challenge_address);

        poiTitle.setText(poi.getTitle());

        poiAddress.setText(poi.getAddress());


    }

    private void checkLocation() {
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showErrorMessage("Error", "Please enable GPS for checkin to proceed");
        } else {
            showProgress();
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    hideProgress();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        if (location.isFromMockProvider()) {
                            showErrorMessage("Error", "Stop cheating! Don't use mock providers !!!");
                            locationManager.removeUpdates(this);
                            return;
                        }
                    }
                    Location poiLocation = new Location("");
                    poiLocation.setLatitude(poi.getLatitude());
                    poiLocation.setLongitude(poi.getLongitude());
                    float range = location.distanceTo(poiLocation);
                    if (range <= ACCEPT_RANGE) {
                        checkinCell.setVisibility(View.GONE);
                        checkinComplete.setVisibility(View.VISIBLE);
                    } else {
                        showErrorMessage("Error", "You are too far from this POI...");
                    }

                    locationManager.removeUpdates(this);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }
}
