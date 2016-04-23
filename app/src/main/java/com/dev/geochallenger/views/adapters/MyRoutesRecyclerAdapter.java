package com.dev.geochallenger.views.adapters;

import android.content.Context;
import android.location.Address;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dev.geochallenger.R;
import com.dev.geochallenger.models.api.Geocoder;
import com.dev.geochallenger.models.entities.routes.RouteResponse;
import com.dev.geochallenger.models.interfaces.IGeocoder;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class MyRoutesRecyclerAdapter extends RecyclerView.Adapter<MyRoutesRecyclerAdapter.RouteViewHolder> {

    private Context context;
    private List<RouteResponse> routes;
    private IOnItemClickListener onItemClickListener;

    public MyRoutesRecyclerAdapter(Context context, List<RouteResponse> routeResponses) {
        this.context = context;
        routes = routeResponses;
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_route_item_layout, parent, false);
        return new RouteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RouteViewHolder holder, final int position) {
        RouteResponse route = routes.get(position);
        holder.tvDistance.setText(String.valueOf(route.getDistanceInMeters() * 0.001));
        holder.tvPois.setText(String.valueOf(route.getPois().size()));
        holder.itemView.setTag(position);
        holder.tvOrigin.setText("Origin");
        holder.tvDestination.setText("Destination");

        Geocoder originGeocoder = new Geocoder(context);
        originGeocoder.getAddress(new LatLng(route.getStartPointLatitude(), route.getStartPointLongitude()), new IGeocoder.IGeocoderListener() {
            @Override
            public void onAddressFetched(Address address) {
                if (holder.itemView.getTag() == position) {
                    if (address != null && address.getMaxAddressLineIndex() >= 0) {
                        String addressString = "";
                        int maxAddressLineIndex = address.getMaxAddressLineIndex();

                        addressString += maxAddressLineIndex >= 0 ? address.getAddressLine(0) : "";
                        if (!addressString.equals("")) {
                            addressString += maxAddressLineIndex >= 1 ? ", " + address.getAddressLine(1) : "";
                        } else {
                            addressString += maxAddressLineIndex >= 1 ? address.getAddressLine(1) : "";
                        }
                        if (!TextUtils.isEmpty(addressString)) {
                            holder.tvOrigin.setText(addressString);
                        }
                    }

                }
            }

            @Override
            public void onError(Exception e) {
            }
        });

        Geocoder destinationGeocoder = new Geocoder(context);
        destinationGeocoder.getAddress(new LatLng(route.getEndPointLatitude(), route.getEndPointLongitude()), new IGeocoder.IGeocoderListener() {
            @Override
            public void onAddressFetched(Address address) {
                if (holder.itemView.getTag() == position) {
                    if (address != null && address.getMaxAddressLineIndex() >= 0) {
                        String addressString = "";
                        int maxAddressLineIndex = address.getMaxAddressLineIndex();

                        addressString += maxAddressLineIndex >= 0 ? address.getAddressLine(0) : "";
                        if (!addressString.equals("")) {
                            addressString += maxAddressLineIndex >= 1 ? ", " + address.getAddressLine(1) : "";
                        } else {
                            addressString += maxAddressLineIndex >= 1 ? address.getAddressLine(1) : "";
                        }
                        if (!TextUtils.isEmpty(addressString)) {
                            holder.tvDestination.setText(addressString);
                        }
                    }
                }
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    public class RouteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvOrigin;
        public TextView tvDestination;
        public TextView tvPois;
        public TextView tvDistance;

        public RouteViewHolder(View itemView) {
            super(itemView);
            tvOrigin = (TextView) itemView.findViewById(R.id.tv_route_item_origin);
            tvDestination = (TextView) itemView.findViewById(R.id.tv_route_item_destination);
            tvPois = (TextView) itemView.findViewById(R.id.tv_route_item_pois);
            tvDistance = (TextView) itemView.findViewById(R.id.tv_route_item_distance);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(v, getAdapterPosition(), routes.get(getAdapterPosition()));
            }
        }
    }

    public void setOnItemClickListener(IOnItemClickListener onItemClickListener) {

        this.onItemClickListener = onItemClickListener;
    }

    public interface IOnItemClickListener {
        void onClick(View view, int position, RouteResponse route);
    }

}
