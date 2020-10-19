package com.pureeats.restaurant.views.location;

import android.content.Context;
import android.location.Address;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.pureeats.restaurant.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;


public class PlaceAutoSuggestAdapter extends RecyclerView.Adapter<PlaceAutoSuggestAdapter.ViewHolder> {
    private final String TAG = this.getClass().getSimpleName();

    Context context;
    List<Address> models;

    public PlaceAutoSuggestAdapter(Context context, List<Address> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public PlaceAutoSuggestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PlaceAutoSuggestAdapter.ViewHolder holder, final int position) {
        try{
            Address address = models.get(position);
            Log.d(TAG, "--------------------------------------------------------");
            Log.d(TAG, "ADDRESS: "+address);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            holder.txt_title.setText(address.getAddressLine(0));
            //holder.txt_sub_title.setText(models.get(position).getRice_txt());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if(models == null) return 0;
        else return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_title, txt_sub_title;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_sub_title = itemView.findViewById(R.id.txt_sub_title);
        }
    }

    public void updateList(List<Address> results) {
        this.models = results;
        notifyDataSetChanged();
    }
}
