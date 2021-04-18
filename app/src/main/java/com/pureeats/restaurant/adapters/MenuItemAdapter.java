package com.pureeats.restaurant.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.pureeats.restaurant.databinding.ItemMenuBinding;
import com.pureeats.restaurant.models.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuItemAdapter extends ListAdapter<MenuItem, MenuItemAdapter.MenuItemViewHolder> implements Filterable {
    private static final String TAG = "MenuItemAdapter";
    ItemCategoryAdapter.ItemCategoryInterface itemCategoryInterface;
//    public MenuItemAdapter() {
//        super(MenuItem.itemCallback);
//    }

    /* If any Parent adapter use this adapter as ChildAdapter */
    public MenuItemAdapter(ItemCategoryAdapter.ItemCategoryInterface itemCategoryInterface){
        super(MenuItem.itemCallback);
        this.itemCategoryInterface = itemCategoryInterface;
    }

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemMenuBinding itemMenuBinding = ItemMenuBinding.inflate(layoutInflater, parent, false);
        return new MenuItemViewHolder(itemMenuBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
        MenuItem menuItem = getItem(position);
        holder.itemMenuBinding.setMenu(menuItem);

        holder.itemMenuBinding.activeSwitch.setOnClickListener(view -> {
            if(itemCategoryInterface != null){
                boolean isActive = ((SwitchCompat) view).isChecked();
                itemCategoryInterface.onSwitchClickListener(menuItem, isActive);
            }
        });

        holder.itemMenuBinding.menu.setOnClickListener(view -> {
            if(itemCategoryInterface != null){
                itemCategoryInterface.onMenuItemClickListener(menuItem);
            }
        });
    }

    @Override
    public Filter getFilter() {
        return filter;
    }


    Filter filter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            Log.d("", "sortType:  " + charSequence);
            List<MenuItem> filteredList = new ArrayList<>();



            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            Log.d(TAG, "FILTER_RESULTS: " + filterResults.values);
            //items = (List<Restaurant>) filterResults;
            submitList((List<MenuItem>) filterResults);
            //notifyDataSetChanged();
        }
    };



    static class MenuItemViewHolder extends RecyclerView.ViewHolder{
        ItemMenuBinding itemMenuBinding ;

        public MenuItemViewHolder(ItemMenuBinding binding) {
            super(binding.getRoot());
            this.itemMenuBinding = binding;
        }
    }

}
