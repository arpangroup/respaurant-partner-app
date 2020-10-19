package com.pureeats.restaurant.adapters;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.pureeats.restaurant.commons.Constants;
import com.pureeats.restaurant.databinding.ItemOrderAcceptBinding;
import com.pureeats.restaurant.models.Order;
import com.pureeats.restaurant.util.FormatTime;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

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

        if(holder.countDownTimer != null){
            holder.countDownTimer.cancel();
        }
        if(holder.timer != null){
            holder.timer.cancel();
        }

        startAcceptCountDownTimer(holder, position);
        startProgressBar(holder);



    }

    class OrderViewHolder extends RecyclerView.ViewHolder{
        ItemOrderAcceptBinding itemAcceptOrderBinding ;
        CountDownTimer countDownTimer;
        Timer timer;

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
                orderAcceptInterface.onRejectClick(getItem(getAdapterPosition()), binding);
                notifyItemChanged(getAdapterPosition());
            });
            itemAcceptOrderBinding.layoutAccept.setOnClickListener(view -> {
                orderAcceptInterface.onAcceptClick(getItem(getAdapterPosition()), binding);
            });

        }
    }


    public interface OrderAcceptInterface {
        void onIncreasePreparationTime(Order order);
        void onDecreasePreparationTime(Order order);
        void onRejectClick(Order order, ItemOrderAcceptBinding binding);
        void onAcceptClick(Order order, ItemOrderAcceptBinding binding);
        void onAutoCancelOrder(Order order);
        void onDialogDismiss(List<Order> notAcceptedOrders);
    }


    private void startAcceptCountDownTimer(OrderViewHolder holder, int position){
        Order order = getItem(position);
        long currentTime = FormatTime.getCurrentTimeInLong();
        long orderTime =  FormatTime.getTimeFromDateString(order.getCreatedAt());
        long targetTime =  orderTime + Constants.ORDER_ACCEPT_WAITING_TIME;
        long remainingTime = targetTime - currentTime;


        final long[] mTimeLeftInMills = {remainingTime};
        //mBinding.txtResend.setEnabled(false);
        holder.countDownTimer = new CountDownTimer(mTimeLeftInMills[0], 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMills[0] = millisUntilFinished;
                updateCancelTimer(mTimeLeftInMills[0], holder);
            }

            @Override
            public void onFinish() {
                //mBinding.txtCounter.setVisibility(View.GONE);
                //mBinding.txtResend.setEnabled(true);
                disableAcceptButton(holder, position);

            }
        }.start();
    }
    private void startProgressBar(OrderViewHolder holder){
        final int[] counter = {0};
        holder.timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                counter[0]++;
                holder.itemAcceptOrderBinding.progressBarAccept.setProgress(counter[0]);

                if (counter[0] == Constants.ORDER_ACCEPT_WAITING_TIME){
                    holder.timer.cancel();
                }
            }
        };
        int period = Constants.ORDER_ACCEPT_WAITING_TIME / 100;
        holder.timer.schedule(timerTask, 0, period);//1% in every 100ms
    }
    private void updateCancelTimer(long mTimeLeftInMills, OrderViewHolder holder){
        int minutes = (int) (mTimeLeftInMills / 1000) /60;// divided by 60 seconds
        int seconds = (int) (mTimeLeftInMills / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%01d:%02d", minutes, seconds);
        holder.itemAcceptOrderBinding.txtCounter.setText("("+ timeLeftFormatted +")");

    }

    private void disableAcceptButton(OrderViewHolder holder, int position){
        holder.itemAcceptOrderBinding.progressBarAccept.setVisibility(View.GONE);
        holder.itemAcceptOrderBinding.layoutAccept.setBackgroundColor(Color.parseColor("#979DD59F"));
        holder.itemAcceptOrderBinding.layoutAccept.setEnabled(false);
        holder.itemAcceptOrderBinding.layoutAccept.setOnClickListener(null);
        holder.itemAcceptOrderBinding.plusText.setEnabled(false);
        holder.itemAcceptOrderBinding.minusTxt.setEnabled(false);
        holder.itemAcceptOrderBinding.txtReject.setEnabled(false);
        //orderAcceptInterface.onAutoCancelOrder(getItem(position));


    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }
}
