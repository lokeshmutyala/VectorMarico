package com.adjointtechnologies.vectormarico;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit2.http.Multipart;
import retrofit2.http.Part;

/**
 * Created by lokeshmutyala on 02-11-2017.
 */

public interface RetrofitProductApi {


    @POST("get_marico_activity_store_list_native.php")
    Call<JsonArray> getToDoStores(@Body JSONObject object);

    @POST("sync_marico_stores_native.php")
    Call<String> syncStoreDetails(@Body JSONArray array);

    @POST("getsurveyercoordinates.php")
    Call<String> getcoordinates(@Body JSONObject object);

    @POST("getsurveyorinfodata.php")
    Call<JsonObject> getInfoData(@Body JSONObject object);

    @POST("getstoreids.php")
    Call<JsonArray> getStoreIds(@Body JSONObject object);

    @Multipart
    @retrofit2.http.POST("ImageUploadApi/Api.php?apicall=upload")
    retrofit2.Call<ImageResponse> uploadImage(@Part("image\"; filename=\"myfile.jpeg\" ") RequestBody file, @Part("desc") RequestBody desc, @Part("name") RequestBody name);


}
