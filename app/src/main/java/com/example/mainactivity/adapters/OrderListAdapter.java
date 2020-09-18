package com.example.mainactivity.adapters;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainactivity.commons.Constants;
import com.example.mainactivity.databinding.ItemOrderPreparingBinding;
import com.example.mainactivity.models.Order;
import com.example.mainactivity.util.FormatDate;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

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

        if(order.getOrderStatusId() == 3 || order.getOrderStatusId() == 4){
            Picasso.get().load(Constants.WEBSITE_URL + order.getDeliveryDetails().getPhoto()).into(holder.itemOrderPreparingBinding.imgDeliveryGuy);
        }

        TimeZone.setDefault(TimeZone.getTimeZone("GMT+05:30"));
        long currentTime = new Date().getTime();
        long orderTime = FormatDate.getTimeFromDateString(order.getCreatedAt());
        long targetTime = orderTime + Constants.ORDER_READY_WAITING_TIME;
        long elapsedTime = currentTime - orderTime;
        long remainingTime = targetTime - currentTime;
//        Log.d("TIME", "........................................\n");
//        Log.d("TIME", "CURRENT_TIME: "+new Date());
//        Log.d("TIME", "ORDER_TIME: "+order.getCreatedAt());
//        Log.d("TIME", "--------------------");
//        Log.d("TIME", "CURRENT_TIME: "+currentTime);
//        Log.d("TIME", "ORDER_TIME: "+orderTime);
//        Log.d("TIME", "TARGET_TIME: "+targetTime);
//        Log.d("TIME", "ELAPSED_TIME: "+elapsedTime);
//        Log.d("TIME", "REMAINING_TIME: "+remainingTime);
//        Log.d("TIME", "\n........................................");

        if(remainingTime >0){
            startAcceptCountDownTimer(holder, position, remainingTime);
            startProgressBar(holder);
        }else{
            holder.itemOrderPreparingBinding.txtCounter.setText("(0:00)");
        }




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

            //Order order =getItem(getAdapterPosition());
            this.itemOrderPreparingBinding.layoutReady.setOnClickListener(view -> orderPrepareInterface.onOrderReady(getAdapterPosition(), getItem(getAdapterPosition()).getId()));
        }
    }


    public interface OrderPrepareInterface{
        void onAutoCancelOrder(Order order);
        void onOrderReady(int position, int orderId);
    }

    public void updateStatus(int position, int newStatus){
        getItem(position).setOrderStatusId(newStatus);
        notifyItemChanged(position);
    }


    private void startAcceptCountDownTimer(OrderListAdapter.OrderViewHolder holder, int position, long remainingTime){
        final long[] mTimeLeftInMills = {remainingTime};
        //mBinding.txtResend.setEnabled(false);
        new CountDownTimer(mTimeLeftInMills[0], 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMills[0] = millisUntilFinished;
                updateCancelTimer(mTimeLeftInMills[0], holder);
            }

            @Override
            public void onFinish() {
                //mBinding.txtCounter.setVisibility(View.GONE);
                //mBinding.txtResend.setEnabled(true);
                disableOrderReadyButton(holder, position);
            }
        }.start();
    }
    private void startProgressBar(OrderListAdapter.OrderViewHolder holder){
        final int[] counter = {0};
        Timer t = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                counter[0]++;
                holder.itemOrderPreparingBinding.progressBarOrderReady.setProgress(counter[0]);

                if (counter[0] == Constants.ORDER_READY_WAITING_TIME){
                    t.cancel();
                }
            }
        };
        int period = Constants.ORDER_READY_WAITING_TIME / 100;
        t.schedule(timerTask, 0, period);//1% in every 100ms
    }
    private void updateCancelTimer(long mTimeLeftInMills, OrderListAdapter.OrderViewHolder holder){
        int minutes = (int) (mTimeLeftInMills / 1000) /60;// divided by 60 seconds
        int seconds = (int) (mTimeLeftInMills / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%01d:%02d", minutes, seconds);
        holder.itemOrderPreparingBinding.txtCounter.setText("("+ timeLeftFormatted +")");

    }

    private void disableOrderReadyButton(OrderListAdapter.OrderViewHolder holder, int position){

    }

}
