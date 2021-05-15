package com.example.test4;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class profileCh extends AppCompatActivity {

    public void Click3(View v){

        TextView tv3 = findViewById(R.id.textView3);
        EditText tv_idch =  findViewById(R.id.idchange);
        tv3.setText(tv_idch.getText().toString());

    }

}
