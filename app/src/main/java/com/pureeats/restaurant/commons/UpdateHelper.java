package com.pureeats.restaurant.commons;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

public class UpdateHelper {
    public static String KEY_UPDATE_ENABLE = "is_update";
    public static String KEY_UPDATE_VERSION = "version";
    public static String KEY_UPDATE_URL = "update_url";

    public interface OnUpdateCheckListener{
        void onUpdateCheckListener(double appVersion, double currentVersion, String urlApp);
    }

    public static Builder with(Context context){
        return new Builder(context);
    }

    private OnUpdateCheckListener onUpdateCheckListener;
    private Context context;

    public UpdateHelper(Context context, OnUpdateCheckListener onUpdateCheckListener){
        this.onUpdateCheckListener = onUpdateCheckListener;
        this.context = context;
    }

    public void check(){
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);

        // Default value:
        Map<String, Object> defaultValue = new HashMap<>();
        defaultValue.put(UpdateHelper.KEY_UPDATE_ENABLE, false);
        defaultValue.put(UpdateHelper.KEY_UPDATE_VERSION, "1.1");
        defaultValue.put(UpdateHelper.KEY_UPDATE_URL, "https://play.google.com/store/apps/details?id=com.arpangroup.pureeats");

        remoteConfig.setDefaultsAsync(defaultValue);


        remoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                if(remoteConfig.getBoolean(KEY_UPDATE_ENABLE)){
                    String currentVersion = remoteConfig.getString(KEY_UPDATE_VERSION);
                    String appVersion = getAppVersion(context);
                    String updateUrl = remoteConfig.getString(KEY_UPDATE_URL);

                    if(!TextUtils.equals(currentVersion, appVersion) && onUpdateCheckListener != null){
                        try{
                            double currentVersionDouble = Double.parseDouble(appVersion);
                            double  availableVersion= Double.parseDouble(currentVersion);
                            Log.d("VERSION", "CURRENT_VERSION : "+ currentVersion );
                            Log.d("VERSION","AVAILABLE_VERSION:" + availableVersion);
                            if(availableVersion > currentVersionDouble){
                                onUpdateCheckListener.onUpdateCheckListener(currentVersionDouble, availableVersion, updateUrl);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


    }

    private String getAppVersion(Context context) {
        String result = "";
        try{
            result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
                    .versionName;
            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static class Builder{
        private Context context;
        private OnUpdateCheckListener onUpdateCheckListener;

        public Builder(Context context){
            this.context = context;
        }

        public Builder onUpdateCheck(OnUpdateCheckListener onUpdateCheckListener){
            System.out.println("##################### onUpdateCheck");
            this.onUpdateCheckListener = onUpdateCheckListener;
            return this;
        }

        public UpdateHelper build(){
            return new UpdateHelper(context, onUpdateCheckListener);
        }

        public UpdateHelper check(){
            System.out.println("##################### check");
            UpdateHelper updateHelper = build();
            updateHelper.check();

            return updateHelper;
        }
    }


}
