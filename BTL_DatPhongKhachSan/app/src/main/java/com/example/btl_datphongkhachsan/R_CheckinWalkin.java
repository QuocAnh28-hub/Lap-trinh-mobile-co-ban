package com.example.btl_datphongkhachsan;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.Room;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class R_CheckinWalkin extends AppCompatActivity {

    private RelativeLayout layoutCheckoutDate;
    private EditText etCheckoutDate, etFullName, etIdCard;
    private Spinner spinnerRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.r_checkin_walkin);

        initViews();
        setupEdgeToEdge();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        layoutCheckoutDate.setOnClickListener(v -> showDatePicker());
        etCheckoutDate.setOnClickListener(v -> showDatePicker());
        findViewById(R.id.btnCheckIn).setOnClickListener(v -> performCheckIn());
    }

    private void initViews() {
        layoutCheckoutDate = findViewById(R.id.layoutCheckoutDate);
        etCheckoutDate = findViewById(R.id.etCheckoutDate);
        etFullName = findViewById(R.id.etFullName);
        etIdCard = findViewById(R.id.etIdCard);
        spinnerRoom = findViewById(R.id.spinnerRoom);
    }

    private void performCheckIn() {
        String fullName = etFullName.getText().toString().trim();
        String cccd = etIdCard.getText().toString().trim();
        String checkoutDate = etCheckoutDate.getText().toString().trim();
        Room selectedRoom = (Room) spinnerRoom.getSelectedItem();

        if (fullName.isEmpty() || cccd.isEmpty() || checkoutDate.isEmpty() || selectedRoom == null) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("FullName", fullName);
        body.put("CCCD", cccd);
        body.put("RoomID", selectedRoom.getRoomID());
        body.put("ExpectedCheckOut", checkoutDate);

        RetrofitClient.getApiService().checkInWalkin(body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(R_CheckinWalkin.this, "Check-in walk-in thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(R_CheckinWalkin.this, "Check-in thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(R_CheckinWalkin.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, (monthOfYear + 1), dayOfMonth);
            etCheckoutDate.setText(selectedDate);
            fetchAvailableRooms(selectedDate);
        }, year, month, day);
        
        // Chỉ cho phép chọn từ ngày mai
        c.add(Calendar.DAY_OF_MONTH, 1);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();
    }

    private void fetchAvailableRooms(String expectedCheckOut) {
        RetrofitClient.getApiService().getAvailableRoomsForWalkin(expectedCheckOut).enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Room> rooms = response.body();
                    ArrayAdapter<Room> adapter = new ArrayAdapter<>(R_CheckinWalkin.this,
                            android.R.layout.simple_spinner_item, rooms);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerRoom.setAdapter(adapter);
                    
                    if (rooms.isEmpty()) {
                        Toast.makeText(R_CheckinWalkin.this, "Không còn phòng trống trong thời gian này", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Hiển thị mã lỗi cụ thể để debug
                    String errorMsg = "Lỗi server: " + response.code();
                    if (response.code() == 405) errorMsg += " (Sai phương thức POST/GET)";
                    if (response.code() == 404) errorMsg += " (Không tìm thấy đường dẫn)";
                    Toast.makeText(R_CheckinWalkin.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Toast.makeText(R_CheckinWalkin.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupEdgeToEdge() {
        View mainView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left + 48, systemBars.top + 48, systemBars.right + 48, systemBars.bottom + 48);
            return insets;
        });
    }
}
