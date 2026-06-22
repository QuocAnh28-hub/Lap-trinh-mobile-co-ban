package com.example.btl_datphongkhachsan;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_datphongkhachsan.adapters.StayingCustomerAdapter;
import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.StayingCustomer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class R_StayingFragment extends Fragment {

    private RecyclerView rvStaying;
    private StayingCustomerAdapter adapter;
    private List<StayingCustomer> customerList = new ArrayList<>();
    private TextView tabStaying;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.r_staying, container, false);

        rvStaying = view.findViewById(R.id.rvStayingList);
        tabStaying = view.findViewById(R.id.tabStaying);
        
        if (rvStaying != null) {
            rvStaying.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new StayingCustomerAdapter(customerList);
            rvStaying.setAdapter(adapter);
        }

        // Tìm tab "Chờ nhận phòng" để thực hiện chuyển đổi
        TextView tabWaiting = view.findViewById(R.id.tabWaiting);
        if (tabWaiting != null) {
            tabWaiting.setOnClickListener(v -> {
                if (getActivity() instanceof R_Navigation) {
                    ((R_Navigation) getActivity()).switchBookingTab(new R_WaitingFragment());
                }
            });
        }

        loadStayingCustomers();

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            loadStayingCustomers();
        }
    }

    private void loadStayingCustomers() {
        RetrofitClient.getApiService().getCurrentStayingCustomers().enqueue(new Callback<List<StayingCustomer>>() {
            @Override
            public void onResponse(Call<List<StayingCustomer>> call, Response<List<StayingCustomer>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    customerList.clear();
                    customerList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<StayingCustomer>> call, Throwable t) {
                if (isAdded()) {
                    Log.e("API_ERROR", "Failed to fetch staying customers: " + t.getMessage());
                }
            }
        });
    }
}
