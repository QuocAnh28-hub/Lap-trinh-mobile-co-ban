package com.example.btl_datphongkhachsan.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_datphongkhachsan.R_InvoiceDetailActivity;
import com.example.btl_datphongkhachsan.R;
import com.example.btl_datphongkhachsan.models.InvoiceHistory;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class InvoiceHistoryAdapter extends RecyclerView.Adapter<InvoiceHistoryAdapter.ViewHolder> {

    private List<InvoiceHistory> historyList;

    public InvoiceHistoryAdapter(List<InvoiceHistory> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InvoiceHistory item = historyList.get(position);
        holder.tvInvoiceId.setText("#" + item.getStayID());
        holder.tvCustomerName.setText(item.getFullName());
        holder.tvInvoiceDate.setText(formatDate(item.getLatestDate()));

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvAmount.setText(formatter.format(item.getTotalAmount()));
        holder.tvPaidStatus.setText(item.getStatus());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), R_InvoiceDetailActivity.class);
            intent.putExtra("STAY_ID", item.getStayID());
            v.getContext().startActivity(intent);
        });
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.length() < 16) return "";
        try {
            String datePart = dateStr.split("T")[0];
            String timePart = dateStr.split("T")[1].substring(0, 5);
            String[] dateArr = datePart.split("-");
            return dateArr[2] + "/" + dateArr[1] + "/" + dateArr[0] + " • " + timePart;
        } catch (Exception e) {
            return dateStr;
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInvoiceId, tvCustomerName, tvInvoiceDate, tvAmount, tvPaidStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInvoiceId = itemView.findViewById(R.id.tvInvoiceId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvInvoiceDate = itemView.findViewById(R.id.tvInvoiceDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvPaidStatus = itemView.findViewById(R.id.tvPaidStatus);
        }
    }
}
