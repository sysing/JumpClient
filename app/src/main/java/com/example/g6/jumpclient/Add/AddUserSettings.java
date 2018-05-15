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
import com.example.g6.jumpclient.Class.Calorie;
import com.example.g6.jumpclient.R;
import com.example.g6.jumpclient.View.ViewUserSettings;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class AddUserSettings extends AppCompatActivity {

    private static final int GALLREQ = 1;
    private EditText height, weight, age, targetWeekWeight;
    private Button addSettingsButton;
    private Uri uri = null;
    private StorageReference storageReference = null;
    private DatabaseReference mRef ;
    private static String userKey;
    private Integer keyType;
    public static final Integer ADD = 1,UPDATE = 2;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_setting);
        mAuth = FirebaseAuth.getInstance();
        userKey = mAuth.getCurrentUser().getUid();
        height = (EditText) findViewById(R.id.height);
        weight = (EditText) findViewById(R.id.weight);
        age = (EditText) findViewById(R.id.age);
        targetWeekWeight = (EditText) findViewById(R.id.targetWeekWeight);
        addSettingsButton = (Button) findViewById(R.id.addSettingsButton);
        storageReference = FirebaseStorage.getInstance().getReference();
        mRef = FirebaseDatabase.getInstance().getReference("users").child(userKey);

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
            ImageButton userImage = findViewById(R.id.userImage);
            Glide.with(getApplicationContext())
                    .load(uri)
                    .override(200, 200)
                    .fitCenter()
                    .error(R.mipmap.warning_icon)
                    .into(userImage);
        }
    }

    public void addSettingsClicked(View view){
        final String height_text= height.getText().toString().trim();
        final String weight_text= weight.getText().toString().trim();
        final String age_text= age.getText().toString().trim();
        final String targetWeekWeight_text = targetWeekWeight.getText().toString().trim();
        Integer radioButtonId = ((RadioGroup)findViewById( R.id.genderRadio )).getCheckedRadioButtonId();
        final Boolean gender = getGender( radioButtonId );

        if (TextUtils.isEmpty(height_text) || TextUtils.isEmpty(weight_text)|| TextUtils.isEmpty(age_text) ||  TextUtils.isEmpty(age_text) ) {
            Toast.makeText(AddUserSettings.this, "Please upload user image & enter name,description,price", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            float temp = Float.parseFloat(height_text);
        } catch (NumberFormatException ex) {
            Toast.makeText(AddUserSettings.this, "Please enter float value height", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            float temp = Float.parseFloat(weight_text);
        } catch (NumberFormatException ex) {
            Toast.makeText(AddUserSettings.this, "Please enter float value weight", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            float temp = Float.parseFloat(age_text);
        } catch (NumberFormatException ex) {
            Toast.makeText(AddUserSettings.this, "Please enter float value age", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            float temp = Float.parseFloat(targetWeekWeight_text);
        } catch (NumberFormatException ex) {
            Toast.makeText(AddUserSettings.this, "Please enter float value target weight gain/loss", Toast.LENGTH_SHORT).show();
            return;
        }
        if (uri != null ) {
            StorageReference filepath = storageReference.child(uri.getLastPathSegment());
            StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = filepath.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            mRef.child("image").setValue(downloadUrl.toString());
                        }
                    });
        }
        mRef.child("height").setValue(Float.parseFloat(height_text));
        mRef.child("weight").setValue(Float.parseFloat(weight_text));
        mRef.child("age").setValue(Float.parseFloat(age_text));
        mRef.child("targetWeekWeight").setValue(Float.parseFloat(targetWeekWeight_text));
        mRef.child("gender").setValue(gender);
        double BMR = Calorie.getBMR(Float.parseFloat(weight_text),Float.parseFloat(height_text),Float.parseFloat(age_text),gender);
        mRef.child("bmr").setValue(BMR);
        double mealIntake = Calorie.getMealIntake(BMR,Float.parseFloat(targetWeekWeight_text));
        mRef.child("mealIntake").setValue(mealIntake);
        mRef.child("updated").setValue(System.currentTimeMillis());
        Toast.makeText(getApplicationContext(),"Settings Updated! You can now get meal recommendations!", Toast.LENGTH_LONG ).show();
        Intent intent = new Intent(getApplicationContext(), ViewUserSettings.class);
        startActivity(intent);
        finishAfterTransition();

    }

    private Boolean getGender( int id ) {
        Boolean res = true;
        switch( id ) {
            case R.id.radio_male:
                res = Calorie.MALE;
                break;
            case R.id.radio_female:
                res = Calorie.FEMALE;
                break;
        }
        return res;
    }

}
