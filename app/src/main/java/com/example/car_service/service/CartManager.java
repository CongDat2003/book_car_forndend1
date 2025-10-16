package com.example.car_service.service;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class CartManager {
    private static final String PREF = "MyAppCart";

    public static void setQuantity(Context ctx, int productId, int quantity) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        if (quantity <= 0) sp.edit().remove(String.valueOf(productId)).apply();
        else sp.edit().putInt(String.valueOf(productId), quantity).apply();
    }

    public static Map<Integer, Integer> getAll(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        Map<Integer, Integer> map = new HashMap<>();
        for (String k : sp.getAll().keySet()) {
            Object v = sp.getAll().get(k);
            if (v instanceof Integer) map.put(Integer.parseInt(k), (Integer) v);
        }
        return map;
    }

    public static void clear(Context ctx) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().clear().apply();
    }
}



