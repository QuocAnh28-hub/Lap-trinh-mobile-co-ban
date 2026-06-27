package com.example.btl_datphongkhachsan;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_datphongkhachsan.adapters.MinibarUsageAdapter;
import com.example.btl_datphongkhachsan.adapters.PenaltyUsageAdapter;
import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.MinibarItem;
import com.example.btl_datphongkhachsan.models.MinibarUsage;
import com.example.btl_datphongkhachsan.models.PenaltyUsage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class R_CheckOut extends AppCompatActivity {

    private EditText edtCustomerName, edtMinibarQuantity, edtFineReason, edtFineAmount;
    private Spinner spnMinibar;
    private RecyclerView rvMinibarUsage, rvPenaltyUsage;
    private TextView tvEmptyMinibar, tvEmptyFine, tvTotal;
    
    private MinibarUsageAdapter minibarAdapter;
    private PenaltyUsageAdapter penaltyAdapter;
    
    private List<MinibarUsage> minibarList = new ArrayList<>();
    private List<PenaltyUsage> penaltyList = new ArrayList<>();
    
    private int stayId, roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.r_check_out);

        initViews();
        getDataFromIntent();
        setupRecyclerViews();
        setupEdgeToEdge();
        
        fetchMinibarItems();
        fetchMinibarUsages();
        fetchPenalties();

        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
        findViewById(R.id.btnAddMinibar).setOnClickListener(v -> performAddMinibar());
        findViewById(R.id.btnAddFine).setOnClickListener(v -> performAddFine());
        findViewById(R.id.btnConfirmCheckout).setOnClickListener(v -> performConfirmCheckout());
    }

    private void performConfirmCheckout() {
        if (stayId == -1 || roomId == -1) {
            Toast.makeText(this, "Thiếu thông tin lượt ở hoặc phòng", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận Check-out")
                .setMessage("Bạn có chắc chắn muốn thực hiện Check-out cho phòng này không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    Map<String, Object> body = new HashMap<>();
                    body.put("StayID", stayId);
                    body.put("RoomID", roomId);

                    RetrofitClient.getApiService().checkoutRoom(body).enqueue(new Callback<Map<String, String>>() {
                        @Override
                        public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(R_CheckOut.this, "Check-out phòng thành công!", Toast.LENGTH_SHORT).show();
                                finish(); // Quay lại màn hình danh sách
                            } else {
                                Toast.makeText(R_CheckOut.this, "Check-out thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, String>> call, Throwable t) {
                            Toast.makeText(R_CheckOut.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void initViews() {
        edtCustomerName = findViewById(R.id.edtCustomerName);
        spnMinibar = findViewById(R.id.spnMinibar);
        edtMinibarQuantity = findViewById(R.id.edtMinibarQuantity);
        edtFineReason = findViewById(R.id.edtFineReason);
        edtFineAmount = findViewById(R.id.edtFineAmount);
        
        rvMinibarUsage = findViewById(R.id.rvMinibarUsage);
        rvPenaltyUsage = findViewById(R.id.rvPenaltyUsage);
        
        tvEmptyMinibar = findViewById(R.id.tvEmptyMinibar);
        tvEmptyFine = findViewById(R.id.tvEmptyFine);
        tvTotal = findViewById(R.id.tvTotal);
    }

    private void getDataFromIntent() {
        stayId = getIntent().getIntExtra("STAY_ID", -1);
        roomId = getIntent().getIntExtra("ROOM_ID", -1);
        String customerName = getIntent().getStringExtra("CUSTOMER_NAME");
        
        edtCustomerName.setText(customerName);
    }

    private void setupRecyclerViews() {
        minibarAdapter = new MinibarUsageAdapter(minibarList, this::deleteMinibarUsage);
        rvMinibarUsage.setLayoutManager(new LinearLayoutManager(this));
        rvMinibarUsage.setAdapter(minibarAdapter);

        penaltyAdapter = new PenaltyUsageAdapter(penaltyList, this::deletePenalty);
        rvPenaltyUsage.setLayoutManager(new LinearLayoutManager(this));
        rvPenaltyUsage.setAdapter(penaltyAdapter);
    }

    private void fetchMinibarItems() {
        if (roomId == -1) return;
        RetrofitClient.getApiService().getMinibarItemsForRoom(roomId).enqueue(new Callback<List<MinibarItem>>() {
            @Override
            public void onResponse(Call<List<MinibarItem>> call, Response<List<MinibarItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayAdapter<MinibarItem> adapter = new ArrayAdapter<>(R_CheckOut.this,
                            android.R.layout.simple_spinner_item, response.body());
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnMinibar.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<MinibarItem>> call, Throwable t) {}
        });
    }

    private void fetchMinibarUsages() {
        if (stayId == -1) return;
        RetrofitClient.getApiService().getMinibarUsages(stayId).enqueue(new Callback<List<MinibarUsage>>() {
            @Override
            public void onResponse(Call<List<MinibarUsage>> call, Response<List<MinibarUsage>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    minibarList.clear();
                    minibarList.addAll(response.body());
                    minibarAdapter.notifyDataSetChanged();
                    updateUIState();
                    calculateTotalSum();
                }
            }
            @Override
            public void onFailure(Call<List<MinibarUsage>> call, Throwable t) {}
        });
    }

    private void fetchPenalties() {
        if (stayId == -1) return;
        RetrofitClient.getApiService().getPenalties(stayId).enqueue(new Callback<List<PenaltyUsage>>() {
            @Override
            public void onResponse(Call<List<PenaltyUsage>> call, Response<List<PenaltyUsage>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    penaltyList.clear();
                    penaltyList.addAll(response.body());
                    penaltyAdapter.notifyDataSetChanged();
                    updateUIState();
                    calculateTotalSum();
                }
            }
            @Override
            public void onFailure(Call<List<PenaltyUsage>> call, Throwable t) {}
        });
    }

    private void deleteMinibarUsage(MinibarUsage usage) {
        RetrofitClient.getApiService().deleteMinibarUsage(usage.getID()).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(R_CheckOut.this, "Đã xóa minibar", Toast.LENGTH_SHORT).show();
                    fetchMinibarUsages();
                }
            }
            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {}
        });
    }

    private void deletePenalty(PenaltyUsage penalty) {
        RetrofitClient.getApiService().deletePenalty(penalty.getPenaltyID()).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(R_CheckOut.this, "Đã xóa phí phạt", Toast.LENGTH_SHORT).show();
                    fetchPenalties();
                }
            }
            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {}
        });
    }

    private void performAddMinibar() {
        MinibarItem selectedItem = (MinibarItem) spnMinibar.getSelectedItem();
        String quantityStr = edtMinibarQuantity.getText().toString().trim();

        if (selectedItem == null) {
            Toast.makeText(this, "Vui lòng chọn món", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantityStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        Map<String, Object> body = new HashMap<>();
        body.put("MinibarID", selectedItem.getMiniBarID());
        body.put("Quantity", quantity);

        RetrofitClient.getApiService().addMinibarUsage(stayId, body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(R_CheckOut.this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
                    edtMinibarQuantity.setText("");
                    fetchMinibarUsages();
                }
            }
            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {}
        });
    }

    private void performAddFine() {
        String reason = edtFineReason.getText().toString().trim();
        String amountStr = edtFineAmount.getText().toString().trim();

        if (reason.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập lý do và số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        Map<String, Object> body = new HashMap<>();
        body.put("Reason", reason);
        body.put("Amount", amount);

        RetrofitClient.getApiService().addPenalty(stayId, body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(R_CheckOut.this, "Thêm phí phạt thành công!", Toast.LENGTH_SHORT).show();
                    edtFineReason.setText("");
                    edtFineAmount.setText("");
                    fetchPenalties();
                }
            }
            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {}
        });
    }

    private void calculateTotalSum() {
        double total = 0;
        for (MinibarUsage m : minibarList) total += m.getTotal();
        for (PenaltyUsage p : penaltyList) total += p.getAmount();
        
        tvTotal.setText(String.format(Locale.getDefault(), "Tổng:  %,.0fđ", total));
    }

    private void updateUIState() {
        tvEmptyMinibar.setVisibility(minibarList.isEmpty() ? View.VISIBLE : View.GONE);
        rvMinibarUsage.setVisibility(minibarList.isEmpty() ? View.GONE : View.VISIBLE);
        
        tvEmptyFine.setVisibility(penaltyList.isEmpty() ? View.VISIBLE : View.GONE);
        rvPenaltyUsage.setVisibility(penaltyList.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
