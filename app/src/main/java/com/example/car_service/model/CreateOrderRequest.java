package com.example.car_service.model;

import java.util.List;

public class CreateOrderRequest {
    private long addressId; // tạm thời có thể truyền 0, backend sẽ dùng địa chỉ mặc định sau
    private List<Item> items;

    public CreateOrderRequest(long addressId, List<Item> items) {
        this.addressId = addressId;
        this.items = items;
    }

    public static class Item {
        private int productId;
        private int quantity;
        public Item(int productId, int quantity) { this.productId = productId; this.quantity = quantity; }
    }
}



