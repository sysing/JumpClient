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
import android.widget.Toast;

import com.example.g6.jumpclient.Add.AddItem;
import com.example.g6.jumpclient.Class.Item;
import com.example.g6.jumpclient.Class.Order;
import com.example.g6.jumpclient.Class.OrderItem;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.VISIBLE;

public class ItemList extends ToolBarActivity {

    private RecyclerView mItemList;
    private Button addItemButton,submitOrderButton;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String restaurantKey, userKey;
    private static Map<String,ItemCounter> myItems = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        restaurantKey = getIntent().getExtras().getString("restaurantKey");
        mItemList = (RecyclerView) findViewById(R.id.itemList);
        addItemButton = (Button) findViewById(R.id.addItemButton);
        submitOrderButton= (Button) findViewById(R.id.submitOrderButton);
        mItemList = (RecyclerView) findViewById(R.id.itemList);
        mItemList.setHasFixedSize(false);
        mItemList.setLayoutManager(new LinearLayoutManager(this));
        mRef = FirebaseDatabase.getInstance().getReference();
        //Check Login Status
        mAuth = FirebaseAuth.getInstance();
        userKey = mAuth.getCurrentUser().getUid();
        mRef.child("restaurants").child(restaurantKey).child("vendorKey").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if (value.equals(userKey)){
                    addItemButton.setVisibility(VISIBLE);
                }

            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        mRef.child("users").child(userKey).child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                if (value == User.USER){
                    submitOrderButton.setVisibility(VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            //redirect if not logged in
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(ItemList.this, MainActivity.class);
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
        FirebaseRecyclerAdapter<Item, ItemViewHolder> mFirebaseAdapter  = new  FirebaseRecyclerAdapter <Item, ItemViewHolder>(
                Item.class,
                R.layout.single_menu_item,
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
                    viewHolder.setTag(itemKey);
                }
            }
        };
        mItemList.setAdapter(mFirebaseAdapter);
    }


    public void addItemClicked (View view) {
        Intent addItemIntent = new Intent(ItemList.this, AddItem.class);
        addItemIntent.putExtra("restaurantKey",restaurantKey);
        addItemIntent.putExtra("type","add");
        startActivity(addItemIntent);
    }
    public void updateTotalPrice(View view){
        Float totalPrice = 0.0f;
        ArrayList<OrderItem> iList = new  ArrayList<>();
        for (Map.Entry<String, ItemCounter> entry : myItems.entrySet()){
            totalPrice+= entry.getValue().getQuantity()*entry.getValue().getPrice();
            OrderItem singleItem= new OrderItem(entry.getKey(),entry.getValue().getQuantity(),entry.getValue().getPrice());
            iList.add(singleItem);
        }
    }
    public void submitOrderClicked (View view) {

        DatabaseReference newOrder = mRef.child("orders").push();
        newOrder.child("iList").setValue(iList);
        newOrder.child("status").setValue(Order.PENDING);
        newOrder.child("userKey").setValue(userKey);
        newOrder.child("restaurantKey").setValue(restaurantKey);
        newOrder.child("created").setValue(System.currentTimeMillis());
        newOrder.child("updated").setValue(System.currentTimeMillis());


    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        View mView;
        Integer intQuantity = 0;
        ItemCounter itemCounter = new ItemCounter(0,0.0f);
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
            myItems.put(this.itemKey,itemCounter);
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
        public void hideLayout(){
            mView.setVisibility(View.GONE);
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
            param.height = 0;
            mView.setLayoutParams(param);
        }
    }
    public static class ItemCounter{
        private Integer quantity;
        private Float price;

        public ItemCounter() {
        }

        public ItemCounter(Integer quantity, Float price) {
            this.quantity = quantity;
            this.price = price;
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
