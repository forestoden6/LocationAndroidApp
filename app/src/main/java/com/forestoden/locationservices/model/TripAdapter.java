package com.forestoden.locationservices.model;

import android.app.Fragment;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forestoden.locationservices.R;
import com.forestoden.locationservices.services.DeleteTripTask;

import java.util.ArrayList;

/**
 * Created by ForestOden on 4/9/2017.
 * Project: LocationServices.
 */

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private ArrayList<Trip> mDataset;
    private Fragment mFragment;

    private int count;

    public interface OnItemLongClickListener {
        public boolean onItemLongClicked(int position);
    }

    public TripAdapter(ArrayList<Trip> dataset, Fragment fragment) {
        mDataset = dataset;
        mFragment = fragment;
        count = 0;
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.past_trip_item, null);
        TripViewHolder viewHolder = new TripViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TripViewHolder holder, final int position) {
        final Trip trip = mDataset.get(position);

        holder.mTextView.setText(trip.toString());

        holder.itemView.setBackgroundColor(trip.isSelected() ? Color.GRAY : Color.WHITE);

        Fragment f = mFragment;

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                trip.setSelected(true);
                holder.itemView.setBackgroundColor(Color.GRAY);
                removeItem(position);
                /*count++;
                mFragment.getActivity().getActionBar().setTitle("Selected: " + count);*/
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                trip.setSelected(false);
                holder.itemView.setBackgroundColor(Color.WHITE);
                count--;
                /*if(count > 0)
                    mFragment.getActivity().getActionBar().setTitle("Selected: " + count);
                else
                    mFragment.getActivity().getActionBar().setTitle(mFragment.getActivity()
                            .getString(R.string.past_trips));*/
            }
        });
    }

    private void removeItem(int position) {
        Trip trip = mDataset.get(position);
        mDataset.remove(position);
        new DeleteTripTask().execute(trip);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataset.size());
    }

    @Override
    public int getItemCount() {
        return (null != mDataset ? mDataset.size() : 0);
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public TripViewHolder(View v) {
            super(v);
            mTextView = (TextView) itemView.findViewById(R.id.trip_entry);
        }
    }
}
