package com.example.btl_datphongkhachsan.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl_datphongkhachsan.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class GenericPaymentAdapter extends RecyclerView.Adapter<GenericPaymentAdapter.ViewHolder> {

    public static class PaymentItem {
        String name;
        String type;
        int quantity;
        double amount;

        public PaymentItem(String name, String type, int quantity, double amount) {
            this.name = name;
            this.type = type;
            this.quantity = quantity;
            this.amount = amount;
        }
    }

    private List<PaymentItem> items;

    public GenericPaymentAdapter(List<PaymentItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_item_invoice_detail_line, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentItem item = items.get(position);
        holder.tvItemName.setText(item.name);
        holder.tvItemType.setText(item.type);
        holder.tvItemQuantity.setText("x" + item.quantity);

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvItemAmount.setText(formatter.format(item.amount));
    }

    @Override
    public int getItemCount() {
        return items.size();
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
