package com.example.broadcastreciever;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity  {
    private EditText editTextUser;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUser = findViewById(R.id.editTextUser);
        editTextPassword = findViewById(R.id.editTextPassword);
    }

    public void moveToRegister(View view){
        Intent moveToRegisterActivity = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(moveToRegisterActivity);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    public void moveToMain() {
        Intent moveToMainActivity = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(moveToMainActivity);
        finish();
    }

    public void login(View view) {
        final String username = editTextUser.getText().toString();
        final String password = editTextPassword.getText().toString();

        CollectionReference accountsRef = FirebaseFirestore.getInstance().collection("accounts");
        accountsRef
                .whereEqualTo("name", username)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot userRecord = task.getResult();
                        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name");
                                String pass = document.getString("password");
                                if(name.equals(username) && pass.equals(password)){
                                    moveToMain();
                                    return;
                                }
                            }
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Wrong username or password.");
                            alertDialog.show();
                        } else {
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Connection error.");
                            alertDialog.show();
                        }
                    }
                });
    }
}