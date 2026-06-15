package com.example.btl_datphongkhachsan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class R_InvoiceFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Sử dụng layout r_room_status.xml đã thiết kế
        View view = inflater.inflate(R.layout.r_invoice, container, false);

        // Khởi tạo RecyclerView nếu cần (sau này bạn sẽ thiết lập Adapter ở đây)
        RecyclerView rvInvoice = view.findViewById(R.id.rvInvoiceHistory);
        if (rvInvoice != null) {
            rvInvoice.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        return view;
    }
}
