package com.dev.geochallenger.models.api;

import android.content.Context;
import android.location.Address;
import android.os.Handler;

import com.dev.geochallenger.models.interfaces.IGeocoder;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

/**
 * Created by Yuriy Diachenko on 19.04.2016.
 */
public class Geocoder implements IGeocoder {

    private Context context;

    public Geocoder(Context context) {
        this.context = context;
    }

    @Override
    public void getAddress(final LatLng location, final IGeocoderListener listener) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    android.location.Geocoder geocoder = new android.location.Geocoder(context, Locale.getDefault());
                    final List<Address> addresses = geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            // In this sample, get just a single address.
                            1);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (addresses != null && addresses.size() > 0) {
                                listener.onAddressFetched(addresses.get(0));
                            } else {
                                listener.onError(new Exception("No results found"));
                            }
                        }
                    });
                } catch (final Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(e);
                        }
                    });
                }
            }
        }).start();
    }
}
