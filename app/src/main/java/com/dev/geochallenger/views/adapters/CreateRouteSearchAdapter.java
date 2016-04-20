package com.dev.geochallenger.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.dev.geochallenger.models.entities.cities.Predictions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a_dibrivnyj on 4/20/16.
 */
public class CreateRouteSearchAdapter extends ArrayAdapter<Predictions> {
    private LayoutInflater layoutInflater;
    List<Predictions> predictionses;

    public void setNewPredictions(List<Predictions> newPredictions) {
        predictionses.clear();
        predictionses.addAll(newPredictions);
    }

    private Filter mFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((Predictions) resultValue).getDescription();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null) {
                ArrayList<Predictions> suggestions = new ArrayList<>();
                for (Predictions customer : predictionses) {
                    // Note: change the "contains" to "startsWith" if you only want starting matches
                    if (customer.getDescription().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(customer);
                    }
                }

                results.values = suggestions;
                results.count = suggestions.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results != null && results.count > 0) {
                // we have filtered results
                addAll((ArrayList<Predictions>) results.values);
            } else {
                // no filter, add entire original list back in
                addAll(predictionses);
            }
            notifyDataSetChanged();
        }
    };

    public CreateRouteSearchAdapter(Context context, int textViewResourceId, List<Predictions> customers) {
        super(context, textViewResourceId, customers);
        // copy all the customers into a master list
        predictionses = new ArrayList<>(customers.size());
        predictionses.addAll(customers);
        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(android.R.layout.simple_dropdown_item_1line, null);
        }

        Predictions predictions = getItem(position);

        TextView name = (TextView) view.findViewById(android.R.id.text1);
        name.setText(predictions.getDescription());

        return view;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }
}