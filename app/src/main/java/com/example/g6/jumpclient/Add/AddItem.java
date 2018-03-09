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

import com.example.g6.jumpclient.Class.Item;
import com.example.g6.jumpclient.List.ItemList;
import com.example.g6.jumpclient.List.VendorItemList;
import com.example.g6.jumpclient.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class AddItem extends AppCompatActivity {

    private ImageButton itemImage;
    private static final int GALLREQ = 1;
    private EditText name, desc, price;
    private Button addItemButton;
    private Uri uri = null;
    private StorageReference storageReference = null;
    private DatabaseReference mRef ;
    private String restaurantKey,itemKey;
    private Integer keyType;
    public static final Integer ADD = 1,UPDATE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        name = (EditText) findViewById(R.id.itemName);
        desc = (EditText) findViewById(R.id.itemDesc);
        price = (EditText) findViewById(R.id.itemPrice);
        price = (EditText) findViewById(R.id.itemPrice);
        addItemButton = (Button) findViewById(R.id.addItemButton);
        storageReference = FirebaseStorage.getInstance().getReference();
        mRef = FirebaseDatabase.getInstance().getReference("items");

        keyType = getIntent().getExtras().getInt("type");
        if (keyType.equals(UPDATE)){ //check if updating existing restaurant
            addItemButton.setText("Update item");
            itemKey = getIntent().getExtras().getString("itemKey");
            mRef = mRef.child(itemKey);
        }else if (keyType.equals(ADD)){  //adding new restaurant
            restaurantKey = getIntent().getExtras().getString("restaurantKey");
            mRef = mRef.push();
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

    public void addItemClicked(View view){
        final String name_text= name.getText().toString().trim();
        final String desc_text= desc.getText().toString().trim();
        final String price_text= price.getText().toString().trim();
        if (TextUtils.isEmpty(name_text) || TextUtils.isEmpty(desc_text)|| TextUtils.isEmpty(price_text) || (uri == null) ) {
            Toast.makeText(AddItem.this, "Please enter name,description and price", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            float temp = Float.parseFloat(price_text);
        } catch (NumberFormatException ex) {
            Toast.makeText(AddItem.this, "Please enter float value price", Toast.LENGTH_SHORT).show();
            return;
        }
        StorageReference filepath = storageReference.child(uri.getLastPathSegment()) ;
        StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = filepath.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        mRef.child("name").setValue(name_text);
                        mRef.child("desc").setValue(desc_text);
                        mRef.child("price").setValue(Float.parseFloat(price_text));
                        mRef.child("image").setValue(downloadUrl.toString());
                        mRef.child("status").setValue(Item.VALID);
                        mRef.child("updated").setValue(System.currentTimeMillis());
                        if (keyType.equals(ADD)) {
                            mRef.child("created").setValue(System.currentTimeMillis());
                            mRef.child("restaurantKey").setValue(restaurantKey);
                        }

                        Toast.makeText(AddItem.this, "Item added", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddItem.this, VendorItemList.class);
                        intent.putExtra("restaurantKey", restaurantKey);
                        startActivity(intent);
                    }
                });
    }

}
