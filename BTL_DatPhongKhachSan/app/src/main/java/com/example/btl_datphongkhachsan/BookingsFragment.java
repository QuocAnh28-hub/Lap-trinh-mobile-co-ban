package com.example.btl_datphongkhachsan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.btl_datphongkhachsan.adapters.ReservationAdapter;
import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.Reservation;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingsFragment extends Fragment {

    private TabLayout tabLayout;
    private RecyclerView rvBookings;
    private ReservationAdapter adapter;
    private List<Reservation> allReservations = new ArrayList<>();
    private List<Reservation> filteredReservations = new ArrayList<>();
    private String currentStatus = "BOOKED";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookings, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        rvBookings = view.findViewById(R.id.rvBookings);

        adapter = new ReservationAdapter(filteredReservations);
        rvBookings.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBookings.setAdapter(adapter);

        adapter.setOnReservationCancelledListener(() -> loadReservations());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentStatus = tab.getText().toString();
                filterReservations();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadReservations();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load lại dữ liệu mỗi khi quay lại Fragment này (ví dụ từ trang Detail)
        loadReservations();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            loadReservations();
        }
    }

    private void loadReservations() {
        if (getActivity() == null) return;

        SharedPreferences sharedPref = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = sharedPref.getString("UserID", null);

        if (userId == null) {
            return;
        }

        RetrofitClient.getApiService().getCustomerReservations(userId).enqueue(new Callback<List<Reservation>>() {
            @Override
            public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allReservations.clear();
                    allReservations.addAll(response.body());
                    filterReservations();
                }
            }

            @Override
            public void onFailure(Call<List<Reservation>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }

    private void filterReservations() {
        filteredReservations.clear();
        for (Reservation res : allReservations) {
            if (res.getStatus() != null && res.getStatus().equalsIgnoreCase(currentStatus)) {
                filteredReservations.add(res);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
