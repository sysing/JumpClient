package com.example.g6.jumpclient.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.g6.jumpclient.Class.Item;
import com.example.g6.jumpclient.Class.Order;
import com.example.g6.jumpclient.Class.OrderItem;
import com.example.g6.jumpclient.Class.Restaurant;
import com.example.g6.jumpclient.Class.User;
import com.example.g6.jumpclient.MainActivity;
import com.example.g6.jumpclient.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.ArrayList;


public class OrderView extends ToolBarActivity {

    private RecyclerView mItemList;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String orderKey,restaurantKey;
    private static TextView tPriceView, ratingText;
    private static RatingBar ratingBar;
    private static Integer orderStatus;
    private Button button1, button2, ratingSubmitButton;
    private static ArrayList<OrderItem> iList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_view);
        orderKey = getIntent().getExtras().getString("orderKey");
        mItemList = findViewById(R.id.itemList);
        tPriceView = findViewById(R.id.totalPrice);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        ratingText = findViewById(R.id.ratingText);
        ratingBar = findViewById(R.id.ratingBar);
        ratingSubmitButton = findViewById(R.id.ratingSubmitButton);
        mItemList.setHasFixedSize(false);
        mItemList.setLayoutManager(new LinearLayoutManager(this));
        mRef = FirebaseDatabase.getInstance().getReference();
        //Check Login Status
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            //redirect if not logged in
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(OrderView.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        String userKey = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = mRef.child("users").child(userKey);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Integer userStatus = user.getStatus();
                loadOrder(userStatus);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    private void loadOrder(final Integer userStatus) {
        DatabaseReference query = mRef.child("orders").child(orderKey);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Order order = dataSnapshot.getValue(Order.class);
                restaurantKey = order.getRestaurantKey();
                showRecycler(order);
                updateTotalPrice(order);
                final Integer orderStatus = order.getStatus();
                if (userStatus.equals(User.USER)) {
                    if (orderStatus.equals(Order.DRAFT)) {
                        setButton1(Order.PENDING, "Confirm Order");
                        setButton2(Order.DELETED, "Delete Order");
                    }
                    if (orderStatus.equals(Order.READY)) {
                        setButton1(Order.COMPLETED, "Order Completed");
                    }
                    if (orderStatus.equals(Order.COMPLETED)) {
                        button1.setVisibility(View.GONE);
                        button2.setVisibility(View.GONE);
                        showRatingBar();
                    }
                    if (orderStatus.equals(Order.RATED)) {
                        ratingText.setText("Thank you for rating!");
                        ratingBar.setVisibility(View.GONE);
                        ratingSubmitButton.setVisibility(View.GONE);
                    }
                }
                if (userStatus.equals(User.VENDOR)) {
                    if (orderStatus.equals(Order.PENDING)) {
                        setButton1(Order.ACCEPTED, "Accept Order");
                        setButton2(Order.REJECTED, "Reject Order");
                    }
                    if (orderStatus.equals(Order.ACCEPTED)) {
                        setButton1(Order.READY, "Order Ready");
                    }
                    if (orderStatus.equals(Order.READY)) {
                        setButton1(Order.COMPLETED, "Order Completed");
                    }
                    if (orderStatus.equals(Order.COMPLETED)) {
                        button1.setVisibility(View.GONE);
                        button2.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void showRecycler(Order order) {
        iList = order.getiList();
        String restaurantKey = order.getRestaurantKey();

        FirebaseRecyclerAdapter<Item, ItemViewHolder> mFirebaseAdapter = new FirebaseRecyclerAdapter<Item, ItemViewHolder>(
                Item.class,
                R.layout.single_order_item,
                ItemViewHolder.class,
                mRef.child("items").orderByChild("restaurantKey").equalTo(restaurantKey)
        ) {
            @Override
            protected void populateViewHolder(ItemViewHolder viewHolder, Item model, final int position) {

                Boolean inOrder = false;
                final String itemKey = getRef(position).getKey();
                for (OrderItem item : iList) {
                    if (itemKey.equals(item.getItemKey())) {
                        viewHolder.setImage(getApplicationContext(), model.getImage());
                        viewHolder.setName(model.getName());
                        viewHolder.setDesc(model.getDesc());
                        viewHolder.setPrice(item.getPrice());
                        viewHolder.setQuantity(item.getQuantity());
                        viewHolder.setRowPrice(item.getPrice() * item.getQuantity());
                        inOrder = true;
                    }
                }
                if (!inOrder) {
                    viewHolder.hideLayout();
                }
            }
        };
        mItemList.setAdapter(mFirebaseAdapter);
    }

    public static void updateTotalPrice(Order order) {
        Float tPrice = 0.0f;
        iList = order.getiList();
        for (OrderItem item : iList) {
            tPrice += item.getQuantity() * item.getPrice();
        }
        String tPriceString = "Total Price: $" + tPrice.toString();
        tPriceView.setText(tPriceString);
    }

    public void buttonClicked(View view) {
        AlertDialog.Builder builder;
        final Integer status = (Integer) view.getTag();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Are you sure?")
                //.setMessage("Are you sure?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        updateOrderStatus(status);
                        Intent intent = new Intent(getApplicationContext(), OrderList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finishAfterTransition();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    private void updateOrderStatus(Integer status) {
        DatabaseReference orderRef = mRef.child("orders").child(orderKey);
        orderRef.child("status").setValue(status);
        orderRef.child("updated").setValue(System.currentTimeMillis());
        if (status.equals(Order.PENDING)) {
            orderRef.child("submitted").setValue(System.currentTimeMillis());
        }
        if (status.equals(Order.READY)) {
            orderRef.child("readied").setValue(System.currentTimeMillis());
            updateRestaurantWaitTime(restaurantKey);
        }
    }

    protected static class ItemViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setQuantity(Integer quantity) {
            TextView itemQuantity = mView.findViewById(R.id.itemQuantity);
            itemQuantity.setText(quantity.toString());
        }

        public void setImage(Context ctx, String image) {
            ImageView itemImage = mView.findViewById(R.id.itemImage);
            Picasso.with(ctx).load(image).into(itemImage);
        }

        public void setName(String name) {
            TextView itemName = mView.findViewById(R.id.itemName);
            itemName.setText(name);
        }

        public void setDesc(String desc) {
            TextView itemDesc = mView.findViewById(R.id.itemDesc);
            itemDesc.setText(desc);
        }

        public void setPrice(Float price) {
            TextView itemPrice = mView.findViewById(R.id.itemPrice);
            String displayPrice = "$" + price.toString();
            itemPrice.setText(displayPrice);
        }

        public void setRowPrice(Float price) {
            TextView itemPrice = mView.findViewById(R.id.rowPrice);
            String displayPrice = "$" + price.toString();
            itemPrice.setText(displayPrice);
        }

        public void hideLayout() {
            mView.setVisibility(View.GONE);
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            param.height = 0;
            mView.setLayoutParams(param);
        }
    }

    private void setButton1(Integer orderStatus, String buttonText) {
        button1.setVisibility(View.VISIBLE);
        button1.setTag(orderStatus);
        button1.setText(buttonText);
    }

    private void setButton2(Integer orderStatus, String buttonText) {
        button2.setVisibility(View.VISIBLE);
        button2.setTag(orderStatus);
        button2.setText(buttonText);
    }

    private void showRatingBar() {
        ratingText.setVisibility(View.VISIBLE);
        ratingBar.setVisibility(View.VISIBLE);
        ratingSubmitButton.setVisibility(View.VISIBLE);
    }

    public void submitRating(View view) {
        DatabaseReference orderRef = mRef.child("orders").child(orderKey);
        orderRef.child("rating").setValue(ratingBar.getRating());
        orderRef.child("status").setValue(Order.RATED);
        orderRef.child("updated").setValue(System.currentTimeMillis());
        ratingText.setText("Thank you for rating!");
        ratingBar.setVisibility(View.GONE);
        ratingSubmitButton.setVisibility(View.GONE);
        orderRef = mRef.child("orders").child(orderKey);
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Order order = dataSnapshot.getValue(Order.class);
                updateRestaurantRating(order.getRestaurantKey(), (int)ratingBar.getRating(), (int)ratingBar.getNumStars());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    public void updateRestaurantRating(String restaurantKey, final int rating, final int numStars) {
        DatabaseReference resRef = mRef.child("restaurants").child(restaurantKey);
        resRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Restaurant restaurant = mutableData.getValue(Restaurant.class);
                if (restaurant == null) {
                    return Transaction.success(mutableData);
                }
                restaurant.setUpvotes(restaurant.getUpvotes()+rating);
                restaurant.setDownvotes(restaurant.getDownvotes()+ numStars - rating);
                mutableData.setValue(restaurant);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
            }
        });
    }
    public void updateRestaurantWaitTime(final String rKey) {
        Query query = mRef.child("orders").orderByChild("restaurantKey").equalTo(restaurantKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double totalWeight = 0.0d;
                Double totalWaitTime = 0.0d;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Order order = childSnapshot.getValue(Order.class);
                    if (order.getStatus() >=  Order.READY) {
                        Double waitTime = (double) order.getReadied() - order.getSubmitted();
                        Double timePassed = (double) System.currentTimeMillis() - order.getReadied();
                        //Double weight = Math.exp((timePassed) * (-3600000));
                        double weight = 1;
                        totalWaitTime += waitTime * weight;
                        totalWeight += weight;
                    }
                }
                double estimatedWaitTime = totalWaitTime / totalWeight;
                DatabaseReference restaurantRef = mRef.child("restaurants").child(rKey);
                restaurantRef.child("waitTime").setValue(estimatedWaitTime);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}