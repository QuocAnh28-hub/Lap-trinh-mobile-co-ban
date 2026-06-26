package com.example.btl_datphongkhachsan;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.AvailableRoom;
import com.example.btl_datphongkhachsan.models.CheckInRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class R_CheckIn_Booking extends AppCompatActivity {

    private EditText edtCustomerName, edtRoomType;
    private Spinner spnRoom;
    private int reservationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.r_check_in_booking);

        initViews();
        getDataFromIntent();
        setupEdgeToEdge();
        fetchAvailableRooms();

        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
        findViewById(R.id.btnCheckIn).setOnClickListener(v -> performCheckIn());
    }

    private void performCheckIn() {
        AvailableRoom selectedRoom = (AvailableRoom) spnRoom.getSelectedItem();
        if (selectedRoom == null) {
            Toast.makeText(this, "Chưa có phòng sẵn sàng", Toast.LENGTH_SHORT).show();
            return;
        }

        CheckInRequest request = new CheckInRequest(reservationId, selectedRoom.getRoomID());
        RetrofitClient.getApiService().checkInByReservation(request).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(R_CheckIn_Booking.this, "Check-in thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Quay lại trang trước
                } else {
                    Toast.makeText(R_CheckIn_Booking.this, "Check-in thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(R_CheckIn_Booking.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        edtCustomerName = findViewById(R.id.edtCustomerName);
        edtRoomType = findViewById(R.id.edtRoomType);
        spnRoom = findViewById(R.id.spnRoom);
    }

    private void getDataFromIntent() {
        String customerName = getIntent().getStringExtra("CUSTOMER_NAME");
        String roomType = getIntent().getStringExtra("ROOM_TYPE");
        reservationId = getIntent().getIntExtra("RESERVATION_ID", -1);

        edtCustomerName.setText(customerName);
        edtRoomType.setText(roomType);
    }

    private void fetchAvailableRooms() {
        if (reservationId == -1) return;

        RetrofitClient.getApiService().getAvailableRoomsForCheckIn(reservationId).enqueue(new Callback<List<AvailableRoom>>() {
            @Override
            public void onResponse(Call<List<AvailableRoom>> call, Response<List<AvailableRoom>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AvailableRoom> rooms = response.body();
                    ArrayAdapter<AvailableRoom> adapter = new ArrayAdapter<>(R_CheckIn_Booking.this,
                            android.R.layout.simple_spinner_item, rooms);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnRoom.setAdapter(adapter);
                } else {
                    Toast.makeText(R_CheckIn_Booking.this, "Không thể tải danh sách phòng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AvailableRoom>> call, Throwable t) {
                Toast.makeText(R_CheckIn_Booking.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int padding20dp = (int) (20 * getResources().getDisplayMetrics().density);
            v.setPadding(
                    systemBars.left + padding20dp,
                    systemBars.top + padding20dp,
                    systemBars.right + padding20dp,
                    systemBars.bottom + padding20dp
            );
            return insets;
        });
    }
}
