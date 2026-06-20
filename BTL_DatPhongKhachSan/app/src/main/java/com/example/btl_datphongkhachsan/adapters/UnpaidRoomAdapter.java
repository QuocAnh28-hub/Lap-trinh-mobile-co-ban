package com.example.btl_datphongkhachsan.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_datphongkhachsan.R;
import com.example.btl_datphongkhachsan.R_Payment;
import com.example.btl_datphongkhachsan.models.PendingInvoice;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class UnpaidRoomAdapter extends RecyclerView.Adapter<UnpaidRoomAdapter.ViewHolder> {

    private List<PendingInvoice> unpaidRooms;

    public UnpaidRoomAdapter(List<PendingInvoice> unpaidRooms) {
        this.unpaidRooms = unpaidRooms;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_unpaid_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PendingInvoice room = unpaidRooms.get(position);
        holder.tvCustomerName.setText(room.getGuestName());
        holder.tvRoomNumber.setText(String.valueOf(room.getStayID()));
        
        String dateRange = formatDate(room.getActualCheckIn()) + " - " + formatDate(room.getActualCheckOut());
        holder.tvBookingDate.setText(dateRange);

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(formatter.format(room.getTotalAmount()));

        holder.btnCreateInvoice.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), R_Payment.class);
            intent.putExtra("STAY_ID", room.getStayID());
            v.getContext().startActivity(intent);
        });
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.length() < 10) return "";
        String[] parts = dateStr.split("T")[0].split("-");
        if (parts.length == 3) {
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        }
        return dateStr;
    }

    @Override
    public int getItemCount() {
        return unpaidRooms.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvRoomNumber, tvBookingDate, tvPrice;
        Button btnCreateInvoice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnCreateInvoice = itemView.findViewById(R.id.btnCreateInvoice);
        }
    }
}
