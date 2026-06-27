package com.example.btl_datphongkhachsan.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_datphongkhachsan.R;
import com.example.btl_datphongkhachsan.models.PenaltyUsage;

import java.util.List;
import java.util.Locale;

public class PenaltyUsageAdapter extends RecyclerView.Adapter<PenaltyUsageAdapter.ViewHolder> {

    private List<PenaltyUsage> penalties;
    private OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(PenaltyUsage penalty);
    }

    public PenaltyUsageAdapter(List<PenaltyUsage> penalties, OnDeleteClickListener deleteClickListener) {
        this.penalties = penalties;
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
        PenaltyUsage penalty = penalties.get(position);
        holder.tvServiceName.setText(penalty.getReason());
        holder.tvServiceDetail.setText(penalty.getCreatedAt() != null ? penalty.getCreatedAt().substring(0, 10) : "");
        holder.tvServiceTotal.setText(String.format(Locale.getDefault(), "%,.0fđ", penalty.getAmount()));

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(penalty);
            }
        });
    }

    @Override
    public int getItemCount() {
        return penalties != null ? penalties.size() : 0;
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
