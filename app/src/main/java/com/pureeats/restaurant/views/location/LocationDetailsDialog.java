package com.pureeats.restaurant.views.location;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.pureeats.restaurant.R;
import com.pureeats.restaurant.commons.LocationDetailsDialogListener;
import com.pureeats.restaurant.databinding.ActivityLocationBinding;
import com.pureeats.restaurant.models.Address;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

public class LocationDetailsDialog extends BottomSheetDialogFragment {
    private final String TAG = this.getClass().getSimpleName();
    private LocationDetailsDialogListener mListener;

    private Address mAddress = null;
    private static final String INPUT_STRING_ADDRESS = "address";
    static LocationDetailsDialog newInstance(Address address){
        LocationDetailsDialog fragment = new LocationDetailsDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString(INPUT_STRING_ADDRESS, new Gson().toJson(address));
        fragment.setArguments(args);
        return fragment;
    }

    private AppBarLayout appBarLayout;
    private LinearLayout linearLayout;

    ImageButton btnClose;
    TextView address_title, full_address;
    TextInputEditText etCompleteAddress, etFloor, etHowToReach;
    Button btnChangeAddress;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        final View rootView = View.inflate(getContext(), R.layout.layout_location_details, null);
        dialog.setContentView(rootView);
        BottomSheetBehavior bottomSheetBehavior  =  BottomSheetBehavior.from((View)rootView.getParent());
        bottomSheetBehavior.setDraggable(false);

        try{
            String addressJson = getArguments().getString(INPUT_STRING_ADDRESS);
            mAddress = new Gson().fromJson(addressJson, Address.class);
            Log.d(TAG, "ADDRESS: "+mAddress);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog);
        }

        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_location_details, container, false);
        init(rootView);

        btnClose.setOnClickListener(view -> dismiss());
        btnChangeAddress.setOnClickListener(view -> {
            //dismiss();
            LocationSearchDialog searchDialog = new LocationSearchDialog();
            searchDialog.show(requireActivity().getSupportFragmentManager(), "LOCATION_SEARCH_DIALOG");
        });

        return rootView;
    }

    private void init(View rootView){
        address_title = rootView.findViewById(R.id.address_title);
        full_address = rootView.findViewById(R.id.full_address);
        btnClose = rootView.findViewById(R.id.btnClose);
        etCompleteAddress = rootView.findViewById(R.id.etCompleteAddress);
        etFloor = rootView.findViewById(R.id.etFloor);
        etHowToReach = rootView.findViewById(R.id.etHowToReach);
        btnChangeAddress = rootView.findViewById(R.id.btnChangeAddress);

        if(mAddress != null){
            address_title.setText(mAddress.getAddressTitle());
            full_address.setText(mAddress.getAddress());
        }



        //initHints:
        etCompleteAddress.setOnFocusChangeListener((view, b) -> {
            if(b)etCompleteAddress.setHint(R.string.complete_address_hint) ;
            else etCompleteAddress.setHint(null);
        });
        etFloor.setOnFocusChangeListener((view, b) -> {
            if(b)etFloor.setHint(R.string.floor_optional_hint) ;
            else etFloor.setHint(null);
        });
        etHowToReach.setOnFocusChangeListener((view, b) -> {
            if(b)etHowToReach.setHint(R.string.how_to_reach_optional_hint) ;
            else etHowToReach.setHint(null);
        });
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            mListener = (LocationDetailsDialogListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement LocationSearchDialogListener");
        }
    }

    private void hideView(View view){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = 0;
        view.setLayoutParams(params);
    }
    private void showView(View view, int size){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = size;
        view.setLayoutParams(params);
    }
    private int getActionBarSize(){
        final TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(new int[]{
            android.R.attr.actionBarSize
        });
        return (int)typedArray.getDimension(0, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setWhiteNavigationBar(@NonNull Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            GradientDrawable dimDrawable = new GradientDrawable();
            // ...customize your dim effect here

            GradientDrawable navigationBarDrawable = new GradientDrawable();
            navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
            navigationBarDrawable.setColor(Color.WHITE);

            Drawable[] layers = {dimDrawable, navigationBarDrawable};

            LayerDrawable windowBackground = new LayerDrawable(layers);
            windowBackground.setLayerInsetTop(1, metrics.heightPixels);

            window.setBackgroundDrawable(windowBackground);
        }
    }

}
