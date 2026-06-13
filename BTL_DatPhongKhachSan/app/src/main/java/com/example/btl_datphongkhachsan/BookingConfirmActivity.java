package com.example.btl_datphongkhachsan;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.BookingRequest;
import com.example.btl_datphongkhachsan.models.BookingResponse;
import com.example.btl_datphongkhachsan.models.RoomType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingConfirmActivity extends AppCompatActivity {

    private TextView tvRoomTypeName, tvTotalPrice, tvPriceDetail;
    private EditText etCheckIn, etCheckOut, etNumRooms, etNumPeople;
    private ImageView ivRoomBooking, ivBack;
    private RoomType roomType;
    private double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirm);

        tvRoomTypeName = findViewById(R.id.tvRoomTypeName);
        etCheckIn = findViewById(R.id.etCheckIn);
        etCheckOut = findViewById(R.id.etCheckOut);
        etNumRooms = findViewById(R.id.etNumRooms);
        etNumPeople = findViewById(R.id.etNumPeople);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvPriceDetail = findViewById(R.id.tvPriceDetail);
        ivRoomBooking = findViewById(R.id.ivRoomBooking);
        ivBack = findViewById(R.id.backIntent);

        ivBack.setOnClickListener(v -> finish());

        // Nhận dữ liệu từ Intent
        try {
            roomType = (RoomType) getIntent().getSerializableExtra("ROOM_TYPE");
        } catch (Exception e) {
            Log.e("BOOKING_CONFIRM", "Error getting RoomType extra: " + e.getMessage());
        }

        if (roomType == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin phòng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvRoomTypeName.setText(roomType.getName());

        // Tự động gán ảnh: Chuyển tên về chữ thường và xóa khoảng trắng (ví dụ: "Double 1" -> "double1")
        String imageName = roomType.getName().toLowerCase().replace(" ", "");
        int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
        if (resId != 0) {
            ivRoomBooking.setImageResource(resId);
        } else {
            ivRoomBooking.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        String checkIn = getIntent().getStringExtra("CHECK_IN");
        String checkOut = getIntent().getStringExtra("CHECK_OUT");
        int numRooms = getIntent().getIntExtra("NUM_ROOMS", 1);
        int numPeople = getIntent().getIntExtra("NUM_PEOPLE", 1);

        etCheckIn.setText(checkIn != null && !checkIn.isEmpty() ? checkIn : "yyyy-MM-dd");
        etCheckOut.setText(checkOut != null && !checkOut.isEmpty() ? checkOut : "yyyy-MM-dd");
        etNumRooms.setText(String.valueOf(numRooms > 0 ? numRooms : 1));
        etNumPeople.setText(String.valueOf(numPeople > 0 ? numPeople : 1));

        // Setup DatePickers
        etCheckIn.setOnClickListener(v -> showDatePicker(etCheckIn));
        etCheckOut.setOnClickListener(v -> showDatePicker(etCheckOut));

        // Setup TextWatchers for automatic recalculation
        TextWatcher recalculateWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                calculateAndDisplayTotal();
            }
        };

        etCheckIn.addTextChangedListener(recalculateWatcher);
        etCheckOut.addTextChangedListener(recalculateWatcher);
        etNumRooms.addTextChangedListener(recalculateWatcher);

        calculateAndDisplayTotal();

        findViewById(R.id.btnConfirmBooking).setOnClickListener(v -> performBooking());
    }

    private void showDatePicker(EditText editText) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, (monthOfYear + 1), dayOfMonth);
            editText.setText(selectedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void calculateAndDisplayTotal() {
        if (roomType == null) return;

        String start = etCheckIn.getText().toString();
        String end = etCheckOut.getText().toString();
        int rooms = 1;
        try {
            String s = etNumRooms.getText().toString();
            if (!s.isEmpty()) rooms = Integer.parseInt(s);
        } catch (NumberFormatException ignored) {}

        long nights = calculateNights(start, end);
        totalPrice = roomType.getDisplayPrice() * rooms * nights;

        tvPriceDetail.setText(String.format(Locale.getDefault(), "Room (%d đêm x %d phòng x %,.0f)", nights, rooms, roomType.getDisplayPrice()));
        tvTotalPrice.setText(String.format(Locale.getDefault(), "%,.0f VNĐ", totalPrice));
    }

    private long calculateNights(String start, String end) {
        if (start == null || end == null || start.isEmpty() || end.isEmpty() || start.contains("yyyy")) return 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date1 = sdf.parse(start);
            Date date2 = sdf.parse(end);
            if (date1 == null || date2 == null) return 1;
            long diff = date2.getTime() - date1.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            return days > 0 ? days : 1;
        } catch (ParseException e) {
            return 1;
        }
    }

    private void performBooking() {
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = sharedPref.getString("UserID", null);

        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để đặt phòng", Toast.LENGTH_SHORT).show();
            return;
        }

        String checkInDate = etCheckIn.getText().toString();
        String checkOutDate = etCheckOut.getText().toString();
        
        if (checkInDate.contains("yyyy") || checkOutDate.contains("yyyy")) {
            Toast.makeText(this, "Vui lòng chọn ngày nhận/trả phòng", Toast.LENGTH_SHORT).show();
            return;
        }

        int numRooms = 1;
        int numPeople = 1;
        try {
            numRooms = Integer.parseInt(etNumRooms.getText().toString());
            numPeople = Integer.parseInt(etNumPeople.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        BookingRequest request = new BookingRequest(userId, roomType.getRoomTypeID(), checkInDate, checkOutDate, numRooms, numPeople);

        RetrofitClient.getApiService().bookRoom(request).enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BookingConfirmActivity.this, "Đặt phòng thành công!", Toast.LENGTH_LONG).show();
                    
                    // Chuyển về HomeActivity và làm mới toàn bộ bằng cách xóa stack cũ
                    Intent intent = new Intent(BookingConfirmActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(BookingConfirmActivity.this, "Đặt phòng thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                Toast.makeText(BookingConfirmActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}