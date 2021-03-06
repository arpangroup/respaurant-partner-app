package com.pureeats.restaurant.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.pureeats.restaurant.databinding.Cart2Binding;
import com.pureeats.restaurant.models.AccountSection;

public class AccountSectionAdapter extends ListAdapter<AccountSection, AccountSectionAdapter.AccountViewHolder> {

    AccountSectionInterface accountSectionInterface;
    public AccountSectionAdapter(AccountSectionInterface accountSectionInterface) {
        super(AccountSection.itemCallback);
        this.accountSectionInterface = accountSectionInterface;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        Cart2Binding cartBinding = Cart2Binding.inflate(layoutInflater, parent, false);
        return new AccountViewHolder(cartBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        AccountSection accountSection = getItem(position);
        holder.cartBinding.setSection(accountSection);
        holder.cartBinding.image.setImageResource(getItem(position).getImage());

        if(getItem(position).getId() == 5){// logout
            holder.cartBinding.image.setColorFilter(Color.RED);
        }
    }

    class AccountViewHolder extends RecyclerView.ViewHolder{
        Cart2Binding cartBinding ;

        public AccountViewHolder(Cart2Binding binding) {
            super(binding.getRoot());
            this.cartBinding = binding;

            this.itemView.setOnClickListener(view -> accountSectionInterface.onItemAccountSectionItemClick(getItem(getAdapterPosition())));
        }
    }


    public interface AccountSectionInterface {
        void onItemAccountSectionItemClick(AccountSection accountSection);
    }

}
