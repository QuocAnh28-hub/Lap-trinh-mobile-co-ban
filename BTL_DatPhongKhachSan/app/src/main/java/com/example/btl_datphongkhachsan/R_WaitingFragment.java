package com.example.btl_datphongkhachsan;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class R_WaitingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.r_waiting, container, false);

        RecyclerView rvWaiting = view.findViewById(R.id.rvWaitingList);
        if (rvWaiting != null) {
            rvWaiting.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        // Xử lý sự kiện click vào Tab "Đang lưu trú" để quay lại
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView tabStaying = view.findViewById(R.id.tabStaying);
        if (tabStaying != null) {
            tabStaying.setOnClickListener(v -> {
                if (getActivity() instanceof R_Navigation) {
                    ((R_Navigation) getActivity()).switchBookingTab(new R_StayingFragment());
                }
            });
        }

        return view;
    }
}
