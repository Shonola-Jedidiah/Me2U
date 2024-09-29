package com.luhyah.me2u;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class PairedDevices_RecyclerViewAdapter extends RecyclerView.Adapter<PairedDevices_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<PairedDevicesModel> pairedDevicesModels;

    public PairedDevices_RecyclerViewAdapter(Context context, ArrayList<PairedDevicesModel> pairedDevicesModels){
        this.context = context;
        this.pairedDevicesModels = pairedDevicesModels;
    }

    @NonNull
    @Override
    public PairedDevices_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.users, parent,false);
        return new PairedDevices_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PairedDevices_RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.Name.setText(pairedDevicesModels.get(position).DeviceName);
        holder.Address.setText(pairedDevicesModels.get(position).MacAddress);
    }

    @Override
    public int getItemCount() {
        return pairedDevicesModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Name, Address;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.DeviceName);
            Address = itemView.findViewById(R.id.MacAddress);
        }
    }
}
