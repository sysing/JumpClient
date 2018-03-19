package com.example.g6.jumpclient.List;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.g6.jumpclient.Class.Item;
import com.example.g6.jumpclient.Class.Locale;
import com.example.g6.jumpclient.Class.Order;
import com.example.g6.jumpclient.Class.OrderItem;
import com.example.g6.jumpclient.Class.Restaurant;
import com.example.g6.jumpclient.Class.User;
import com.example.g6.jumpclient.MainActivity;
import com.example.g6.jumpclient.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class UserItemList extends ToolBarActivity {

    private RecyclerView mItemList;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static String restaurantKey, userKey;
    private Button subscribeButton ;
    private static TextView tPriceView, tCalView;
    private static Map<String,ItemCounter> myItems = new HashMap<>();
    private static double mealIntake,getTargetWeekWeight;
    private static float orderDistance;
    private static Boolean isSubscribed;
    //Location
    private FusedLocationProviderClient mFusedLocationClient;
    private static Location myLocation;
    private static final Integer TAG_CODE_PERMISSION_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_item_list);
        restaurantKey = getIntent().getExtras().getString("restaurantKey");
        mItemList = (RecyclerView) findViewById(R.id.itemList);
        tPriceView = (TextView) findViewById(R.id.totalPrice);
        tCalView = (TextView) findViewById(R.id.totalCal);
        subscribeButton =  findViewById(R.id.subscribeButton);
        isSubscribed = false;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            myLocation = location;
                            if (location != null) {
                                // Logic to handle location object
                            }
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    TAG_CODE_PERMISSION_LOCATION);
        }

        myItems.clear();
        updateTotalPrice();
        updateTotalCal();
        mItemList.setHasFixedSize(false);
        mItemList.setLayoutManager(new LinearLayoutManager(this));
        mRef = FirebaseDatabase.getInstance().getReference();
        //Check Login Status
        mAuth = FirebaseAuth.getInstance();
        userKey = mAuth.getCurrentUser().getUid();
        mRef.child("users").child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getTargetWeekWeight()!= null) {
                    getTargetWeekWeight = user.getTargetWeekWeight();
                    mealIntake = user.getMealIntake();
                }
            }
            public void onCancelled(DatabaseError error) {
            }
        });
        mRef.child("restaurants").child(restaurantKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                ArrayList<String> subs = restaurant.getSubscribers();
                if (subs.contains(userKey)) {
                    isSubscribed = true;
                    subscribeButton.setText("Unsubscribe");
                }
            }
            public void onCancelled(DatabaseError error) {
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            //redirect if not logged in
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(UserItemList.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };
    }

    @Override
    protected void onResume(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            myLocation = location;
                            if (location != null) {
                                // Logic to handle location object
                            }
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    TAG_CODE_PERMISSION_LOCATION);
        }
        super.onResume();
        myItems.clear();
        updateTotalPrice();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<Item, ItemViewHolder> mFirebaseAdapter  = new  FirebaseRecyclerAdapter <Item, ItemViewHolder>(
                Item.class,
                R.layout.single_user_item,
                ItemViewHolder.class,
                mRef.child("items").orderByChild("restaurantKey").equalTo(restaurantKey)
        ){
            @Override
            protected void populateViewHolder(ItemViewHolder viewHolder, Item model, final int position){

                if ( model.getStatus() == null || model.getStatus() ==  Item.DELETED ){
                    viewHolder.hideLayout();
                }else {
                    final String itemKey = getRef(position).getKey();
                    viewHolder.setImage(getApplicationContext(),model.getImage());
                    viewHolder.setName(model.getName());
                    viewHolder.setDesc(model.getDesc());
                    viewHolder.setPrice(model.getPrice());
                    viewHolder.setCal(model.getCal());
                    viewHolder.setTag(itemKey);
                }
            }
        };
        mItemList.setAdapter(mFirebaseAdapter);
    }

    public static Float updateTotalPrice(){
        Float tPrice = 0.0f;
        for (Map.Entry<String, ItemCounter> entry : myItems.entrySet()) {
            tPrice += entry.getValue().getQuantity() * entry.getValue().getPrice();
        }
        String tPriceString = "Total Price: $" + tPrice.toString();
        tPriceView.setText(tPriceString);
        return tPrice;
    }

    public static Float updateTotalCal(){
        Float tCal = 0.0f;
        for (Map.Entry<String, ItemCounter> entry : myItems.entrySet()) {
            tCal += entry.getValue().getQuantity() * entry.getValue().getCalories();
        }
        String tCalString = "Total Calories: " + tCal.toString() +" kCal";
        tCalView.setText(tCalString);
        if (mealIntake > 0) {
            if (tCal < mealIntake == getTargetWeekWeight < 0) {
                tCalView.setTextColor(Color.GREEN);
            } else {
                tCalView.setTextColor(Color.RED);
            }
        }
        return tCal;
    }

    public static void setOrderDistance(final DatabaseReference dbRef) {
        if (myLocation != null) {
            Query resQuery = FirebaseDatabase.getInstance().getReference().child("restaurants").child(restaurantKey).child("localeKey");
            resQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String localeKey = dataSnapshot.getValue(String.class);
                    Query locQuery = FirebaseDatabase.getInstance().getReference().child("locales").child(localeKey);
                    locQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Locale locale = dataSnapshot.getValue(Locale.class);
                            Location targetLoc = new Location("");
                            targetLoc.setLatitude(locale.getLatitude());
                            targetLoc.setLongitude(locale.getLongitude());
                            orderDistance= myLocation.distanceTo(targetLoc);
                            dbRef.setValue(orderDistance);
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });
                }
                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
        }
    }

    public void submitOrderClicked(View view){
        if (myItems.isEmpty()){
            Toast.makeText(this, "You did not order any item!",Toast.LENGTH_SHORT).show();
            return;
        }
        String orderKey = uploadOrder();
        Intent intent = new Intent(UserItemList.this, OrderView.class);
        intent.putExtra("orderKey",orderKey);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    public void subscribeButtonClicked(View view) {
        DatabaseReference resRef = mRef.child("restaurants").child(restaurantKey).child("subscribers");
        if (isSubscribed) {
            Toast.makeText(getApplicationContext(), "Unsubscribed!", Toast.LENGTH_SHORT).show();
            subscribeButton.setText("Subscribe");
            isSubscribed = false;
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
        } else {
            Toast.makeText(getApplicationContext(), "Subscribed!", Toast.LENGTH_SHORT).show();
            subscribeButton.setText("Unsubscribe");
            isSubscribed = true;
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
    }


    private String uploadOrder () {
        ArrayList<OrderItem> iList = new  ArrayList<>();
        for (Map.Entry<String, ItemCounter> entry : myItems.entrySet()){
            OrderItem singleItem= new OrderItem(entry.getKey(),entry.getValue().getQuantity(),entry.getValue().getPrice());
            iList.add(singleItem);
        }
        DatabaseReference newOrder = mRef.child("orders").push();
        newOrder.child("iList").setValue(iList);
        newOrder.child("status").setValue(Order.DRAFT);
        newOrder.child("userKey").setValue(userKey);
        newOrder.child("restaurantKey").setValue(restaurantKey);
        newOrder.child("created").setValue(System.currentTimeMillis());
        newOrder.child("updated").setValue(System.currentTimeMillis());

        //OrderSimilarity Stats
        newOrder.child("totalPrice").setValue(updateTotalPrice());
        newOrder.child("totalCal").setValue(updateTotalCal());
        setOrderDistance(newOrder.child("orderDistance"));
        String orderKey = newOrder.getKey();
        return orderKey;
    }

    protected static class ItemViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Integer intQuantity = 0;
        ItemCounter itemCounter = new ItemCounter(0,0.0f,0.0f);
        String itemKey;
        public ItemViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            Button itemPlus = itemView.findViewById(R.id.itemPlus);
            Button itemMinus = itemView.findViewById(R.id.itemMinus);

            itemPlus.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View v){
                    //manageBlinkEffect();
                    if (intQuantity == 99){
                        return;
                    }
                    intQuantity++;
                    setQuantity(intQuantity);
                }
            });
            itemMinus.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View v){
                    if (intQuantity == 0){
                        return;
                    }
                    intQuantity--;
                    setQuantity(intQuantity);
                }
            });
        }
        public void setTag(String itemKey){
            this.itemKey = itemKey;
        }

        public void setQuantity(Integer quantity){
            this.intQuantity = quantity;
            TextView itemQuantity= mView.findViewById(R.id.itemQuantity);
            itemQuantity.setText(quantity.toString());
            this.itemCounter.setQuantity(quantity);
            if (this.intQuantity== 0 && myItems.get(this.itemKey)!= null){
                myItems.remove(this.itemKey);
            }else {
                myItems.put(this.itemKey, itemCounter);
            }
            updateTotalPrice();
            updateTotalCal();
        }
        public void setImage(Context ctx ,String image){
            ImageView itemImage = mView.findViewById(R.id.itemImage);
            Picasso.with(ctx).load(image).into(itemImage);
        }
        public void setName(String name){
            TextView itemName = mView.findViewById(R.id.itemName);
            itemName.setText(name);
        }
        public void setDesc(String desc){
            TextView itemDesc = mView.findViewById(R.id.itemDesc);
            itemDesc.setText(desc);
        }
        public void setPrice(Float price){
            TextView itemPrice =  mView.findViewById(R.id.itemPrice);
            String displayPrice = "$"+ price.toString();
            itemPrice.setText(displayPrice);
            this.itemCounter.setPrice(price);
        }
        public void setCal(Float cal){
            TextView itemCal =  mView.findViewById(R.id.itemCalorie);
            String displayCal =  cal.toString() + " kCal";
            itemCal.setText(displayCal);
            this.itemCounter.setCalories(cal);
        }
        public void hideLayout(){
            mView.setVisibility(View.GONE);
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
            param.height = 0;
            mView.setLayoutParams(param);
        }
    }

    protected static class ItemCounter{
        private Integer quantity;
        private Float price;
        private Float calories;

        public ItemCounter() {
        }

        public ItemCounter(Integer quantity, Float price, Float calories) {
            this.quantity = quantity;
            this.price = price;
            this.calories = calories;
        }

        public Float getCalories() {
            return calories;
        }

        public void setCalories(Float calories) {
            this.calories = calories;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Float getPrice() {
            return price;
        }

        public void setPrice(Float price) {
            this.price = price;
        }
    }



}
