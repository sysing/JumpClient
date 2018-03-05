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
        import com.example.g6.jumpclient.Class.Restaurant;
        import com.firebase.ui.database.FirebaseRecyclerAdapter;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.squareup.picasso.Picasso;

public class RestaurantListActivity extends AppCompatActivity {

    private RecyclerView mItemList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        mItemList = (RecyclerView) findViewById(R.id.restaurantList);
        mItemList.setHasFixedSize(true);
        mItemList.setLayoutManager(new LinearLayoutManager(this));
        mDatabase = FirebaseDatabase.getInstance().getReference().child("restaurants");
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            //redirect if not logged in
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    return;/**
                    Intent loginIntent = new Intent(RestaurantListActivity.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);**/
                }
            }
        };

    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<Restaurant, ItemViewHolder> FRBA = new FirebaseRecyclerAdapter <Restaurant, ItemViewHolder>(
                Restaurant.class,
                R.layout.single_restaurant_item,
                ItemViewHolder.class,
                mDatabase
        ){
            @Override
            protected void populateViewHolder(ItemViewHolder viewHolder, Restaurant model, final int position){
                Integer deleteStatus = model.getDelete();
                if (deleteStatus == null || deleteStatus == 1){
                    viewHolder.hideLayout();
                }
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setName(model.getName());
                viewHolder.setDesc(model.getDesc());
                String RestaurantKey = getRef(position).getKey();
                viewHolder.setButtonTag(RestaurantKey);
            }
        };
        mItemList.setAdapter(FRBA);
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        View mView;
        String id;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("restaurants");

        public ItemViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            Button  editButton = itemView.findViewById(R.id.editButton);
            Button  deleteButton = itemView.findViewById(R.id.deleteButton);

            deleteButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View v){
                    String restaurantId = v.getTag().toString();
                    mDatabase.child(restaurantId).child("delete").setValue(1);
                }
            });
            editButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View v){
                    String restaurantId = v.getTag().toString();
                    Intent intent = new Intent(v.getContext(), AddRestaurant.class);
                    intent.putExtra ("restaurantId",restaurantId);
                    v.getContext().startActivity(intent);
                }
            });


        }

        public void setButtonTag(String tag){
            Button  editButton = itemView.findViewById(R.id.editButton);
            Button  deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton.setTag(tag);
            deleteButton.setTag(tag);

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
            mView.setVisibility(View.GONE);
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
            param.height = 0;
            mView.setLayoutParams(param);
        }
        public void deleteItem(String id){
            mDatabase.child(id).child("delete").setValue(1);
        }
    }

    public void addRestaurantClicked (View view) {
        Intent intent = new Intent(RestaurantListActivity.this, AddRestaurant.class);
        startActivity(intent);
    }


}

