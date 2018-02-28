package com.example.g6.jumpclient;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;


import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.enterEmail);
        password = (EditText) findViewById(R.id.enterPassword);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("user");
    }

    public void signInButtonClicked(View view){
        String email_text =  email.getText().toString().trim();
        String password_text = password.getText().toString().trim();
        if (TextUtils.isEmpty(email_text) || TextUtils.isEmpty(password_text)){
            Toast.makeText(LoginActivity.this, "Please fill in all fields",
                    Toast.LENGTH_SHORT).show();
        }else{
            mAuth.signInWithEmailAndPassword(email_text,password_text).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                @Override
                public void onComplete(@NonNull Task<AuthResult> task){
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Sign In Successful",
                                Toast.LENGTH_SHORT).show();
                        Intent menuIntent = new Intent(LoginActivity.this, MenuActivity.class);
                        startActivity(menuIntent);
                    }else{
                        Toast.makeText(LoginActivity.this, "Sign In Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void checkUserExist(){
        final String user_id = mAuth.getCurrentUser().getUid();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)){
                    Intent menuIntent = new Intent(LoginActivity.this, MenuActivity.class);
                    startActivity(menuIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
