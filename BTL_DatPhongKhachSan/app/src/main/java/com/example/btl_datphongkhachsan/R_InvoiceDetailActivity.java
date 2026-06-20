package com.example.btl_datphongkhachsan;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_datphongkhachsan.adapters.InvoiceDetailAdapter;
import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.InvoiceFullResponse;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class R_InvoiceDetailActivity extends AppCompatActivity {

    private int stayId;
    private TextView tvDetailInvoiceId, tvDetailDate, tvDetailGuestName, tvDetailCheckInOut, tvDetailVat, tvDetailTotalAmount;
    private RecyclerView rvInvoiceDetails;
    private InvoiceDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_activity_invoice_detail);

        stayId = getIntent().getIntExtra("STAY_ID", -1);
        if (stayId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID hóa đơn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        fetchInvoiceDetail();
    }

    private void initViews() {
        tvDetailInvoiceId = findViewById(R.id.tvDetailInvoiceId);
        tvDetailDate = findViewById(R.id.tvDetailDate);
        tvDetailGuestName = findViewById(R.id.tvDetailGuestName);
        tvDetailCheckInOut = findViewById(R.id.tvDetailCheckInOut);
        tvDetailVat = findViewById(R.id.tvDetailVat);
        tvDetailTotalAmount = findViewById(R.id.tvDetailTotalAmount);
        rvInvoiceDetails = findViewById(R.id.rvInvoiceDetails);

        rvInvoiceDetails.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết hóa đơn");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void fetchInvoiceDetail() {
        RetrofitClient.getApiService().getFullInvoiceDetail(stayId).enqueue(new Callback<InvoiceFullResponse>() {
            @Override
            public void onResponse(Call<InvoiceFullResponse> call, Response<InvoiceFullResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayData(response.body());
                } else {
                    Toast.makeText(R_InvoiceDetailActivity.this, "Không thể tải chi tiết hóa đơn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<InvoiceFullResponse> call, Throwable t) {
                Log.e("API_ERROR", "Fetch detail failed: " + t.getMessage());
                Toast.makeText(R_InvoiceDetailActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayData(InvoiceFullResponse data) {
        InvoiceFullResponse.Invoice inv = data.getInvoice();
        tvDetailInvoiceId.setText("Hóa đơn #" + inv.getInvoiceID());
        tvDetailDate.setText(formatDate(inv.getDate()));
        tvDetailGuestName.setText("Khách hàng: " + inv.getFullName());
        tvDetailCheckInOut.setText(formatDateSimple(inv.getActualCheckIn()) + " - " + formatDateSimple(inv.getActualCheckOut()));
        tvDetailVat.setText(inv.getVAT() + "%");

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvDetailTotalAmount.setText(formatter.format(inv.getTotalAmount()));

        adapter = new InvoiceDetailAdapter(data.getDetails());
        rvInvoiceDetails.setAdapter(adapter);
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.length() < 16) return "";
        try {
            String datePart = dateStr.split("T")[0];
            String timePart = dateStr.split("T")[1].substring(0, 5);
            String[] dateArr = datePart.split("-");
            return dateArr[2] + "/" + dateArr[1] + "/" + dateArr[0] + " " + timePart;
        } catch (Exception e) {
            return dateStr;
        }
    }

    private String formatDateSimple(String dateStr) {
        if (dateStr == null || dateStr.length() < 10) return "";
        try {
            String datePart = dateStr.split("T")[0];
            String[] dateArr = datePart.split("-");
            return dateArr[2] + "/" + dateArr[1] + "/" + dateArr[0];
        } catch (Exception e) {
            return dateStr;
        }
    }
}
