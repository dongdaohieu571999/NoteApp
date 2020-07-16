package com.example.broadcastreciever;

import android.inputmethodservice.ExtractEditText;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WriteNote extends AppCompatActivity {
    EditText writeText;
    TextView countText;
    TextWatcher textWatcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_note);
        writeText = findViewById(R.id.writeNote);
        countText = findViewById(R.id.countCharacter);

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                countText.setText(" | "+s.length()+" ký tự ");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        writeText.addTextChangedListener(textWatcher);

    }

    public void backToMain(View view){
        finish();
    }

    public void saveAndBack(View view){

        finish();
    }


}
