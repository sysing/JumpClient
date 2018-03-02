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

    private ImageButton foodImage;
    private static final int GALLREQ = 1;
    private EditText name, desc, price;
    private Uri uri = null;
    private StorageReference storageReference = null;
    private DatabaseReference mRef;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        name = (EditText) findViewById(R.id.itemName);
        desc = (EditText) findViewById(R.id.itemDesc);
        price = (EditText) findViewById(R.id.itemPrice);
        storageReference = FirebaseStorage.getInstance().getReference();
        mRef = FirebaseDatabase.getInstance().getReference("items");
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
            foodImage = (ImageButton) findViewById(R.id.foodImageButton);
            foodImage.setImageURI(uri);
        }
    }

    public void addItemButtonClicked(View view){
        final String name_text= name.getText().toString().trim();
        final String desc_text= name.getText().toString().trim();
        final String price_text= name.getText().toString().trim();
        if (!TextUtils.isEmpty(name_text)&& !TextUtils.isEmpty(desc_text)&& !TextUtils.isEmpty(price_text) ){
            StorageReference filepath = storageReference.child(uri.getLastPathSegment()) ;

            filepath.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Toast.makeText(AddItem.this,"Image uploaded",Toast.LENGTH_SHORT).show();
                            final DatabaseReference newPost = mRef.push();
                            newPost.child("name").setValue(name_text);
                            newPost.child("desc").setValue(desc_text);
                            newPost.child("price").setValue(price_text);
                            newPost.child("image").setValue(downloadUrl.toString());
                        }
                    });
        }else {
            Toast.makeText(AddItem.this, "Please enter name,description and price", Toast.LENGTH_SHORT).show();
        }
    }

}
