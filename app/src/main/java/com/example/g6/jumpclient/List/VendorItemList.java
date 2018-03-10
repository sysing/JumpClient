package com.example.g6.jumpclient.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.g6.jumpclient.Add.AddItem;
import com.example.g6.jumpclient.Class.Item;
import com.example.g6.jumpclient.MainActivity;
import com.example.g6.jumpclient.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class VendorItemList extends ToolBarActivity {

    private RecyclerView mItemList;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static String restaurantKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_item_list);
        restaurantKey = getIntent().getExtras().getString("restaurantKey");
        mItemList = (RecyclerView) findViewById(R.id.itemList);
        mItemList = (RecyclerView) findViewById(R.id.itemList);
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
                    Intent loginIntent = new Intent(VendorItemList.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<Item, ItemViewHolder> mFirebaseAdapter  = new  FirebaseRecyclerAdapter <Item, ItemViewHolder>(
                Item.class,
                R.layout.single_vendor_item,
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
                    viewHolder.configLayout(itemKey);
                }
            }
        };
        mItemList.setAdapter(mFirebaseAdapter);
    }



    public void addItemClicked (View view) {
        Intent intent = new Intent(VendorItemList.this, AddItem.class);
        intent.putExtra("restaurantKey",restaurantKey);
        intent.putExtra("type",AddItem.ADD);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    protected static class ItemViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Button  editButton = itemView.findViewById(R.id.editButton);
        Button  deleteButton = itemView.findViewById(R.id.deleteButton);
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("items");
        public ItemViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void configLayout( String itemKey) {
            editButton.setTag(itemKey);
            deleteButton.setTag(itemKey);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String restaurantKey = v.getTag().toString();
                    mRef.child(restaurantKey).child("status").setValue(Item.DELETED);
                }
            });
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String itemKey = v.getTag().toString();
                    Intent intent = new Intent(v.getContext(), AddItem.class);
                    intent.putExtra("type", AddItem.UPDATE);
                    intent.putExtra("itemKey", itemKey);
                    intent.putExtra("restaurantKey",restaurantKey);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);
                }
            });
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
        }
        public void hideLayout(){
            mView.setVisibility(View.GONE);
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
            param.height = 0;
            mView.setLayoutParams(param);
        }
    }
}
