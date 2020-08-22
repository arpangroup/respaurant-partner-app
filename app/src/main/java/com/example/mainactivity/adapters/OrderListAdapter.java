package com.example.mainactivity.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainactivity.databinding.ItemAcceptOrderBinding;
import com.example.mainactivity.models.Order;

public class OrderListAdapter extends ListAdapter<Order, OrderListAdapter.OrderViewHolder> {

    public OrderListAdapter() {
        super(Order.itemCallback);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemAcceptOrderBinding itemAcceptOrderBinding = ItemAcceptOrderBinding.inflate(layoutInflater, parent, false);
        return new OrderViewHolder(itemAcceptOrderBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {

    }

    class OrderViewHolder extends RecyclerView.ViewHolder{
        ItemAcceptOrderBinding itemAcceptOrderBinding ;

        public OrderViewHolder(ItemAcceptOrderBinding binding) {
            super(binding.getRoot());
            this.itemAcceptOrderBinding = binding;
        }
    }


    public interface OrderInterface{
        void addItem();
        void onItemClicked(Order order);
    }

}
