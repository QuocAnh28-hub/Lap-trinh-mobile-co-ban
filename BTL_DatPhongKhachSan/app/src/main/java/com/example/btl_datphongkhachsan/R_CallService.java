package com.example.btl_datphongkhachsan;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_datphongkhachsan.adapters.ServiceUsageAdapter;
import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.HotelService;
import com.example.btl_datphongkhachsan.models.ServiceUsage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class R_CallService extends AppCompatActivity {

    private EditText edtCustomerName, edtRoomNumber, edtServiceQuantity;
    private Spinner spnService;
    private RecyclerView rvServiceUsage;
    private TextView tvEmptyService, tvTotalService;
    private ServiceUsageAdapter adapter;
    private List<ServiceUsage> usageList = new ArrayList<>();
    private int stayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.r_call_service);

        initViews();
        getDataFromIntent();
        setupRecyclerView();
        setupEdgeToEdge();
        fetchHotelServices();
        fetchServiceUsages();

        findViewById(R.id.btnCloseActivity).setOnClickListener(v -> finish());
        findViewById(R.id.btnClose).setOnClickListener(v -> finish());
        findViewById(R.id.btnAddService).setOnClickListener(v -> performAddService());
    }

    private void initViews() {
        edtCustomerName = findViewById(R.id.edtCustomerName);
        edtRoomNumber = findViewById(R.id.edtRoomNumber);
        edtServiceQuantity = findViewById(R.id.edtServiceQuantity);
        spnService = findViewById(R.id.spnService);
        rvServiceUsage = findViewById(R.id.rvServiceUsage);
        tvEmptyService = findViewById(R.id.tvEmptyService);
        tvTotalService = findViewById(R.id.tvTotalService);
    }

    private void getDataFromIntent() {
        stayId = getIntent().getIntExtra("STAY_ID", -1);
        String customerName = getIntent().getStringExtra("CUSTOMER_NAME");
        String roomNumber = getIntent().getStringExtra("ROOM_NUMBER");

        edtCustomerName.setText(customerName);
        edtRoomNumber.setText(roomNumber);
    }

    private void setupRecyclerView() {
        adapter = new ServiceUsageAdapter(usageList, this::deleteServiceUsage);
        rvServiceUsage.setLayoutManager(new LinearLayoutManager(this));
        rvServiceUsage.setAdapter(adapter);
    }

    private void fetchHotelServices() {
        RetrofitClient.getApiService().getAllHotelServices().enqueue(new Callback<List<HotelService>>() {
            @Override
            public void onResponse(Call<List<HotelService>> call, Response<List<HotelService>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<HotelService> allServices = response.body();
                    List<HotelService> activeServices = new ArrayList<>();

                    for (HotelService service : allServices) {
                        if ("TRUE".equalsIgnoreCase(service.getStatus())) {
                            activeServices.add(service);
                        }
                    }

                    ArrayAdapter<HotelService> adapter = new ArrayAdapter<>(R_CallService.this,
                            android.R.layout.simple_spinner_item, activeServices);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnService.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<HotelService>> call, Throwable t) {
                Toast.makeText(R_CallService.this, "Lỗi tải dịch vụ: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchServiceUsages() {
        if (stayId == -1) return;

        RetrofitClient.getApiService().getServiceUsages(stayId).enqueue(new Callback<List<ServiceUsage>>() {
            @Override
            public void onResponse(Call<List<ServiceUsage>> call, Response<List<ServiceUsage>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usageList.clear();
                    usageList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    updateUIState();
                    calculateTotal();
                }
            }

            @Override
            public void onFailure(Call<List<ServiceUsage>> call, Throwable t) {
                Toast.makeText(R_CallService.this, "Lỗi tải danh sách sử dụng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteServiceUsage(ServiceUsage usage) {
        RetrofitClient.getApiService().deleteServiceUsage(usage.getUsageID()).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(R_CallService.this, "Đã xóa dịch vụ", Toast.LENGTH_SHORT).show();
                    fetchServiceUsages();
                } else {
                    Toast.makeText(R_CallService.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(R_CallService.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performAddService() {
        HotelService selectedService = (HotelService) spnService.getSelectedItem();
        String quantityStr = edtServiceQuantity.getText().toString().trim();

        if (selectedService == null) {
            Toast.makeText(this, "Vui lòng chọn dịch vụ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantityStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        if (quantity <= 0) {
            Toast.makeText(this, "Số lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("ServiceID", selectedService.getServiceID());
        body.put("Quantity", quantity);

        RetrofitClient.getApiService().addServiceUsage(stayId, body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(R_CallService.this, "Thêm dịch vụ thành công!", Toast.LENGTH_SHORT).show();
                    edtServiceQuantity.setText(""); // Reset quantity
                    fetchServiceUsages(); // Refresh list
                } else {
                    Toast.makeText(R_CallService.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(R_CallService.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIState() {
        if (usageList.isEmpty()) {
            tvEmptyService.setVisibility(View.VISIBLE);
            rvServiceUsage.setVisibility(View.GONE);
        } else {
            tvEmptyService.setVisibility(View.GONE);
            rvServiceUsage.setVisibility(View.VISIBLE);
        }
    }

    private void calculateTotal() {
        double total = 0;
        for (ServiceUsage usage : usageList) {
            total += usage.getTotal();
        }
        tvTotalService.setText(String.format(Locale.getDefault(), "Tổng dịch vụ:  %,.0fđ", total));
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
