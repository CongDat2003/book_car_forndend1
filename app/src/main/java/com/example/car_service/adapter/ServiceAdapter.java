package com.example.car_service.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.car_service.R;
import com.example.car_service.model.Service;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> implements Filterable {

    // --- Interface for callbacks when an item is selected/deselected ---
    public interface OnServiceToggleListener {
        void onToggle(long serviceId, boolean isSelected);
    }

    private final OnServiceToggleListener toggleListener;
    private final Set<Long> selectedIds = new HashSet<>();

    private List<Service> serviceList; // The list currently displayed (can be filtered)
    private final List<Service> serviceListFull; // The original, complete list for filtering

    /**
     * Constructor for the adapter.
     * @param services The initial list of services to display.
     * @param toggleListener The listener to be notified of selection changes.
     */
    public ServiceAdapter(List<Service> services, OnServiceToggleListener toggleListener) {
        this.toggleListener = toggleListener;
        this.serviceList = services != null ? new ArrayList<>(services) : new ArrayList<>();
        this.serviceListFull = services != null ? new ArrayList<>(services) : new ArrayList<>();
    }

    /**
     * Returns the set of currently selected service IDs.
     */
    public Set<Long> getSelectedIds() {
        return selectedIds;
    }

    /**
     * Updates the adapter with a new list of services. This will reset the current list
     * and the filter.
     */
    public void updateList(List<Service> newList) {
        this.serviceList.clear();
        this.serviceListFull.clear();
        if (newList != null) {
            this.serviceList.addAll(newList);
            this.serviceListFull.addAll(newList);
        }
        notifyDataSetChanged(); // Inform the adapter that the entire dataset has changed
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.bind(service);
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    // --- Filterable Interface Implementation ---
    @Override
    public Filter getFilter() {
        return serviceFilter;
    }

    private final Filter serviceFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Service> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(serviceListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Service item : serviceListFull) {
                    if (item.getServiceName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, @NonNull FilterResults results) {
            serviceList.clear();
            serviceList.addAll((List<Service>) results.values);
            notifyDataSetChanged();
        }
    };


    // --- ViewHolder Inner Class ---
    class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView imageIcon;
        TextView textName;
        TextView textPrice;
        Button buttonAdd;

        ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            // Make sure your R.layout.item_service XML file contains these exact IDs
            imageIcon = itemView.findViewById(R.id.imageServiceIcon);
            textName = itemView.findViewById(R.id.textServiceName);
            textPrice = itemView.findViewById(R.id.textServicePrice);
            buttonAdd = itemView.findViewById(R.id.buttonAddService);
        }

        /**
         * Binds a Service object to the ViewHolder's views and sets up interactions.
         */
        void bind(final Service service) {
            textName.setText(service.getServiceName());

            // Format price using locale-aware currency formatter for Vietnamese Dong
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            textPrice.setText(currencyFormat.format(service.getPrice()));

            // Load image using Glide
            Glide.with(itemView.getContext())
                    .load(service.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imageIcon);

            // Update button based on selection state
            final boolean isSelected = selectedIds.contains(service.getId());
            buttonAdd.setText(isSelected ? "—" : "+"); // Using em dash for better visuals

            // Set click listener for the add/remove button
            buttonAdd.setOnClickListener(v -> {
                boolean nowSelected;
                if (selectedIds.contains(service.getId())) {
                    selectedIds.remove(service.getId());
                    nowSelected = false;
                } else {
                    selectedIds.add(service.getId());
                    nowSelected = true;
                }

                // Efficiently update only this item
                notifyItemChanged(getBindingAdapterPosition());

                // Notify the listener (e.g., the Activity/Fragment) about the change
                if (toggleListener != null) {
                    toggleListener.onToggle(service.getId(), nowSelected);
                }
            });
        }
    }
}