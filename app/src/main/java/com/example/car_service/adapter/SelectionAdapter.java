package com.example.car_service.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.car_service.R;
import com.example.car_service.model.Selectable;

import java.util.ArrayList;
import java.util.List;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.SelectionViewHolder> implements Filterable {

    // Interface để gửi sự kiện click item về Activity/Fragment
    public interface OnItemSelectedListener {
        void onItemSelected(Selectable item);
    }

    private List<Selectable> itemList;
    private List<Selectable> itemListFull; // Danh sách đầy đủ để tìm kiếm
    private OnItemSelectedListener listener;

    public SelectionAdapter(List<Selectable> itemList, OnItemSelectedListener listener) {
        this.itemList = itemList;
        this.itemListFull = new ArrayList<>(itemList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public SelectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selection, parent, false);
        return new SelectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectionViewHolder holder, int position) {
        Selectable item = itemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    private Filter itemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Selectable> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(itemListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Selectable item : itemListFull) {
                    if (item.getDisplayName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemList.clear();
            itemList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    // ViewHolder cho một item
    class SelectionViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public SelectionViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewSelection);
        }

        public void bind(final Selectable item) {
            textView.setText(item.getDisplayName());
            itemView.setOnClickListener(v -> listener.onItemSelected(item));
        }
    }
}