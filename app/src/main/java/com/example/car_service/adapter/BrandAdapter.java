package com.example.car_service.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.car_service.R;
import com.example.car_service.model.Brand;

import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder> {

    public interface OnBrandClickListener {
        void onBrandClick(Brand brand);
    }

    private final List<Brand> brandList;
    private final OnBrandClickListener onBrandClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public BrandAdapter(List<Brand> brandList, OnBrandClickListener onBrandClickListener) {
        this.brandList = brandList;
        this.onBrandClickListener = onBrandClickListener;
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_brand, parent, false);
        return new BrandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        Brand brand = brandList.get(position);
        holder.textBrandName.setText(brand.getName());
        holder.itemView.setSelected(selectedPosition == position);
        holder.itemView.setOnClickListener(v -> {
            int previous = selectedPosition;
            selectedPosition = holder.getBindingAdapterPosition();
            notifyItemChanged(previous);
            notifyItemChanged(selectedPosition);
            onBrandClickListener.onBrandClick(brand);
        });
    }

    @Override
    public int getItemCount() {
        return brandList != null ? brandList.size() : 0;
    }

    static class BrandViewHolder extends RecyclerView.ViewHolder {
        TextView textBrandName;

        BrandViewHolder(@NonNull View itemView) {
            super(itemView);
            textBrandName = itemView.findViewById(R.id.textBrandName);
        }
    }
}
