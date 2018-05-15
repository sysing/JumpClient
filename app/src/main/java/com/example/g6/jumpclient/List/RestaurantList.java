package com.example.g6.jumpclient.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.g6.jumpclient.Add.AddRestaurant;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;

public class RestaurantList extends ToolBarActivity {

        private RecyclerView mItemList;
        private DatabaseReference mDatabase;
        private FirebaseAuth mAuth;
        private FirebaseAuth.AuthStateListener mAuthListener;
        private static String localeKey, userKey;
        private Integer user_status;
        private Button addRestaurantButton;
        private static final String TAG = "RestaurantList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.restaurant_list);
            mItemList = (RecyclerView) findViewById(R.id.restaurantList);
            mItemList.setHasFixedSize(false);
            mItemList.setLayoutManager(new LinearLayoutManager(this));
            mDatabase = FirebaseDatabase.getInstance().getReference();
            addRestaurantButton = (Button) findViewById(R.id.addRestaurantButton);
            localeKey = getIntent().getExtras().getString("localeKey");

            //Check Login Status
            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                //redirect if not logged in
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        Intent loginIntent = new Intent(RestaurantList.this, MainActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(loginIntent);
                    }
                }
            };
            mAuth.addAuthStateListener(mAuthListener);

            //Check user type
            userKey = mAuth.getCurrentUser().getUid();

            mDatabase.child("users").child(userKey).child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user_status = dataSnapshot.getValue(Integer.class);
                    showRecycler();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG,databaseError.getMessage());
                }
            });
        }

    private void showRecycler(){
        FirebaseRecyclerAdapter<Restaurant, ItemViewHolder> FRBA = new FirebaseRecyclerAdapter <Restaurant, ItemViewHolder>(
                Restaurant.class,
                R.layout.single_restaurant_item,
                ItemViewHolder.class,
                mDatabase.child("restaurants")
        ){
            @Override
            protected void populateViewHolder(ItemViewHolder viewHolder, Restaurant model, final int position){
                final String restaurantKey = getRef(position).getKey();
                if (!(model.getLocaleKey().equals(localeKey)) ||
                        (user_status == User.VENDOR && !(model.getVendorKey().equals(userKey))) ||
                        (user_status == User.USER && model.getStatus().intValue() == Restaurant.DELETED.intValue()) ){
                    viewHolder.hideLayout();
                }else{
                    viewHolder.configLayout(user_status,restaurantKey);
                    viewHolder.setImage(getApplicationContext(), model.getImage());
                    viewHolder.setName(model.getName());
                    if (model.getWaitTime() != null){
                        double waitTimeMinutes = (model.getWaitTime()/60000);
                        String waitTimeStr = "Wait Time: " + String.format("%.1f", waitTimeMinutes) + "mins" ;
                        model.setDesc(waitTimeStr);
                    }
                    ArrayList<String> subs = model.getSubscribers();
                    if (subs.contains(userKey)) {
                        viewHolder.setSubscribe(true,restaurantKey);
                    }else{
                        viewHolder.setSubscribe(false,restaurantKey);
                    }
                    viewHolder.setDesc(model.getDesc());
                    viewHolder.setRating((float)model.getWilsonRating());
                    viewHolder.mView.setOnClickListener(new OnClickListener() { //Redirect to menu
                        @Override
                        public void onClick(View v) {
                            if (user_status == User.USER) {
                                Intent intent = new Intent(RestaurantList.this, UserItemList.class);
                                intent.putExtra("restaurantKey",restaurantKey);
                                startActivity(intent);
                            }
                            if (user_status == User.VENDOR || user_status == User.ADMIN ) {
                                Intent intent = new Intent(RestaurantList.this, VendorItemList.class);
                                intent.putExtra("restaurantKey",restaurantKey);
                                startActivity(intent);
                            }

                        }
                    });
                }
            }
        };
        mItemList.setAdapter(FRBA);
        if (user_status == User.VENDOR){
            addRestaurantButton.setVisibility(VISIBLE);
        }
    }

    protected static class ItemViewHolder extends RecyclerView.ViewHolder{
            View mView;
            String id;
            Button  editButton = itemView.findViewById(R.id.editButton);
            Button  deleteButton = itemView.findViewById(R.id.deleteButton);
            Button  subscribeButton = itemView.findViewById(R.id.subscribeButton);
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("restaurants");

            public ItemViewHolder(View itemView){
                super(itemView);
                mView = itemView;
                editButton.setVisibility(GONE);
                deleteButton.setVisibility(GONE);
                subscribeButton.setVisibility(GONE);
            }

        public void configLayout(Integer user_status, String restaurantKey) {
            if (user_status == User.VENDOR ) {
                editButton.setVisibility(VISIBLE);
                deleteButton.setVisibility(VISIBLE);
                editButton.setTag(restaurantKey);
                deleteButton.setTag(restaurantKey);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String restaurantKey = v.getTag().toString();
                        mRef.child(restaurantKey).child("status").setValue(Restaurant.DELETED);
                    }
                });
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String restaurantKey = v.getTag().toString();
                        Intent intent = new Intent(v.getContext(), AddRestaurant.class);
                        intent.putExtra("type", AddRestaurant.UPDATE);
                        intent.putExtra("localeKey", localeKey);
                        intent.putExtra("restaurantKey", restaurantKey);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        v.getContext().startActivity(intent);
                    }
                });
            }
            if (user_status == User.USER ) {
                subscribeButton.setVisibility(VISIBLE);
            }
        }

        public void setSubscribe(Boolean isSubscribed, String restaurantKey){
            final DatabaseReference resRef = mRef.child(restaurantKey).child("subscribers");
            if (isSubscribed){
                subscribeButton.setBackgroundResource(R.mipmap.notification_on_icon);
                subscribeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subscribeButton.setBackgroundResource(R.mipmap.notification_off_icon);
                        resRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                                ArrayList<String> subs = mutableData.getValue(t);
                                if (subs == null) {
                                    return Transaction.success(mutableData);
                                }
                                subs.remove(userKey);
                                mutableData.setValue(subs);
                                return Transaction.success(mutableData);
                            }
                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b,
                                                   DataSnapshot dataSnapshot) {
                            }
                        });
                    }
                });
            }else {
                subscribeButton.setBackgroundResource(R.mipmap.notification_off_icon);
                subscribeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subscribeButton.setBackgroundResource(R.mipmap.notification_on_icon);
                        resRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                                ArrayList<String> subs = mutableData.getValue(t);
                                if (subs == null) {
                                    return Transaction.success(mutableData);
                                }
                                subs.add(userKey);
                                mutableData.setValue(subs);
                                return Transaction.success(mutableData);
                            }
                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b,
                                                   DataSnapshot dataSnapshot) {
                            }
                        });
                    }
                });
            }
        }
        public void setImage(Context ctx , String image){
            ImageView itemImage = mView.findViewById(R.id.restaurantImage);
            Picasso.with(ctx).load(image).into(itemImage);
        }
        public void setName(String name){
            TextView itemName = mView.findViewById(R.id.restaurantName);
            itemName.setText(name);
        }
        public void setDesc(String desc){
            TextView itemName = mView.findViewById(R.id.restaurantDesc);
            itemName.setText(desc);
        }
        public void setRating(float rating){
            RatingBar ratingBar = mView.findViewById(R.id.ratingDisplay);
            TextView ratingText = mView.findViewById(R.id.ratingText);
            ratingBar.setRating(rating);
            ratingText.setText("Rating:" + String.format("%.2f", rating));
        }
        public void setId(String id){
            this.id = id;
        }

        public void hideLayout(){
            mView.setVisibility(GONE);
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
            param.height = 0;
            mView.setLayoutParams(param);
        }
    }

    public void addRestaurantClicked (View view) {
        Intent intent = new Intent(RestaurantList.this, AddRestaurant.class);
        intent.putExtra("type",AddRestaurant.ADD);
        intent.putExtra("localeKey",localeKey);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
         startActivity(intent);
    }

}

