package com.example.car_service.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.car_service.R;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {
    private int[] storeImages;

    public StoreAdapter(int[] storeImages) {
        this.storeImages = storeImages;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        holder.imageViewStore.setImageResource(storeImages[position]);
    }

    @Override
    public int getItemCount() {
        return storeImages.length;
    }

    static class StoreViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewStore;
        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewStore = itemView.findViewById(R.id.imageViewStore);
        }
    }
}