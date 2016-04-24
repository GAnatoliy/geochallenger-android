package com.dev.geochallenger.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
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
import com.dev.geochallenger.models.RetrofitModel;
import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.repositories.TokenRepository;
import com.dev.geochallenger.presenters.ChallengePresenter;
import com.dev.geochallenger.views.interfaces.ABaseActivityView;
import com.dev.geochallenger.views.interfaces.IChallengeView;
import com.google.gson.Gson;

public class ChallengeActivity extends ABaseActivityView<ChallengePresenter> implements IChallengeView {

    public static final int ACCEPT_RANGE = 1000;
    private Poi poi;

    private ViewGroup checkinCell;
    private ViewGroup checkinComplete;
    private TextView poiTitle;
    private TextView poiAddress;
    private LocationListener locationListener;
    private LocationManager locationManager;

    @Override
    protected ChallengePresenter createPresenter() {
        return new ChallengePresenter(this, RetrofitModel.getInstance(), poi, new TokenRepository(getApplicationContext()));
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
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_COARSE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); //

        String provider = locationManager.getBestProvider(criteria, true);
        if (TextUtils.isEmpty(provider)) {
            showErrorMessage("Error", "Please enable GPS for checkin to proceed");
        } else {
            showProgress();
/*
            Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
            if (lastKnownLocation == null) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (lastKnownLocation != null) {
*/
/*
                hideProgress();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    if (lastKnownLocation.isFromMockProvider()) {
                        showErrorMessage("Error", "Stop cheating! Don't use mock providers !!!");
                        return;
                    }
                }
                Location poiLocation = new Location("");
                poiLocation.setLatitude(poi.getLatitude());
                poiLocation.setLongitude(poi.getLongitude());
                float range = lastKnownLocation.distanceTo(poiLocation);
                if (range <= ACCEPT_RANGE) {
                    presenter.checkin();
                } else {
                    showErrorMessage("Error", "You are too far from this POI...");
                }
*/

            //} else {
                locationListener = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        hideProgress();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            if (location.isFromMockProvider()) {
                                showErrorMessage("Error", "Stop cheating! Don't use mock providers !!!");
                                locationManager.removeUpdates(this);
                                locationListener = null;
                                return;
                            }
                        }
                        Location poiLocation = new Location("");
                        poiLocation.setLatitude(poi.getLatitude());
                        poiLocation.setLongitude(poi.getLongitude());
                        float range = location.distanceTo(poiLocation);
                        if (range <= ACCEPT_RANGE) {
                            presenter.checkin();
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


                locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
            //}
        }
    }

    @Override
    public void checkingSuccess() {
        checkinCell.setVisibility(View.GONE);
        checkinComplete.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (locationListener != null && locationListener != null) {
                        locationManager.removeUpdates(locationListener);
                        progressDialog = null;
                    }
                }
            });
            progressDialog.show();
        }
    }
}
