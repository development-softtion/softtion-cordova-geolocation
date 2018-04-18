
package com.softtion.geolocation;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.location.LocationListener;
import android.location.Criteria;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import com.sun.webkit.plugin.Plugin;

public class Geolocation extends CordovaPlugin {
    
    private static LocationManager locationManager;
    private static final int REQUEST_LOCATION = 0;
    
    private static final String [] permissions = { 
        Manifest.permission.ACCESS_COARSE_LOCATION, 
        Manifest.permission.ACCESS_FINE_LOCATION 
    };
    
    private Context context; 
    private Activity activity; 
    private CallbackContext callbackContext;
    private LocationListener locationListener;
    private JSONObject result;
    
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        locationManager = (LocationManager) 
            this.cordova.getActivity().getSystemService(Context.LOCATION_SERVICE);
        
        activity = this.cordova.getActivity();
        context = activity.getApplicationContext();
        super.initialize(cordova, webView);
    }
    
    public boolean execute(
        String action, 
        JSONArray args, 
        CallbackContext callbackContext
    )
    throws JSONException {
        this.callbackContext = callbackContext;
        try {
            if(action.equals("hasPermission")){
                this.callbackContext.success(hasPermisions() ? 1 : 0);
                return true;
            }
            else if(action.equals("requestPermission")){
                requestPermisions(); 
                return true;
            }
            else if(action.equals("openLocationSettings")){
                openLocationSettings();
                return true;
            }
            else {
                if(hasPermisions()){
                    if(action.equals("getAllProviders")){
                        result = new JSONObject();
                        result.put("providers", getAllProviders());
                        this.callbackContext.success(result); 
                        return true;
                    }
                    else if(action.equals("getEnabledProviders")){
                        result = new JSONObject();
                        result.put("providers", getEnabledProviders());
                        this.callbackContext.success(result); 
                        return true;
                    }
                    else if(action.equals("isLocationAvailable")){
                        this.callbackContext.success(isLocationAvailable() ? 1 : 0);
                        return true;
                    }
                    else if(action.equals("isLocationEnabled")){
                        this.callbackContext.success(isLocationEnabled() ? 1 : 0);
                        return true;
                    }
                    else if(action.equals("isGpsAvailable")){
                        this.callbackContext.success(
                            getProvider(LocationManager.GPS_PROVIDER) != null ? 1 : 0
                        );
                        return true;
                    }
                    else if (action.equals("isGpsEnabled")) {
                        this.callbackContext.success(
                            isProviderEnabled(LocationManager.GPS_PROVIDER) ? 1 : 0
                        );
                        return true;
                    }
                    else if(action.equals("isNetworkAvailable")){
                        this.callbackContext.success(
                            getProvider(LocationManager.NETWORK_PROVIDER) != null ? 1 : 0
                        );
                        return true;
                    }
                    else if (action.equals("isNetworkEnabled")) {
                        this.callbackContext.success(
                            isProviderEnabled(LocationManager.NETWORK_PROVIDER) ? 1 : 0
                        );
                        return true;
                    }
                    else if (action.equals("enableLocationListener")){
                        enableLocationListener(args.getLong(0), args.getLong(1), this.callbackContext);
                        return true;
                    }
                    else if (action.equals("disableLocationListener")){
                        if(locationListener instanceof LocationListener){
                            disableLocationListener();
                            this.callbackContext.success("The location listener disabled successfully");
                        }
                        else{
                            this.callbackContext.error("The location listener is disabled"); 
                            return false;
                        }
                    }
                }
                else{
                    this.callbackContext.error("Location permission not granted"); 
                    return false;
                }
            }
        } catch (Exception e) {
            this.callbackContext.error(e.getMessage()); return false;
        }
        return true;
    }
    
    public boolean isLocationEnabled(){
        List<String> listProviders = getProviders(accuracyFineCriteria(), true);
        return !(listProviders.size() <= 0);
    };
    
    public boolean isLocationAvailable(){
        List<String> listProviders = getProviders(accuracyFineCriteria(), false);
        return !(listProviders.size() <= 0);
    }
    
    public List<String> getAllProviders(){
        return locationManager.getProviders(false);
    }
    
    public List<String> getEnabledProviders(){
        return locationManager.getProviders(true);
    }
    
    public List<String> getProviders(Criteria criteria, boolean enabledOnly){
        return locationManager.getProviders(criteria, enabledOnly);
    }
    
    public LocationProvider getProvider(String name){
        return locationManager.getProvider(name);
    }
    
    public boolean isProviderEnabled(String name){
        return locationManager.isProviderEnabled(name);
    }
    
    public void openLocationSettings(){
        cordova.getActivity().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        PluginResult result = new PluginResult(PluginResult.Status.OK);
        result.setKeepCallback(true);
        this.callbackContext.sendPluginResult(result);
    }
    
    public boolean hasPermisions(){
        return !(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);  
    }
    
    public void requestPermisions(){
        ActivityCompat.requestPermissions(activity, permissions,REQUEST_LOCATION);
    }
    
    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults){
        boolean band = true; PluginResult pluginResult; 
        if (grantResults.length >= 1) {
            for (int r : grantResults) {
                if (r != PackageManager.PERMISSION_GRANTED) {
                    band = false;
                }
            }
        }
        else{
            band = false;
        }
        if (band){
            pluginResult = new PluginResult(PluginResult.Status.OK);
        }
        else{
            pluginResult = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
        }
        pluginResult.setKeepCallback(true);
        this.callbackContext.sendPluginResult(pluginResult);
    }
    
    public void enableLocationListener(long minTime, long minDistance, CallbackContext callbackContext){
        try {
            instanceListener(callbackContext);
            String provider = locationManager.getBestProvider(accuracyFineCriteria(), true);
            locationManager.requestLocationUpdates(provider,minTime,minDistance,locationListener);
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }
    
    public void disableLocationListener(){
        locationListener = null;
    }
    
    private Criteria accuracyFineCriteria(){
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        return criteria;
    }    
    
    private void instanceListener(CallbackContext callbackContext) {
        locationListener = new LocationListener(){
            @Override
            public void onLocationChanged(Location location) {
                try {
                    JSONObject data = new JSONObject();
                    data.put("latitude", location.getLatitude());
                    data.put("longitude", location.getLongitude());

                    result = new JSONObject();
                    result.put("location", data);
                    result.put("message", "Get new geolocation");
                    result.put("code", 0);
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, result);
                    pluginResult.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResult);
                } catch (Exception e) {
                    callbackContext.error(e.getMessage());
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                try {
                    result = new JSONObject();
                    result.put("message", "State location provider Changed");
                    result.put("provider", provider); result.put("status", status); result.put("code", 1);
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, result);
                    pluginResult.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResult);
                } catch (Exception e) {
                    callbackContext.error(e.getMessage());
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                try {
                    result = new JSONObject();
                    result.put("message", "provider enabled");
                    result.put("provider", provider); result.put("code", 2);
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, result);
                    pluginResult.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResult);
                } catch (Exception e) {
                    callbackContext.error(e.getMessage());
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
                try {
                    result = new JSONObject();
                    result.put("message", "provider disabled");
                    result.put("provider", provider); result.put("code", 3);
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, result);
                    pluginResult.setKeepCallback(true);
                    callbackContext.sendPluginResult(pluginResult);
                } catch (Exception e) {
                    callbackContext.error(e.getMessage());
                }
            }
        };
    }
    
}