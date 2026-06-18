package com.example.btl_datphongkhachsan;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class R_DashBoard extends AppCompatActivity {

    private TextView tvTotalRooms, tvRoomSummary, tvOccupancyRate, tvCheckInOut, tvMonthlyRevenue;
    private TextView tvAvailableStatusCount, tvOccupiedStatusCount, tvDirtyStatusCount;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.r_dash_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        apiService = RetrofitClient.getApiService();
        loadDashboardData();
    }

    private void initViews() {
        tvTotalRooms = findViewById(R.id.tvTotalRooms);
        tvRoomSummary = findViewById(R.id.tvRoomSummary);
        tvOccupancyRate = findViewById(R.id.tvOccupancyRate);
        tvCheckInOut = findViewById(R.id.tvCheckInOut);
        tvMonthlyRevenue = findViewById(R.id.tvMonthlyRevenue);
        
        tvAvailableStatusCount = findViewById(R.id.tvAvailableStatusCount);
        tvOccupiedStatusCount = findViewById(R.id.tvOccupiedStatusCount);
        tvDirtyStatusCount = findViewById(R.id.tvDirtyStatusCount);
    }

    private void loadDashboardData() {
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
                if (response.isSuccessful() && response.body() != null) {
                    RoomStatisticsResponse stats = response.body();
                    tvTotalRooms.setText(String.valueOf(stats.getTotalRooms()));
                    tvRoomSummary.setText("Trống: " + stats.getAvailableRooms() + " | Dùng: " + stats.getOccupiedRooms());
                }
            }

            @Override
            public void onFailure(Call<RoomStatisticsResponse> call, Throwable t) {
                showError("Lỗi tải thống kê phòng");
            }
        });
    }

    private void fetchOccupancyRate() {
        apiService.getOccupancyRate().enqueue(new Callback<OccupancyRateResponse>() {
            @Override
            public void onResponse(Call<OccupancyRateResponse> call, Response<OccupancyRateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvOccupancyRate.setText(String.format(Locale.getDefault(), "%.2f%%", response.body().getOccupancyRate()));
                }
            }

            @Override
            public void onFailure(Call<OccupancyRateResponse> call, Throwable t) {
                showError("Lỗi tải công suất phòng");
            }
        });
    }

    private void fetchRoomStatusSummary() {
        apiService.getRoomStatusSummary().enqueue(new Callback<RoomStatusSummaryResponse>() {
            @Override
            public void onResponse(Call<RoomStatusSummaryResponse> call, Response<RoomStatusSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RoomStatusSummaryResponse summary = response.body();
                    tvAvailableStatusCount.setText(String.valueOf(summary.getAvailableRooms()));
                    tvOccupiedStatusCount.setText(String.valueOf(summary.getOccupiedRooms()));
                    tvDirtyStatusCount.setText(String.valueOf(summary.getDirtyRooms()));
                }
            }

            @Override
            public void onFailure(Call<RoomStatusSummaryResponse> call, Throwable t) {
                showError("Lỗi tải trạng thái phòng");
            }
        });
    }

    private void fetchTodayCheckInOut() {
        apiService.getTodayCheckInOut().enqueue(new Callback<TodayCheckInOutResponse>() {
            @Override
            public void onResponse(Call<TodayCheckInOutResponse> call, Response<TodayCheckInOutResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TodayCheckInOutResponse data = response.body();
                    tvCheckInOut.setText(data.getTodayCheckIn() + " / " + data.getTodayCheckOut());
                }
            }

            @Override
            public void onFailure(Call<TodayCheckInOutResponse> call, Throwable t) {
                showError("Lỗi tải check-in/out");
            }
        });
    }

    private void fetchRevenueThisMonth() {
        apiService.getRevenueThisMonth().enqueue(new Callback<RevenueThisMonthResponse>() {
            @Override
            public void onResponse(Call<RevenueThisMonthResponse> call, Response<RevenueThisMonthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RevenueThisMonthResponse data = response.body();
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                    tvMonthlyRevenue.setText(formatter.format(data.getTotalRevenue()));
                    // Cập nhật text phụ nếu cần (ví dụ: "Từ X lượt lưu trú")
                    // Tìm TextView phụ trong layout r_dash_board
                    // (Giả sử bạn muốn cập nhật TextView hiển thị lượt lưu trú, bạn cần gán ID cho nó trong XML)
                }
            }

            @Override
            public void onFailure(Call<RevenueThisMonthResponse> call, Throwable t) {
                showError("Lỗi tải doanh thu");
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
