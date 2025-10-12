package com.example.car_service.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;
import java.util.Locale;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> implements Filterable {

    private Context context;
    private List<Service> serviceList;
    private List<Service> serviceListFull;

    public ServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
        this.serviceListFull = new ArrayList<>(serviceList);
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service, parent, false);
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

    @Override
    public Filter getFilter() {
        return serviceFilter;
    }

    private Filter serviceFilter = new Filter() {
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
        protected void publishResults(CharSequence constraint, FilterResults results) {
            serviceList.clear();
            serviceList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    // Cần hàm này để cập nhật lại danh sách đầy đủ khi có dữ liệu mới
    public void updateList(List<Service> newList) {
        this.serviceList = newList;
        this.serviceListFull = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewService;
        TextView textViewServiceName, textViewServicePrice;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewService = itemView.findViewById(R.id.imageViewService);
            textViewServiceName = itemView.findViewById(R.id.textViewServiceName);
            textViewServicePrice = itemView.findViewById(R.id.textViewServicePrice);
        }

        public void bind(Service service) {
            textViewServiceName.setText(service.getServiceName());

            // Format giá tiền
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            textViewServicePrice.setText(currencyFormat.format(service.getPrice()));

            // Dùng Glide để tải ảnh
            Glide.with(context)
                    .load(service.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background) // Ảnh tạm trong khi chờ tải
                    .error(R.drawable.ic_launcher_foreground) // Ảnh khi lỗi
                    .into(imageViewService);
        }
    }
}