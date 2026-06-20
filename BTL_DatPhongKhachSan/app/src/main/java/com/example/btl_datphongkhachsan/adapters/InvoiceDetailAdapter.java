package com.example.btl_datphongkhachsan.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_datphongkhachsan.R;
import com.example.btl_datphongkhachsan.models.InvoiceFullResponse;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class InvoiceDetailAdapter extends RecyclerView.Adapter<InvoiceDetailAdapter.ViewHolder> {

    private List<InvoiceFullResponse.Detail> details;

    public InvoiceDetailAdapter(List<InvoiceFullResponse.Detail> details) {
        this.details = details;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_item_invoice_detail_line, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InvoiceFullResponse.Detail detail = details.get(position);
        holder.tvItemName.setText(detail.getItemName());
        holder.tvItemType.setText(detail.getItemType());
        holder.tvItemQuantity.setText("x" + detail.getQuantity());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvItemAmount.setText(formatter.format(detail.getAmount()));
    }

    @Override
    public int getItemCount() {
        return details != null ? details.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemType, tvItemQuantity, tvItemAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemType = itemView.findViewById(R.id.tvItemType);
            tvItemQuantity = itemView.findViewById(R.id.tvItemQuantity);
            tvItemAmount = itemView.findViewById(R.id.tvItemAmount);
        }
    }
}
