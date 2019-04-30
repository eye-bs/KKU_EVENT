package com.sudjunham.boonyapon;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Event_list> event_lists;
    private LayoutInflater inflater;
    private RecyclerViewItemClickListener recyclerViewItemClickListener;

    //ViewHolder class
    //TextView and ImageView holders are binded with relevant views in item of recyclerview.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName ;
        TextView textViewDate ;
        TextView textViewLocation ;
        ImageView btn_like;

        public int position=0;
        public ViewHolder(View v) {
            super(v);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //When item view is clicked, trigger the itemclicklistener
                        //Because that itemclicklistener is indicated in MainActivity
                        recyclerViewItemClickListener.onItemClick(v, position);
                    }
                });

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //When item view is clicked long, trigger the itemclicklistener
                    //Because that itemclicklistener is indicated in MainActivity
                    recyclerViewItemClickListener.onItemLongClick(v,position);
                    return true;
                }
            });
            textViewName = v.findViewById(R.id.event_name);
            textViewDate = v.findViewById(R.id.event_time);
            textViewLocation = v.findViewById(R.id.event_location);

        }
    }

    public void setOnItemClickListener(RecyclerViewItemClickListener recyclerViewItemClickListener){
        this.recyclerViewItemClickListener=recyclerViewItemClickListener;
    }

    //Constructor of RecyclerViewAdapter
    //It obtains model list coming from MainActivity here
    public RecyclerViewAdapter(Context context, List<Event_list> albumList) {
        this.context=context;
        this.event_lists=albumList;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //Adapter request a new item view
    //Create and return it.
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_listview_layout, parent, false);
        return new ViewHolder(v);
    }

    //Last step before item is placed in recyclerview
    //TextViews and ImageView in viewholder which is attached to view is set with datas in model list
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {



        holder.position=position;
        String evName = event_lists.get(position).getName();
        evName = evName.replaceAll("&quot;", "\"");
        holder.textViewName.setText(evName);
        holder.textViewDate.setText(event_lists.get(position).getDate());
        holder.textViewLocation.setText(event_lists.get(position).getLocation());

//        holder.btn_like.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return event_lists.size();
    }

    public Object getItem(int position) {
        Event_list eventgetItem = event_lists.get(position);
        return eventgetItem;
    }

    //setBackground method is different for some android versions.

    public void setImageViewBackgroundWithADrawable(ImageView image, int drawable){
        if(Build.VERSION.SDK_INT >=22){
            image.setBackground(context.getResources().getDrawable(drawable, null));
        }
        else if(Build.VERSION.SDK_INT >= 16){
            image.setBackground(context.getResources().getDrawable(drawable));
        }else{
            image.setBackgroundDrawable(context.getResources().getDrawable(drawable));
        }
    }

    public List<Event_list> getEvent_lists() {
        return event_lists;
    }
}