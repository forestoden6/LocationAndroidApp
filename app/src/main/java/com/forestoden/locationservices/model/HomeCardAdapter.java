package com.forestoden.locationservices.model;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forestoden.locationservices.R;

/**
 * Created by ForestOden on 4/16/2017.
 * Project: LocationServices.
 */

public class HomeCardAdapter extends RecyclerView.Adapter<HomeCardAdapter.ViewHolder> {

    private final String TAG = HomeCardAdapter.class.getName();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_home, null);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position) {
                    case 0:
                        Log.d(TAG, "Position 0");
                        break;
                    case 1:
                        Log.d(TAG, "Position 1");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

}
