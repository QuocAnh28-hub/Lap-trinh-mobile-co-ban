package com.example.btl_datphongkhachsan;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class R_Navigation extends AppCompatActivity {

    private final Fragment dashBoardFragment = new DashBoardFragment();
    private final Fragment profileFragment = new R_ProfileFragment();
    private final Fragment roomsFragment = new R_RoomStatusFragment();
    private final Fragment bookingsFragment = new BookingsFragment();
    
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment active = dashBoardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_navigation);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        // Khởi tạo các Fragment và ẩn chúng đi, chỉ hiện dashBoardFragment mặc định
        fm.beginTransaction().add(R.id.fragment_container, roomsFragment, "rooms").hide(roomsFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, profileFragment, "profile").hide(profileFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, dashBoardFragment, "home").commit();

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                fm.beginTransaction().hide(active).show(dashBoardFragment).commit();
                active = dashBoardFragment;
                return true;
            } else if (itemId == R.id.nav_profile) {
                fm.beginTransaction().hide(active).show(profileFragment).commit();
                active = profileFragment;
                return true;
            } else if (itemId == R.id.nav_rooms) {
                fm.beginTransaction().hide(active).show(roomsFragment).commit();
                active = roomsFragment;
                return true;
            }
            return false;
        });
    }
}
