package com.example.mike4christ.medmanager.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.mike4christ.medmanager.R;

public class RecyclerViewHolders extends RecyclerView.ViewHolder{

    private static final String TAG = RecyclerViewHolders.class.getSimpleName();

    public final TextView profileHeader;

    public final TextView profileContent;

    public RecyclerViewHolders(final View itemView) {
        super(itemView);
        profileHeader = itemView.findViewById(R.id.heading);
        profileContent = itemView.findViewById(R.id.profile_content);
    }
}
