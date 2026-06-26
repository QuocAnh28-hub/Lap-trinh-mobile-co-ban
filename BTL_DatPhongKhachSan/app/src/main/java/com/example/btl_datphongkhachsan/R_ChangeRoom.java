package com.example.btl_datphongkhachsan;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.Room;
import com.example.btl_datphongkhachsan.models.TransferRoomRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class R_ChangeRoom extends AppCompatActivity {

    private EditText edtCurrentRoom, edtReason;
    private Spinner spnNewRoom;
    private int stayId, oldRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.r_change_room);

        initViews();
        getDataFromIntent();
        setupEdgeToEdge();
        fetchAvailableRooms();

        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
        findViewById(R.id.btnClose).setOnClickListener(v -> finish());
        findViewById(R.id.btnConfirm).setOnClickListener(v -> performTransferRoom());
    }

    private void performTransferRoom() {
        Room selectedRoom = (Room) spnNewRoom.getSelectedItem();
        if (selectedRoom == null) {
            Toast.makeText(this, "Chưa có phòng sẵn sàng", Toast.LENGTH_SHORT).show();
            return;
        }

        TransferRoomRequest request = new TransferRoomRequest(stayId, oldRoomId, selectedRoom.getRoomID());
        RetrofitClient.getApiService().transferRoom(request).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(R_ChangeRoom.this, "Chuyển phòng thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(R_ChangeRoom.this, "Chuyển phòng thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(R_ChangeRoom.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        edtCurrentRoom = findViewById(R.id.edtCurrentRoom);
        spnNewRoom = findViewById(R.id.spnNewRoom);
    }

    private void getDataFromIntent() {
        stayId = getIntent().getIntExtra("STAY_ID", -1);
        oldRoomId = getIntent().getIntExtra("OLD_ROOM_ID", -1);
        String currentRoom = getIntent().getStringExtra("CURRENT_ROOM");
        edtCurrentRoom.setText(currentRoom);
    }

    private void fetchAvailableRooms() {
        RetrofitClient.getApiService().getAllRooms().enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Room> allRooms = response.body();
                    List<Room> availableRooms = new ArrayList<>();
                    
                    for (Room room : allRooms) {
                        if ("AVAILABLE".equalsIgnoreCase(room.getStatus())) {
                            availableRooms.add(room);
                        }
                    }

                    ArrayAdapter<Room> adapter = new ArrayAdapter<>(R_ChangeRoom.this,
                            android.R.layout.simple_spinner_item, availableRooms);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnNewRoom.setAdapter(adapter);
                } else {
                    Toast.makeText(R_ChangeRoom.this, "Không thể tải danh sách phòng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Toast.makeText(R_ChangeRoom.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int padding20dp = (int) (20 * getResources().getDisplayMetrics().density);
            v.setPadding(
                    systemBars.left + padding20dp,
                    systemBars.top + padding20dp,
                    systemBars.right + padding20dp,
                    systemBars.bottom + padding20dp
            );
            return insets;
        });
    }
}
