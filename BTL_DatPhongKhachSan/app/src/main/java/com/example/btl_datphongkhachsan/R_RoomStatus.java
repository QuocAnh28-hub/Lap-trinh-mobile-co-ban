package com.example.btl_datphongkhachsan;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

public class R_RoomStatus extends AppCompatActivity {

    private RecyclerView rvRoomStatus;
    private RoomStatusAdapter adapter;
    private EditText etSearchRoom;
    private TextView filterAll, filterAvailable, filterDirty, filterOccupied;

    private List<Room> fullRoomList = new ArrayList<>();
    private List<Room> displayedList = new ArrayList<>();

    private String currentFilterStatus = "ALL";
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.r_room_status);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupListeners();
        
        rvRoomStatus.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RoomStatusAdapter(displayedList);
        rvRoomStatus.setAdapter(adapter);

        loadRooms();
    }

    private void initViews() {
        rvRoomStatus = findViewById(R.id.rvRoomStatus);
        etSearchRoom = findViewById(R.id.etSearchRoom);
        filterAll = findViewById(R.id.filterAll);
        filterAvailable = findViewById(R.id.filterAvailable);
        filterDirty = findViewById(R.id.filterDirty);
        filterOccupied = findViewById(R.id.filterOccupied);
    }

    private void setupListeners() {
        filterAll.setOnClickListener(v -> updateFilter("ALL"));
        filterAvailable.setOnClickListener(v -> updateFilter("AVAILABLE"));
        filterDirty.setOnClickListener(v -> updateFilter("DIRTY"));
        filterOccupied.setOnClickListener(v -> updateFilter("OCCUPIED"));

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
        currentFilterStatus = status;
        updateFilterUI();
        applyFilterAndSearch();
    }

    private void applyFilterAndSearch() {
        displayedList.clear();
        for (Room room : fullRoomList) {
            boolean matchesStatus = currentFilterStatus.equals("ALL") || room.getStatus().equalsIgnoreCase(currentFilterStatus);
            boolean matchesSearch = currentSearchQuery.isEmpty() || 
                                   room.getRoomNumber().toLowerCase().contains(currentSearchQuery) ||
                                   String.valueOf(room.getRoomID()).contains(currentSearchQuery);

            if (matchesStatus && matchesSearch) {
                displayedList.add(room);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void updateFilterUI() {
        resetStyle(filterAll);
        resetStyle(filterAvailable);
        resetStyle(filterDirty);
        resetStyle(filterOccupied);

        switch (currentFilterStatus) {
            case "ALL": setSelected(filterAll); break;
            case "AVAILABLE": setSelected(filterAvailable); break;
            case "DIRTY": setSelected(filterDirty); break;
            case "OCCUPIED": setSelected(filterOccupied); break;
        }
    }

    private void resetStyle(TextView tv) {
        if (tv == null) return;
        tv.setBackgroundResource(R.drawable.bg_filter_chip_unselected);
        tv.setTextColor(Color.parseColor("#666666"));
    }

    private void setSelected(TextView tv) {
        if (tv == null) return;
        tv.setBackgroundResource(R.drawable.bg_filter_chip_selected);
        tv.setTextColor(Color.parseColor("#0D47A1"));
    }

    private void loadRooms() {
        RetrofitClient.getApiService().getAllRooms().enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fullRoomList.clear();
                    fullRoomList.addAll(response.body());
                    applyFilterAndSearch();
                }
            }
            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch rooms: " + t.getMessage());
            }
        });
    }
}
