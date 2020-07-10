package com.example.broadcastreciever;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


    }
    public void moveToRegister(View view){
        Intent moveToRegisterActivity = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(moveToRegisterActivity);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    public void moveToMain(View view){
        // write code here
        Intent moveToMainActivity = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(moveToMainActivity);
        finish();
    }
}
