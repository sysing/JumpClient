package com.example.g6.jumpclient;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.g6.jumpclient.Class.Item;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView mItemList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mItemList = (RecyclerView) findViewById(R.id.itemList);
        mItemList.setHasFixedSize(true);
        mItemList.setLayoutManager(new LinearLayoutManager(this));
        mDatabase = FirebaseDatabase.getInstance().getReference().child("items");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            //redirect if not logged in
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MenuActivity.this, MainActivity.class);
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
        FirebaseRecyclerAdapter<Item, ItemViewHolder> FRBA = new FirebaseRecyclerAdapter <Item, ItemViewHolder>(
                Item.class,
                R.layout.single_menu_item,
                ItemViewHolder.class,
                mDatabase
        ){
            @Override
            protected void populateViewHolder(ItemViewHolder viewHolder, Item model, final int position){
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setName(model.getName());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setPrice(model.getPrice());

            }
        };
        mItemList.setAdapter(FRBA);
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ItemViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            Button itemPlus = itemView.findViewById(R.id.itemPlus);
            Button itemMinus = itemView.findViewById(R.id.itemMinus);

            itemPlus.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View v){
                    TextView quantity = mView.findViewById(R.id.itemQuantity);
                    int quantityInt = Integer.valueOf(quantity.getText().toString());
                    if (quantityInt == 99){
                        return;
                    }
                    setQuantity(""+(quantityInt + 1));
                }
            });

            itemMinus.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View v){
                    TextView quantity = mView.findViewById(R.id.itemQuantity);
                    int quantityInt = Integer.valueOf(quantity.getText().toString());
                    if (quantityInt == 0){
                        return;
                    }
                    setQuantity(""+(quantityInt - 1));
                }
            });
        }

        public void setQuantity(String quantity){
            TextView itemName = mView.findViewById(R.id.itemQuantity);
            itemName.setText(quantity);
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
        public void setPrice(String price){
            TextView itemPrice =  mView.findViewById(R.id.itemPrice);
            itemPrice.setText(price);
        }

    }

    public void addItemRedirect (View view) {
        Intent addItemIntent = new Intent(MenuActivity.this, AddItem.class);
        startActivity(addItemIntent);
    }



}
