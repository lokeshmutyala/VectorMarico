package com.adjointtechnologies.vectormarico;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class ImageActivity extends AppCompatActivity {
ImageView imageView;
    boolean first=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView=(ImageView)findViewById(R.id.imageView);
        final String storeid=getIntent().getStringExtra("storeid");
        if(storeid.isEmpty()){
            Toast.makeText(getApplicationContext(),"error getting image",Toast.LENGTH_SHORT).show();
            finish();
        }
        Log.i("imagetest","storeid="+storeid);
        String url="https://firebasestorage.googleapis.com/v0/b/storechecker-9a5cb.appspot.com/o/images%2F"+storeid+".jpeg?alt=media&token=a630e4ae-f7f2-4a25-af9f-e98685b0bd03";
        Picasso picasso=new Picasso.Builder(this).listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                loadImage(storeid);
                Log.i("imagetest","error="+exception.toString());
            }
        }).build();
        picasso.load(url).into(imageView);
    }
    private void loadImage(final String storeid){
        Log.i("imagetest","sub storeid="+storeid);
        String url="https://firebasestorage.googleapis.com/v0/b/storechecker-9a5cb.appspot.com/o/images%2F"+storeid+"-outer.jpeg?alt=media&token=a630e4ae-f7f2-4a25-af9f-e98685b0bd03";
        if(first){
            first=false;

//            Picasso.with(getApplicationContext()).load(url).error(R.drawable.error)
//                    .into(imageView);
            Picasso picasso=new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
//                   String storeid_change="AUD999"+storeid;
                    loadImage(storeid);
                }
            }).build();
            picasso.load(url).into(imageView);

        }else {
            Picasso.with(getApplicationContext()).load(url).error(R.drawable.error)
                    .into(imageView);
        }

    }
}
