package com.example.btl_datphongkhachsan;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_datphongkhachsan.adapters.RoomStatusAdapter;
import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.Room;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class R_RoomStatusFragment extends Fragment {

    private RecyclerView rvRoomStatus;
    private RoomStatusAdapter adapter;
    private EditText etSearchRoom;
    private TextView filterAll, filterAvailable, filterDirty, filterOccupied;
    
    private List<Room> fullRoomList = new ArrayList<>(); 
    private List<Room> displayedList = new ArrayList<>(); 
    
    private String currentFilterStatus = "ALL";
    private String currentSearchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.r_room_status, container, false);

        initViews(view);
        setupListeners();
        
        // Cấu hình RecyclerView
        rvRoomStatus.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RoomStatusAdapter(displayedList);
        rvRoomStatus.setAdapter(adapter);

        loadRooms();

        return view;
    }

    private void initViews(View view) {
        rvRoomStatus = view.findViewById(R.id.rvRoomStatus);
        etSearchRoom = view.findViewById(R.id.etSearchRoom);
        filterAll = view.findViewById(R.id.filterAll);
        filterAvailable = view.findViewById(R.id.filterAvailable);
        filterDirty = view.findViewById(R.id.filterDirty);
        filterOccupied = view.findViewById(R.id.filterOccupied);
    }

    private void setupListeners() {
        // Cài đặt sự kiện Click cho các nút lọc
        filterAll.setOnClickListener(v -> updateFilter("ALL"));
        filterAvailable.setOnClickListener(v -> updateFilter("AVAILABLE"));
        filterDirty.setOnClickListener(v -> updateFilter("DIRTY"));
        filterOccupied.setOnClickListener(v -> updateFilter("OCCUPIED"));

        // Cài đặt sự kiện Tìm kiếm khi nhập chữ
        etSearchRoom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().toLowerCase().trim();
                applyFilterAndSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateFilter(String status) {
        Log.d("FILTER_DEBUG", "Chọn lọc: " + status);
        currentFilterStatus = status;
        updateFilterUI();
        applyFilterAndSearch();
    }

    private void applyFilterAndSearch() {
        displayedList.clear();
        for (Room room : fullRoomList) {
            // Kiểm tra trạng thái
            boolean matchesStatus = currentFilterStatus.equals("ALL") || 
                                   room.getStatus().equalsIgnoreCase(currentFilterStatus);
            
            // Kiểm tra từ khóa tìm kiếm (Số phòng hoặc ID)
            boolean matchesSearch = currentSearchQuery.isEmpty() || 
                                   room.getRoomNumber().toLowerCase().contains(currentSearchQuery) ||
                                   String.valueOf(room.getRoomID()).contains(currentSearchQuery);

            if (matchesStatus && matchesSearch) {
                displayedList.add(room);
            }
        }
        Log.d("FILTER_DEBUG", "Số lượng hiển thị: " + displayedList.size());
        adapter.notifyDataSetChanged();
    }

    private void updateFilterUI() {
        // Reset giao diện các nút
        resetStyle(filterAll);
        resetStyle(filterAvailable);
        resetStyle(filterDirty);
        resetStyle(filterOccupied);

        // Highlight nút đang chọn
        switch (currentFilterStatus) {
            case "ALL": setSelected(filterAll); break;
            case "AVAILABLE": setSelected(filterAvailable); break;
            case "DIRTY": setSelected(filterDirty); break;
            case "OCCUPIED": setSelected(filterOccupied); break;
        }
    }

    private void resetStyle(TextView tv) {
        if (tv != null) {
            tv.setBackgroundResource(R.drawable.bg_filter_chip_unselected);
            tv.setTextColor(Color.parseColor("#666666"));
        }
    }

    private void setSelected(TextView tv) {
        if (tv != null) {
            tv.setBackgroundResource(R.drawable.bg_filter_chip_selected);
            tv.setTextColor(Color.parseColor("#0D47A1"));
        }
    }

    private void loadRooms() {
        Log.d("API_DEBUG", "Đang tải danh sách phòng...");
        RetrofitClient.getApiService().getAllRooms().enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    fullRoomList.clear();
                    fullRoomList.addAll(response.body());
                    Log.d("API_DEBUG", "Tải thành công " + fullRoomList.size() + " phòng");
                    applyFilterAndSearch();
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                if (isAdded()) {
                    Log.e("API_DEBUG", "Lỗi tải phòng: " + t.getMessage());
                    Toast.makeText(getContext(), "Không thể kết nối server", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            Log.d("FILTER_DEBUG", "Tab Phòng hiện lên, làm mới dữ liệu...");
            loadRooms();
        }
    }
}
