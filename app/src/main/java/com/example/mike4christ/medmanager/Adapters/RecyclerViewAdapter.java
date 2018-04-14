package com.example.mike4christ.medmanager.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mike4christ.medmanager.R;
import com.example.mike4christ.medmanager.UserProfile;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {

    private final List<UserProfile> user;

    private final Context context;

    public RecyclerViewAdapter(Context context, List<UserProfile> user) {
        this.user = user;
        this.context = context;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerViewHolders viewHolder = null;
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_data_list, parent, false);
        viewHolder = new RecyclerViewHolders(layoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {
        holder.profileHeader.setText(user.get(position).getHeader());
        holder.profileContent.setText(user.get(position).getProfileContent());
    }

    @Override
    public int getItemCount() {
        return this.user.size();
    }

}
