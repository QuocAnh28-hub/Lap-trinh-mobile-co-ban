package com.example.btl_datphongkhachsan;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private final Fragment homeFragment = new HomeFragment();
    private final Fragment bookingsFragment = new BookingsFragment();
    private final Fragment profileFragment = new ProfileFragment();
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment active = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Khởi tạo các Fragment và ẩn chúng đi, chỉ hiện HomeFragment
        fm.beginTransaction().add(R.id.fragment_container, profileFragment, "3").hide(profileFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, bookingsFragment, "2").hide(bookingsFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, homeFragment, "1").commit();

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                fm.beginTransaction().hide(active).show(homeFragment).commit();
                active = homeFragment;
                return true;
            } else if (itemId == R.id.nav_bookings) {
                // Sử dụng detach và attach để load lại Fragment (vòng đời view sẽ được chạy lại)
                fm.beginTransaction().hide(active).detach(bookingsFragment).attach(bookingsFragment).show(bookingsFragment).commit();
                active = bookingsFragment;
                return true;
            } else if (itemId == R.id.nav_profile) {
                fm.beginTransaction().hide(active).show(profileFragment).commit();
                active = profileFragment;
                return true;
            }
            return false;
        });
    }
}