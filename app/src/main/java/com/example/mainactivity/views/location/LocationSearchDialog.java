package com.example.mainactivity.views.location;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainactivity.R;
import com.example.mainactivity.commons.LocationSearchDialogListener;
import com.example.mainactivity.util.ScreenUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LocationSearchDialog extends BottomSheetDialogFragment {
    private final String TAG = this.getClass().getSimpleName();
    private LocationSearchDialogListener mListener;

    private AppBarLayout appBarLayout;
    private LinearLayout linearLayout;

    LinearLayout layout_location_list;
    RecyclerView search_recycler;
    List<Address> addressList = new ArrayList<>();
    PlaceAutoSuggestAdapter searchAdapter;
    private EditText txt_search;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        final View rootView = View.inflate(getContext(), R.layout.layout_location_search, null);
        dialog.setContentView(rootView);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View)rootView.getParent());
        //bottomSheetBehavior.setPeekHeight(screenUtils.getHeight());
        bottomSheetBehavior.setDraggable(false);

        /*
        final View view = View.inflate(getContext(), R.layout.layout_location_search, null);
        dialog.setContentView(view);
        BottomSheetBehavior bottomSheetBehavior  =  BottomSheetBehavior.from((View)view.getParent());
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(bottomSheetBehavior.STATE_EXPANDED == newState){
                    showView(appBarLayout, getActionBarSize());
                    hideView(linearLayout);
                }

                if(bottomSheetBehavior.STATE_COLLAPSED == newState){
                    hideView(appBarLayout);
                    showView(linearLayout, getActionBarSize());
                }

                if(bottomSheetBehavior.STATE_HIDDEN == newState){
                   dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
         */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog);
        }

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_location_search, container, false);

        LinearLayout layoutSearch = rootView.findViewById(R.id.layout_search);
        ScreenUtils screenUtils = new ScreenUtils(requireActivity());
        layoutSearch.setMinimumHeight(screenUtils.getHeight());

        Geocoder geocoder = new Geocoder(getActivity());
        layout_location_list =  rootView.findViewById(R.id.layout_location_list);
        search_recycler = rootView.findViewById(R.id.search_recycler);
        searchAdapter = new PlaceAutoSuggestAdapter(requireActivity(), new ArrayList<>());
        search_recycler.setAdapter(searchAdapter);

        hideSearchResult();

        txt_search = rootView.findViewById(R.id.txt_search);
        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editable.toString().trim().toLowerCase();
                if(searchText.length() < 1) hideSearchResult();
                else if(searchText.length() > 2){
                    Log.d(TAG, "Searching...............");
                    try {
                        addressList = geocoder.getFromLocationName(searchText, 100);
                        Log.d(TAG, "RESULT: "+addressList);
                        searchAdapter.updateList(addressList);
                        showSearchResult();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    //searchAdapter.updateList(new ArrayList<>());
                    //searchAdapter.notifyDataSetChanged();
                }

            }
        });


        ImageButton btnClose = rootView.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(view -> dismiss());

        return rootView;
    }

    private void showSearchResult(){
        search_recycler.setVisibility(View.VISIBLE);
        layout_location_list.setVisibility(View.GONE);
    }

    private void hideSearchResult(){
        search_recycler.setVisibility(View.GONE);
        layout_location_list.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            mListener = (LocationSearchDialogListener)context;
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
