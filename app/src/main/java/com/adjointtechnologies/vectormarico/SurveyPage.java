package com.adjointtechnologies.vectormarico;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adjointtechnologies.vectormarico.database.StoreDetailsEntity;
import com.adjointtechnologies.vectormarico.database.To_doEntity;
import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class SurveyPage extends AppCompatActivity {

    EditText store_id, store_name, store_type, owner_name, owner_mobile_number,alternate_mobile_number, no_poster_reason;
    Button take_store_picture, take_poster_picture, take_retailer_mobile_picture, verify_mobile,verify_alternate_mobile, submit;
    TextView distance_between, marico_id;
    RadioGroup owner_smart_phone, marico_salesman_visit, sell_hair_oil, poster_pasted, number_saved_retailer_phone, outlet_activated;
    RadioButton poster_pasted_radio_button, marico_number_retailer_phone_button;
    LinearLayout outlet_activated_parent;
    ImageView imageview, poster_image, marico_retailer_phone_image;
    boolean isMsgSent = false;
    boolean isMobileVerified = false;
    boolean isMsgSent2=false;
    boolean isMobileVerified2=false;
    final String TAG = "SurveyPage";
    Verification mVerification;
    private String storeId="";
    private ReactiveEntityStore<Persistable> data;
    int IMG_TYPE=0;
    final int STORE_IMAGE=1;
    final int POSTER_IMAGE=2;
    final int RETAILER_PHONE_IMAGE=3;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    private boolean goback=false;
    long back_time=0;
    LinearLayout rootLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_page);
        storeId=getIntent().getStringExtra("storeid");
        if(storeId.isEmpty()){
            Toast.makeText(getApplicationContext(),"Empty Details",Toast.LENGTH_LONG).show();
            finish();
        }
        data=((ProductApplication)getApplication()).getData();
        store_id = (EditText) findViewById(R.id.store_id);
        store_name = (EditText)findViewById(R.id.store_name);
        store_type = (EditText)findViewById(R.id.store_type);
        owner_name = (EditText)findViewById(R.id.owner_name);
        owner_mobile_number = (EditText)findViewById(R.id.mobile_number);
        no_poster_reason = (EditText)findViewById(R.id.reason_no_poster_picture);

        rootLayout=(LinearLayout) findViewById(R.id.rootlayout);
        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(SurveyPage.this);
            }
        });
        take_store_picture = (Button)findViewById(R.id.take_store_picture);
        take_poster_picture = (Button)findViewById(R.id.take_poster_picture);
        take_retailer_mobile_picture = (Button)findViewById(R.id.take_retailer_phone_picture);

        verify_mobile = (Button) findViewById(R.id.verify_mobile);
        submit = (Button)findViewById(R.id.submit);

        distance_between = (TextView)findViewById(R.id.distance_between);
        marico_id = (TextView)findViewById(R.id.marico_poster_id);

        owner_smart_phone = (RadioGroup)findViewById(R.id.owner_smart_phone);
        marico_salesman_visit = (RadioGroup)findViewById(R.id.marico_salesman_visit);
        sell_hair_oil = (RadioGroup)findViewById(R.id.store_sell_hair_oil);
        poster_pasted = (RadioGroup)findViewById(R.id.poster_pasted);
        number_saved_retailer_phone = (RadioGroup)findViewById(R.id.marico_number_retailer_phone);
        outlet_activated = (RadioGroup)findViewById(R.id.outlet_activated);

        imageview = (ImageView)findViewById(R.id.imageview);
        poster_image = (ImageView)findViewById(R.id.poster_image);
        marico_retailer_phone_image = (ImageView)findViewById(R.id.marico_retailer_phone_image);

        outlet_activated_parent = (LinearLayout)findViewById(R.id.outlet_activated_parent);

        verify_alternate_mobile=(Button) findViewById(R.id.verify_alternate_mobile);
        alternate_mobile_number=(EditText) findViewById(R.id.alternate_mobile_number);

        //Poster Pasted Radio Button Selection Condition
        poster_pasted.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                poster_pasted_radio_button = (RadioButton) findViewById(i);
                if(poster_pasted_radio_button.getText().equals("Yes")){
                    take_poster_picture.setVisibility(View.VISIBLE);
                    no_poster_reason.setVisibility(View.GONE);
                    outlet_activated_parent.setVisibility(View.VISIBLE);
                    poster_image.setVisibility(View.VISIBLE);
                }else{
                    take_poster_picture.setVisibility(View.GONE);
                    no_poster_reason.setVisibility(View.VISIBLE);
                    outlet_activated_parent.setVisibility(View.GONE);
                    poster_image.setVisibility(View.GONE);
                }

            }
        });


        //Marico Number Saved on Retailer Phone Selection Condition
        number_saved_retailer_phone.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                marico_number_retailer_phone_button = (RadioButton) findViewById(i);
                if(marico_number_retailer_phone_button.getText().equals("Yes")){
                    take_retailer_mobile_picture.setVisibility(View.VISIBLE);
                    marico_retailer_phone_image.setVisibility(View.VISIBLE);
                }else{
                    take_retailer_mobile_picture.setVisibility(View.GONE);
                    marico_retailer_phone_image.setVisibility(View.GONE);
                }
            }
        });

        take_store_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IMG_TYPE=STORE_IMAGE;
                dispatchTakePictureIntent();
            }
        });
        take_poster_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IMG_TYPE=POSTER_IMAGE;
                dispatchTakePictureIntent();
            }
        });
        take_retailer_mobile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IMG_TYPE=RETAILER_PHONE_IMAGE;
                dispatchTakePictureIntent();
            }
        });
        verify_alternate_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify_alternate_mobile.setEnabled(false);
                if(!isNetworkAvailable()){
                    Toast.makeText(getApplicationContext(),"No Network Available",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isMobileVerified2) {
                    if(isMsgSent2){
                        Toast.makeText(getApplicationContext(),"Message Already Sent",Toast.LENGTH_LONG).show();
                        return;
                    }
                } else {
                    String no = alternate_mobile_number.getText().toString();
                    if (no.isEmpty() || no.length() < 10) {
                        Log.i(TAG, "no format error");
                        verify_alternate_mobile.setEnabled(true);
                        Toast.makeText(getApplicationContext(),"Enter valid mobile no",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (no.charAt(0) == '6' || no.charAt(0) == '7' || no.charAt(0) == '8' || no.charAt(0) == '9') {
                        Log.i(TAG, "sending msg");
                        final AlertDialog.Builder builder = new AlertDialog.Builder(SurveyPage.this);
                        builder.setTitle("Enter Otp");
                        View inflatedView = LayoutInflater.from(SurveyPage.this).inflate(R.layout.otp_layout, null, false);
                        final EditText otp = (EditText) inflatedView.findViewById(R.id.input);
//                            final ProgressBar progressBar = (ProgressBar) inflatedView.findViewById(R.id.otp_progress);
                        builder.setView(inflatedView);
                        builder.setCancelable(false);
                        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                    progressBar.setVisibility(View.VISIBLE);
//                                    otp.setVisibility(View.GONE);
                                mVerification.verify(otp.getText().toString());
                                Log.i(TAG, "entered otp=" + otp.getText().toString());
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                verify_alternate_mobile.setEnabled(true);
                            }
                        });
                        final AlertDialog alertDialog=builder.create();
//                            builder.create();
                        mVerification = SendOtpVerification.createSmsVerification
                                (SendOtpVerification
                                        .config("" + no).message("Share Your OTP To Verify Your Mobile Number. Your OTP is ##OTP##")
                                        .context(getApplicationContext())
                                        .autoVerification(false)
                                        .build(), new VerificationListener() {
                                    @Override
                                    public void onInitiated(String response) {
                                        Log.i(TAG, "otp initiated=" + response);
                                        isMsgSent2=true;
                                        alertDialog.show();
                                    }

                                    @Override
                                    public void onInitiationFailed(Exception paramException) {
                                        Log.i(TAG, "otp initiated error=" + paramException.toString());
                                        Toast.makeText(getApplicationContext(), "network error", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onVerified(String response) {
                                        Log.i(TAG, "otp verified=" + response);
                                        alertDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "OTP Verification Successful", Toast.LENGTH_SHORT).show();
                                        isMobileVerified2 = true;
                                        isMsgSent2 = true;
                                        alternate_mobile_number.setKeyListener(null);
                                        verify_alternate_mobile.setClickable(false);
                                    }

                                    @Override
                                    public void onVerificationFailed(Exception paramException) {
                                        Log.i(TAG, "otp verification failed=" + paramException.toString());
                                        Toast.makeText(getApplicationContext(), "Incorrect OTP", Toast.LENGTH_SHORT).show();
                                        if(!alertDialog.isShowing()){
                                            alertDialog.show();
                                        }
                                    }
                                });
                        mVerification.initiate();
                    } else {
                        Toast.makeText(getApplicationContext(),"Enter Valid Mobile Number",Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "first char error=" + no.charAt(0));
                    }
                }
            }
        });
        //When Verify Mobile Button Clicked
        verify_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify_mobile.setEnabled(false);
                if(!isNetworkAvailable()){
                    Toast.makeText(getApplicationContext(),"No Network Available",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isMobileVerified) {
                    if(isMsgSent){
                        Toast.makeText(getApplicationContext(),"Message Already Sent",Toast.LENGTH_LONG).show();
                        return;
                    }
                } else {
                    String no = owner_mobile_number.getText().toString();
                    if (no.isEmpty() || no.length() < 10) {
                        Log.i(TAG, "no format error");
                        verify_mobile.setEnabled(true);
                        Toast.makeText(getApplicationContext(),"Enter valid mobile no",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (no.charAt(0) == '6' || no.charAt(0) == '7' || no.charAt(0) == '8' || no.charAt(0) == '9') {
                        Log.i(TAG, "sending msg");
                        final AlertDialog.Builder builder = new AlertDialog.Builder(SurveyPage.this);
                        builder.setTitle("Enter Otp");
                        View inflatedView = LayoutInflater.from(SurveyPage.this).inflate(R.layout.otp_layout, null, false);
                        final EditText otp = (EditText) inflatedView.findViewById(R.id.input);
//                            final ProgressBar progressBar = (ProgressBar) inflatedView.findViewById(R.id.otp_progress);
                        builder.setView(inflatedView);
                        builder.setCancelable(false);
                        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                    progressBar.setVisibility(View.VISIBLE);
//                                    otp.setVisibility(View.GONE);
                                mVerification.verify(otp.getText().toString());
                                Log.i(TAG, "entered otp=" + otp.getText().toString());
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                verify_mobile.setEnabled(true);
                            }
                        });
                        final AlertDialog alertDialog=builder.create();
//                            builder.create();
                        mVerification = SendOtpVerification.createSmsVerification
                                (SendOtpVerification
                                        .config("" + no).message("Share Your OTP To Verify Your Mobile Number. Your OTP is ##OTP##")
                                        .context(getApplicationContext())
                                        .autoVerification(false)
                                        .build(), new VerificationListener() {
                                    @Override
                                    public void onInitiated(String response) {
                                        Log.i(TAG, "otp initiated=" + response);
                                        isMsgSent=true;
                                        alertDialog.show();
                                    }

                                    @Override
                                    public void onInitiationFailed(Exception paramException) {
                                        Log.i(TAG, "otp initiated error=" + paramException.toString());
                                        Toast.makeText(getApplicationContext(), "network error", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onVerified(String response) {
                                        Log.i(TAG, "otp verified=" + response);
                                        alertDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "OTP Verification Successful", Toast.LENGTH_SHORT).show();
                                        isMobileVerified = true;
                                        isMsgSent = true;
                                        owner_mobile_number.setKeyListener(null);
                                        verify_mobile.setClickable(false);
                                    }

                                    @Override
                                    public void onVerificationFailed(Exception paramException) {
                                        Log.i(TAG, "otp verification failed=" + paramException.toString());
                                        Toast.makeText(getApplicationContext(), "Incorrect OTP", Toast.LENGTH_SHORT).show();
                                        if(!alertDialog.isShowing()){
                                            alertDialog.show();
                                        }
                                    }
                                });
                        mVerification.initiate();
                    } else {
                        Toast.makeText(getApplicationContext(),"Enter Valid Mobile Number",Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "first char error=" + no.charAt(0));
                    }
                }

            }
        });


        //Submit Button Clicked
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageview.getDrawable()==null){
                    Toast.makeText(getApplicationContext(),"Take Outlet Picture",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(store_name.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter Store Name",Toast.LENGTH_SHORT).show();
                    return;
                }
//                if(owner_name.getText().toString().isEmpty()){
//                    Toast.makeText(getApplicationContext(),"Enter Owner Name",Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if(owner_mobile_number.getText().toString().isEmpty() && alternate_mobile_number.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter Owner Mobile No ",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(owner_smart_phone.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Check if Owner Has Smart Phone",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(marico_salesman_visit.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Check if Marico SalesMan Visit The Store",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(sell_hair_oil.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Check if Store Sells Hair Oil",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(poster_pasted.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Check If Poster Pasted",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(poster_pasted.getCheckedRadioButtonId()==R.id.yes_poster_pasted && poster_image.getDrawable()==null){
                    Toast.makeText(getApplicationContext(),"Take Poster Image",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(poster_pasted.getCheckedRadioButtonId()==R.id.yes_poster_pasted && outlet_activated.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Check If Outlet Activated",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(poster_pasted.getCheckedRadioButtonId()==R.id.no_poster_pasted && no_poster_reason.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter Reason For No Pasting Poster",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(number_saved_retailer_phone.getCheckedRadioButtonId()==-1){
                    Toast.makeText(getApplicationContext(),"Check If Owner Has Marico Retailer No ",Toast.LENGTH_SHORT).show();
                    return;
                }
                savedata();
            }
        });
        updateViews();
    }
    private void savedata(){
        StoreDetailsEntity entity=new StoreDetailsEntity();
        entity.setStoreId(storeId);
        entity.setStoreName(store_name.getText().toString());
        entity.setStoreCategory(store_type.getText().toString());
        entity.setOwnerName(owner_name.getText().toString());
        entity.setOwnerMobile(owner_mobile_number.getText().toString());
        entity.setOtpSent(isMsgSent);
        entity.setOtpVerified(isMobileVerified);
        entity.setOwnerHasSmartPhone(((RadioButton)findViewById(owner_smart_phone.getCheckedRadioButtonId())).getText().toString());
        entity.setIsMaricoSalesMan(((RadioButton)findViewById(marico_salesman_visit.getCheckedRadioButtonId())).getText().toString());
        entity.setIsHairOil(((RadioButton)findViewById(sell_hair_oil.getCheckedRadioButtonId())).getText().toString());
        entity.setIsPosterPasted(((RadioButton)findViewById(poster_pasted.getCheckedRadioButtonId())).getText().toString());
        entity.setPosterReason(no_poster_reason.getText().toString());
        entity.setIsMaricoNoRetailerPhone(((RadioButton)findViewById(number_saved_retailer_phone.getCheckedRadioButtonId())).getText().toString());
        entity.setIsOutletActivated(outlet_activated.getCheckedRadioButtonId()==-1?"No":((RadioButton)findViewById(outlet_activated.getCheckedRadioButtonId())).getText().toString());
        entity.setSyncStatus(false);
        entity.setSurveyTime("" + new SimpleDateFormat("yyy.MM.dd.HH.mm.ss", Locale.ENGLISH).format(new Date()));
        entity.setAlternateMobile(alternate_mobile_number.getText().toString());
        entity.setOtpSent2(isMsgSent2);
        entity.setOtpVerified2(isMobileVerified2);
        entity.setIsStoreFound("Yes");
        entity.setStoreCondition("Open");
        entity.setVectorId(((ProductApplication)getApplication()).vector_id);
        entity.setUniqueId(((ProductApplication)getApplication()).vector_id+System.currentTimeMillis());
        data.upsert(entity).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Consumer<StoreDetailsEntity>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull StoreDetailsEntity storeDetailsEntity) throws Exception {
                final Integer value = data.update(To_doEntity.class).set(To_doEntity.COMPLETEFLAG, true).where(To_doEntity.STORE_ID.eq(storeId)).get().value();
                if(value==1){
                    Toast.makeText(getApplicationContext(),"Details Saved Successfully",Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(),"Details Saved With Error",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }

    private void updateViews(){
        To_doEntity to_doEntity = data.select(To_doEntity.class).where(To_doEntity.STORE_ID.eq(storeId)).get().firstOrNull();
        if(to_doEntity==null){
            Toast.makeText(getApplicationContext(),"No Data Available Try Again",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        store_id.setText(to_doEntity.getStoreId());
        store_name.setText(to_doEntity.getStoreName());
        store_type.setText(to_doEntity.getStoreCategory());
        store_type.setEnabled(false);
        owner_name.setText(to_doEntity.getOwnerName());
        owner_mobile_number.setText(to_doEntity.getOwnerMobileNo());
        if(to_doEntity.getAlreadyVerified()){
            isMsgSent=true;
            isMobileVerified=true;
            owner_mobile_number.setEnabled(false);
            verify_mobile.setEnabled(false);
        }
        marico_id.setText("Marico Id : "+to_doEntity.getItcStoreId());

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.adjointtechnologies.maricovector",
                        photoFile);
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    takePictureIntent.setClipData(ClipData.newRawUri("", photoURI));
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            File imgfile=new File(mCurrentPhotoPath);
            Bitmap bitmap= BitmapFactory.decodeFile(mCurrentPhotoPath);
            FileOutputStream out=null;
            try{
                out=new FileOutputStream(imgfile);
                boolean compress = bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            }catch (FileNotFoundException e){
                e.printStackTrace();
                Log.i(TAG,"image error="+e.toString());
            }finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG,"out error="+e.toString());
                }
            }
            if(IMG_TYPE==STORE_IMAGE){
                bitmap= Bitmap.createScaledBitmap(bitmap,imageview.getWidth(),imageview.getHeight(),true);
                imageview.setImageBitmap(bitmap);
            }else if(IMG_TYPE==POSTER_IMAGE){
                bitmap= Bitmap.createScaledBitmap(bitmap,poster_image.getWidth(),poster_image.getHeight(),true);
                poster_image.setImageBitmap(bitmap);
            }else if(IMG_TYPE==RETAILER_PHONE_IMAGE){
                bitmap= Bitmap.createScaledBitmap(bitmap,marico_retailer_phone_image.getWidth(),marico_retailer_phone_image.getHeight(),true);
                marico_retailer_phone_image.setImageBitmap(bitmap);
            }
        }
    }
    private File createImageFile() throws IOException {
        if(IMG_TYPE==0){
            return null;
        }
        String imgName=ConstantValues.imagepath+"/"+storeId+"-";
        switch (IMG_TYPE){
            case STORE_IMAGE:
                imgName+="outer";
                break;
            case POSTER_IMAGE:
                imgName+="poster";
                break;
            case RETAILER_PHONE_IMAGE:
                imgName+="marico_retailer_phone_picture";
                break;
            default:
                return null;

        }
        File image = new File(imgName+".jpeg");
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i("currentpath","="+mCurrentPhotoPath);
        return image;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        if(goback && back_time+2000>System.currentTimeMillis()) {
            super.onBackPressed();
        }else {
            Toast.makeText(getApplicationContext(), "Press again to exit", Toast.LENGTH_SHORT).show();
            goback=true;
            back_time=System.currentTimeMillis();
        }
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
