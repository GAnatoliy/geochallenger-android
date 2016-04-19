package com.dev.geochallenger.models.interfaces;

import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Yuriy Diachenko on 19.04.2016.
 */
public interface IGeocoder {

    public void getAddress(LatLng location, IGeocoderListener callback);

    public static interface IGeocoderListener {

        void onAddressFetched(Address address);
        void onError(Exception e);
    }
}
