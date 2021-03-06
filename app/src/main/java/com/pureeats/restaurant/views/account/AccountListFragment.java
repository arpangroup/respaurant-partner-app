package com.pureeats.restaurant.views.account;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pureeats.restaurant.R;
import com.pureeats.restaurant.adapters.AccountSectionAdapter;
import com.pureeats.restaurant.commons.Actions;
import com.pureeats.restaurant.databinding.FragmentAccountListBinding;
import com.pureeats.restaurant.databinding.FragmentMenuListBinding;
import com.pureeats.restaurant.models.AccountSection;
import com.pureeats.restaurant.services.FetchOrderService;
import com.pureeats.restaurant.sharedpref.ServiceTracker;
import com.pureeats.restaurant.sharedpref.UserSession;
import com.pureeats.restaurant.util.CommonUtils;
import com.pureeats.restaurant.viewmodels.AuthenticationViewModel;
import com.pureeats.restaurant.viewmodels.RestaurantViewModel;
import com.pureeats.restaurant.views.MainActivity;
import com.pureeats.restaurant.views.auth.AuthActivity;
import com.pureeats.restaurant.views.menuitem.MenuActivity;

import java.util.Arrays;
import java.util.List;

public class AccountListFragment extends Fragment implements AccountSectionAdapter.AccountSectionInterface {
    private final String TAG = this.getClass().getSimpleName();

    private FragmentAccountListBinding mBinding;
    AuthenticationViewModel authenticationViewModel;
    RestaurantViewModel restaurantViewModel;
    private AccountSectionAdapter accountSectionAdapter;
    private NavController navController;

    //private final MutableLiveData<Boolean>isRestaurantLoaded = new MutableLiveData<>(false);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentAccountListBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Initialize ViewModel
        authenticationViewModel = new ViewModelProvider(requireActivity()).get(AuthenticationViewModel.class);
        restaurantViewModel = new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
        authenticationViewModel.init();
        restaurantViewModel.init();


        restaurantViewModel.loadDashboard();

        // Initialize NavController
        navController = Navigation.findNavController(rootView);
        initClicks();
        initRecyclerView();

//        restaurantViewModel.loadDashboard();

//
//        isRestaurantLoaded.observe(requireActivity(), isLoaded -> {
//            if(isLoaded){
//                mBinding.toolbar.restaurantOnOfSwitch.setEnabled(true);
//            }
//        });




        mBinding.toolbar.restaurantOnOfSwitch.setOnClickListener(view -> {
            Log.d(TAG, "CLICKED");
            boolean status = mBinding.toolbar.restaurantOnOfSwitch.isChecked();
            restaurantViewModel.toggleRestaurant(status).observe(requireActivity(), apiResponse -> {
                if(apiResponse.isSuccess()){
                    handleEndlessService();
                }
            });
        });

        restaurantViewModel.getIsLoading().observe(requireActivity(), aBoolean -> {
            if(aBoolean) mBinding.progressLayout.setVisibility(View.VISIBLE);
            else mBinding.progressLayout.setVisibility(View.GONE);
        });

        restaurantViewModel.getRestaurantDetails().observe(getViewLifecycleOwner(), restaurant -> {
            mBinding.restaurantName.setText(restaurant.getName());
            mBinding.desc.setText(restaurant.getAddress());
            if(restaurant.getIsActive() == 1)mBinding.toolbar.restaurantOnOfSwitch.setChecked(true);
            else mBinding.toolbar.restaurantOnOfSwitch.setChecked(false);
            handleEndlessService();

            //isRestaurantLoaded.setValue(true);
        });


    }

    private void handleEndlessService(){
        Log.d(TAG, "Inside handleEndlessService().............");
        Log.d(TAG, "PUSH_TOKEN: "+UserSession.getPushNotificationToken());
        boolean serviceStarted = ServiceTracker.getServiceState(requireActivity()) == ServiceTracker.ServiceState.STARTED;
        boolean isRestaurantActive = UserSession.isRestaurantActive(requireActivity());
        if(isRestaurantActive && serviceStarted) {
            Log.d(TAG, "Restaurant is ACTIVE and Service STARTED; ==> return; ");
            actionOnService(Actions.START);
        }
        else if(!isRestaurantActive && !serviceStarted) {
            Log.d(TAG, "Restaurant is NOT_ACTIVE and Service NOT_STARTED; ==> return; ");
            actionOnService(Actions.STOP);
        }
        else if(isRestaurantActive && !serviceStarted) {
            Log.d(TAG, "Restaurant is ACTIVE and Service NOT_STARTED; ==> try to START service ");
            actionOnService(Actions.START);
        }
        else if(!isRestaurantActive && serviceStarted){
            Log.d(TAG, "Restaurant is NOT_ACTIVE and Service STARTED; ==> try to STOP service ");
            actionOnService(Actions.STOP);
        }
        else{
            Log.d(TAG, "STOP THE FOREGROUND SERVICE ON DEMAND");
            actionOnService(Actions.STOP);
        }
    }

    private void initClicks() {

        mBinding.bottomNavigation.orderLinear.setOnClickListener(view ->{
            requireActivity().startActivity(new Intent(requireActivity(), MainActivity.class));
            requireActivity().finish();
        });

        mBinding.bottomNavigation.menuLinear.setOnClickListener(view ->{
            requireActivity().startActivity(new Intent(requireActivity(), MenuActivity.class));
            requireActivity().finish();
        });

        mBinding.layoutRestaurant.setOnClickListener(view -> {
            navController.navigate(R.id.action_accountListFragment_to_editRestaurantFragment);
        });


    }
    private void initRecyclerView(){
        accountSectionAdapter = new AccountSectionAdapter(this);
        mBinding.sectionRecycler.setAdapter(accountSectionAdapter);
        List<AccountSection> sections = Arrays.asList(
                new AccountSection(1, "Schedule Restaurant Open", "Schedule off time in advance", R.drawable.ic_baseline_access_time_24),
                new AccountSection(2, "Order History","View all previous orders",  R.drawable.ic_baseline_shopping_cart_24),
                new AccountSection(3, "Earning","View all earnings",  R.drawable.ic_baseline_payment_24),
                new AccountSection(4, "Support","Raise support ",  R.drawable.ic_baseline_help_outline_24),
                new AccountSection(5, "Logout","Signout from your current account",  R.drawable.ic_baseline_power_settings_new_24)
        );
        accountSectionAdapter.submitList(sections);
    }

    private void actionOnService(Actions action){
        if(ServiceTracker.getServiceState(requireActivity()) == ServiceTracker.ServiceState.STOPPED && action == Actions.STOP) return;
        Intent intent = new Intent(getActivity(), FetchOrderService.class);
        intent.setAction(action.name());
        try{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                Log.d(TAG, "Starting the service in >=26 Mode");
                ContextCompat.startForegroundService(requireActivity(), intent);
            }else{
                Log.d(TAG, "Starting the service in < 26 Mode");
                requireActivity().startService(intent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }




    @Override
    public void onItemAccountSectionItemClick(AccountSection accountSection) {
        switch (accountSection.getId()){
            case 1:
                break;
            case 2:
                navController.navigate(R.id.action_accountListFragment_to_orderHistoryFragment);
                break;
            case 3:
                navController.navigate(R.id.action_accountListFragment_to_earningFragment);
                break;
            case 4:
                CommonUtils.openWhatsApp(requireActivity());
                break;
            case 5:
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("Are you sure !");
                builder.setMessage("You are about to delete the saved address from database. Do you really want to proceed ?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", (dialog, which) -> handleLogout());
                builder.setNegativeButton("No", (dialog, which) -> {
                    //Toast.makeText(getApplicationContext(), "You've changed your mind to delete all records", Toast.LENGTH_SHORT).show();
                });
                builder.show();
                break;
            default:
                break;
        }
    }
    private void handleLogout(){
        authenticationViewModel.logout();

        Intent intentLogin = new Intent(requireActivity(), AuthActivity.class);
        intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        requireActivity().finishAffinity();
        actionOnService(Actions.STOP);
        startActivity(intentLogin);
        requireActivity().finish();
    }
}