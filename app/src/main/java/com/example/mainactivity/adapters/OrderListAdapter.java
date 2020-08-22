package com.example.mainactivity.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainactivity.databinding.ItemAcceptOrderBinding;
import com.example.mainactivity.models.Order;

public class OrderListAdapter extends ListAdapter<Order, OrderListAdapter.OrderViewHolder> {

    OrderInterface orderInterface;
    public OrderListAdapter(OrderInterface orderInterface) {
        super(Order.itemCallback);
        this.orderInterface = orderInterface;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemAcceptOrderBinding itemAcceptOrderBinding = ItemAcceptOrderBinding.inflate(layoutInflater, parent, false);
        itemAcceptOrderBinding.setOrderInterface(orderInterface);
        return new OrderViewHolder(itemAcceptOrderBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = getItem(position);
        holder.itemAcceptOrderBinding.setOrder(order);
        holder.itemAcceptOrderBinding.executePendingBindings();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder{
        ItemAcceptOrderBinding itemAcceptOrderBinding ;

        public OrderViewHolder(ItemAcceptOrderBinding binding) {
            super(binding.getRoot());
            this.itemAcceptOrderBinding = binding;
        }
    }


    public interface OrderInterface{
        void onIncreasePreparationTime(Order order);
        void onDecreasePreparationTime(Order order);
        void onRejectClick(Order order);
        void onAcceptClick(Order order);
    }

}
