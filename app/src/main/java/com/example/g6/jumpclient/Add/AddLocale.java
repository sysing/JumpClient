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

import com.bumptech.glide.Glide;
import com.example.g6.jumpclient.Class.Locale;
import com.example.g6.jumpclient.List.LocaleList;
import com.example.g6.jumpclient.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class AddLocale extends AppCompatActivity {

    private ImageButton localeImage;
    private static final int GALLREQ = 1;
    private EditText name, desc, price;
    private Button addLocaleButton;
    private Uri uri = null;
    private StorageReference storageReference = null;
    private DatabaseReference mRef ;
    private String localeKey;
    private Integer keyType;
    public static final Integer ADD = 1,UPDATE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_locale);

        name = (EditText) findViewById(R.id.localeName);
        desc = (EditText) findViewById(R.id.localeDesc);
        addLocaleButton = (Button) findViewById(R.id.addLocaleButton);
        storageReference = FirebaseStorage.getInstance().getReference();
        mRef = FirebaseDatabase.getInstance().getReference("locales");
        localeKey = getIntent().getExtras().getString("localeKey");

        keyType = getIntent().getExtras().getInt("type");
        if (keyType.equals(UPDATE)){ //check if updating existing locale
            addLocaleButton.setText("Update locale");
            localeKey = getIntent().getExtras().getString("localeKey");
            mRef = mRef.child(localeKey);
        }else if (keyType.equals(ADD)){  //adding new locale
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
            localeImage = (ImageButton) findViewById(R.id.localeImage);
            Glide.with(getApplicationContext())
                    .load(uri)
                    .error(R.mipmap.warning_icon)
                    .override(200, 200)
                    .fitCenter()
                    .into(localeImage);
        }
    }

    public void addLocaleButtonClicked(View view){
        final String name_text= name.getText().toString().trim();
        final String desc_text= desc.getText().toString().trim();
        if (TextUtils.isEmpty(name_text) || TextUtils.isEmpty(desc_text)|| (uri == null) ) {
            Toast.makeText(AddLocale.this, "Please upload locale image & enter name,description,price", Toast.LENGTH_SHORT).show();
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
                        mRef.child("image").setValue(downloadUrl.toString());
                        mRef.child("status").setValue(Locale.VALID);
                        mRef.child("latitude").setValue(0.0);
                        mRef.child("longitude").setValue(0.0);
                        mRef.child("updated").setValue(System.currentTimeMillis());
                        if (keyType.equals(ADD)) {
                            mRef.child("created").setValue(System.currentTimeMillis());
                            mRef.child("localeKey").setValue(localeKey);
                        }

                        Toast.makeText(AddLocale.this, "Locale added", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddLocale.this, LocaleList.class);
                        intent.putExtra("localeKey", localeKey);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
    }

}
