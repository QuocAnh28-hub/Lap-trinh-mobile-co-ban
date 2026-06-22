package com.example.btl_datphongkhachsan.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_datphongkhachsan.R;
import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.Room;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomStatusAdapter extends RecyclerView.Adapter<RoomStatusAdapter.ViewHolder> {

    private List<Room> roomList;

    public RoomStatusAdapter(List<Room> roomList) {
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_status, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = roomList.get(position);
        
        holder.tvRoomId.setText("#" + room.getRoomID());
        holder.tvRoomNumber.setText("Phòng " + room.getRoomNumber());
        holder.tvRoomType.setText(room.getRoomTypeName());
        holder.tvStatusLabel.setText(room.getStatus());
        holder.tvGuestCount.setText(room.getCapacity() + " Khách");

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvRoomPrice.setText(formatter.format(room.getDefaultPrice()));

        updateUIBasedOnStatus(holder, room);
    }

    private void updateUIBasedOnStatus(ViewHolder holder, Room room) {
        int statusColor;
        String status = room.getStatus();

        // Ẩn nút mặc định
        holder.btnAction.setVisibility(View.GONE);

        if ("AVAILABLE".equalsIgnoreCase(status)) {
            statusColor = Color.parseColor("#00FF15");
            holder.tvStatusLabel.setBackgroundResource(R.drawable.bg_status_available);
        } else if ("OCCUPIED".equalsIgnoreCase(status)) {
            statusColor = Color.parseColor("#FF0000");
            holder.tvStatusLabel.setBackgroundResource(R.drawable.bg_badge_blue);
        } else if ("DIRTY".equalsIgnoreCase(status)) {
            statusColor = Color.parseColor("#ECF004");
            holder.tvStatusLabel.setBackgroundResource(R.drawable.bg_status_dirty);
            
            // Hiển thị nút dọn dẹp
            holder.btnAction.setVisibility(View.VISIBLE);
            holder.btnAction.setText("Dọn dẹp");
            holder.btnAction.setOnClickListener(v -> performCleanRoom(holder, room));
        } else if ("MAINTENANCE".equalsIgnoreCase(status)) {
            statusColor = Color.parseColor("#FFEBEE");
            holder.tvStatusLabel.setBackgroundResource(R.drawable.bg_status_maintenance);
        } else {
            statusColor = Color.GRAY;
        }

        holder.viewStatusIndicator.setBackgroundColor(statusColor);
        holder.tvStatusLabel.setText(status.toUpperCase());
    }

    private void performCleanRoom(ViewHolder holder, Room room) {
        RetrofitClient.getApiService().cleanRoom(room.getRoomID()).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message");
                    Toast.makeText(holder.itemView.getContext(), message, Toast.LENGTH_SHORT).show();
                    
                    // Cập nhật UI ngay lập tức mà không cần load lại toàn bộ list
                    room.setStatus("AVAILABLE");
                    updateUIBasedOnStatus(holder, room);
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Lỗi khi dọn phòng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(holder.itemView.getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomId, tvStatusLabel, tvRoomNumber, tvRoomType, tvGuestCount, tvRoomPrice;
        View viewStatusIndicator;
        MaterialButton btnAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomId = itemView.findViewById(R.id.tvRoomId);
            tvStatusLabel = itemView.findViewById(R.id.tvStatusLabel);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
            tvRoomType = itemView.findViewById(R.id.tvRoomType);
            tvGuestCount = itemView.findViewById(R.id.tvGuestCount);
            tvRoomPrice = itemView.findViewById(R.id.tvRoomPrice);
            viewStatusIndicator = itemView.findViewById(R.id.viewStatusIndicator);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}
