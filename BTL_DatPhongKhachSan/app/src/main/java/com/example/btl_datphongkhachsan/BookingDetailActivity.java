package com.example.btl_datphongkhachsan;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.Reservation;
import com.example.btl_datphongkhachsan.models.ReservationModifyRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetailActivity extends AppCompatActivity {

    private ImageView ivRoomDetail, ivBack;
    private TextView tvRoomTypeName, tvBookingID, tvStatus, tvPriceInfo;
    private TextView tvCheckInFormatted, tvCheckOutFormatted;
    private EditText etCheckIn, etCheckOut, etQuantity;
    private LinearLayout btnEditCheckIn, btnEditCheckOut;
    private Button btnSaveChanges;
    private Reservation reservation;
    private double pricePerNight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        ivRoomDetail = findViewById(R.id.ivRoomDetail);
        tvRoomTypeName = findViewById(R.id.tvRoomTypeName);
        tvBookingID = findViewById(R.id.tvBookingID);
        tvStatus = findViewById(R.id.tvStatus);
        tvPriceInfo = findViewById(R.id.tvPriceInfo);
        
        tvCheckInFormatted = findViewById(R.id.tvCheckInFormatted);
        tvCheckOutFormatted = findViewById(R.id.tvCheckOutFormatted);
        etCheckIn = findViewById(R.id.etCheckIn);
        etCheckOut = findViewById(R.id.etCheckOut);
        
        btnEditCheckIn = findViewById(R.id.btnEditCheckIn);
        btnEditCheckOut = findViewById(R.id.btnEditCheckOut);
        
        etQuantity = findViewById(R.id.etQuantity);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        ivBack = findViewById(R.id.backIntent);

        ivBack.setOnClickListener(v -> finish());

        reservation = (Reservation) getIntent().getSerializableExtra("RESERVATION");

        if (reservation != null) {
            displayData();
            calculateInitialPricePerNight();
        }

        
        btnEditCheckIn.setOnClickListener(v -> showDatePicker(etCheckIn, tvCheckInFormatted));
        btnEditCheckOut.setOnClickListener(v -> showDatePicker(etCheckOut, tvCheckOutFormatted));

        btnSaveChanges.setOnClickListener(v -> updateReservation());
        
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                updatePriceDisplay();
            }
        };
        etCheckIn.addTextChangedListener(watcher);
        etCheckOut.addTextChangedListener(watcher);
        etQuantity.addTextChangedListener(watcher);

        if (!"BOOKED".equalsIgnoreCase(reservation.getStatus())) {
            btnSaveChanges.setVisibility(View.GONE);
            btnEditCheckIn.setEnabled(false);
            btnEditCheckOut.setEnabled(false);
            etQuantity.setEnabled(false);
        }
    }

    private void calculateInitialPricePerNight() {
        long nights = calculateNights(reservation.getCheckIn(), reservation.getCheckOut());
        int qty = reservation.getQuantity() > 0 ? reservation.getQuantity() : 1;
        pricePerNight = reservation.getTotalAmount() / (nights * qty);
        updatePriceDisplay();
    }

    private void updatePriceDisplay() {
        String start = etCheckIn.getText().toString();
        String end = etCheckOut.getText().toString();
        long nights = calculateNights(start, end);
        int qty = 1;
        try {
            qty = Integer.parseInt(etQuantity.getText().toString());
        } catch (Exception ignored) {}
        
        double total = pricePerNight * nights * qty;
        String info = String.format(Locale.getDefault(), "%,.0f VNĐ x %d đêm x %d phòng\nTổng cộng: %,.0f VNĐ", 
                pricePerNight, nights, qty, total);
        if (tvPriceInfo != null) tvPriceInfo.setText(info);
    }

    private void displayData() {
        tvRoomTypeName.setText(reservation.getRoomType());
        tvBookingID.setText("Booking ID: #" + reservation.getReservationID());
        tvStatus.setText(reservation.getStatus());
        
        String checkIn = formatDate(reservation.getCheckIn());
        String checkOut = formatDate(reservation.getCheckOut());
        
        etCheckIn.setText(checkIn);
        etCheckOut.setText(checkOut);
        
        tvCheckInFormatted.setText(formatDatePretty(checkIn));
        tvCheckOutFormatted.setText(formatDatePretty(checkOut));
        
        etQuantity.setText(String.valueOf(reservation.getQuantity()));

        String imageName = reservation.getRoomType().toLowerCase().replace(" room", "").replace(" ", "");
        int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
        ivRoomDetail.setImageResource(resId != 0 ? resId : android.R.drawable.ic_menu_gallery);
    }

    private void showDatePicker(EditText editText, TextView tvFormatted) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, (monthOfYear + 1), dayOfMonth);
            editText.setText(selectedDate);
            tvFormatted.setText(formatDatePretty(selectedDate));
        }, year, month, day);
        datePickerDialog.show();
    }

    private void updateReservation() {
        ReservationModifyRequest request = new ReservationModifyRequest(
                reservation.getRoomTypeID(),
                Integer.parseInt(etQuantity.getText().toString()),
                etCheckIn.getText().toString(),
                etCheckOut.getText().toString()
        );

        RetrofitClient.getApiService().modifyReservation(reservation.getReservationID(), request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(BookingDetailActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(BookingDetailActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(BookingDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private long calculateNights(String start, String end) {
        if (start == null || end == null || start.length() < 10 || end.length() < 10) return 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date d1 = sdf.parse(start.substring(0, 10));
            Date d2 = sdf.parse(end.substring(0, 10));
            if (d1 == null || d2 == null) return 1;
            long diff = d2.getTime() - d1.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            return days > 0 ? days : 1;
        } catch (ParseException e) { return 1; }
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.length() < 10) return dateStr;
        return dateStr.substring(0, 10);
    }

    private String formatDatePretty(String dateStr) {
        if (dateStr == null || dateStr.length() < 10) return dateStr;
        SimpleDateFormat fromSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat toSdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        try {
            Date date = fromSdf.parse(dateStr);
            return toSdf.format(date);
        } catch (ParseException e) {
            return dateStr;
        }
    }
}
