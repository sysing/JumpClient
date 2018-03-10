package com.example.g6.jumpclient.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.g6.jumpclient.Class.Location;
import com.example.g6.jumpclient.MainActivity;
import com.example.g6.jumpclient.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class LocationList extends ToolBarActivity {

    private RecyclerView mItemList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_list);
        mItemList = (RecyclerView) findViewById(R.id.locationList);
        mItemList.setHasFixedSize(true);
        mItemList.setLayoutManager(new LinearLayoutManager(this));
        mDatabase = FirebaseDatabase.getInstance().getReference().child("locations");
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            //redirect if not logged in
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                     Intent loginIntent = new Intent(LocationList.this, MainActivity.class);
                     loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                     startActivity(loginIntent);
                }
            }
        };

    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<Location, ItemViewHolder> FRBA = new FirebaseRecyclerAdapter <Location, ItemViewHolder>(
                Location.class,
                R.layout.single_location_item,
                ItemViewHolder.class,
                mDatabase
        ){
            @Override
            protected void populateViewHolder(ItemViewHolder viewHolder, Location model, final int position){
                if (model.getStatus() == null || model.getStatus() == Location.DELETED){
                    viewHolder.hideLayout();
                }
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setName(model.getName());
                viewHolder.setDesc(model.getDesc());
                final String LocationKey = getRef(position).getKey();
                viewHolder.mView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Intent intent = new Intent(LocationList.this, RestaurantList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("locationKey",LocationKey);
                        startActivity(intent);
                    }
                });
            }
        };
        mItemList.setAdapter(FRBA);
    }


    protected static class ItemViewHolder extends RecyclerView.ViewHolder{
        View mView;
        String id;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("restaurants");

        public ItemViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }
        public void setImage(Context ctx , String image){
            ImageView itemImage = mView.findViewById(R.id.locationImage);
            Picasso.with(ctx).load(image).into(itemImage);
        }
        public void setName(String name){
            TextView itemName = mView.findViewById(R.id.locationName);
            itemName.setText(name);
        }
        public void setDesc(String desc){
            TextView itemDesc = mView.findViewById(R.id.locationDesc);
            itemDesc.setText(desc);
        }
        public void setDistance(String distance){
            TextView itemDist = mView.findViewById(R.id.locationDistance);
            itemDist.setText(distance);
        }
        public void setId(String id){
            this.id = id;
        }
        public void hideLayout(){
            mView.setVisibility(View.GONE);
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
            param.height = 0;
            mView.setLayoutParams(param);
        }
    }


}

