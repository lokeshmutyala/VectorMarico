package com.adjointtechnologies.vectormarico.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adjointtechnologies.vectormarico.ImageActivity;
import com.adjointtechnologies.vectormarico.ProductApplication;
import com.adjointtechnologies.vectormarico.R;
import com.adjointtechnologies.vectormarico.StoreData;
import com.adjointtechnologies.vectormarico.SurveyPage;

import java.text.DecimalFormat;
import java.util.List;

import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

/**
 * Created by lokeshmutyala on 31-10-2017.
 */

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.MyViewHolder>{
    private List<StoreData> storeDataList;
    private ReactiveEntityStore<Persistable> data;
int selecteditem=-1;
    Context context;
    public AdapterClass(List<StoreData> storeDataList , Context context) {
        this.storeDataList = storeDataList;
        this.context=context;
        data= ((ProductApplication)context).getData();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        if(selecteditem==position ){
                holder.navigate.setVisibility(View.VISIBLE);
            holder.landmark.setVisibility(View.VISIBLE);
            holder.landmark.setText("Landmark : "+storeDataList.get(position).getLandmark());
            holder.mobile.setVisibility(View.VISIBLE);
            holder.mobile.setText("Mobile : "+storeDataList.get(position).getMobile());
            holder.category.setVisibility(View.VISIBLE);
            holder.category.setText("Category : "+storeDataList.get(position).getCategory());
            holder.showimage.setVisibility(View.VISIBLE);
            holder.action.setVisibility(View.VISIBLE);
            holder.showimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context,ImageActivity.class);
                    intent.putExtra("storeid",storeDataList.get(position).getStoreid());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
                holder.navigate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(context, "button clicked", Toast.LENGTH_SHORT).show();
                        Uri gmmIntentUri = Uri.parse("geo:0,0?q="+storeDataList.get(position).getLatitude()+","+storeDataList.get(position).getLongitude()+"("+storeDataList.get(position).getStore_name()+")");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(mapIntent);
                        }
                    }
                });
            holder.action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setEnabled(false);

//                    if(AuditPage.lastTime!=0 && (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())-TimeUnit.MILLISECONDS.toSeconds(AuditPage.lastTime))<30) {
////                        startActivity(new Intent(getApplicationContext(), AuditPage.class));
//                        Toast.makeText(context,"Wait For Atleast 30 sec",Toast.LENGTH_SHORT).show();
//                        return;
//                    }
                    if(storeDataList.get(position).getDistance()<100 && storeDataList.get(position).getDistance()!=0) {
                        Intent intent = new Intent(context, SurveyPage.class);
                        intent.putExtra("storeid", storeDataList.get(position).getStoreid());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }else {
                        Toast.makeText(context,"You Are 100m Away From Store, Move Around To Refresh Distance",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            holder.action.setEnabled(true);
            holder.navigate.setVisibility(View.GONE);
            holder.showimage.setVisibility(View.GONE);
            holder.action.setVisibility(View.GONE);
            holder.landmark.setVisibility(View.GONE);
            holder.mobile.setVisibility(View.GONE);
            holder.category.setVisibility(View.GONE);
        }
        holder.storeName.setText(storeDataList.get(position).getStore_name());
        holder.distance.setText(""+new DecimalFormat("#.##").format(storeDataList.get(position).getDistance()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selecteditem=position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return storeDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView storeName,distance,landmark,mobile,category;
        Button navigate,showimage,action;
        LinearLayout row;
        public MyViewHolder(View itemView) {
            super(itemView);
            storeName=(TextView)itemView.findViewById(R.id.store_name);
            distance=(TextView)itemView.findViewById(R.id.distance);
            navigate=(Button)itemView.findViewById(R.id.navigate);
            showimage=(Button)itemView.findViewById(R.id.show_pic);
            action=(Button)itemView.findViewById(R.id.action);
            row=(LinearLayout)itemView.findViewById(R.id.row);
            landmark=(TextView)itemView.findViewById(R.id.list_landmark);
            mobile=(TextView)itemView.findViewById(R.id.mobile_list);
            category=(TextView)itemView.findViewById(R.id.list_category);
        }
    }

}
