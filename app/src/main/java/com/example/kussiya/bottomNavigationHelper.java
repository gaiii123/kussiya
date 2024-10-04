package com.example.kussiya;

import android.content.Context;
import android.content.Intent;

import com.example.Kussiya.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class bottomNavigationHelper {

    public static void setupBottomNavigation(final Context context, BottomNavigationView bottomNavigationView, int selectedItemId) {
        bottomNavigationView.setSelectedItemId(selectedItemId);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                if (selectedItemId != R.id.bottom_home) {
                    context.startActivity(new Intent(context, home.class));
                }
                return true;
            } else if (item.getItemId() == R.id.bottom_add) {
                if (selectedItemId != R.id.bottom_add) {
                    context.startActivity(new Intent(context, add_item.class));
                }
                return true;
            } else if (item.getItemId() == R.id.bottom_notification) {
                if (selectedItemId != R.id.bottom_notification) {
                    context.startActivity(new Intent(context, notification.class));
                }
                return true;
            } else if (item.getItemId() == R.id.bottom_profile) {
                if (selectedItemId != R.id.bottom_profile) {
                    context.startActivity(new Intent(context, account.class));
                }
                return true;
            }
            return false;
        });
    }
}
