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

import com.example.btl_datphongkhachsan.api.ApiService;
import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.OccupancyRateResponse;
import com.example.btl_datphongkhachsan.models.RevenueThisMonthResponse;
import com.example.btl_datphongkhachsan.models.RoomStatisticsResponse;
import com.example.btl_datphongkhachsan.models.RoomStatusSummaryResponse;
import com.example.btl_datphongkhachsan.models.TodayCheckInOutResponse;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class R_DashBoardFragment extends Fragment {

    private TextView tvTotalRooms, tvRoomSummary, tvOccupancyRate, tvCheckInOut, tvMonthlyRevenue;
    private TextView tvAvailableStatusCount, tvOccupiedStatusCount, tvDirtyStatusCount;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.r_dash_board, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        apiService = RetrofitClient.getApiService();
        loadDashboardData(); // Tải lần đầu khi view được tạo
    }

    // Quan trọng: Hàm này chạy mỗi khi fragment được show/hide trong R_Navigation
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            Log.d("API_DEBUG", "Dashboard trở lại màn hình, đang tải lại dữ liệu...");
            loadDashboardData(); 
        }
    }

    private void initViews(View view) {
        tvTotalRooms = view.findViewById(R.id.tvTotalRooms);
        tvRoomSummary = view.findViewById(R.id.tvRoomSummary);
        tvOccupancyRate = view.findViewById(R.id.tvOccupancyRate);
        tvCheckInOut = view.findViewById(R.id.tvCheckInOut);
        tvMonthlyRevenue = view.findViewById(R.id.tvMonthlyRevenue);
        
        tvAvailableStatusCount = view.findViewById(R.id.tvAvailableStatusCount);
        tvOccupiedStatusCount = view.findViewById(R.id.tvOccupiedStatusCount);
        tvDirtyStatusCount = view.findViewById(R.id.tvDirtyStatusCount);
    }

    private void loadDashboardData() {
        if (apiService == null) apiService = RetrofitClient.getApiService();
        fetchRoomStatistics();
        fetchOccupancyRate();
        fetchRoomStatusSummary();
        fetchTodayCheckInOut();
        fetchRevenueThisMonth();
    }

    private void fetchRoomStatistics() {
        apiService.getRoomStatistics().enqueue(new Callback<RoomStatisticsResponse>() {
            @Override
            public void onResponse(Call<RoomStatisticsResponse> call, Response<RoomStatisticsResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    RoomStatisticsResponse stats = response.body();
                    tvTotalRooms.setText(String.valueOf(stats.getTotalRooms()));
                    tvRoomSummary.setText("Trống: " + stats.getAvailableRooms() + " | Dùng: " + stats.getOccupiedRooms());
                }
            }
            @Override
            public void onFailure(Call<RoomStatisticsResponse> call, Throwable t) {
                if (isAdded()) Log.e("API_DEBUG", "RoomStats Error: " + t.getMessage());
            }
        });
    }

    private void fetchOccupancyRate() {
        apiService.getOccupancyRate().enqueue(new Callback<OccupancyRateResponse>() {
            @Override
            public void onResponse(Call<OccupancyRateResponse> call, Response<OccupancyRateResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    tvOccupancyRate.setText(String.format(Locale.getDefault(), "%.2f%%", response.body().getOccupancyRate()));
                }
            }
            @Override
            public void onFailure(Call<OccupancyRateResponse> call, Throwable t) {
                if (isAdded()) Log.e("API_DEBUG", "Occupancy Error: " + t.getMessage());
            }
        });
    }

    private void fetchRoomStatusSummary() {
        apiService.getRoomStatusSummary().enqueue(new Callback<RoomStatusSummaryResponse>() {
            @Override
            public void onResponse(Call<RoomStatusSummaryResponse> call, Response<RoomStatusSummaryResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    RoomStatusSummaryResponse summary = response.body();
                    tvAvailableStatusCount.setText(String.valueOf(summary.getAvailableRooms()));
                    tvOccupiedStatusCount.setText(String.valueOf(summary.getOccupiedRooms()));
                    tvDirtyStatusCount.setText(String.valueOf(summary.getDirtyRooms()));
                }
            }
            @Override
            public void onFailure(Call<RoomStatusSummaryResponse> call, Throwable t) {
                if (isAdded()) Log.e("API_DEBUG", "Status Error: " + t.getMessage());
            }
        });
    }

    private void fetchTodayCheckInOut() {
        apiService.getTodayCheckInOut().enqueue(new Callback<TodayCheckInOutResponse>() {
            @Override
            public void onResponse(Call<TodayCheckInOutResponse> call, Response<TodayCheckInOutResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    TodayCheckInOutResponse data = response.body();
                    tvCheckInOut.setText(data.getTodayCheckIn() + " / " + data.getTodayCheckOut());
                }
            }
            @Override
            public void onFailure(Call<TodayCheckInOutResponse> call, Throwable t) {
                if (isAdded()) Log.e("API_DEBUG", "CheckInOut Error: " + t.getMessage());
            }
        });
    }

    private void fetchRevenueThisMonth() {
        apiService.getRevenueThisMonth().enqueue(new Callback<RevenueThisMonthResponse>() {
            @Override
            public void onResponse(Call<RevenueThisMonthResponse> call, Response<RevenueThisMonthResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    RevenueThisMonthResponse data = response.body();
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                    tvMonthlyRevenue.setText(formatter.format(data.getTotalRevenue()));
                }
            }
            @Override
            public void onFailure(Call<RevenueThisMonthResponse> call, Throwable t) {
                if (isAdded()) Log.e("API_DEBUG", "Revenue Error: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}