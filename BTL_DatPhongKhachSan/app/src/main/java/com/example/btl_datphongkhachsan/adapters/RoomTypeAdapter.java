package com.example.btl_datphongkhachsan.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.btl_datphongkhachsan.R;
import com.example.btl_datphongkhachsan.RoomDetailsActivity;
import com.example.btl_datphongkhachsan.models.RoomType;
import java.util.List;
import java.util.Locale;

public class RoomTypeAdapter extends RecyclerView.Adapter<RoomTypeAdapter.ViewHolder> {

    private List<RoomType> roomTypeList;
    private String checkIn, checkOut;
    private int numRooms = 1, numPeople = 1;

    public RoomTypeAdapter(List<RoomType> roomTypeList) {
        this.roomTypeList = roomTypeList;
    }

    public void setSearchParameters(String checkIn, String checkOut, int numRooms, int numPeople) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numRooms = numRooms;
        this.numPeople = numPeople;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_available_room_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoomType roomType = roomTypeList.get(position);
        holder.tvRoomName.setText(roomType.getName());
        holder.tvRoomDesc.setText("Capacity: " + roomType.getCapacity() + " - " + roomType.getDescription());
        
        holder.tvPrice.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", roomType.getDisplayPrice()) + " / đêm");

        // Tự động gán ảnh: Chuyển tên về chữ thường và XÓA KHOẢNG TRẮNG (ví dụ: "Double 1" -> "double1")
        String imageName = roomType.getName().toLowerCase().replace(" ", "");
        int resId = holder.itemView.getContext().getResources().getIdentifier(imageName, "drawable", holder.itemView.getContext().getPackageName());
        
        if (resId != 0) {
            holder.ivRoomImage.setImageResource(resId);
        } else {
            // Nếu không tìm thấy ảnh, sử dụng icon mặc định của hệ thống Android
            holder.ivRoomImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        
        holder.btnDetails.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RoomDetailsActivity.class);
            intent.putExtra("ROOM_TYPE", roomType);
            intent.putExtra("CHECK_IN", checkIn);
            intent.putExtra("CHECK_OUT", checkOut);
            intent.putExtra("NUM_ROOMS", numRooms);
            intent.putExtra("NUM_PEOPLE", numPeople);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return roomTypeList == null ? 0 : roomTypeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName, tvRoomDesc, tvPrice;
        ImageView ivRoomImage;
        Button btnDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvRoomDesc = itemView.findViewById(R.id.tvRoomDesc);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivRoomImage = itemView.findViewById(R.id.ivRoomImage);
            btnDetails = itemView.findViewById(R.id.btnDetails);
        }
    }
}
