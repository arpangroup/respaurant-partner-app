package com.example.mainactivity.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainactivity.databinding.ItemGroupBinding;
import com.example.mainactivity.models.ItemCategory;
import com.example.mainactivity.models.MenuItem;

public class ItemCategoryAdapter extends ListAdapter<ItemCategory, ItemCategoryAdapter.ItemGroupViewHolder> {

    ItemCategoryInterface itemCategoryInterface;
    MenuItemAdapter menuItemAdapter;
    public ItemCategoryAdapter(ItemCategoryInterface itemCategoryInterface) {
        super(ItemCategory.itemCallback);
        this.itemCategoryInterface = itemCategoryInterface;
        menuItemAdapter = new MenuItemAdapter(itemCategoryInterface);
    }
    public ItemCategoryAdapter() {
        super(ItemCategory.itemCallback);
    }

    @NonNull
    @Override
    public ItemGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemGroupBinding itemGroupBinding = ItemGroupBinding.inflate(layoutInflater, parent, false);
        return new ItemGroupViewHolder(itemGroupBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemGroupViewHolder holder, int position) {
        ItemCategory category = getItem(position);
        holder.itemGroupBinding.setCategory(category);
        holder.itemGroupBinding.setParentInterface(itemCategoryInterface);
        //holder.itemGroupBinding.dishRecycler.setAdapter(menuItemAdapter);
        //menuItemAdapter.submitList(category.getMenuItems());
        //menuItemAdapter.notifyDataSetChanged();

        holder.itemGroupBinding.executePendingBindings();


        holder.itemGroupBinding.toggle.setOnClickListener(view -> {
            if(category.getToggle() == 0)category.setToggle(1);
            else if(category.getToggle() == 1)category.setToggle(0);
            holder.itemGroupBinding.setCategory(category);
        });
    }

    class ItemGroupViewHolder extends RecyclerView.ViewHolder{
        ItemGroupBinding itemGroupBinding ;

        public ItemGroupViewHolder(ItemGroupBinding binding) {
            super(binding.getRoot());
            this.itemGroupBinding = binding;

            this.itemGroupBinding.btnEdit.setOnClickListener(view -> itemCategoryInterface.onEditCategoryListener(getItem(getAdapterPosition())));
        }
    }


    public interface ItemCategoryInterface {
        void onMenuItemClickListener(MenuItem menuItem);
        void onSwitchClickListener(MenuItem menuItem, boolean isActive);
        void onEditCategoryListener(ItemCategory itemCategory);
    }

}
