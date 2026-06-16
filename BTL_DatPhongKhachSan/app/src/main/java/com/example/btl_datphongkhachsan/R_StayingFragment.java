package com.example.btl_datphongkhachsan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class R_StayingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.r_staying, container, false);

        RecyclerView rvStayinh = view.findViewById(R.id.rvStayingList);
        if (rvStayinh != null) {
            rvStayinh.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        return view;
    }
}
