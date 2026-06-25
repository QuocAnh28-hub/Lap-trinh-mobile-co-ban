package com.example.btl_datphongkhachsan;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class R_CallService extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.r_call_service);

        findViewById(R.id.btnCloseActivity).setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Chuyển đổi 20dp sang pixel để cộng dồn vào Insets
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