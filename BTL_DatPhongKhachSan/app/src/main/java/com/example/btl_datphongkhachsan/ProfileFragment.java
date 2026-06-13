package com.example.btl_datphongkhachsan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.CustomerInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvProfileEmail, tvTotalStay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvTotalStay = view.findViewById(R.id.tvTotalStay);

        View cardLogout = view.findViewById(R.id.cardLogout);
        if (cardLogout != null) {
            cardLogout.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }

        View cardPersonalInfo = view.findViewById(R.id.cardPersonalInfo);
        if (cardPersonalInfo != null) {
            cardPersonalInfo.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), PersonalInfoActivity.class);
                startActivity(intent);
            });
        }

        loadProfileData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfileData(); // Cập nhật lại tên nếu vừa đổi ở PersonalInfoActivity
    }

    private void loadProfileData() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = sharedPref.getString("UserID", null);

        if (userId != null) {
            RetrofitClient.getApiService().getCustomerInfo(userId).enqueue(new Callback<CustomerInfo>() {
                @Override
                public void onResponse(Call<CustomerInfo> call, Response<CustomerInfo> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        CustomerInfo info = response.body();
                        tvProfileName.setText(info.getFullName());
                        tvProfileEmail.setText(info.getEmail());
                        tvTotalStay.setText(String.valueOf(info.getTotalStay()));
                    }
                }

                @Override
                public void onFailure(Call<CustomerInfo> call, Throwable t) {
                    Log.e("API_ERROR", t.getMessage());
                }
            });
        }
    }
}