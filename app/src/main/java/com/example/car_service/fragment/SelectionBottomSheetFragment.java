package com.example.car_service.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.car_service.R;
import com.example.car_service.adapter.SelectionAdapter;
import com.example.car_service.model.Selectable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class SelectionBottomSheetFragment extends BottomSheetDialogFragment {

    // Interface để gửi dữ liệu đã chọn về cho Activity
    public interface OnItemSelectedListener {
        void onItemSelected(Selectable item);
    }

    private OnItemSelectedListener mListener;
    private ArrayList<Selectable> items;
    private String title;
    private SelectionAdapter adapter;

    // Phương thức "nhà máy" để tạo Fragment và truyền dữ liệu vào an toàn
    public static SelectionBottomSheetFragment newInstance(ArrayList<Selectable> items, String title) {
        SelectionBottomSheetFragment fragment = new SelectionBottomSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable("items", items);
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            items = (ArrayList<Selectable>) getArguments().getSerializable("items");
            title = getArguments().getString("title");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_selection, container, false);

        TextView textViewTitle = view.findViewById(R.id.textViewSheetTitle);
        SearchView searchView = view.findViewById(R.id.searchViewSelection);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewSelection);

        textViewTitle.setText(title);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo adapter và lắng nghe sự kiện item được chọn
        adapter = new SelectionAdapter(items, selectedItem -> {
            mListener.onItemSelected(selectedItem); // Gửi item đã chọn về Activity
            dismiss(); // Đóng BottomSheet
        });
        recyclerView.setAdapter(adapter);

        // Thiết lập logic tìm kiếm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Đảm bảo Activity chứa Fragment này đã implement OnItemSelectedListener
        try {
            mListener = (OnItemSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnItemSelectedListener");
        }
    }
}