package com.example.btl_datphongkhachsan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_datphongkhachsan.models.RoomType;

import java.util.Locale;

public class RoomDetailsActivity extends AppCompatActivity {

    private ImageView ivRoomDetail, ivBack;
    private TextView tvRoomTitle, tvPrice, tvPriceBottom, tvRoomDesc;
    private RoomType roomType;
    private String checkIn, checkOut;
    private int numRooms, numPeople;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        ivRoomDetail = findViewById(R.id.ivRoomDetail);
        tvRoomTitle = findViewById(R.id.tvRoomTitle);
        tvPrice = findViewById(R.id.tvPrice);
        tvPriceBottom = findViewById(R.id.tvPriceBottom);
        tvRoomDesc = findViewById(R.id.tvRoomDesc);

        // Nhận dữ liệu an toàn
        try {
            roomType = (RoomType) getIntent().getSerializableExtra("ROOM_TYPE");
            checkIn = getIntent().getStringExtra("CHECK_IN");
            checkOut = getIntent().getStringExtra("CHECK_OUT");
            numRooms = getIntent().getIntExtra("NUM_ROOMS", 1);
            numPeople = getIntent().getIntExtra("NUM_PEOPLE", 1);
        } catch (Exception e) {
            Log.e("ROOM_DETAILS", "Error receiving intent data: " + e.getMessage());
        }

        if (roomType != null) {
            tvRoomTitle.setText(roomType.getName());
            
            // Tự động gán ảnh: Chuyển tên về chữ thường và xóa khoảng trắng (ví dụ: "Double 1" -> "double1")
            String imageName = roomType.getName().toLowerCase().replace(" ", "");
            int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
            if (resId != 0) {
                ivRoomDetail.setImageResource(resId);
            } else {
                // Ảnh mặc định hệ thống nếu không tìm thấy
                ivRoomDetail.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            double displayPrice = roomType.getDisplayPrice();
            String priceStr = String.format(Locale.getDefault(), "%,.0f VNĐ", displayPrice);
            tvPrice.setText(priceStr);
            tvPriceBottom.setText(priceStr + " / đêm");
            tvRoomDesc.setText(roomType.getDescription());
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin phòng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Button btnBook = findViewById(R.id.btnBookThisSuite);
        if (btnBook != null) {
            btnBook.setOnClickListener(v -> {
                if (roomType == null) {
                    Toast.makeText(RoomDetailsActivity.this, "Dữ liệu phòng bị lỗi", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(RoomDetailsActivity.this, BookingConfirmActivity.class);
                intent.putExtra("ROOM_TYPE", roomType);
                intent.putExtra("CHECK_IN", checkIn);
                intent.putExtra("CHECK_OUT", checkOut);
                intent.putExtra("NUM_ROOMS", numRooms);
                intent.putExtra("NUM_PEOPLE", numPeople);
                startActivity(intent);
            });
        }

        ivBack = findViewById(R.id.backIntent);
        ivBack.setOnClickListener(v -> {
            finish();
        });


    }
}
