package com.example.broadcastreciever;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.concurrent.Executor;

import pl.droidsonroids.gif.GifImageView;

public class Custom_ListView_ViewChild_Adapter  extends BaseAdapter {

    private List<Note> listData;
    private int layout;
    private Context context;
    PopupWindow mypopupWindow;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    static int po;



    public Custom_ListView_ViewChild_Adapter(List<Note> listData, int layout, Context context) {
        this.listData = listData;
        this.layout = layout;
        this.context = context;
        po=0;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(layout,null);

        TextView title =  convertView.findViewById(R.id.title);
        TextView date = convertView.findViewById(R.id.date);
        ImageView check_view = convertView.findViewById(R.id.checked);
        final LinearLayout linearLayout = convertView.findViewById(R.id.item_background);

        title.setText(listData.get(position).getTitle());
        date.setText(listData.get(position).getDate());
        check_view.setVisibility(listData.get(position).isChecked() ? View.VISIBLE : View.INVISIBLE);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listData.get(position).isChecked()){
                    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    final SharedPreferences sharedPreferences = context.getSharedPreferences("settingdata", Context.MODE_PRIVATE);

                    BiometricManager biometricManager = BiometricManager.from(context);
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

                    Executor executor = ContextCompat.getMainExecutor(context);
                    final BiometricPrompt biometricPrompt = new BiometricPrompt((FragmentActivity) context, executor, new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                        }

                        @Override
                        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                            moveToWriteNote(position);
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                        }
                    });
                    final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                            .setTitle("REQUIRE ACCESS")
                            .setDescription("place your finger print to scanner")
                            .setNegativeButtonText("NO THANK")
                            .build();
                    biometricPrompt.authenticate(promptInfo);

                } else {
                    moveToWriteNote(position);
                }
            }
        });
        
        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mypopupWindow.showAsDropDown(v,v.getMeasuredWidth()-290,0);
                po = position;
                return true;
            }
        });

        View view = inflater.inflate(R.layout.popup, null);
        mypopupWindow = new PopupWindow(view, 290, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
        mypopupWindow.getContentView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("notes").document(listData.get(po).getId()).delete();
                if(context instanceof MainActivity){
                    MainActivity.setDailyNotes();
                } else if (context instanceof SearchAll){
                    SearchAll.getAllNotes();
                }
                mypopupWindow.dismiss();
            }
        });


        return convertView;
    }

    public void moveToWriteNote(int position){
        Intent intent = new Intent(context,WriteNote.class);
        intent.putExtra("Note_Item_Choose",listData.get(position));
        context.startActivity(intent);
    }
}
