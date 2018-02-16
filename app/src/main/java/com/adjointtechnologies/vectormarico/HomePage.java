package com.adjointtechnologies.vectormarico;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adjointtechnologies.vectormarico.database.MapperInfoEntity;
import com.adjointtechnologies.vectormarico.database.StoreDetailsEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class HomePage extends AppCompatActivity {
    final String TAG="homepage";
    File imgFolder;
    boolean isSyncing;
    File imgProcess;
    File imgComplete;
    File databsePath;
    boolean isImgError=false;
    private ReactiveEntityStore<Persistable> data;
    Button start,dataSync,imageSync,logout,details;
    TextView todayData,pendingData,pendingImages;
    int inCount=0;
    final int AUDIT_REQUEST=123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        data=((ProductApplication)getApplication()).getData();
//        SyncUtils.CreateSyncAccount(getApplicationContext());
        setContentView(R.layout.activity_home_page);
        imgFolder=new File(ConstantValues.imagepath);
        imgProcess=new File(ConstantValues.imageprocess);
        imgComplete=new File(ConstantValues.imagecomplete);
        databsePath=new File(ConstantValues.dataBasePath);
        start=(Button)findViewById(R.id.start);
        dataSync=(Button)findViewById(R.id.sync_data);
        details=(Button)findViewById(R.id.details);
        imageSync=(Button)findViewById(R.id.sync_images);
        todayData=(TextView) findViewById(R.id.today_surveys);
        pendingData=(TextView)findViewById(R.id.data_sync_pending);
        pendingImages=(TextView)findViewById(R.id.images_pending);
        if(!((ProductApplication)getApplication()).vector_id.contains("MAR")){
            if(!((ProductApplication)getApplication()).vector_id.contentEquals("AUD999")){
                Toast.makeText(getApplicationContext(),"Login Error Please Try Again",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        }
        updateRecords();
        exportDatabse();
        logout=(Button)findViewById(R.id.logout);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(getApplicationContext(),DetailsActivity.class);
//                intent.putExtra("isaudit",true);
//                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.delete(MapperInfoEntity.class).get().value();
                ((ProductApplication)getApplication()).vector_id="";
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), StoreListActivity.class));
            }
        });
        dataSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                syncData();
            }
        });
        imageSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImages();
            }
        });
    }
    private void updateRecords(){
        Calendar tm=Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
        final String tmmp=sdf.format(tm.getTime());
        final int todayCount=data.count(StoreDetailsEntity.class).where(StoreDetailsEntity.SURVEY_TIME.like(tmmp+"%")).get().value();
        todayData.setText(""+todayCount);
        File[] images=imgFolder.listFiles();
        int no=0;
        if(images!=null){no=images.length;}
        File[] process=imgProcess.listFiles();
        if(process!=null){
            no+=process.length;
        }
        final int total=no;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int size = data.select(StoreDetailsEntity.STORE_ID).distinct().where(StoreDetailsEntity.SYNC_STATUS.eq(false)).get().toList().size();
                pendingData.setText(""+size);//String.valueOf(data.count(StoreDataEntity.STORE_ID).where(StoreDataEntity.SYNC_STATUS.eq(false)).get().value()));
                pendingImages.setText(""+total);
            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void exportDatabse() {
        try {
            if (databsePath.canWrite()) {
                String currentDBPath = "//data//data//"+getPackageName()+"//databases//default";
                File backupDB = new File(databsePath,"backup.db");
                File currentDB=new File(currentDBPath);
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(getApplicationContext(),"Database Copied Successfully",Toast.LENGTH_SHORT).show();
                }else {
                }
            }else {
            }
        } catch (Exception e) {
            Log.i(TAG,"export error="+e.toString());
        }
    }
    private void syncData(){
        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(),"Network Not Available",Toast.LENGTH_SHORT).show();
            return;
        }
        dataSync.setEnabled(false);
        final List<StoreDetailsEntity> entities = data.select(StoreDetailsEntity.class).where(StoreDetailsEntity.SYNC_STATUS.eq(false)).get().toList();
        if(entities.size()<=0){
//            Toast.makeText(getApplicationContext(),"No Pending Data",Toast.LENGTH_SHORT).show();
            dataSync.setEnabled(true);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray array=new JSONArray();
                for (StoreDetailsEntity entity:entities) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("store_id", entity.getStoreId());
                        object.put("store_name", entity.getStoreName());
                        object.put("owner_name",entity.getOwnerName());
                        object.put("owner_mobile_no",entity.getOwnerMobile());
                        object.put("store_found",entity.getIsStoreFound());
                        object.put("store_status",entity.getStoreCondition());
                        object.put("visit_store",entity.getIsMaricoSalesMan());
                        object.put("smart_phone",entity.getOwnerHasSmartPhone());
                        object.put("sell_hair_oil",entity.getIsHairOil());
                        object.put("poster_pasted",entity.getIsPosterPasted());
                        object.put("reasons_poster",entity.getPosterReason());
                        object.put("number_retailer_phone",entity.getIsMaricoNoRetailerPhone());
                        object.put("outlet_activated",entity.getIsOutletActivated());
                        object.put("agent_unique_record",entity.getUniqueId());
//                        object.put("audit_id",entity.getVectorId());
                        object.put("verify_otp_flag",entity.getOtpVerified());
                        object.put("sent_otp_flag",entity.getOtpSent());
                        object.put("verify_otp_flag2",entity.getOtpVerified2());
                        object.put("sent_otp_flag2",entity.getOtpSent2());
                        object.put("alternate_mobile",entity.getAlternateMobile());

                        object.put("store_category", entity.getStoreCategory());
                        if(entity.getVectorId().isEmpty()){
                            Log.i(TAG,"empty changing="+((ProductApplication)getApplication()).vector_id);
                            object.put("audit_id", ((ProductApplication)getApplication()).vector_id);
                        }else {
                            Log.i(TAG,"not empty="+entity.VECTOR_ID);
                            object.put("audit_id", entity.getVectorId());
                        }
                        object.put("time", entity.getSurveyTime());
                        array.put(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i(TAG, "json exception=" + e.toString());
                        errorMsg();
                        return;
                    }
                }
                Retrofit retrofit = new Retrofit.Builder().baseUrl(ConstantValues.baseUrl).addConverterFactory(new ToStringConverterFactory())
                        .addConverterFactory(GsonConverterFactory.create()).build();
                RetrofitProductApi api = retrofit.create(RetrofitProductApi.class);
                Call<String> syncDetails = api.syncStoreDetails(array);
                syncDetails.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Response<String> response, Retrofit retrofit) {
                        if (response.body() == null) {
                            Log.i(TAG, "null response");
                            errorMsg();
                        } else {
                            Log.i(TAG, "response=" + response.body());
                            if (response.body().contains("success")) {
                                for (StoreDetailsEntity store : entities
                                        ) {
                                    store.setSyncStatus(true);
                                    data.update(store);
                                }
                                isSyncing = false;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateRecords();
                                    }
                                });
                                data.update(entities).observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.io()).subscribe(new Consumer<Iterable<StoreDetailsEntity>>() {
                                    @Override
                                    public void accept(@NonNull Iterable<StoreDetailsEntity> storeDataEntities) throws Exception {
                                        //do nothing
                                        Log.i("synccheck", "completed");
                                        isSyncing = false;
                                        updateRecords();
                                    }
                                });
                                successMsg();
                            } else {
                                errorMsg();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.i("synccheck","error="+t.toString());
                        errorMsg();
                    }
                });
            }
        }).start();

    }

    private void errorMsg(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Error While Syncing",Toast.LENGTH_SHORT).show();
                dataSync.setEnabled(true);
            }
        });
    }
    private void successMsg(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Sync Completed Successfully",Toast.LENGTH_SHORT).show();
                dataSync.setEnabled(true);
            }
        });
    }
    private void UploadImages(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(imgFolder.isDirectory() && imgProcess.isDirectory() && imgComplete.isDirectory()){
                    //The gson builder
                    Gson gson = new GsonBuilder()
                            .create();


                    //creating retrofit object
                    retrofit2.Retrofit retrofit =new retrofit2.Retrofit.Builder().baseUrl(ConstantValues.baseUrl)
                            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                            .build();

                    //creating our api
                    RetrofitProductApi api = retrofit.create(RetrofitProductApi.class);

                    File[] listcompletedimgs=imgComplete.listFiles();
                    for(int k=0;k<listcompletedimgs.length;k++){
                        listcompletedimgs[k].delete();
                    }
                    File[] listimgs=imgProcess.listFiles();
                    if(listimgs.length>0){
                        for(int i=0;i<listimgs.length;i++){
                            if (listimgs[i].renameTo(new File(imgFolder+"/" + listimgs[i].getName()))) {
                                Log.i("firebase", "success copying file");
                            } else {
                                Log.i("firebase", "file moving error");
                                return;
                            }
                        }
                    }
                    listimgs=imgFolder.listFiles();
                    if(listimgs.length>0) {
                        isImgError = false;
                        for(int i=0;i<listimgs.length && i<1 && !isImgError ;i++) { //replace 1 with
                            if (listimgs[i].renameTo(new File(imgProcess + "/" + listimgs[i].getName()))) {
                                Log.i("firebase", "success copying file");
                            } else {
                                Log.i("firebase", "file moving error");
                                return;
                            }
                            File[] listimgProcess = imgProcess.listFiles();
                            if(listimgProcess.length>0) {
                                Uri imgfile = Uri.fromFile(new File(imgProcess + "/" + listimgs[i].getName()));//listimgProcess[listimgProcess.length-1]);
                                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), listimgProcess[0]);
                                RequestBody descBody = RequestBody.create(MediaType.parse("text/plain"), "extra data");
                                RequestBody imgName=RequestBody.create(MediaType.parse("text/plain"),listimgProcess[0].getName().substring(0,listimgProcess[0].getName().indexOf('.')));
                                Log.i(TAG,"name="+listimgProcess[0].getName().substring(0,listimgProcess[0].getName().indexOf('.')));
                                retrofit2.Call<ImageResponse> upload=api.uploadImage(requestFile,descBody,imgName);
                                upload.enqueue(new retrofit2.Callback<ImageResponse>() {
                                    @Override
                                    public void onResponse(retrofit2.Call<ImageResponse> call, retrofit2.Response<ImageResponse> response) {
                                        if(response.body()==null){
                                            Log.i(TAG,"null response");
                                            return;
                                        }
                                        Log.i(TAG,"response="+response.body().toString());
                                        if(response.body().status.contentEquals("OK")){
                                            File tmp=new File(imgProcess+"/"+response.body().imgName);
                                            if(tmp.exists()) {
                                                Log.i("firebase", "image name=" + response.body().imgName+"exists");
                                                if (tmp.renameTo(new File(imgComplete + "/" + tmp.getName()))) {
                                                    Log.i("firebase", "success moving image");
                                                    updateRecords();
                                                    UploadImages();
                                                } else {
                                                    tmp.delete();
                                                }
                                            }else {
                                                Log.i("firebase","image name="+response.body().imgName+" not exiists");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(retrofit2.Call<ImageResponse> call, Throwable t) {
                                        isImgError = true;
                                        Log.i(TAG,"error="+t.toString());
                                    }
                                });
                            }
                        }
                    }
                    }else {
                    if(!imgFolder.mkdirs() ){
                        Log.i("folder","creation error persists for imgfolder");
                    }
                    if(!imgProcess.mkdirs()){
                        Log.i("folder","creation error persists for imgProcess");
                    }
                    if(!imgComplete.mkdirs()){
                        Log.i("folder","creation error persists for imgComplete");
                    }
                }
            }
        }).start();

    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateRecords();
        if(((ProductApplication)getApplication()).vector_id==null){
            Toast.makeText(getApplicationContext(),"Login Error Please Try Again",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }
        if(((ProductApplication)getApplication()).vector_id.isEmpty()){
            List<MapperInfoEntity> auditInfoEntities = data.select(MapperInfoEntity.class).get().toList();
            if(auditInfoEntities.size()>0){
                ((ProductApplication)getApplication()).vector_id=auditInfoEntities.get(0).getAuditId();
                Log.i(TAG,"found id="+((ProductApplication)getApplication()).vector_id);
            }else {
                ((ProductApplication)getApplication()).vector_id="";
            }
            if(!((ProductApplication)getApplication()).vector_id.startsWith("MAR")){
                if(!((ProductApplication)getApplication()).vector_id.contentEquals("AUD999")){
                    Toast.makeText(getApplicationContext(),"Login Error Please Try Again",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    finish();
                }
            }
        }
    }
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
