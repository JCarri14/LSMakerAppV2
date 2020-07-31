package com.salle.projects.lsmakerappv2.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.salle.projects.lsmakerappv2.R;
import com.salle.projects.lsmakerappv2.model.BtDevice;
import com.salle.projects.lsmakerappv2.view.callbacks.ListItemCallback;

import java.util.List;

public class OptionItemAdapter extends RecyclerView.Adapter<OptionItemAdapter.ViewHolder> {

    private static final String TAG = OptionItemAdapter.class.getName();

    private List<BtDevice> mDevices;
    private ListItemCallback mCallback;

    public OptionItemAdapter(ListItemCallback callback, List<BtDevice> devices) {
        mDevices = devices;
        mCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_alt, parent, false);
        return new OptionItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mDevices.get(position) != null) {
            holder.tvName.setText(mDevices.get(position).getName());
            holder.tvAddress.setText(mDevices.get(position).getAddress());
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onDeleteItem(mDevices.get(position));
                }
            });
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onItemClick(mDevices.get(position), position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDevices != null ? mDevices.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvAddress;
        ImageView ivDelete;
        ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.item_device_alt_name);
            tvAddress = (TextView) itemView.findViewById(R.id.item_device_alt_address);
            ivDelete = (ImageView) itemView.findViewById(R.id.item_device_alt_del);
            layout = itemView.findViewById(R.id.item_device_alt_layout);
        }
    }
}
