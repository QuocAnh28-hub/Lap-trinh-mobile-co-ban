package com.example.btl_datphongkhachsan.adapters;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.btl_datphongkhachsan.BookingDetailActivity;
import com.example.btl_datphongkhachsan.R;
import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.Reservation;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    private List<Reservation> reservationList;
    private OnReservationCancelledListener listener;

    public interface OnReservationCancelledListener {
        void onCancelled();
    }

    public ReservationAdapter(List<Reservation> reservationList) {
        this.reservationList = reservationList;
    }

    public void setOnReservationCancelledListener(OnReservationCancelledListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reservation res = reservationList.get(position);
        holder.tvBookingID.setText("BOOKING ID: " + res.getReservationID());
        holder.tvBookingRoomType.setText(res.getRoomType());
        holder.tvBookingPrice.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", res.getTotalAmount()));
        holder.tvBookingStatus.setText(res.getStatus());
        holder.tvBookingCheckIn.setText(formatDate(res.getCheckIn()));
        holder.tvBookingCheckOut.setText(formatDate(res.getCheckOut()));

        // Tự động gán ảnh dựa trên tên loại phòng
        if (res.getRoomType() != null) {
            String imageName = res.getRoomType().toLowerCase()
                    .replace(" room", "")
                    .replace(" ", "");
            
            int resId = holder.itemView.getContext().getResources().getIdentifier(imageName, "drawable", holder.itemView.getContext().getPackageName());
            if (resId != 0) {
                holder.ivBookingRoom.setImageResource(resId);
            } else {
                holder.ivBookingRoom.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }

        // Click vào View Details để sang trang chi tiết
        holder.btnBookingDetails.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), BookingDetailActivity.class);
            intent.putExtra("RESERVATION", res);
            v.getContext().startActivity(intent);
        });

        // Chỉ hiển thị nút Cancel nếu status là BOOKED
        if ("BOOKED".equalsIgnoreCase(res.getStatus())) {
            holder.btnCancelBooking.setVisibility(View.VISIBLE);
        } else {
            holder.btnCancelBooking.setVisibility(View.GONE);
        }

        holder.btnCancelBooking.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Xác nhận hủy")
                    .setMessage("Bạn có chắc chắn muốn hủy đặt phòng này không?")
                    .setPositiveButton("Hủy ngay", (dialog, which) -> cancelReservation(res.getReservationID(), v.getContext()))
                    .setNegativeButton("Quay lại", null)
                    .show();
        });
    }

    private void cancelReservation(int reservationId, android.content.Context context) {
        RetrofitClient.getApiService().cancelReservation(reservationId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Đã hủy đặt phòng thành công", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onCancelled();
                    }
                } else {
                    Toast.makeText(context, "Hủy thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.length() < 10) return dateStr;
        return dateStr.substring(0, 10);
    }

    @Override
    public int getItemCount() {
        return reservationList == null ? 0 : reservationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookingID, tvBookingRoomType, tvBookingPrice, tvBookingStatus, tvBookingCheckIn, tvBookingCheckOut;
        ImageView ivBookingRoom;
        Button btnCancelBooking, btnBookingDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingID = itemView.findViewById(R.id.tvBookingID);
            tvBookingRoomType = itemView.findViewById(R.id.tvBookingRoomType);
            tvBookingPrice = itemView.findViewById(R.id.tvBookingPrice);
            tvBookingStatus = itemView.findViewById(R.id.tvBookingStatus);
            tvBookingCheckIn = itemView.findViewById(R.id.tvBookingCheckIn);
            tvBookingCheckOut = itemView.findViewById(R.id.tvBookingCheckOut);
            ivBookingRoom = itemView.findViewById(R.id.ivBookingRoom);
            btnCancelBooking = itemView.findViewById(R.id.btnCancelBooking);
            btnBookingDetails = itemView.findViewById(R.id.btnBookingDetails);
        }
    }
}
