package com.example.mainactivity.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainactivity.databinding.ItemMenuBinding;
import com.example.mainactivity.models.MenuItem;

public class MenuItemAdapter extends ListAdapter<MenuItem, MenuItemAdapter.MenuItemViewHolder> {

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


    static class MenuItemViewHolder extends RecyclerView.ViewHolder{
        ItemMenuBinding itemMenuBinding ;

        public MenuItemViewHolder(ItemMenuBinding binding) {
            super(binding.getRoot());
            this.itemMenuBinding = binding;
        }
    }

}
