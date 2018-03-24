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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.g6.jumpclient.Class.Item;
import com.example.g6.jumpclient.List.VendorItemList;
import com.example.g6.jumpclient.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class AddItem extends AppCompatActivity {

    private ImageButton itemImage;
    private static final int GALLREQ = 1;
    private EditText name, desc, price, calorie;
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
        calorie = (EditText) findViewById(R.id.itemCalorie);
        addItemButton = (Button) findViewById(R.id.addItemButton);
        storageReference = FirebaseStorage.getInstance().getReference();
        mRef = FirebaseDatabase.getInstance().getReference("items");
        restaurantKey = getIntent().getExtras().getString("restaurantKey");

        keyType = getIntent().getExtras().getInt("type");
        if (keyType.equals(UPDATE)){ //check if updating existing restaurant
            addItemButton.setText("Update item");
            itemKey = getIntent().getExtras().getString("itemKey");
            mRef = mRef.child(itemKey);
        }else if (keyType.equals(ADD)){  //adding new restaurant
            mRef = mRef.push();
            itemKey = mRef.getKey();
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
            Glide.with(getApplicationContext())
                    .load(uri)
                    .error(R.mipmap.warning_icon)
                    .override(200, 200)
                    .fitCenter()
                    .into(itemImage);
        }
    }

    public void addItemClicked(View view){
        final String name_text= name.getText().toString().trim();
        final String desc_text= desc.getText().toString().trim();
        final String price_text= price.getText().toString().trim();
        final String calorie_text = calorie.getText().toString().trim();
        if (TextUtils.isEmpty(name_text) || TextUtils.isEmpty(price_text) || (uri == null) ) {
            Toast.makeText(AddItem.this, "Please upload item image & enter name,description,price", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            float temp = Float.parseFloat(price_text);
        } catch (NumberFormatException ex) {
            Toast.makeText(AddItem.this, "Please enter float value price", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            float temp = Float.parseFloat(calorie_text);
        } catch (NumberFormatException ex) {
            Toast.makeText(AddItem.this, "Please enter float value calorie", Toast.LENGTH_SHORT).show();
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
                        mRef.child("cal").setValue(Float.parseFloat(calorie_text));
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
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        Integer radioButtonId = ((RadioGroup)findViewById( R.id.notifySubscribers )).getCheckedRadioButtonId();
        final Boolean isNotify = getNotify( radioButtonId);
        if (isNotify){
            final DatabaseReference notiRef = FirebaseDatabase.getInstance().getReference().child("promotions").push();
            DatabaseReference resRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(restaurantKey).child("subscribers");
            resRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                    ArrayList<String>  subs = dataSnapshot.getValue(t);
                    notiRef.child("itemKey").setValue(itemKey);
                    notiRef.child("subscribers").setValue(subs);
                    notiRef.child("created").setValue(System.currentTimeMillis());
                }
                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
        }
    }

    private Boolean getNotify( int id ) {
        Boolean res = true;
        switch( id ) {
            case R.id.radio_yes:
                res =  true;
                break;
            case R.id.radio_no:
                res = false;
                break;
        }
        return res;
    }

}
