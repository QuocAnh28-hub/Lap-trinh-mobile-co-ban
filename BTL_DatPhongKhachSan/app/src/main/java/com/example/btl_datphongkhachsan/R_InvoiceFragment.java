package com.example.btl_datphongkhachsan;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_datphongkhachsan.adapters.InvoiceHistoryAdapter;
import com.example.btl_datphongkhachsan.adapters.UnpaidRoomAdapter;
import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.InvoiceHistory;
import com.example.btl_datphongkhachsan.models.PendingInvoice;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class R_InvoiceFragment extends Fragment {

    private RecyclerView rvUnpaidRooms, rvInvoiceHistory;
    private TextView tvUnpaidCount;
    private UnpaidRoomAdapter unpaidAdapter;
    private InvoiceHistoryAdapter historyAdapter;
    private List<PendingInvoice> unpaidList = new ArrayList<>();
    private List<InvoiceHistory> historyList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.r_invoice, container, false);

        initViews(view);
        setupRecyclerViews();

        loadData();

        return view;
    }

    private void initViews(View view) {
        rvUnpaidRooms = view.findViewById(R.id.rvUnpaidRooms);
        rvInvoiceHistory = view.findViewById(R.id.rvInvoiceHistory);
        tvUnpaidCount = view.findViewById(R.id.tvUnpaidCount);
    }

    private void setupRecyclerViews() {
        // Horizontal list for unpaid rooms
        rvUnpaidRooms.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        unpaidAdapter = new UnpaidRoomAdapter(unpaidList);
        rvUnpaidRooms.setAdapter(unpaidAdapter);

        // Vertical list for invoice history
        rvInvoiceHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new InvoiceHistoryAdapter(historyList);
        rvInvoiceHistory.setAdapter(historyAdapter);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            loadData();
        }
    }

    private void loadData() {
        fetchPendingInvoices();
        fetchInvoiceHistory();
    }

    private void fetchPendingInvoices() {
        RetrofitClient.getApiService().getPendingInvoices().enqueue(new Callback<List<PendingInvoice>>() {
            @Override
            public void onResponse(Call<List<PendingInvoice>> call, Response<List<PendingInvoice>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    unpaidList.clear();
                    unpaidList.addAll(response.body());
                    unpaidAdapter.notifyDataSetChanged();
                    tvUnpaidCount.setText(unpaidList.size() + " phòng");
                }
            }

            @Override
            public void onFailure(Call<List<PendingInvoice>> call, Throwable t) {
                if (isAdded()) {
                    Log.e("API_ERROR", "Pending Invoices failed: " + t.getMessage());
                }
            }
        });
    }

    private void fetchInvoiceHistory() {
        RetrofitClient.getApiService().getInvoiceHistory().enqueue(new Callback<List<InvoiceHistory>>() {
            @Override
            public void onResponse(Call<List<InvoiceHistory>> call, Response<List<InvoiceHistory>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    historyList.clear();
                    historyList.addAll(response.body());
                    historyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<InvoiceHistory>> call, Throwable t) {
                if (isAdded()) {
                    Log.e("API_ERROR", "Invoice History failed: " + t.getMessage());
                }
            }
        });
    }
}
