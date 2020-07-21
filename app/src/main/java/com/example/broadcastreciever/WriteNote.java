package com.example.broadcastreciever;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteNote extends AppCompatActivity {
    EditText writeText;
    TextView countText;
    TextWatcher textWatcher;
    ImageView lock,unlock;
    AlertDialog.Builder builder;
    int sign;
    Note note;
    static boolean lock_status=false;
    FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_note);
        writeText = findViewById(R.id.writeNote);
        countText = findViewById(R.id.countCharacter);
        sign = getIntent().getIntExtra("saveNoteSign",0);
        firebaseFirestore = FirebaseFirestore.getInstance();
        builder= new AlertDialog.Builder(this);
        lock = findViewById(R.id.lock_icon);
        unlock = findViewById(R.id.unlock_icon);
        note = (Note) getIntent().getSerializableExtra("Note_Item_Choose");
        if(note != null){
            firebaseFirestore.collection("notes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot doc : task.getResult()){
                            if (note.getId().equals(doc.getId())){
                                writeText.setText(doc.get("content").toString());
                                if(note.isChecked()){
                                    lock.setVisibility(View.VISIBLE);
                                    unlock.setVisibility(View.INVISIBLE);
                                    lock_status = true;
                                } else {
                                    lock.setVisibility(View.INVISIBLE);
                                    unlock.setVisibility(View.VISIBLE);
                                    lock_status =false;
                                }
                                break;
                            }
                        }
                    }
                }
            });
        }

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

    public void lockStatus(View view){
        if(lock.getVisibility() == View.VISIBLE){
            lock.setVisibility(View.INVISIBLE);
            unlock.setVisibility(View.VISIBLE);
            lock_status = false;
        } else {
            lock_status = true;
            lock.setVisibility(View.VISIBLE);
            unlock.setVisibility(View.INVISIBLE);
        }
    }

    public void backToMain(View view){
        finish();
    }

    public void saveAndUpdate(View view){
        if(sign == 100){
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/YYYY");
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alertdialogcustom, null);
            builder.setView(dialogView);
            TextView save = dialogView.findViewById(R.id.save);
            final EditText editText = dialogView.findViewById(R.id.editText);

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note note;
                    String time = getIntent().getStringExtra("time");
                    if(time == null || time.equals("")){
                        note = new Note(editText.getText().toString(),simpleDateFormat.format(new Date()),writeText.getText().toString(),0, lock_status);
                    } else {
                        note = new Note(editText.getText().toString(),time,writeText.getText().toString(),0,false);
                    }
                    firebaseFirestore.collection("notes").add(note)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    firebaseFirestore.collection("notes").document(documentReference.getId()).update("id",documentReference.getId());
                                    finish();
                                    MainActivity.setDailyNotes();
                                }
                            });
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        } else {
            firebaseFirestore.collection("notes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot doc : task.getResult()){
                            if(doc.getId().equals(note.getId())){
                                firebaseFirestore.collection("notes").document(doc.getId()).update("content",writeText.getText().toString());
                                firebaseFirestore.collection("notes").document(doc.getId()).update("checked",lock_status);
                                finish();
                                MainActivity.setDailyNotes();
                            }
                        }
                    }
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
