package com.example.car_service.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.car_service.R;
import com.example.car_service.model.VehicleModel;

public class VehicleModelAdapter extends ListAdapter<VehicleModel, VehicleModelAdapter.ModelViewHolder> {

    public interface OnModelClickListener {
        void onModelClick(VehicleModel model);
    }

    private final OnModelClickListener onModelClickListener;

    public VehicleModelAdapter(@NonNull java.util.List<VehicleModel> initial, OnModelClickListener listener) {
        super(DIFF_CALLBACK);
        this.onModelClickListener = listener;
        submitList(initial);
    }

    private static final DiffUtil.ItemCallback<VehicleModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<VehicleModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull VehicleModel oldItem, @NonNull VehicleModel newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull VehicleModel oldItem, @NonNull VehicleModel newItem) {
            if (oldItem.getId() != newItem.getId()) return false;
            if (oldItem.getName() == null) {
                if (newItem.getName() != null) return false;
            } else if (!oldItem.getName().equals(newItem.getName())) return false;
            if (oldItem.getImageUrl() == null) {
                return newItem.getImageUrl() == null;
            } else {
                return oldItem.getImageUrl().equals(newItem.getImageUrl());
            }
        }
    };

    @NonNull
    @Override
    public ModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehicle_model, parent, false);
        return new ModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModelViewHolder holder, int position) {
        VehicleModel model = getItem(position);
        holder.textModelName.setText(model.getName());
        Glide.with(holder.itemView.getContext())
                .load(model.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imageModel);
        holder.itemView.setOnClickListener(v -> onModelClickListener.onModelClick(model));
    }

    static class ModelViewHolder extends RecyclerView.ViewHolder {
        ImageView imageModel;
        TextView textModelName;

        ModelViewHolder(@NonNull View itemView) {
            super(itemView);
            imageModel = itemView.findViewById(R.id.imageModel);
            textModelName = itemView.findViewById(R.id.textModelName);
        }
    }
}














