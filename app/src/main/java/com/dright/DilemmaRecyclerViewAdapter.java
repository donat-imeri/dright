package com.dright;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class DilemmaRecyclerViewAdapter extends RecyclerView.Adapter<DilemmaRecyclerViewAdapter.ViewHolder> {

    public ArrayList<DilemmaModel> mDilemma;


    private Context mContext;


    public DilemmaRecyclerViewAdapter(Context context, ArrayList<DilemmaModel> Dilemma) {
        mContext = context;
        mDilemma = Dilemma;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dilemmas_in_progress_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        view.setClickable(true);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {
        DilemmaModel dilemmaModel = mDilemma.get(i);
        ((TextView)holder.ProfileView.findViewById(R.id.dilemma_description)).setText(dilemmaModel.description);
        ((TextView)holder.ProfileView.findViewById(R.id.dilemma_comment)).setText(dilemmaModel.commentNumber);
        ((TextView)holder.ProfileView.findViewById(R.id.dilemma_hash)).setText(dilemmaModel.hash);
        ((TextView)holder.ProfileView.findViewById(R.id.dilemma_time)).setText(dilemmaModel.timeLeft);
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DecisionsAcitivity.class); //DecisionDetails... se kam at klasen ende
                intent.putExtra("dilema_hash",((TextView)holder.ProfileView.findViewById(R.id.dilemma_hash)).getText());
                mContext.startActivity(intent);
            }
        });

    }



    @Override
    public int getItemCount() {
        return mDilemma.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private View ProfileView;

        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ProfileView = itemView;
            parentLayout = itemView.findViewById(R.id.dilemma_in_progress_layout);
        }
    }
}

