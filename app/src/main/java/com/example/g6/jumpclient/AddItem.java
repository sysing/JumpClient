package com.example.g6.jumpclient;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.view.View;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddItem extends AppCompatActivity {

    private ImageButton itemImage;
    private static final int GALLREQ = 1;
    private EditText name, desc, price;
    private Uri uri = null;
    private StorageReference storageReference = null;
    private DatabaseReference mRef;
    private FirebaseDatabase firebaseDatabase;
    private Boolean itemExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        name = (EditText) findViewById(R.id.itemName);
        desc = (EditText) findViewById(R.id.itemDesc);
        price = (EditText) findViewById(R.id.itemPrice);
        storageReference = FirebaseStorage.getInstance().getReference();
        mRef = FirebaseDatabase.getInstance().getReference("items");

        if (getIntent().getExtras() != null){ //check if updating existing restaurant
            String itemId = getIntent().getExtras().getString("itemId");
            mRef = mRef.child(itemId);
            itemExist = true;
        }else{
            mRef = mRef.push();
            itemExist = false;
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
            itemImage = (ImageButton) findViewById(R.id.itemImage);
            itemImage.setImageURI(uri);
        }
    }

    public void addItemButtonClicked(View view){
        final String name_text= name.getText().toString().trim();
        final String desc_text= desc.getText().toString().trim();
        final String price_text= price.getText().toString().trim();
        if (!TextUtils.isEmpty(name_text)&& !TextUtils.isEmpty(desc_text)&& !TextUtils.isEmpty(price_text) && (uri != null) ){
            StorageReference filepath = storageReference.child(uri.getLastPathSegment()) ;

            filepath.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            final DatabaseReference newPost = mRef.push();
                            newPost.child("name").setValue(name_text);
                            newPost.child("desc").setValue(desc_text);
                            newPost.child("price").setValue(price_text);
                            newPost.child("image").setValue(downloadUrl.toString());
                            newPost.child("deleted").setValue("0");
                            newPost.child("updated").setValue(System.currentTimeMillis());
                            if (!itemExist){
                                newPost.child("created").setValue(System.currentTimeMillis());
                            }

                            Toast.makeText(AddItem.this, "Item added", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddItem.this, RestaurantListActivity.class);
                            startActivity(intent);
                        }
                    });
        }else {
            Toast.makeText(AddItem.this, "Please enter name,description and price", Toast.LENGTH_SHORT).show();
        }
    }

}
