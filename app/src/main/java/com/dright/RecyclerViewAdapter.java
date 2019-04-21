package com.dright;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable {

    private static final String TAG = "RecyclerViewAdapter";

    public ArrayList<ProfileModel> mUser;
    public ArrayList<ProfileModel> filterList;


    private Context mContext;
    ContentFilter filter;

    public RecyclerViewAdapter(Context context, ArrayList<ProfileModel> User) {
        mContext = context;
        mUser = User;
        this.filterList = mUser;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_items, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        view.setClickable(true);

        return holder;
    }



    @SuppressLint("WrongConstant")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ProfileModel profileModel = mUser.get(position);
        ((TextView)holder.ProfileView.findViewById(R.id.txt_user)).setText(profileModel.name);
        ((TextView)holder.ProfileView.findViewById(R.id.txt_user_hash))
                .setText(profileModel.hash);
        holder.ProfileView.findViewById(R.id.txt_user_hash).setVisibility(0);
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),((TextView) holder.ProfileView.findViewById(R.id.txt_user_hash)).getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new ContentFilter(filterList,this);
        }

        return filter;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private View ProfileView;

        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ProfileView = itemView;

            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}





