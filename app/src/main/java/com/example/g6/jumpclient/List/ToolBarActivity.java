package com.example.g6.jumpclient.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.g6.jumpclient.Add.AddRestaurant;
import com.example.g6.jumpclient.Class.BadgeDrawable;
import com.example.g6.jumpclient.Class.Order;
import com.example.g6.jumpclient.Class.Restaurant;
import com.example.g6.jumpclient.Class.User;
import com.example.g6.jumpclient.Class.Util;
import com.example.g6.jumpclient.R;
import com.github.juanlabrador.badgecounter.BadgeCounter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ToolBarActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        final String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        Query query =  FirebaseDatabase.getInstance().getReference().child("users").child(userKey);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                if (user.getStatus() == User.USER) {
                    Query query = FirebaseDatabase.getInstance().getReference().child("orders").orderByChild("userKey").equalTo(userKey);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Integer count = 0;
                            for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                Order order = childSnapshot.getValue(Order.class);
                                if (order.getStatus() != (Order.DRAFT) && order.getUpdated() > user.getViewOrdersTime()){
                                    count++;
                                }
                            }
                            updateOrderNotification(menu,count);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
                if (user.getStatus() == User.VENDOR) {
                    Query query = FirebaseDatabase.getInstance().getReference().child("restaurants").orderByChild("vendorKey").equalTo(userKey).limitToFirst(1);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String restaurantKey = "";
                            for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                restaurantKey = childSnapshot.getKey();
                            }
                            Query query = FirebaseDatabase.getInstance().getReference().child("orders").orderByChild("restaurantKey").equalTo(restaurantKey);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Integer count = 0;
                                    for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                        Order order = childSnapshot.getValue(Order.class);
                                        if (order.getStatus() != (Order.DRAFT) && order.getUpdated() > user.getViewOrdersTime()){
                                            count++;
                                        }
                                    }
                                    updateOrderNotification(menu,count);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_orders:
                viewOrders();
                break;
            case R.id.action_signOut:
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle("Sign Out")
                        .setMessage("Are you sure you want to sign out?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                mAuth.signOut();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
                break;

            case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;

            default:
                break;
        }
        return true;
    }

    public void viewOrders() {
        Intent intent = new Intent(getApplicationContext(), OrderList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public void updateOrderNotification(Menu menu, Integer notificationCount) {
        if (notificationCount > 0) {
            BadgeCounter.update(this,
                    menu.findItem(R.id.action_orders),
                    R.drawable.menu_icon,
                    BadgeCounter.BadgeColor.BLACK,
                    notificationCount);
        }
    }
}
