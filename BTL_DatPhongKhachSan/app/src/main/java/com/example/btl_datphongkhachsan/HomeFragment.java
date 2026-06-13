package com.example.btl_datphongkhachsan;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.btl_datphongkhachsan.adapters.RoomTypeAdapter;
import com.example.btl_datphongkhachsan.api.RetrofitClient;
import com.example.btl_datphongkhachsan.models.RoomType;
import com.example.btl_datphongkhachsan.models.SearchAvailableRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private TextView tvCheckIn, tvCheckOut;
    private EditText etGuests, etRooms, etSearchOurSuites;
    private RecyclerView rvAvailableRoomTypes, rvOurSuites;
    private RoomTypeAdapter availabilityAdapter, suitesAdapter;
    private List<RoomType> availableRoomTypes = new ArrayList<>();
    private List<RoomType> allRoomTypes = new ArrayList<>();
    private List<RoomType> filteredSuites = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvCheckIn = view.findViewById(R.id.tvCheckIn);
        tvCheckOut = view.findViewById(R.id.tvCheckOut);
        etGuests = view.findViewById(R.id.etGuests);
        etRooms = view.findViewById(R.id.etRooms);
        etSearchOurSuites = view.findViewById(R.id.etSearchOurSuites);
        rvAvailableRoomTypes = view.findViewById(R.id.rvAvailableRoomTypes);
        rvOurSuites = view.findViewById(R.id.rvOurSuites);
        Button btnCheckAvailability = view.findViewById(R.id.btnCheckAvailability);
        ImageButton btnSearchSuites = view.findViewById(R.id.btnSearchSuites);

        // Date Picker listeners
        view.findViewById(R.id.btnCheckIn).setOnClickListener(v -> showDatePicker(tvCheckIn));
        view.findViewById(R.id.btnCheckOut).setOnClickListener(v -> showDatePicker(tvCheckOut));

        // Setup RecyclerView for Availability (horizontal)
        availabilityAdapter = new RoomTypeAdapter(availableRoomTypes);
        rvAvailableRoomTypes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvAvailableRoomTypes.setAdapter(availabilityAdapter);

        // Setup RecyclerView for Our Suites (horizontal)
        suitesAdapter = new RoomTypeAdapter(filteredSuites);
        rvOurSuites.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvOurSuites.setAdapter(suitesAdapter);

        btnCheckAvailability.setOnClickListener(v -> performSearch());

        // Handle Search Suites Button click
        if (btnSearchSuites != null) {
            btnSearchSuites.setOnClickListener(v -> filterOurSuites());
        }

        // Load room types from API
        loadAllRoomTypes();

        return view;
    }

    private void showDatePicker(TextView textView) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, monthOfYear, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, (monthOfYear + 1), dayOfMonth);
            textView.setText(selectedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void loadAllRoomTypes() {
        RetrofitClient.getApiService().getRoomTypesWithPrice().enqueue(new Callback<List<RoomType>>() {
            @Override
            public void onResponse(Call<List<RoomType>> call, Response<List<RoomType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allRoomTypes.clear();
                    allRoomTypes.addAll(response.body());
                    filteredSuites.clear();
                    filteredSuites.addAll(allRoomTypes);
                    suitesAdapter.setSearchParameters("yyyy-mm-dd", "yyyy-mm-dd", 1, 1);
                    suitesAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<RoomType>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage() != null ? t.getMessage() : "Unknown error");
            }
        });
    }

    private void filterOurSuites() {
        String query = etSearchOurSuites.getText().toString().trim().toLowerCase();
        filteredSuites.clear();
        if (query.isEmpty()) {
            filteredSuites.addAll(allRoomTypes);
        } else {
            for (RoomType rt : allRoomTypes) {
                boolean matchesName = rt.getName() != null && rt.getName().toLowerCase().contains(query);
                boolean matchesDesc = rt.getDescription() != null && rt.getDescription().toLowerCase().contains(query);
                if (matchesName || matchesDesc) {
                    filteredSuites.add(rt);
                }
            }
        }
        suitesAdapter.notifyDataSetChanged();
        if (filteredSuites.isEmpty()) {
            Toast.makeText(getContext(), "No suites found matching your search", Toast.LENGTH_SHORT).show();
        }
    }

    private void performSearch() {
        String checkIn = tvCheckIn.getText().toString();
        String checkOut = tvCheckOut.getText().toString();

        if (checkIn.equals("yyyy-mm-dd") || checkOut.equals("yyyy-mm-dd")) {
            Toast.makeText(getContext(), "Vui lòng chọn ngày nhận/trả phòng", Toast.LENGTH_SHORT).show();
            return;
        }

        int guests = 1, rooms = 1;
        try {
            String g = etGuests.getText().toString();
            String r = etRooms.getText().toString();
            if(!g.isEmpty()) guests = Integer.parseInt(g);
            if(!r.isEmpty()) rooms = Integer.parseInt(r);
        } catch (NumberFormatException e) {
            Log.e("HOME_FRAGMENT", "Invalid numeric input");
        }

        final int finalGuests = guests;
        final int finalRooms = rooms;

        SearchAvailableRequest request = new SearchAvailableRequest(checkIn, checkOut, guests, rooms);
        RetrofitClient.getApiService().searchAvailable(request).enqueue(new Callback<List<RoomType>>() {
            @Override
            public void onResponse(Call<List<RoomType>> call, Response<List<RoomType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    availableRoomTypes.clear();
                    availableRoomTypes.addAll(response.body());
                    availabilityAdapter.setSearchParameters(checkIn, checkOut, finalRooms, finalGuests);
                    availabilityAdapter.notifyDataSetChanged();
                    
                    if (availableRoomTypes.isEmpty()) {
                        Toast.makeText(getContext(), "Không tìm thấy phòng trống", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<List<RoomType>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage() != null ? t.getMessage() : "Unknown error");
            }
        });
    }
}
