package com.example.car_service.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.car_service.R;
import com.example.car_service.model.Appointment;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<Appointment> appointmentList;

    public AppointmentAdapter(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.bind(appointment);
    }

    @Override
    public int getItemCount() {
        if (appointmentList == null) {
            return 0;
        }
        return appointmentList.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewServiceName, textViewVehicleInfo, textViewAppointmentDate, textViewStatus;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewServiceName = itemView.findViewById(R.id.textViewServiceName);
            textViewVehicleInfo = itemView.findViewById(R.id.textViewVehicleInfo);
            textViewAppointmentDate = itemView.findViewById(R.id.textViewAppointmentDate);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }

        public void bind(Appointment appointment) {
            if (appointment.getService() != null) {
                textViewServiceName.setText(appointment.getService().getServiceName());
            }
            if (appointment.getVehicle() != null) {
                String vehicleInfo = appointment.getVehicle().getLicensePlate() + " - " + appointment.getVehicle().getBrand();
                textViewVehicleInfo.setText(vehicleInfo);
            }
            textViewAppointmentDate.setText("Ngày hẹn: " + appointment.getAppointmentDate());
            textViewStatus.setText("Trạng thái: " + appointment.getStatus());
        }
    }
}