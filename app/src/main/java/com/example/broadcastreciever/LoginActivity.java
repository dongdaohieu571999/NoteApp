package com.example.broadcastreciever;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextUser;
    private EditText editTextPassword;
    private ImageView fingerprint;
    private ProcessBar poProcessBar = new ProcessBar(LoginActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUser = findViewById(R.id.editTextUser);
        editTextPassword = findViewById(R.id.editTextPassword);
        fingerprint = findViewById(R.id.fingerprint);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        final SharedPreferences sharedPreferences = getSharedPreferences("settingdata", Context.MODE_PRIVATE);

        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE: {
                alertDialog.setTitle("ERROR");
                alertDialog.setMessage("YOUR DEVICE HAVE NO SCANNER PRINT");
                alertDialog.show();
                break;
            }
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE: {
                alertDialog.setTitle("ERROR");
                alertDialog.setMessage("YOUR SCANNER PRINT UNAVAILABLE");
                alertDialog.show();
                break;
            }
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED: {
                alertDialog.setTitle("ERROR");
                alertDialog.setMessage("YOUR DEVICE HAVE NO FINGER, PLEASE ADD MORE FINGER PRINT");
                alertDialog.show();
                break;
            }
        }

        Executor executor = ContextCompat.getMainExecutor(this);
        final BiometricPrompt biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                moveToMain();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("LOGIN BY FINGER PRINT")
                .setDescription("place your finger print to scanner")
                .setNegativeButtonText("NO THANK")
                .build();

        fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login_finger = sharedPreferences.getString("fingerLogin", "");
                if (login_finger.equals("") || login_finger.equals("no")) {
                    alertDialog.setTitle("ALERT");
                    alertDialog.setMessage("Please Login to change this Setting");
                    alertDialog.setIcon(R.drawable.icon);
                    alertDialog.show();
                    return;
                } else {
                    biometricPrompt.authenticate(promptInfo);
                }

            }
        });
    }

    public void moveToRegister(View view) {
        Intent moveToRegisterActivity = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(moveToRegisterActivity);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void moveToMain() {
        Intent moveToMainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(moveToMainActivity);
        finish();
    }

    public void login(View view) {
        final String username = editTextUser.getText().toString();
        final String password = editTextPassword.getText().toString();
        poProcessBar.start();
        CollectionReference accountsRef = FirebaseFirestore.getInstance().collection("accounts");
        accountsRef
                .whereEqualTo("name", username)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name");
                                String pass = document.getString("password");
                                if (name.equals(username) && pass.equals(password)) {
                                    moveToMain();
                                    poProcessBar.end();
                                    return;
                                }
                            }
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Wrong username or password.");
                            alertDialog.setIcon(R.drawable.icon);
                            alertDialog.show();
                            poProcessBar.end();
                        } else {
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("Connection error.");
                            alertDialog.setIcon(R.drawable.icon);
                            alertDialog.show();
                            poProcessBar.end();
                        }
                    }
                });

    }
}