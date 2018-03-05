package com.example.g6.jumpclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.content.Intent;

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
    private static final Integer TYPE_USER = 1, TYPE_VENDOR = 2;

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
        final String password_text = password.getText().toString().trim();
        final String password_confirm = confirmPassword.getText().toString().trim();

        Integer radioButtonId = ((RadioGroup)findViewById( R.id.accountTypeRadio )).getCheckedRadioButtonId();
        final Integer account_type = getAccountType( radioButtonId );

        if (TextUtils.isEmpty(email_text) || TextUtils.isEmpty(password_text) || TextUtils.isEmpty(password_confirm)) {
            Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }else if(!password_text.equals(password_confirm)){
            Toast.makeText(MainActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }else{
            mAuth.createUserWithEmailAndPassword(email_text, password_text)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if ( task.isSuccessful()) {
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user = mDatabase.child(user_id);

                                current_user.child("name").setValue(email_text);
                                current_user.child("type").setValue(account_type);
                                current_user.child("delete").setValue(0);
                                current_user.child("created").setValue(System.currentTimeMillis());
                                current_user.child("updated").setValue(System.currentTimeMillis());

                                Toast.makeText(MainActivity.this, "Account Created",Toast.LENGTH_SHORT).show();
                                Intent login = new Intent(MainActivity.this,LoginActivity.class);
                                startActivity(login);
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Authentication Failed",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private int getAccountType( int id ) {
        Integer res = 0;
        switch( id ) {
            case R.id.radio_user:
                res = TYPE_USER;
                break;
            case R.id.radio_vendor:
                res = TYPE_VENDOR;
                break;
        }
        return res;
    }

    public void loginRedirect (View view) {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }
/** test buttons **/
    public void addItemRedirect (View view) {
        Intent addItemIntent = new Intent(MainActivity.this, AddItem.class);
        startActivity(addItemIntent);
    }
    public void menuRedirect (View view) {
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(intent);
    }
    public void test (View view) {
        Intent intent = new Intent(MainActivity.this, RestaurantListActivity.class);
        startActivity(intent);
    }

}