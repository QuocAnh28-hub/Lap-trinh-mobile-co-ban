package com.example.btl_datphongkhachsan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.CustomerInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class R_ChangeProfile extends AppCompatActivity {

    private EditText etFullName, etPhone, etEmail;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.r_changeprofile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lấy UserID từ SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPref.getString("UserID", null);

        initViews();
        loadCurrentUserInfo();

        findViewById(R.id.btnUpdate).setOnClickListener(v -> performUpdate());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
    }

    private void loadCurrentUserInfo() {
        if (userId == null) return;

        RetrofitClient.getApiService().getCustomerInfo(userId).enqueue(new Callback<CustomerInfo>() {
            @Override
            public void onResponse(Call<CustomerInfo> call, Response<CustomerInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CustomerInfo info = response.body();
                    etEmail.setText(info.getEmail());
                }
            }

            @Override
            public void onFailure(Call<CustomerInfo> call, Throwable t) {
                Log.e("API_ERROR", "Failed to load user info: " + t.getMessage());
            }
        });
    }

    private void performUpdate() {
        String name = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        CustomerInfo updatedInfo = new CustomerInfo();
        updatedInfo.setFullName(name);
        updatedInfo.setPhone(phone);
        updatedInfo.setEmail(email);

        // Sử dụng đúng API updateProfile đã thêm vào ApiService
        RetrofitClient.getApiService().updateProfile(userId, updatedInfo).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Cập nhật lại SharedPreferences
                    SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("FullName", name);
                    editor.apply();

                    Toast.makeText(R_ChangeProfile.this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(R_ChangeProfile.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(R_ChangeProfile.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
