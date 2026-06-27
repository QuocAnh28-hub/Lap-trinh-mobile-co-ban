package com.example.btl_datphongkhachsan.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_datphongkhachsan.R;
import com.example.btl_datphongkhachsan.models.MinibarUsage;

import java.util.List;
import java.util.Locale;

public class MinibarUsageAdapter extends RecyclerView.Adapter<MinibarUsageAdapter.ViewHolder> {

    private List<MinibarUsage> usages;
    private OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(MinibarUsage usage);
    }

    public MinibarUsageAdapter(List<MinibarUsage> usages, OnDeleteClickListener deleteClickListener) {
        this.usages = usages;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_usage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MinibarUsage usage = usages.get(position);
        holder.tvServiceName.setText(usage.getItemName());
        holder.tvServiceDetail.setText(String.format(Locale.getDefault(), "Số lượng: %d • Đơn giá: %,.0fđ", 
                usage.getQuantity(), usage.getPrice()));
        holder.tvServiceTotal.setText(String.format(Locale.getDefault(), "%,.0fđ", usage.getTotal()));

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(usage);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usages != null ? usages.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName, tvServiceDetail, tvServiceTotal;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvServiceDetail = itemView.findViewById(R.id.tvServiceDetail);
            tvServiceTotal = itemView.findViewById(R.id.tvServiceTotal);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
