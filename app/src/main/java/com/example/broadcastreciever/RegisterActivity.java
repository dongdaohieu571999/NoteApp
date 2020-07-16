package com.example.broadcastreciever;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private int i = 0;
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextMobile;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextMobile = findViewById(R.id.editTextMobile);
        editTextPassword = findViewById(R.id.editTextPassword);
    }
    public void moveBackLogin(View view){
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    public void registerAcc(View view){
        String name = editTextName.getText().toString();
        String email = editTextEmail.getText().toString();
        String mobile = editTextMobile.getText().toString();
        String password = editTextPassword.getText().toString();

        User user = new User(name, email, mobile, password);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("accounts").add(user);



        Intent moveToLoginActivity = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(moveToLoginActivity);
        finish();
    }
}
