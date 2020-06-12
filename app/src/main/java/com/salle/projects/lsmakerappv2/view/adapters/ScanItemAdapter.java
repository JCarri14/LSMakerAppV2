package com.salle.projects.lsmakerappv2.view.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.salle.projects.lsmakerappv2.R;
import com.salle.projects.lsmakerappv2.view.callbacks.ScanItemCallback;
import com.salle.projects.lsmakerappv2.view.ui.ScanActivity;

import java.util.List;
import java.util.Map;

public class ScanItemAdapter extends RecyclerView.Adapter<ScanItemAdapter.ViewHolder>
implements AdapterView.OnItemClickListener {

    private static final String TAG = ScanItemAdapter.class.getName();

    private List<BluetoothDevice> mDevices;
    private Map<String, Integer> mRssiValues;
    private ScanItemCallback mCallback;


    public ScanItemAdapter(ScanItemCallback callback, List<BluetoothDevice> devices, Map<String, Integer> rssiValues ) {
        mDevices = devices;
        mRssiValues = rssiValues;
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
            holder.tvAddress.setText(mDevices.get(position).getAddress());
            byte rssival = (byte) mRssiValues.get(mDevices.get(position).getAddress()).intValue();
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
        BluetoothDevice device = mDevices.get(position);
        mCallback.onItemClick(device);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvAddress, tvRssi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.item_device_name);
            tvAddress = (TextView) itemView.findViewById(R.id.item_device_address);
            tvRssi = (TextView) itemView.findViewById(R.id.item_device_rssi);
        }
    }
}
