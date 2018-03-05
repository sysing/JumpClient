package com.example.g6.jumpclient;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.view.View;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.database.Query;

public class AddRestaurant extends AppCompatActivity {

    private ImageButton restaurantImage;
    private static final int GALLREQ = 1;
    private EditText name, desc;
    private Button addRestaurantButton;
    private Uri uri = null;
    private StorageReference storageReference = null;
    private DatabaseReference mRef;
    private FirebaseDatabase firebaseDatabase;
    private Boolean restaurantExist ;
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
        if (getIntent().getExtras() != null){ //check if updating existing restaurant
            String restaurantId = getIntent().getExtras().getString("restaurantId");
            mRef = mRef.child(restaurantId);
            restaurantExist = true;
            addRestaurantButton.setText("Update Restaurant");
        }else{
            mRef = mRef.push();
            restaurantExist = false;
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
                            mRef.child("delete").setValue(0);
                            mRef.child("updated").setValue(System.currentTimeMillis());
                            if (!restaurantExist) {
                                mRef.child("created").setValue(System.currentTimeMillis());
                            }
                            Toast.makeText(AddRestaurant.this,"Restaurant Added",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddRestaurant.this, RestaurantListActivity.class);
                            startActivity(intent);

                        }
                    });
        }else {
            Toast.makeText(AddRestaurant.this, "Please enter name,description & upload restaurant image!", Toast.LENGTH_SHORT).show();
        }
    }

}
