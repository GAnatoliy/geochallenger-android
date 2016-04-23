package com.dev.geochallenger.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.geochallenger.R;
import com.dev.geochallenger.models.entities.routes.RouteResponse;
import com.dev.geochallenger.views.adapters.holders.RelatedPhotoHolder;

import java.util.List;

public class MyRoutesRecyclerAdapter extends RecyclerView.Adapter<RelatedPhotoHolder> {

    private Context context;

    public MyRoutesRecyclerAdapter(Context context, List<RouteResponse> routeResponses) {
        this.context = context;
    }

    @Override
    public RelatedPhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RelatedPhotoHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
