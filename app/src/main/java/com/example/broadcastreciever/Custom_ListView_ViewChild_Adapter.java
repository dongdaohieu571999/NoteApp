package com.example.broadcastreciever;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class Custom_ListView_ViewChild_Adapter  extends BaseAdapter {

    private List<Note> listData;
    private int layout;
    private Context context;
    PopupWindow mypopupWindow;

    public Custom_ListView_ViewChild_Adapter(List<Note> listData, int layout, Context context) {
        this.listData = listData;
        this.layout = layout;
        this.context = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(layout,null);

        TextView title =  convertView.findViewById(R.id.title);
        TextView date = convertView.findViewById(R.id.date);
        ImageView check_view = convertView.findViewById(R.id.checked);
        final LinearLayout linearLayout = convertView.findViewById(R.id.item_background);

        title.setText(listData.get(position).getTitle());
        date.setText(listData.get(position).getDate());
        check_view.setVisibility(listData.get(position).isChecked() ? View.INVISIBLE : View.VISIBLE);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,WriteNote.class);
                context.startActivity(intent);
            }
        });

        setPopUpWindow();
        
        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mypopupWindow.showAsDropDown(v,v.getMeasuredWidth()-290,0);
                return true;
            }
        });
        mypopupWindow.getContentView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"sjhbcjsdc",Toast.LENGTH_LONG).show();
                mypopupWindow.dismiss();
            }
        });



        return convertView;
    }
    private void setPopUpWindow() {
        LayoutInflater inflater = (LayoutInflater)
                context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup, null);
        mypopupWindow = new PopupWindow(view, 290, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
    }
}
