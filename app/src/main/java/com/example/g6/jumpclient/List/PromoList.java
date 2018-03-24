package com.example.g6.jumpclient.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.g6.jumpclient.Class.Item;
import com.example.g6.jumpclient.Class.Promotion;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.view.View.GONE;

public class PromoList extends ToolBarActivity {

    private RecyclerView mItemList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static String userKey;
    private static final String TAG = "ViewPromo";
    private Integer filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.promo_list);
        mItemList = (RecyclerView) findViewById(R.id.promoList);
        mItemList.setHasFixedSize(false);
        //recyclerView reverse
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mItemList.setLayoutManager((mLayoutManager));

        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Check Login Status
        mAuth = FirebaseAuth.getInstance();
        userKey = mAuth.getCurrentUser().getUid();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            //redirect if not logged in
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(PromoList.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        userKey = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = mDatabase.child("users").child(userKey);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                final Long viewPromosTime = user.getViewPromosTime();
                showRecycler(viewPromosTime);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        updateViewPromosTime();
    }

    private void showRecycler(final Long viewOrdersTime){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseRecyclerAdapter<Promotion, PromoList.ItemViewHolder> FRBA = new FirebaseRecyclerAdapter<Promotion, PromoList.ItemViewHolder>(
                        Promotion.class,
                        R.layout.single_promo_item,
                        PromoList.ItemViewHolder.class,
                        mDatabase.child("promotions")
                ){
            @Override
            protected void populateViewHolder(final PromoList.ItemViewHolder viewHolder, final Promotion promo, final int position){
                final String orderKey = getRef(position).getKey();
                if ( !promo.getSubscribers().contains(userKey)){
                    viewHolder.hideLayout();
                }else {
                    final Query mRef = FirebaseDatabase.getInstance().getReference().child("items").child(promo.getItemKey());
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String promoKey = getRef(position).getKey();
                            Item model = dataSnapshot.getValue(Item.class);
                            viewHolder.setImage(getApplicationContext(),model.getImage());
                            viewHolder.setName(model.getName());
                            viewHolder.setDesc(model.getDesc());
                            viewHolder.setCal(model.getCal().toString()+" kCal");
                            viewHolder.setPrice("$" + model.getPrice().toString());
                            viewHolder.setUnread((model.getUpdated() > viewOrdersTime));
                            viewHolder.setDelete(promoKey,userKey);
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {
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
        public void setDesc(String desc){
            TextView itemName = mView.findViewById(R.id.itemDesc);
            itemName.setText(desc);
        }
        public void setCal(String cal){
            TextView itemName = mView.findViewById(R.id.itemCalorie);
            itemName.setText(cal);
        }
        public void setPrice(String price){
            TextView itemName = mView.findViewById(R.id.itemPrice);
            itemName.setText(price);
        }
        public void setStatus(String status){
            TextView itemName = mView.findViewById(R.id.itemStatus);
            itemName.setText(status);
        }
        public void setDelete(final String promoKey, final String userKey){
            Button deleteButton = mView.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("promotions").child(promoKey).child("subscribers");
                    ref.runTransaction(new Transaction.Handler() {
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

        }

        public void hideLayout(){
            mView.setVisibility(GONE);
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
            param.height = 0;
            mView.setLayoutParams(param);
        }
        public void setUnread(Boolean unread){
            if (unread){
                TextView itemName = mView.findViewById(R.id.itemName);
                itemName.setBackgroundColor(Color.GREEN);
            }
        }
    }


    public void updateViewPromosTime(){
        userKey = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = mDatabase.child("users").child(userKey);
        userRef.child("viewPromosTime").setValue(System.currentTimeMillis());
    }




}

