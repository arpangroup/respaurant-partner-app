package com.example.mainactivity.models;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class AccountSection {
    private int id;
    private String title;
    private String subTitle;
    private int image;

    public AccountSection(int id, String title, String subTitle, int image) {
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.image = image;
    }

    public static DiffUtil.ItemCallback<AccountSection> itemCallback = new DiffUtil.ItemCallback<AccountSection>() {
        @Override
        public boolean areItemsTheSame(@NonNull AccountSection oldItem, @NonNull AccountSection newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull AccountSection oldItem, @NonNull AccountSection newItem) {
            return oldItem.equals(newItem);
        }
    };


}
