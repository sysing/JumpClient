package com.example.g6.jumpclient.Add;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.g6.jumpclient.Class.Restaurant;
import com.example.g6.jumpclient.List.RestaurantList;
import com.example.g6.jumpclient.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddRestaurant extends AppCompatActivity {

    private ImageButton restaurantImage;
    private static final int GALLREQ = 1;
    private EditText name, desc;
    private Button addRestaurantButton;
    private Uri uri = null;
    private StorageReference storageReference = null;
    private DatabaseReference mRef;
    private FirebaseDatabase firebaseDatabase;
    private String restaurantKey, locationKey,keyType;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        name = (EditText) findViewById(R.id.restaurantName);
        desc = (EditText) findViewById(R.id.restaurantDesc);
        restaurantImage = (ImageButton) findViewById(R.id.restaurantImage);
        addRestaurantButton = (Button) findViewById(R.id.addRestaurantButton);
        storageReference = FirebaseStorage.getInstance().getReference();
        mRef = FirebaseDatabase.getInstance().getReference("restaurants");
        mAuth = FirebaseAuth.getInstance();

        keyType = getIntent().getExtras().getString("type");
        if (keyType.equals("update")){ //check if updating existing restaurant
            addRestaurantButton.setText("Update Restaurant");
            restaurantKey = getIntent().getExtras().getString("restaurantKey");
            mRef = mRef.child(restaurantKey);
            mRef.child("locationKey").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    locationKey = dataSnapshot.getValue(String.class);
                }
                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
        }else if (keyType.equals("add")){  //adding new restaurant
            mRef = mRef.push();
            locationKey = getIntent().getExtras().getString("locationKey");
        }

    }



    public void imageButtonClicked(View view){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLREQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLREQ && RESULT_OK == resultCode){
            uri = data.getData();
            restaurantImage.setImageURI(uri);
        }
    }

    public void addRestaurantButtonClicked(View view){
        final String name_text= name.getText().toString().trim();
        final String desc_text= desc.getText().toString().trim();

        if (!TextUtils.isEmpty(name_text)&& !TextUtils.isEmpty(desc_text) && (uri != null) ) {
            StorageReference filepath = storageReference.child(uri.getLastPathSegment()) ;
            filepath.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            mRef.child("name").setValue(name_text);
                            mRef.child("desc").setValue(desc_text);
                            mRef.child("image").setValue(downloadUrl.toString());
                            mRef.child("status").setValue(Restaurant.VALID);
                            mRef.child("updated").setValue(System.currentTimeMillis());
                            if (keyType.equals("add")) {
                                mRef.child("created").setValue(System.currentTimeMillis());
                                mRef.child("locationKey").setValue(locationKey);
                                mRef.child("vendorKey").setValue(mAuth.getCurrentUser().getUid());
                            }
                            Toast.makeText(AddRestaurant.this,"Restaurant Added",Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(AddRestaurant.this, RestaurantList.class);
                            intent.putExtra("locationKey",locationKey);
                            startActivity(intent);

                        }
                    });
        }else {
            Toast.makeText(AddRestaurant.this, "Please enter name,description & upload restaurant image!", Toast.LENGTH_SHORT).show();
        }
    }

}
