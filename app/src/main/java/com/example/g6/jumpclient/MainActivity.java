package com.example.g6.jumpclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import android.support.annotation.NonNull;



public class MainActivity extends AppCompatActivity {

    private EditText email, password, confirmPassword;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText) findViewById(R.id.editEmail);
        password = (EditText) findViewById(R.id.editPassword);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    }

    public void signUpButtonClicked(View view) {
        final String email_text = email.getText().toString().trim();
        String password_text = password.getText().toString().trim();
        String password_confirm = confirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email_text) || TextUtils.isEmpty(password_text) || TextUtils.isEmpty(password_confirm)) {
            Toast.makeText(MainActivity.this, "Please fill in all fields",
                    Toast.LENGTH_SHORT).show();
        }else if(!password_text.equals(password_confirm)){
            Toast.makeText(MainActivity.this, "Passwords do not match",
                    Toast.LENGTH_SHORT).show();
        }else{
            mAuth.createUserWithEmailAndPassword(email_text, password_text)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user = mDatabase.child(user_id);
                            current_user.child("name").setValue(email_text);
                            if ( task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Account Created",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Authentication Failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
}