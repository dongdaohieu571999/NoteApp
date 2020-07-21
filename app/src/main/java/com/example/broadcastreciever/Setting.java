package com.example.broadcastreciever;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor;

public class Setting extends AppCompatActivity {
    Switch login_finger,read_finger;
    Button log_out;
    AlertDialog.Builder builder;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        sharedPreferences = Setting.this.getSharedPreferences("settingdata", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        builder= new AlertDialog.Builder(this);
        map();
        login_finger.setChecked(sharedPreferences.getString("fingerLogin","").equals("yes") ? true : false);
        read_finger.setChecked(sharedPreferences.getString("fingerRead","").equals("yes") ? true : false);

        fingerAccess(login_finger,"fingerLogin");

        fingerAccess(read_finger,"fingerRead");

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("Alert");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.clear();
                        editor.apply();
                        Intent intent = new Intent(Setting.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("NO",null);
                builder.setMessage("Are you sure want to log out?");
                builder.setIcon(R.drawable.icon);
                builder.show();
            }
        });


    }

    public void map(){
        login_finger = findViewById(R.id.switch_login_finger);
        read_finger = findViewById(R.id.switch_read_finger);
        log_out = findViewById(R.id.log_out);
    }

    public void fingerAccess(final Switch action_finger, final String key){
        final AlertDialog alertDialog = new AlertDialog.Builder(Setting.this).create();
        action_finger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    BiometricManager biometricManager = BiometricManager.from(Setting.this);
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
                    Executor executor = ContextCompat.getMainExecutor(Setting.this);
                    final BiometricPrompt biometricPrompt = new BiometricPrompt(Setting.this, executor, new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                            action_finger.setChecked(false);
                            editor.putString(key,"no");
                            editor.apply();
                        }

                        @Override
                        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                            editor.putString(key,"yes");
                            editor.apply();
                        }

                        @Override
                        public void onAuthenticationFailed() {
                        }
                    });
                    final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                            .setTitle("ALERT")
                            .setDescription("place your finger print to scanner")
                            .setNegativeButtonText("NO THANK")
                            .build();
                    biometricPrompt.authenticate(promptInfo);

                } else {
                    editor.putString(key,"no");
                    editor.apply();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
