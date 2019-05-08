package com.dright;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<CommentsRecyclerViewAdapter.ViewHolder> {

    public ArrayList<CommentsModel> mUser;


    private Context mContext;
    ContentFilter filter;

    public CommentsRecyclerViewAdapter(Context context, ArrayList<CommentsModel> User) {
        mContext = context;
        mUser = User;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dilema_comment_items, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        view.setClickable(true);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int i) {
        CommentsModel commentsModel = mUser.get(i);
        ((TextView)holder.ProfileView.findViewById(R.id.txt_comment_user)).setText(commentsModel.name);
        ((TextView)holder.ProfileView.findViewById(R.id.user_comment)).setText(commentsModel.comment);
        ((TextView)holder.ProfileView.findViewById(R.id.txt_usercomment_hash)).setText(commentsModel.hash);
        if(commentsModel.image.equals(""))
        {

        }
        else {
            Glide.with(holder.ProfileView.getContext()).load(commentsModel.image).apply(RequestOptions.circleCropTransform()).
                    into((ImageView) holder.ProfileView.findViewById(R.id.user_comment_profile));

        }
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private View ProfileView;

        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ProfileView = itemView;

            parentLayout = itemView.findViewById(R.id.comments_parent_layout);
        }
    }
}
