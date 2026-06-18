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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class R_ProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.r_profile, container, false);

        TextView tvProfileName = view.findViewById(R.id.tvProfileName);
        TextView tvProfileTitle = view.findViewById(R.id.tvProfileTitle);
        View btnLogout = view.findViewById(R.id.btnLogout);
        View btnEditProfile = view.findViewById(R.id.btnEditProfile);
        View btnChangePassword = view.findViewById(R.id.btnChangePassword);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String fullName = sharedPref.getString("FullName", "User");
        String role = sharedPref.getString("Role", "");

        if (tvProfileName != null) tvProfileName.setText(fullName);
        if (tvProfileTitle != null && !role.isEmpty()) tvProfileTitle.setText(role.toUpperCase());

        // Chuyển đến trang cập nhật thông tin
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), R_ChangeProfile.class);
                startActivity(intent);
            });
        }

        // Chuyển đến trang đổi mật khẩu
        if (btnChangePassword != null) {
            btnChangePassword.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), R_ChangePass.class);
                startActivity(intent);
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Xác nhận đăng xuất")
                        .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                        .setPositiveButton("Đăng xuất", (dialog, which) -> {
                            // Xóa thông tin đăng nhập
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.clear();
                            editor.apply();

                            // Quay về màn hình đăng nhập
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            });
        }

        return view;
    }
}
