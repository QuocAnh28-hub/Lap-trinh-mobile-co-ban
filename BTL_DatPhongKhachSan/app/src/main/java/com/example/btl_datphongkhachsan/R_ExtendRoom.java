package com.example.btl_datphongkhachsan;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl_datphongkhachsan.api.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class R_ExtendRoom extends AppCompatActivity {

    private EditText edtCustomerName, edtRoomNumber, edtNewCheckOutDate, edtExtendInfo;
    private int stayId;
    private String currentCheckOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.r_extend_room);

        initViews();
        getDataFromIntent();
        setupEdgeToEdge();

        edtNewCheckOutDate.setOnClickListener(v -> showDatePicker());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
        findViewById(R.id.btnClose).setOnClickListener(v -> finish());
        findViewById(R.id.btnConfirmExtend).setOnClickListener(v -> performExtend());
    }

    private void initViews() {
        edtCustomerName = findViewById(R.id.edtCustomerName);
        edtRoomNumber = findViewById(R.id.edtRoomNumber);
        edtNewCheckOutDate = findViewById(R.id.edtNewCheckOutDate);
    }

    private void getDataFromIntent() {
        stayId = getIntent().getIntExtra("STAY_ID", -1);
        String customerName = getIntent().getStringExtra("CUSTOMER_NAME");
        String roomNumber = getIntent().getStringExtra("ROOM_NUMBER");
        currentCheckOut = getIntent().getStringExtra("CURRENT_CHECKOUT");

        edtCustomerName.setText(customerName);
        edtRoomNumber.setText(roomNumber);
        
        if (currentCheckOut != null) {
            edtNewCheckOutDate.setText(formatDatePretty(currentCheckOut));
        }
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        
        // Nếu đã có ngày checkout hiện tại, bắt đầu từ ngày đó
        if (currentCheckOut != null && currentCheckOut.length() >= 10) {
            try {
                String datePart = currentCheckOut.split("T")[0];
                String[] parts = datePart.split("-");
                c.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
            } catch (Exception ignored) {}
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year1, monthOfYear, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            edtNewCheckOutDate.setText(sdf.format(selected.getTime()));
        }, year, month, day);
        
        // Không cho phép chọn ngày trước ngày checkout hiện tại
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();
    }

    private void performExtend() {
        String newDate = edtNewCheckOutDate.getText().toString().trim();
        if (newDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày mới", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Chuyển đổi từ MM/dd/yyyy sang ISO format
            SimpleDateFormat inputSdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(inputSdf.parse(newDate));
            
            SimpleDateFormat outputSdf = new SimpleDateFormat("yyyy-MM-dd'T'12:00:00.000'Z'", Locale.getDefault());
            String isoDate = outputSdf.format(calendar.getTime());

            Map<String, String> body = new HashMap<>();
            body.put("NewCheckOut", isoDate);

            RetrofitClient.getApiService().extendStay(stayId, body).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(R_ExtendRoom.this, "Gia hạn lưu trú thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(R_ExtendRoom.this, "Gia hạn thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    Toast.makeText(R_ExtendRoom.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Định dạng ngày không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatDatePretty(String dateStr) {
        if (dateStr == null || dateStr.length() < 10) return dateStr;
        try {
            String datePart = dateStr.split("T")[0];
            String[] parts = datePart.split("-");
            return parts[1] + "/" + parts[2] + "/" + parts[0]; // MM/dd/yyyy
        } catch (Exception e) {
            return dateStr;
        }
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
