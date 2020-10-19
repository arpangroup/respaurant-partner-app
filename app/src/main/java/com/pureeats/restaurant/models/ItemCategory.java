package com.pureeats.restaurant.models;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.pureeats.restaurant.adapters.ItemCategoryAdapter;
import com.pureeats.restaurant.adapters.MenuItemAdapter;
import com.google.gson.annotations.SerializedName;

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

    //public static ItemCategoryAdapter.ItemCategoryInterface onClickInterface;

    @BindingAdapter(value = {"setMenus", "setParent"}, requireAll = false)
    public static void setMenus(RecyclerView recyclerView, List<MenuItem> menuItems, ItemCategoryAdapter.ItemCategoryInterface onClickInterface){
        if(menuItems != null){
            MenuItemAdapter menuItemAdapter = new MenuItemAdapter(onClickInterface);
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
