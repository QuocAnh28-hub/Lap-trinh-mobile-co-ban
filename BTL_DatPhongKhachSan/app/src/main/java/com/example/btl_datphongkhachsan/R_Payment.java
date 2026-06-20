package com.example.btl_datphongkhachsan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_datphongkhachsan.adapters.GenericPaymentAdapter;
import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.CreateInvoiceRequest;
import com.example.btl_datphongkhachsan.models.CreateInvoiceResponse;
import com.example.btl_datphongkhachsan.models.MinibarUsage;
import com.example.btl_datphongkhachsan.models.PenaltyUsage;
import com.example.btl_datphongkhachsan.models.RoomStayHistory;
import com.example.btl_datphongkhachsan.models.ServiceUsage;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class R_Payment extends AppCompatActivity {

    private int stayId;
    private RecyclerView rvRoomHistory, rvServiceUsages, rvMinibarUsages, rvPenalties;
    private TextView tvTotalPayment;
    private Spinner spinnerPaymentMethod, spinnerVat;
    
    private double subtotal = 0;
    private double selectedVatPercent = 0.05;
    private String[] vatOptions = {"5%", "8%", "10%", "15%"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_payment);

        stayId = getIntent().getIntExtra("STAY_ID", -1);
        if (stayId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID lưu trú", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupSpinners();
        loadAllData();

        findViewById(R.id.btnPayNow).setOnClickListener(v -> performPayment());
    }

    private void initViews() {
        rvRoomHistory = findViewById(R.id.rvRoomHistory);
        rvServiceUsages = findViewById(R.id.rvServiceUsages);
        rvMinibarUsages = findViewById(R.id.rvMinibarUsages);
        rvPenalties = findViewById(R.id.rvPenalties);
        tvTotalPayment = findViewById(R.id.tvTotalPayment);
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        spinnerVat = findViewById(R.id.spinnerVat);

        rvRoomHistory.setLayoutManager(new LinearLayoutManager(this));
        rvServiceUsages.setLayoutManager(new LinearLayoutManager(this));
        rvMinibarUsages.setLayoutManager(new LinearLayoutManager(this));
        rvPenalties.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupSpinners() {
        // Payment Method Spinner
        String[] methods = {"Cash", "Transfer"};
        ArrayAdapter<String> methodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, methods);
        spinnerPaymentMethod.setAdapter(methodAdapter);

        // VAT Spinner
        ArrayAdapter<String> vatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, vatOptions);
        spinnerVat.setAdapter(vatAdapter);

        spinnerVat.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String selected = vatOptions[position];
                selectedVatPercent = Double.parseDouble(selected.replace("%", "")) / 100.0;
                updateTotalDisplay();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void loadAllData() {
        subtotal = 0; // Reset subtotal when reloading
        fetchRoomHistory();
        fetchServiceUsages();
        fetchMinibarUsages();
        fetchPenalties();
    }

    private void fetchRoomHistory() {
        RetrofitClient.getApiService().getRoomStayHistory(stayId).enqueue(new Callback<List<RoomStayHistory>>() {
            @Override
            public void onResponse(Call<List<RoomStayHistory>> call, Response<List<RoomStayHistory>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GenericPaymentAdapter.PaymentItem> items = new ArrayList<>();
                    for (RoomStayHistory r : response.body()) {
                        items.add(new GenericPaymentAdapter.PaymentItem("Phòng " + r.getSoPhong(), r.getRoomType(), 1, r.getAmount()));
                        subtotal += r.getAmount();
                    }
                    rvRoomHistory.setAdapter(new GenericPaymentAdapter(items));
                    updateTotalDisplay();
                }
            }
            @Override
            public void onFailure(Call<List<RoomStayHistory>> call, Throwable t) {
                Log.e("API_ERROR", "Room history failed: " + t.getMessage());
            }
        });
    }

    private void fetchServiceUsages() {
        RetrofitClient.getApiService().getServiceUsages(stayId).enqueue(new Callback<List<ServiceUsage>>() {
            @Override
            public void onResponse(Call<List<ServiceUsage>> call, Response<List<ServiceUsage>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GenericPaymentAdapter.PaymentItem> items = new ArrayList<>();
                    for (ServiceUsage s : response.body()) {
                        items.add(new GenericPaymentAdapter.PaymentItem(s.getServiceName(), "SERVICE", s.getQuantity(), s.getTotal()));
                        subtotal += s.getTotal();
                    }
                    rvServiceUsages.setAdapter(new GenericPaymentAdapter(items));
                    updateTotalDisplay();
                }
            }
            @Override
            public void onFailure(Call<List<ServiceUsage>> call, Throwable t) {
                Log.e("API_ERROR", "Service usages failed: " + t.getMessage());
            }
        });
    }

    private void fetchMinibarUsages() {
        RetrofitClient.getApiService().getMinibarUsages(stayId).enqueue(new Callback<List<MinibarUsage>>() {
            @Override
            public void onResponse(Call<List<MinibarUsage>> call, Response<List<MinibarUsage>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GenericPaymentAdapter.PaymentItem> items = new ArrayList<>();
                    for (MinibarUsage m : response.body()) {
                        items.add(new GenericPaymentAdapter.PaymentItem(m.getItemName(), "MINIBAR", m.getQuantity(), m.getTotal()));
                        subtotal += m.getTotal();
                    }
                    rvMinibarUsages.setAdapter(new GenericPaymentAdapter(items));
                    updateTotalDisplay();
                }
            }
            @Override
            public void onFailure(Call<List<MinibarUsage>> call, Throwable t) {
                Log.e("API_ERROR", "Minibar failed: " + t.getMessage());
            }
        });
    }

    private void fetchPenalties() {
        RetrofitClient.getApiService().getPenalties(stayId).enqueue(new Callback<List<PenaltyUsage>>() {
            @Override
            public void onResponse(Call<List<PenaltyUsage>> call, Response<List<PenaltyUsage>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GenericPaymentAdapter.PaymentItem> items = new ArrayList<>();
                    for (PenaltyUsage p : response.body()) {
                        items.add(new GenericPaymentAdapter.PaymentItem(p.getReason(), "PENALTY", 1, p.getAmount()));
                        subtotal += p.getAmount();
                    }
                    rvPenalties.setAdapter(new GenericPaymentAdapter(items));
                    updateTotalDisplay();
                }
            }
            @Override
            public void onFailure(Call<List<PenaltyUsage>> call, Throwable t) {
                Log.e("API_ERROR", "Penalties failed: " + t.getMessage());
            }
        });
    }

    private void updateTotalDisplay() {
        double total = subtotal * (1 + selectedVatPercent);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalPayment.setText(formatter.format(total));
    }

    private void performPayment() {
        String method = spinnerPaymentMethod.getSelectedItem().toString();
        String vatText = spinnerVat.getSelectedItem().toString().replace("%", "");
        int vatValue = Integer.parseInt(vatText);

        CreateInvoiceRequest request = new CreateInvoiceRequest(stayId, method, vatValue);

        RetrofitClient.getApiService().createAndPayInvoice(request).enqueue(new Callback<CreateInvoiceResponse>() {
            @Override
            public void onResponse(Call<CreateInvoiceResponse> call, Response<CreateInvoiceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CreateInvoiceResponse res = response.body();
                    String message = "Thanh toán thành công!\nHóa đơn #" + res.getInvoiceID() + 
                                     "\nTổng tiền: " + formatCurrency(res.getTotal());
                    
                    Toast.makeText(R_Payment.this, message, Toast.LENGTH_LONG).show();
                    finish(); // Close payment screen after success
                } else {
                    Toast.makeText(R_Payment.this, "Thanh toán thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateInvoiceResponse> call, Throwable t) {
                Toast.makeText(R_Payment.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }
}
