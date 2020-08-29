package com.example.mainactivity.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainactivity.databinding.ItemOrderPreparingBinding;
import com.example.mainactivity.models.Order;

public class OrderListAdapter extends ListAdapter<Order, OrderListAdapter.OrderViewHolder> {

    OrderPrepareInterface orderPrepareInterface;
    public OrderListAdapter(OrderPrepareInterface orderPrepareInterface) {
        super(Order.itemCallback);
        this.orderPrepareInterface = orderPrepareInterface;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemOrderPreparingBinding itemOrderPreparingBinding = ItemOrderPreparingBinding.inflate(layoutInflater, parent, false);
        //itemAcceptOrderBinding.setOrderInterface(orderInterface);
        return new OrderViewHolder(itemOrderPreparingBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = getItem(position);
        holder.itemOrderPreparingBinding.setOrder(order);
        holder.itemOrderPreparingBinding.setOrder(order);

        holder.itemOrderPreparingBinding.executePendingBindings();


        holder.itemOrderPreparingBinding.toggleOrderTotal.setOnClickListener(view -> {
            if(order.getToggle() == 0)order.setToggle(1);
            else if(order.getToggle() == 1)order.setToggle(0);
            holder.itemOrderPreparingBinding.setOrder(order);
        });
    }

    class OrderViewHolder extends RecyclerView.ViewHolder{
        ItemOrderPreparingBinding itemOrderPreparingBinding ;

        public OrderViewHolder(ItemOrderPreparingBinding binding) {
            super(binding.getRoot());
            this.itemOrderPreparingBinding = binding;
        }



    }


    public interface OrderPrepareInterface{
    }

}
