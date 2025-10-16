package com.example.car_service.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.car_service.R;
import com.example.car_service.model.Product;

public class ProductAdapter extends ListAdapter<Product, ProductAdapter.ProductViewHolder> {

    public interface OnCartChangedListener {
        void onCartChanged(Integer productId, int quantity, double unitPrice);
    }

    private final OnCartChangedListener cartListener;

    public ProductAdapter(java.util.List<Product> initial, OnCartChangedListener cartListener) {
        super(DIFF);
        this.cartListener = cartListener;
        submitList(initial);
    }

    private static final DiffUtil.ItemCallback<Product> DIFF = new DiffUtil.ItemCallback<Product>() {
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getName().equals(newItem.getName()) && oldItem.getPrice() == newItem.getPrice();
        }
    };

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product p = getItem(position);
        holder.textName.setText(p.getName());
        holder.textPrice.setText(String.format("%,.0f₫", p.getPrice()));
        Glide.with(holder.itemView.getContext()).load(p.getImageUrl()).placeholder(R.drawable.ic_launcher_background).into(holder.image);

        holder.buttonMinus.setOnClickListener(v -> {
            int q = Math.max(0, Integer.parseInt(holder.textQty.getText().toString()) - 1);
            holder.textQty.setText(String.valueOf(q));
            cartListener.onCartChanged(p.getId(), q, p.getPrice());
        });
        holder.buttonPlus.setOnClickListener(v -> {
            int q = Integer.parseInt(holder.textQty.getText().toString()) + 1;
            holder.textQty.setText(String.valueOf(q));
            cartListener.onCartChanged(p.getId(), q, p.getPrice());
        });
    }

    public Product findProductById(Integer id) {
        for (int i = 0; i < getItemCount(); i++) {
            if (getItem(i).getId().equals(id)) return getItem(i);
        }
        return null;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView textName;
        TextView textPrice;
        Button buttonMinus;
        Button buttonPlus;
        TextView textQty;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageProduct);
            textName = itemView.findViewById(R.id.textProductName);
            textPrice = itemView.findViewById(R.id.textProductPrice);
            buttonMinus = itemView.findViewById(R.id.buttonMinus);
            buttonPlus = itemView.findViewById(R.id.buttonPlus);
            textQty = itemView.findViewById(R.id.textQty);
        }
    }
}



