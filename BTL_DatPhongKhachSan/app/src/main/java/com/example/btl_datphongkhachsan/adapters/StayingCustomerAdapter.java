package com.example.btl_datphongkhachsan.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_datphongkhachsan.R;
import com.example.btl_datphongkhachsan.R_CallService;
import com.example.btl_datphongkhachsan.R_ChangeRoom;
import com.example.btl_datphongkhachsan.R_CheckOut;
import com.example.btl_datphongkhachsan.R_ExtendRoom;
import com.example.btl_datphongkhachsan.R_Payment;
import com.example.btl_datphongkhachsan.models.StayingCustomer;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class StayingCustomerAdapter extends RecyclerView.Adapter<StayingCustomerAdapter.ViewHolder> {

    private List<StayingCustomer> customers;

    public StayingCustomerAdapter(List<StayingCustomer> customers) {
        this.customers = customers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staying_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StayingCustomer customer = customers.get(position);
        holder.tvGuestName.setText(customer.getFullName());
        holder.tvStatus.setText(customer.getStatus());
        holder.tvRoomInfo.setText("🏨 Phòng " + customer.getRoomNumber());
        holder.tvCheckInTime.setText(formatDate(customer.getCheckInDate()));
        holder.tvCheckOutTime.setText(formatDate(customer.getCheckOutDate()));
        
        holder.tvVipBadge.setVisibility(View.GONE);

        // Xử lý nút Chuyển phòng
        holder.btnChangeRoom.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), R_ChangeRoom.class);
            intent.putExtra("STAY_ID", customer.getStayID());
            intent.putExtra("OLD_ROOM_ID", customer.getRoomID());
            intent.putExtra("CURRENT_ROOM", customer.getRoomNumber());
            v.getContext().startActivity(intent);
        });

        // Xử lý nút Gia hạn
        holder.btnExtend.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), R_ExtendRoom.class);
            intent.putExtra("STAY_ID", customer.getStayID());
            intent.putExtra("CUSTOMER_NAME", customer.getFullName());
            intent.putExtra("ROOM_NUMBER", customer.getRoomNumber());
            v.getContext().startActivity(intent);
        });

        // Xử lý nút Gọi dịch vụ
        holder.btnService.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), R_CallService.class);
            intent.putExtra("STAY_ID", customer.getStayID());
            intent.putExtra("CUSTOMER_NAME", customer.getFullName());
            intent.putExtra("ROOM_NUMBER", customer.getRoomNumber());
            v.getContext().startActivity(intent);
        });

        // Xử lý nút Check-out
        holder.btnCheckOut.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), R_CheckOut.class);
            // Truyền dữ liệu sang trang Check-out
            intent.putExtra("STAY_ID", customer.getStayID());
            intent.putExtra("CUSTOMER_NAME", customer.getFullName());
            intent.putExtra("ROOM_NUMBER", customer.getRoomNumber());
            v.getContext().startActivity(intent);
        });
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.length() < 10) return "";
        try {
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
        TextView tvGuestName, tvStatus, tvRoomInfo, tvCheckInTime, tvCheckOutTime, tvVipBadge;
        MaterialButton btnCheckOut;
        View btnChangeRoom, btnExtend, btnService;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGuestName = itemView.findViewById(R.id.tvGuestName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRoomInfo = itemView.findViewById(R.id.tvRoomInfo);
            tvCheckInTime = itemView.findViewById(R.id.tvCheckInTime);
            tvCheckOutTime = itemView.findViewById(R.id.tvCheckOutTime);
            tvVipBadge = itemView.findViewById(R.id.tvVipBadge);
            btnCheckOut = itemView.findViewById(R.id.btnCheckOut);
            btnChangeRoom = itemView.findViewById(R.id.btnChangeRoom);
            btnExtend = itemView.findViewById(R.id.btnExtend);
            btnService = itemView.findViewById(R.id.btnService);
        }
    }
}
