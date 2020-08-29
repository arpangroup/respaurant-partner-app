package com.example.mainactivity.models;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainactivity.adapters.DishListAdapter;
import com.example.mainactivity.adapters.ItemCategoryAdapter;
import com.example.mainactivity.adapters.MenuItemAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ItemCategory {
    private int id;
    private String name;
    @SerializedName("is_enabled")
    private int isEnabled;
    private List<MenuItem> menuItems;

    private int toggle = 1;

    public ItemCategory(int id, String name, int isEnabled, List<MenuItem> menuItems) {
        this.id = id;
        this.name = name;
        this.isEnabled = isEnabled;
        this.menuItems = menuItems;
    }


    @BindingAdapter(value = "setMenus")
    public static void setMenus(RecyclerView recyclerView, List<MenuItem> menuItems){
        if(menuItems != null){
            MenuItemAdapter menuItemAdapter = new MenuItemAdapter();
            menuItemAdapter.submitList(menuItems);
            recyclerView.setAdapter(menuItemAdapter);
        }
    }



    public static DiffUtil.ItemCallback<ItemCategory> itemCallback = new DiffUtil.ItemCallback<ItemCategory>() {
        @Override
        public boolean areItemsTheSame(@NonNull ItemCategory oldItem, @NonNull ItemCategory newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ItemCategory oldItem, @NonNull ItemCategory newItem) {
            return false;
        }
    };
}
