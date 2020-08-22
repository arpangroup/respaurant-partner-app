package com.example.mainactivity.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainactivity.databinding.ItemAcceptOrderBinding;
import com.example.mainactivity.databinding.ItemDish1Binding;
import com.example.mainactivity.models.Dish;
import com.example.mainactivity.models.Order;

public class DishListAdapter extends ListAdapter<Dish, DishListAdapter.DishViewHolder> {

    public DishListAdapter() {
        super(Dish.itemCallback);
    }

    @NonNull
    @Override
    public DishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemDish1Binding itemDish1Binding = ItemDish1Binding.inflate(layoutInflater, parent, false);
        return new DishViewHolder(itemDish1Binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DishViewHolder holder, int position) {
        Dish dish = getItem(position);
        holder.itemDish1Binding.setDish(dish);
    }

    class DishViewHolder extends RecyclerView.ViewHolder{
        ItemDish1Binding itemDish1Binding ;

        public DishViewHolder(ItemDish1Binding binding) {
            super(binding.getRoot());
            this.itemDish1Binding = binding;
        }
    }


}
