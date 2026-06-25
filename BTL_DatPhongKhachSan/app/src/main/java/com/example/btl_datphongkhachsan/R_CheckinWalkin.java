package com.example.btl_datphongkhachsan;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class R_CheckinWalkin extends AppCompatActivity {

    private RelativeLayout checkoutdate;
    private EditText etCheckoutDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.r_checkin_walkin);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        checkoutdate = findViewById(R.id.layoutCheckoutDate);
        etCheckoutDate = findViewById(R.id.etCheckoutDate);

        checkoutdate.setOnClickListener(v -> showDatePicker(etCheckoutDate, etCheckoutDate));

        View mainView = findViewById(R.id.main);
        int pLeft = mainView.getPaddingLeft();
        int pTop = mainView.getPaddingTop();
        int pRight = mainView.getPaddingRight();
        int pBottom = mainView.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left + pLeft, systemBars.top + pTop, systemBars.right + pRight, systemBars.bottom + pBottom);
            return insets;
        });
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