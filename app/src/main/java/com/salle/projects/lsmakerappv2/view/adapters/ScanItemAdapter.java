package com.salle.projects.lsmakerappv2.view.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.salle.projects.lsmakerappv2.R;
import com.salle.projects.lsmakerappv2.model.BtDevice;
import com.salle.projects.lsmakerappv2.view.callbacks.ListItemCallback;

import java.util.List;

public class ScanItemAdapter extends RecyclerView.Adapter<ScanItemAdapter.ViewHolder>
implements AdapterView.OnItemClickListener {

    private static final String TAG = ScanItemAdapter.class.getName();

    private List<BtDevice> mDevices;
    private int currentItem = -1;
    private ListItemCallback mCallback;
    private Context mContext;


    public ScanItemAdapter(Context context, ListItemCallback callback, List<BtDevice> devices) {
        mDevices = devices;
        mContext = context;
        mCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        return new ScanItemAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mDevices.get(position) != null) {
            StringBuilder name = new StringBuilder();
            name.append("Device's name: ");
            name.append(mDevices.get(position).getName());
            holder.tvName.setText(name);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BtDevice device = mDevices.get(position);
                    mCallback.onItemClick(device, position);
                }
            });
            /*
            if (currentItem == position) {
                Drawable d;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    d = mContext.getDrawable(R.drawable.back_green_rad);
                } else {
                    d = ContextCompat.getDrawable(mContext, R.drawable.back_green_rad);
                }
                holder.itemView.setBackground(d);
            } else {
                Drawable d;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    d = mContext.getDrawable(R.drawable.back_white_rad);
                } else {
                    d = ContextCompat.getDrawable(mContext, R.drawable.back_white_rad);
                }
                holder.itemView.setBackground(d);
            }
            */
            if (mDevices.get(position).isConnected()) {
                holder.tvState.setVisibility(View.VISIBLE);
            } else {
                holder.tvState.setVisibility(View.GONE);
            }

            holder.tvAddress.setText(mDevices.get(position).getAddress());
            byte rssival = mDevices.get(position).getRssiValue().byteValue();
            if (rssival != 0) {
                StringBuilder text =new StringBuilder("Rssi = ").append(String.valueOf(rssival));
                holder.tvRssi.setText(text);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDevices != null ? mDevices.size():0;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BtDevice device = mDevices.get(position);
        mCallback.onItemClick(device, position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvAddress, tvRssi;
        Chip tvState;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.item_device_name);
            tvAddress = (TextView) itemView.findViewById(R.id.item_device_address);
            tvRssi = (TextView) itemView.findViewById(R.id.item_device_rssi);
            tvState = (Chip) itemView.findViewById(R.id.item_device_state);
        }
    }
}
