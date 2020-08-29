package com.example.mainactivity.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainactivity.databinding.ItemOrderAcceptBinding;
import com.example.mainactivity.models.Order;

public class OrderAcceptListAdapter extends ListAdapter<Order, OrderAcceptListAdapter.OrderViewHolder> {

    OrderAcceptInterface orderAcceptInterface;
    public OrderAcceptListAdapter(OrderAcceptInterface orderAcceptInterface) {
        super(Order.itemCallback);
        this.orderAcceptInterface = orderAcceptInterface;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemOrderAcceptBinding itemAcceptOrderBinding = ItemOrderAcceptBinding.inflate(layoutInflater, parent, false);
        return new OrderViewHolder(itemAcceptOrderBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = getItem(position);
        holder.itemAcceptOrderBinding.setOrder(order);
        holder.itemAcceptOrderBinding.executePendingBindings();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder{
        ItemOrderAcceptBinding itemAcceptOrderBinding ;

        public OrderViewHolder(ItemOrderAcceptBinding binding) {
            super(binding.getRoot());
            this.itemAcceptOrderBinding = binding;

            itemAcceptOrderBinding.minusTxt.setOnClickListener(view -> {
                orderAcceptInterface.onDecreasePreparationTime(getItem(getAdapterPosition()));
                notifyItemChanged(getAdapterPosition());
            });
            itemAcceptOrderBinding.plusText.setOnClickListener(view -> {
                orderAcceptInterface.onIncreasePreparationTime(getItem(getAdapterPosition()));
                notifyItemChanged(getAdapterPosition());
            });
            itemAcceptOrderBinding.txtReject.setOnClickListener(view -> {
                orderAcceptInterface.onRejectClick(getItem(getAdapterPosition()));
                notifyItemChanged(getAdapterPosition());
            });
            itemAcceptOrderBinding.txtAccept.setOnClickListener(view -> {
                orderAcceptInterface.onAcceptClick(getItem(getAdapterPosition()));
                notifyItemChanged(getAdapterPosition());
            });

        }
    }


    public interface OrderAcceptInterface {
        void onIncreasePreparationTime(Order order);
        void onDecreasePreparationTime(Order order);
        void onRejectClick(Order order);
        void onAcceptClick(Order order);
    }

}
