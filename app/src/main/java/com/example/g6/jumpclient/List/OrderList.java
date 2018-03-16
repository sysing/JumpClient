package com.example.g6.jumpclient.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.g6.jumpclient.Add.ViewUserSettings;
import com.example.g6.jumpclient.Class.Order;
import com.example.g6.jumpclient.Class.OrderItem;
import com.example.g6.jumpclient.Class.Restaurant;
import com.example.g6.jumpclient.Class.User;
import com.example.g6.jumpclient.Class.Util;
import com.example.g6.jumpclient.MainActivity;
import com.example.g6.jumpclient.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.OnClickListener;

public class OrderList extends ToolBarActivity {

    private RecyclerView mItemList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static String userKey;
    private static final String TAG = "ViewOrder";
    private Integer filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.order_list);
        mItemList = (RecyclerView) findViewById(R.id.orderList);
        mItemList.setHasFixedSize(false);
        //recyclerView reverse
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mItemList.setLayoutManager((mLayoutManager));

        mDatabase = FirebaseDatabase.getInstance().getReference();
        filter = getIntent().getIntExtra("filter",Order.DELETED);

        //Check Login Status
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            //redirect if not logged in
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(OrderList.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        //Check user type
        userKey = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = mDatabase.child("users").child(userKey);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                final Long viewOrdersTime = user.getViewOrdersTime();
                if (user.getStatus().equals(User.VENDOR)) {
                    Query query = mDatabase.child("restaurants").orderByChild("vendorKey").equalTo(userKey).limitToFirst(1);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String restaurantKey = "";
                            for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                restaurantKey = childSnapshot.getKey();
                            }
                            showRecycler("restaurantKey", restaurantKey, viewOrdersTime);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });
                }
                if (user.getStatus().equals(User.USER)) {
                    showRecycler("userKey", userKey, viewOrdersTime);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    @Override
    protected void onPause(){
        super.onPause();
        updateViewOrderTime();
    }

    private void showRecycler(final String keyType, final String key , final Long viewOrdersTime){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseRecyclerAdapter<Order, ItemViewHolder> FRBA = new FirebaseRecyclerAdapter<Order, ItemViewHolder>(
                        Order.class,
                        R.layout.single_order_listng,
                        ItemViewHolder.class,
                        mDatabase.child("orders").orderByChild(keyType).equalTo(key)
                ){
                    @Override
                    protected void populateViewHolder(final ItemViewHolder viewHolder, final Order order, final int position){
                        final String orderKey = getRef(position).getKey();
                        if ( order.getStatus().equals(Order.DELETED)||
                                (keyType.equals("restaurantKey") && order.getStatus().equals(Order.DRAFT)) ||
                                (!filter.equals(Order.DELETED) && !order.getStatus().equals(filter) )
                                ){
                            viewHolder.hideLayout();
                        }else{
                            if (keyType.equals("userKey")) {
                                String restaurantKey = order.getRestaurantKey();
                                DatabaseReference RestaurantRef = mDatabase.child("restaurants").child(restaurantKey);
                                RestaurantRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Restaurant res = dataSnapshot.getValue(Restaurant.class);
                                        viewHolder.setImage(getApplicationContext(), res.getImage());
                                        viewHolder.setName(res.getName());

                                    }
                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        Log.w(TAG, "Failed to read value.", error.toException());
                                    }
                                });
                            }else if (keyType.equals("restaurantKey")) {
                                DatabaseReference RestaurantRef = mDatabase.child("users").child(order.getUserKey());
                                RestaurantRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        viewHolder.setImage(getApplicationContext(), user.getImage());
                                        viewHolder.setName(user.getName());
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        Log.w(TAG, "Failed to read value.", error.toException());
                                    }
                                });
                            }
                            //set totalPrice
                            Float tPrice = 0.0f;
                            ArrayList<OrderItem> iList = order.getiList();
                            for (OrderItem item : iList) {
                                tPrice += item.getQuantity() * item.getPrice();
                            }
                            String tPriceString = "$" + tPrice.toString();
                            viewHolder.setPrice(tPriceString);

                            viewHolder.setUpdateTime(Util.getTimeString(order.getUpdated()));
                            if (!order.getStatus().equals(Order.RATED)) {
                                viewHolder.setStatus(Order.getStatusString(order.getStatus()));
                            }else{
                                viewHolder.setStatus("Rated "+ order.getRating().toString() + " stars");
                            }
                            if (order.getUpdated() > viewOrdersTime){
                                viewHolder.setUnread(true);
                            }
                            viewHolder.mView.setOnClickListener(new OnClickListener() { //Redirect to menu
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(OrderList.this, OrderView.class);
                                    intent.putExtra("orderKey",orderKey);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                };
                mItemList.setAdapter(FRBA);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    protected static class ItemViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public ItemViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setImage(Context ctx , String image){
            ImageView itemImage = mView.findViewById(R.id.itemImage);
            Picasso.with(ctx).load(image).into(itemImage);
        }
        public void setName(String name){
            TextView itemName = mView.findViewById(R.id.itemName);
            itemName.setText(name);
        }
        public void setPrice(String price){
            TextView itemName = mView.findViewById(R.id.itemPrice);
            itemName.setText(price);
        }
        public void setStatus(String status){
            TextView itemName = mView.findViewById(R.id.itemStatus);
            itemName.setText(status);
        }
        public void setUpdateTime(String updateTime){
            TextView itemName = mView.findViewById(R.id.itemUpdateTime);
            itemName.setText(updateTime);
        }
        public void hideLayout(){
            mView.setVisibility(GONE);
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
            param.height = 0;
            mView.setLayoutParams(param);
        }
        public void setUnread(Boolean unread){
            if (unread){
                TextView itemName = mView.findViewById(R.id.itemStatus);
                itemName.setBackgroundColor(Color.GREEN);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.order_list_menu, menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
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
                Intent intent = new Intent(getApplicationContext(),ViewUserSettings.class);
                startActivity(intent);
                break;
            case R.id.all:
                intent = new Intent(getApplicationContext(), OrderList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.draft:
                intent = new Intent(getApplicationContext(), OrderList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("filter",Order.DRAFT);
                startActivity(intent);
                break;
            case R.id.pending:
                intent = new Intent(getApplicationContext(), OrderList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("filter",Order.PENDING);
                startActivity(intent);
                break;
            case R.id.accepted:
                intent = new Intent(getApplicationContext(), OrderList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("filter",Order.ACCEPTED);
                startActivity(intent);
                break;
            case R.id.rejected:
                intent = new Intent(getApplicationContext(), OrderList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("filter",Order.REJECTED);
                startActivity(intent);
                break;
            case R.id.ready:
                intent = new Intent(getApplicationContext(), OrderList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("filter",Order.READY);
                startActivity(intent);
                break;
            case R.id.completed:
                intent = new Intent(getApplicationContext(), OrderList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("filter",Order.COMPLETED);
                startActivity(intent);
                break;

            default:
                break;
        }
        return true;
    }

    public void updateViewOrderTime(){
        userKey = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = mDatabase.child("users").child(userKey);
        userRef.child("viewOrdersTime").setValue(System.currentTimeMillis());
    }




}

