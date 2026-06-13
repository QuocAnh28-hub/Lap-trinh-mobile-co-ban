package com.example.btl_datphongkhachsan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.CustomerInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalInfoActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etCCCD, etPassword;
    private String userId;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etCCCD = findViewById(R.id.etCCCD);
        etPassword = findViewById(R.id.etPassword);
        ivBack = findViewById(R.id.backIntent);

        ivBack.setOnClickListener(v -> finish());

        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPref.getString("UserID", null);

        if (userId != null) {
            loadCustomerInfo();
        }

        findViewById(R.id.btnUpdate).setOnClickListener(v -> updateProfile());
    }

    private void loadCustomerInfo() {
        RetrofitClient.getApiService().getCustomerInfo(userId).enqueue(new Callback<CustomerInfo>() {
            @Override
            public void onResponse(Call<CustomerInfo> call, Response<CustomerInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CustomerInfo info = response.body();
                    etFullName.setText(info.getFullName());
                    etEmail.setText(info.getEmail());
                    etPhone.setText(info.getPhone());
                    etCCCD.setText(info.getCCCD());
                    etPassword.setText(info.getPasswordHash());
                }
            }

            @Override
            public void onFailure(Call<CustomerInfo> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }

    private void updateProfile() {
        CustomerInfo info = new CustomerInfo();
        info.setFullName(etFullName.getText().toString());
        info.setEmail(etEmail.getText().toString());
        info.setPhone(etPhone.getText().toString());
        info.setCCCD(etCCCD.getText().toString());
        info.setPasswordHash(etPassword.getText().toString());

        RetrofitClient.getApiService().updateCustomerProfile(userId, info).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PersonalInfoActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    
                    // Cập nhật lại FullName trong SharedPreferences
                    SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("FullName", info.getFullName());
                    editor.apply();
                    
                    finish();
                } else {
                    Toast.makeText(PersonalInfoActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(PersonalInfoActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}