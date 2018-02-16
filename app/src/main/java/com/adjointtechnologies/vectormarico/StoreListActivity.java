package com.adjointtechnologies.vectormarico;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adjointtechnologies.vectormarico.adapter.AdapterClass;
import com.adjointtechnologies.vectormarico.adapter.CustomInfoWindowAdapter;
import com.adjointtechnologies.vectormarico.database.StoreDetailsEntity;
import com.adjointtechnologies.vectormarico.database.To_doEntity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.maps.android.SphericalUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.quentinklein.slt.LocationTracker;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class StoreListActivity extends FragmentActivity implements OnMapReadyCallback {

    private ReactiveEntityStore<Persistable> data;
    private List<StoreData> storeDataList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AdapterClass adapterClass;
    private View infoWindow;
    private Marker clickedMarker=null;
    final String Tag="storelistactivity";
    private GoogleMap mMap;
    private boolean first=true;
    private boolean isMapReady=false;
    private Button listview,mapview;
    private FloatingActionButton refresh,navigate,picture;
    private ProgressBar progressBar;
    private FrameLayout rootlayout;
    public static Location lastLocation=null;
    private LocationTracker tracker;
    private PopupWindow popupWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Tag,"in oncreate");
        data= ((ProductApplication)getApplication()).getData();
        setContentView(R.layout.activity_store_list);
        if(((ProductApplication)getApplication()).vector_id.isEmpty()){
                Toast.makeText(getApplicationContext(),"Login Error, Please Login Again",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
        }
        refresh=(FloatingActionButton) findViewById(R.id.refresh_list);
        navigate=(FloatingActionButton) findViewById(R.id.navigate_list);
        picture=(FloatingActionButton) findViewById(R.id.request_image_list);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapterClass = new AdapterClass(storeDataList, getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapterClass);
        progressBar=(ProgressBar) findViewById(R.id.progress_list);
        rootlayout=(FrameLayout) findViewById(R.id.root_layout);
        loadData();
        infoWindow=getLayoutInflater().inflate(R.layout.info_window,null);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getstores();
//                loadData();
            }
        });
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateTo();
            }
        });
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickedMarker==null){
                    return;
                }else {
                    final To_doEntity entity = data.select(To_doEntity.class).where(To_doEntity.LATITUDE.eq(clickedMarker.getPosition().latitude).and(To_doEntity.LONGITUDE.eq(clickedMarker.getPosition().longitude))).get().firstOrNull();
                    if (entity != null) {
//                        Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
//                        intent.putExtra("storeid", entity.getStoreId());
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
                        LayoutInflater inflater=(LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popUpView=inflater.inflate(R.layout.image_popup,null);
                        popupWindow=new PopupWindow(popUpView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        if(Build.VERSION.SDK_INT>=21){
                            popupWindow.setElevation(5.0f);
                        }
                        final ProgressBar popUpProgress=(ProgressBar) popUpView.findViewById(R.id.popup_progress);
                        final ImageView popUpImage=(ImageView) popUpView.findViewById(R.id.popup_image);
                        String url="https://firebasestorage.googleapis.com/v0/b/storechecker-9a5cb.appspot.com/o/images%2F"+entity.getStoreId()+".jpeg?alt=media&token=a630e4ae-f7f2-4a25-af9f-e98685b0bd03";
                        Glide.with(getApplicationContext()).load(url).error(R.drawable.error).listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
                                popUpProgress.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                popUpProgress.setVisibility(View.GONE);
                                return false;
                            }
                        }).into(popUpImage);
//                        Picasso.with(getApplicationContext()).load(url).error(R.drawable.error).into(popUpImage);
                        Button closeButton = (Button) popUpView.findViewById(R.id.close_popup);
                        Button actionPopup=(Button) popUpView.findViewById(R.id.action_popup);
                        actionPopup.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(lastLocation==null){
                                    Log.i(Tag,"null location");
                                    Toast.makeText(getApplicationContext(),"Unable To Get Your Location",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                double distanceBetween = SphericalUtil.computeDistanceBetween(clickedMarker.getPosition(), new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()));
                                if(distanceBetween>100 || distanceBetween<=0){
                                    Toast.makeText(getApplicationContext(),"You Are 100m Away From Store, Move Around To Refresh Distance",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                final To_doEntity entity = data.select(To_doEntity.class).where(To_doEntity.LATITUDE.eq(clickedMarker.getPosition().latitude).and(To_doEntity.LONGITUDE.eq(clickedMarker.getPosition().longitude))).get().firstOrNull();
                                if(entity!=null){
                                            Intent intent = new Intent(getApplicationContext(), SurveyPage.class);
                                            intent.putExtra("storeid", entity.getStoreId());
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                }

                            }
                        });
                        // Set a click listener for the popup window close button
                        closeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Dismiss the popup window
                                popupWindow.dismiss();
                            }
                        });
                        popupWindow.showAtLocation(rootlayout, Gravity.CENTER,0,0);
                    }
                }
            }
        });
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);
        mapFragment.getView().setVisibility(View.GONE);
        listview=(Button)findViewById(R.id.listview);
        mapview=(Button)findViewById(R.id.mapview);
        listview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragment.getView().setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
//                adapterClass.notifyDataSetChanged();
                listview.setAlpha(1f);
                listview.setClickable(false);
                mapview.setClickable(true);
                mapview.setAlpha(0.5f);
            }
        });
        mapview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.GONE);
                mapFragment.getView().setVisibility(View.VISIBLE);
                listview.setClickable(true);
                listview.setAlpha(0.5f);
                mapview.setClickable(false);
                mapview.setAlpha(1f);
            }
        });
        recyclerView.setVisibility(View.GONE);
        mapFragment.getView().setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostResume() {
        Log.i(Tag,"in post resume");
        super.onPostResume();
        loadData();
        if(popupWindow!=null){
            if(popupWindow.isShowing()){
                popupWindow.dismiss();
            }
        }
    }

    private void loadData() {
        Log.i(Tag,"loading data");
        rootlayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        storeDataList.clear();
        List<To_doEntity> to_doEntities = data.select(To_doEntity.class).where(To_doEntity.COMPLETEFLAG.eq(false)).get().toList();
        for (int i=0;i<to_doEntities.size();i++){
            storeDataList.add(new StoreData(to_doEntities.get(i).getStoreName(),to_doEntities.get(i).getLatitude(),to_doEntities.get(i).getLongitude(),0,to_doEntities.get(i).getStoreId(),to_doEntities.get(i).getLandMark(),to_doEntities.get(i).getOwnerMobileNo(),to_doEntities.get(i).getOwnerName(),to_doEntities.get(i).getStoreCategory()));
        }
        updateDistance();
        setMarkers();
        adapterClass.notifyDataSetChanged();
//        if(!first) {
            progressBar.setVisibility(View.GONE);
            rootlayout.setVisibility(View.VISIBLE);
//        }
    }
    private void setMarkers(){
        Log.i(Tag,"in set markers");
        if(isMapReady) {
            if (storeDataList.size() > 0) {
                mMap.clear();
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(storeDataList.get(0).getLatitude(),storeDataList.get(0).getLongitude())));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(storeDataList.get(0).getLatitude(),storeDataList.get(0).getLongitude()),17));//CameraUpdateFactory.zoomTo(17));
                for (int i = 0; i < storeDataList.size(); i++) {
//                    Log.i(Tag,"setting markers");
                    Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(storeDataList.get(i).getLatitude(), storeDataList.get(i).getLongitude())).title(storeDataList.get(i).getStore_name()));
                    marker.setTag(storeDataList.get(i));
                }
            }else {
                Log.i(Tag,"store list zero");
            }
        }else {
            Log.i(Tag,"map no ready");
        }
    }

    private void updateDistance() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        tracker = new LocationTracker(getApplicationContext()) {

            @Override
            public void onLocationFound(Location location) {
                // Stop listening for updates
                stopListening();
                lastLocation=location;
                if(first) {
                updateDistance();
                    first=false;
                    progressBar.setVisibility(View.GONE);
                    rootlayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTimeout() {
                Toast.makeText(getApplicationContext(),"unable to get your location",Toast.LENGTH_SHORT).show();
                stopListening();
            }
        };
        tracker.startListening();
        if(lastLocation!=null){
            first=false;
            final LatLng startpoint=new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
            for(int i=0;i<storeDataList.size();i++) {
                LatLng latLng = new LatLng(storeDataList.get(i).getLatitude(),storeDataList.get(i).getLongitude());
                double distanceBetween = SphericalUtil.computeDistanceBetween(startpoint, latLng);
                storeDataList.get(i).setDistance(distanceBetween);
            }
            // Do some stuff when a new GPS Location has been found
            Collections.sort(storeDataList, new Comparator<StoreData>() {
                @Override
                public int compare(StoreData storeData, StoreData t1) {
                    if(storeData.getDistance()>t1.getDistance())
                        return 1;
                    else
                        return -1;
                }
            });
            adapterClass.notifyDataSetChanged();
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(Tag,"map ready ");
        mMap=googleMap;
        isMapReady=true;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        CustomInfoWindowAdapter adapter=new CustomInfoWindowAdapter(StoreListActivity.this);
        mMap.setInfoWindowAdapter(adapter);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(lastLocation==null){
                    Log.i(Tag,"null location");
                    Toast.makeText(getApplicationContext(),"Unable To Get Your Location",Toast.LENGTH_SHORT).show();
                    return;
                }
                double distanceBetween = SphericalUtil.computeDistanceBetween(marker.getPosition(), new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()));
                if(distanceBetween>100 || distanceBetween<=0){
                    Toast.makeText(getApplicationContext(),"You Are 100m Away From Store, Move Around To Refresh Distance",Toast.LENGTH_SHORT).show();
//                    return;
                }
                final To_doEntity entity = data.select(To_doEntity.class).where(To_doEntity.LATITUDE.eq(marker.getPosition().latitude).and(To_doEntity.LONGITUDE.eq(marker.getPosition().longitude))).get().firstOrNull();
                if(entity!=null){
                            Intent intent = new Intent(getApplicationContext(), SurveyPage.class);
                            intent.putExtra("storeid", entity.getStoreId());
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        lastLocation = mMap.getMyLocation();
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                lastLocation=location;
//                loadData();
//                Log.i(Tag,"location change from maps"+location.toString());
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                clickedMarker=null;
                navigate.setVisibility(View.GONE);
                picture.setVisibility(View.GONE);
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i(Tag,"on Marker Click called");
                marker.showInfoWindow();
                clickedMarker=marker;
                navigate.setVisibility(View.VISIBLE);
                picture.setVisibility(View.VISIBLE);
                return true;
            }
        });
        setMarkers();
    }
    private void getstores(){
        if(!isNetworkAvailable()){
            Log.i(Tag,"no network");
            loadData();
            return;
        }
        if(data.count(StoreDetailsEntity.class).where(StoreDetailsEntity.SYNC_STATUS.eq(false)).get().value()>0){
            Log.i(Tag,"sync first");
            loadData();
            return;
        }
        if(lastLocation==null){
            Log.i(Tag,"last location null");
            loadData();
            return;
        }
        Log.i(Tag,"trying to get stores");
        rootlayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        JSONObject object=new JSONObject();
        try {
            object.put("vec_id",((ProductApplication)getApplication()).vector_id);
            object.put("current_latitude",lastLocation.getLatitude());
            object.put("current_longitude",lastLocation.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(Tag,"json error="+e.toString());
        }
       //TO DO get stores
        Retrofit retrofit=new Retrofit.Builder().baseUrl(ConstantValues.baseUrl).addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitProductApi api=retrofit.create(RetrofitProductApi.class);
        Call<JsonArray> getToDo=api.getToDoStores(object);
        getToDo.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Response<JsonArray> response, Retrofit retrofit) {
                if(response.body()!=null){
                    Log.i(Tag,"response="+response.body().toString());
                    if(response.body().size()>0) {
                        data.delete(To_doEntity.class).get().value();
                        List<To_doEntity> store_list=new ArrayList<To_doEntity>();
                        for (JsonElement element : response.body()
                                ) {
                            try {
                                JSONObject object = new JSONObject(element.toString());
                                To_doEntity entity = new To_doEntity();
                                entity.setStoreId(object.getString("store_id"));
                                entity.setItcStoreId(object.getString("itc_store_id"));
                                entity.setItcStoreIdCount(object.getInt("itc_store_id_count"));
                                entity.setItcStoreName(object.getString("itc_store_name"));
                                entity.setOwnerName(object.getString("owner_name"));
                                entity.setOwnerMobileNo(object.getString("owner_mobile_no"));
                                entity.setAlreadyVerified(object.getInt("already_verified")==1);
                                entity.setLatitude(object.getDouble("store_latitude"));
                                entity.setLongitude(object.getDouble("store_longitude"));
                                entity.setStoreName(object.getString("store_name"));
                                entity.setStoreGoogleAddress(object.getString("store_google_address"));
                                entity.setLandMark("");
                                entity.setStoreCategory(object.getString("category_of_outlet"));
                                store_list.add(entity);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.i(Tag, "json error=" + e.toString());
                            }
                        }
                        data.insert(store_list).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                                .subscribe(new Consumer<Iterable<To_doEntity>>() {
                                    @Override
                                    public void accept(@NonNull Iterable<To_doEntity> to_doEntities) throws Exception {
                                        loadData();
                                    }
                                });
                    }
                }else {
                    Log.i(Tag,"null response");
                    loadData();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(Tag,"failed to get stores error="+t.toString());
                loadData();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        tracker.stopListening();
        navigate.setVisibility(View.GONE);
        picture.setVisibility(View.GONE);
    }
    private void navigateTo(){
        if(clickedMarker!=null){
            Uri gmmIntentUri = Uri.parse("geo:0,0?q="+clickedMarker.getPosition().latitude+","+clickedMarker.getPosition().longitude+"("+clickedMarker.getTitle()+")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
//            mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
                navigate.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(popupWindow!=null){
            if(popupWindow.isShowing()){
                popupWindow.dismiss();
                return;
            }
        }
        Intent intent=new Intent(getApplicationContext(), HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tracker.isListening()){
            tracker.stopListening();
        }
    }
}
