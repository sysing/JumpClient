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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;

public class RestaurantList extends ToolBarActivity {

        private RecyclerView mItemList;
        private DatabaseReference mDatabase;
        private FirebaseAuth mAuth;
        private FirebaseAuth.AuthStateListener mAuthListener;
        private String locationKey, user_id;
        private Integer user_status;
        private Button addRestaurantButton;
        private static final String TAG = "RestaurantList";



    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_restaurant_list);
            mItemList = (RecyclerView) findViewById(R.id.restaurantList);
            mItemList.setHasFixedSize(false);
            mItemList.setLayoutManager(new LinearLayoutManager(this));
            mDatabase = FirebaseDatabase.getInstance().getReference();
            addRestaurantButton = (Button) findViewById(R.id.addRestaurantButton);
            locationKey = getIntent().getExtras().getString("locationKey");

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
            user_id = mAuth.getCurrentUser().getUid();

            mDatabase.child("users").child(user_id).child("status").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user_status = dataSnapshot.getValue(Integer.class);
                    FirebaseRecyclerAdapter<Restaurant, ItemViewHolder> FRBA = new FirebaseRecyclerAdapter <Restaurant, ItemViewHolder>(
                            Restaurant.class,
                            R.layout.single_restaurant_item,
                            ItemViewHolder.class,
                            mDatabase.child("restaurants")
                    ){
                        @Override
                        protected void populateViewHolder(ItemViewHolder viewHolder, Restaurant model, final int position){
                            final String restaurantKey = getRef(position).getKey();
                            if (!(model.getLocationKey().equals(locationKey)) ||
                                    (user_status == User.VENDOR && !(model.getVendorKey().equals(user_id))) ||
                                    model.getStatus() == Restaurant.DELETED ){
                                viewHolder.hideLayout();
                            }else{
                                viewHolder.configLayout(user_status,restaurantKey);
                                viewHolder.setImage(getApplicationContext(), model.getImage());
                                viewHolder.setName(model.getName());
                                viewHolder.setDesc(model.getDesc());
                                viewHolder.mView.setOnClickListener(new OnClickListener() { //Redirect to menu
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(RestaurantList.this, ItemList.class);
                                        intent.putExtra("restaurantKey",restaurantKey);
                                        startActivity(intent);
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
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG,databaseError.getMessage());
                }
            });
        }

        public static class ItemViewHolder extends RecyclerView.ViewHolder{
            View mView;
            String id;
            Button  editButton = itemView.findViewById(R.id.editButton);
            Button  deleteButton = itemView.findViewById(R.id.deleteButton);
            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("restaurants");

            public ItemViewHolder(View itemView){
                super(itemView);
                mView = itemView;
                editButton.setVisibility(GONE);
                deleteButton.setVisibility(GONE);
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
                        mRef.child(restaurantKey).child("delete").setValue(1);
                    }
                });
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String restaurantKey = v.getTag().toString();
                        Intent intent = new Intent(v.getContext(), AddRestaurant.class);
                        intent.putExtra("type", "update");
                        intent.putExtra("restaurantKey", restaurantKey);
                        v.getContext().startActivity(intent);
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
        intent.putExtra("type","add");
        intent.putExtra("locationKey",locationKey);
        startActivity(intent);
    }

}
