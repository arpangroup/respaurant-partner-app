package com.pureeats.restaurant.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.pureeats.restaurant.databinding.ItemHistoryBinding;
import com.pureeats.restaurant.databinding.ItemOrderAcceptBinding;
import com.pureeats.restaurant.databinding.ItemOrderPreparingBinding;
import com.pureeats.restaurant.models.Order;

public class OrderHistoryAdapter extends ListAdapter<Order, OrderHistoryAdapter.OrderViewHolder> {
    OrderHistoryInterface orderHistoryInterface;


    public OrderHistoryAdapter(OrderHistoryInterface orderHistoryInterface) {
        super(Order.itemCallback);
        this.orderHistoryInterface = orderHistoryInterface;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemHistoryBinding itemBinding = ItemHistoryBinding.inflate(layoutInflater, parent, false);
        //itemAcceptOrderBinding.setOrderInterface(orderInterface);
        return new OrderViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = getItem(position);
        holder.itemBinding.setOrder(order);

        holder.itemBinding.executePendingBindings();




    }

    class OrderViewHolder extends RecyclerView.ViewHolder{
        ItemHistoryBinding itemBinding ;

        public OrderViewHolder(ItemHistoryBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;

            itemBinding.getRoot().setOnClickListener(view -> {
                orderHistoryInterface.onOrderHistoryClick(getItem(getAdapterPosition()));
            });
        }
    }
    public interface OrderHistoryInterface {
        void onOrderHistoryClick(Order order);
    }

}
