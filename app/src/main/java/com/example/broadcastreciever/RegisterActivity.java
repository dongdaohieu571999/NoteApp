package com.example.broadcastreciever;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


    }
    public void moveBackLogin(View view){
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    public void registerAcc(View view){
        // write code here
        Intent moveToLoginActivity = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(moveToLoginActivity);
        finish();
    }
}
