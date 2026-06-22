package com.example.btl_datphongkhachsan.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_datphongkhachsan.R;
import com.example.btl_datphongkhachsan.models.WaitingCustomer;

import java.util.List;

public class WaitingCustomerAdapter extends RecyclerView.Adapter<WaitingCustomerAdapter.ViewHolder> {

    private List<WaitingCustomer> customers;

    public WaitingCustomerAdapter(List<WaitingCustomer> customers) {
        this.customers = customers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_waiting_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WaitingCustomer customer = customers.get(position);
        holder.tvCustomerName.setText(customer.getFullName() + " (" + customer.getPhone() + ")");
        holder.tvRoomInfo.setText("🏨 " + customer.getName());
        holder.tvCheckIn.setText(formatDate(customer.getCheckInDate()));
        holder.tvCheckOut.setText(formatDate(customer.getCheckOutDate()));
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.length() < 10) return "";
        try {
            // ISO format: 2026-07-08T00:00:00.000Z -> 08/07/2026
            String datePart = dateStr.split("T")[0];
            String[] parts = datePart.split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) {
            return dateStr;
        }
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvRoomInfo, tvCheckIn, tvCheckOut, tvBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvRoomInfo = itemView.findViewById(R.id.tvRoomInfo);
            tvCheckIn = itemView.findViewById(R.id.tvCheckIn);
            tvCheckOut = itemView.findViewById(R.id.tvCheckOut);
        }
    }
}
