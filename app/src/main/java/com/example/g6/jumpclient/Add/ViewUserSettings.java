package com.example.g6.jumpclient.Add;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.g6.jumpclient.Class.User;
import com.example.g6.jumpclient.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ViewUserSettings extends AppCompatActivity {

    private static final int GALLREQ = 1;
    private TextView height, weight, age, targetWeekWeight, bmr, mealIntake, gender;
    private ImageView userImage;
    private Button editSettingsButton;
    private Uri uri = null;
    private StorageReference storageReference = null;
    private DatabaseReference mRef;
    private static String userKey;
    private Integer keyType;
    public static final Integer ADD = 1, UPDATE = 2;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users_settings);
        mAuth = FirebaseAuth.getInstance();
        userImage = findViewById(R.id.userImage);
        userKey = mAuth.getCurrentUser().getUid();
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        age = findViewById(R.id.age);
        gender = findViewById(R.id.gender);
        targetWeekWeight = findViewById(R.id.targetWeekWeight);
        bmr = findViewById(R.id.bmr);
        mealIntake = findViewById(R.id.mealIntake);
        editSettingsButton = (Button) findViewById(R.id.editSettingsButton);
        mRef = FirebaseDatabase.getInstance().getReference("users").child(userKey);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getHeight() != null) {
                    String image = user.getImage();
                    Picasso.with(getApplicationContext()).load(image).into(userImage);
                    String heightStr = "Height: " + String.format("%.1f", user.getHeight()) + " cm";
                    height.setText(heightStr);
                    String weightStr = "Weight: " + String.format("%.1f",user.getWeight()) + " kg";
                    weight.setText(weightStr);
                    String ageStr = "Age: " + String.format("%.0f" ,user.getAge()) + " years old";
                    age.setText(ageStr);
                    String targetWeekWeightStr = "Target Weekly Gain/Loss: " + String.format("%.2f", user.getTargetWeekWeight()) + " kg";
                    targetWeekWeight.setText(targetWeekWeightStr);
                    String bmrStr = "Basal Metabolic Rate: " + String.format("%.1f",user.getBmr()) + " kCal";
                    bmr.setText(bmrStr);
                    String mealIntakeStr = "Recommended Mealy Intake:" + String.format("%.1f",user.getMealIntake()) + " kCal";
                    mealIntake.setText(mealIntakeStr);
                    String genderStr = "Gender: " + (user.getGender() ? "Male" : "Female");
                    gender.setText(genderStr);
                }else{

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void editSettingsClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), AddUserSettings.class);
        startActivity(intent);
        finishAfterTransition();
    }
}
