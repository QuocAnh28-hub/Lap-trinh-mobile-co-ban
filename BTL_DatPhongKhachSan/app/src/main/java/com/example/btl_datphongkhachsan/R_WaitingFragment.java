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

import com.example.btl_datphongkhachsan.adapters.WaitingCustomerAdapter;
import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.WaitingCustomer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class R_WaitingFragment extends Fragment {

    private RecyclerView rvWaiting;
    private WaitingCustomerAdapter adapter;
    private List<WaitingCustomer> waitingList = new ArrayList<>();
    private TextView tvSectionCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.r_waiting, container, false);

        rvWaiting = view.findViewById(R.id.rvWaitingList);
        // Find the count badge (it's a TextView after tvSectionTitle in the XML)
        // Note: In r_waiting.xml, it doesn't have an ID. I'll need to update the XML or find it by position.
        // Let's check the XML again.
        
        if (rvWaiting != null) {
            rvWaiting.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new WaitingCustomerAdapter(waitingList);
            rvWaiting.setAdapter(adapter);
        }

        // Tab switching logic
        TextView tabStaying = view.findViewById(R.id.tabStaying);
        if (tabStaying != null) {
            tabStaying.setOnClickListener(v -> {
                if (getActivity() instanceof R_Navigation) {
                    ((R_Navigation) getActivity()).switchBookingTab(new R_StayingFragment());
                }
            });
        }

        loadWaitingCustomers();

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            loadWaitingCustomers();
        }
    }

    private void loadWaitingCustomers() {
        RetrofitClient.getApiService().getWaitingCheckinCustomers().enqueue(new Callback<List<WaitingCustomer>>() {
            @Override
            public void onResponse(Call<List<WaitingCustomer>> call, Response<List<WaitingCustomer>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    waitingList.clear();
                    waitingList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    
                    Log.d("WAITING_DEBUG", "Loaded " + waitingList.size() + " customers");
                }
            }

            @Override
            public void onFailure(Call<List<WaitingCustomer>> call, Throwable t) {
                if (isAdded()) {
                    Log.e("API_ERROR", "Failed to fetch waiting customers: " + t.getMessage());
                }
            }
        });
    }
}
