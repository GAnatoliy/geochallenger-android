package com.dev.geochallenger.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.geochallenger.R;
import com.dev.geochallenger.views.adapters.holders.RelatedPhotoHolder;

public class RecyclerRelatedPhotosAdapter extends RecyclerView.Adapter<RelatedPhotoHolder> {

    private Context context;

    public RecyclerRelatedPhotosAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RelatedPhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View inflate = LayoutInflater.from(context).inflate(R.layout.related_photo_item_layout, parent, false);
        return new RelatedPhotoHolder(inflate);
    }

    @Override
    public void onBindViewHolder(RelatedPhotoHolder holder, int position) {
        holder.itemView.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
