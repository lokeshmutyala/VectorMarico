package com.adjointtechnologies.vectormarico;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.adjointtechnologies.vectormarico.admin.AdminPage;
import com.adjointtechnologies.vectormarico.database.MapperInfoEntity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.intentfilter.androidpermissions.PermissionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class SplashActivity extends AppCompatActivity {
    final String TAG="splashactivity";
    List<String> permissionList=new ArrayList<>();
    boolean isPermissionsGranted=false;
    boolean isLocationEnabled=false;
    private ReactiveEntityStore<Persistable> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        data=((ProductApplication)getApplication()).getData();
        permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionList.add(Manifest.permission.INTERNET);
        permissionList.add(Manifest.permission.ACCESS_WIFI_STATE);
        permissionList.add(Manifest.permission.CAMERA);
        permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionList.add(Manifest.permission.REORDER_TASKS);
        permissionList.add(Manifest.permission.READ_SYNC_STATS);
        permissionList.add(Manifest.permission.WRITE_SYNC_SETTINGS);
        permissionList.add(Manifest.permission.GET_ACCOUNTS);
        permissionList.add(Manifest.permission.READ_PHONE_STATE);
        permissionList.add(Manifest.permission.RECEIVE_SMS);
        permissionList.add(Manifest.permission.SEND_SMS);
        permissionList.add(Manifest.permission.READ_SMS);

        List<MapperInfoEntity> auditInfoEntities = data.select(MapperInfoEntity.class).get().toList();
        if(auditInfoEntities.size()>0){
            ((ProductApplication)getApplication()).vector_id=auditInfoEntities.get(0).getAuditId();
            Log.i(TAG,"found id="+((ProductApplication)getApplication()).vector_id);
        }else {
            ((ProductApplication)getApplication()).vector_id="";
        }
        File imgFolder=new File(ConstantValues.imagepath);
        File imgproces=new File(ConstantValues.imageprocess);
        File imgComplete=new File(ConstantValues.imagecomplete);
        File databasepath=new File(ConstantValues.dataBasePath);
        if(!imgFolder.isDirectory() || !imgproces.isDirectory() || !imgComplete.isDirectory() || !databasepath.isDirectory()){
            if(!imgFolder.mkdirs()){
                Log.i(TAG,"error creating folder img");
            }
            if(!databasepath.mkdirs()){
                Log.i(TAG,"error creating folder database");
            }
            if(!imgproces.mkdirs()){
                Log.i(TAG,"error creating folder process");
            }
            if(!imgComplete.mkdirs()){
                Log.i(TAG,"error creating folder imgcomplete");
            }
        }
        PermissionManager manager=PermissionManager.getInstance(getApplicationContext());
        manager.checkPermissions(permissionList, new PermissionManager.PermissionRequestListener() {
            @Override
            public void onPermissionGranted() {
                Log.i(TAG,"permissions granted");
                isPermissionsGranted=true;
                if(isLocationEnabled && isPermissionsGranted) {
                    startNextActivity();
                }
//                Toast.makeText(getApplicationContext(),"permissions granted",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied() {
                Log.i(TAG,"permissions deneid");
                Toast.makeText(getApplicationContext(),"can't proceed without permissions",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}
        if(!gps_enabled){
            Log.i(TAG,"requesting location on");
            displayLocationSettingsRequest(getApplicationContext());
        }else {
            isLocationEnabled=true;
            if(isPermissionsGranted) {
                startNextActivity();
            }
        }
    }
    private void displayLocationSettingsRequest(final Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                Log.i(TAG,"status="+result.getStatus());
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(SplashActivity.this, 0x1);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0x1 && resultCode==RESULT_OK){
            isLocationEnabled=true;
            Log.i(TAG,"code matched");
            if(isLocationEnabled && isPermissionsGranted) {
                startNextActivity();
            }
        }else {
            Log.i(TAG,"code not matched code="+requestCode+" resultcode="+resultCode+" data="+data.toString());
        }

    }
    private void startNextActivity(){
        if(((ProductApplication)getApplication()).vector_id.isEmpty()){
            Log.i(TAG,"auditid="+((ProductApplication)getApplication()).vector_id);
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }
        if(((ProductApplication)getApplication()).vector_id.startsWith("MAR") || ((ProductApplication)getApplication()).vector_id.contentEquals("AUD999")){
            startActivity(new Intent(getApplicationContext(),HomePage.class));
            finish();
        }
        else if(((ProductApplication)getApplication()).vector_id.contentEquals("admin") || ((ProductApplication)getApplication()).vector_id.contentEquals("super")){
            startActivity(new Intent(getApplicationContext(),AdminPage.class));
            finish();
        }
    }
}
