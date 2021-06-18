package com.example.test4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

public class filtre extends AppCompatActivity {

    static CheckBox checkBox2,checkBox3,checkBox4,checkBox5,checkBox6,checkBox7,checkBox8,checkBox9,checkBox10,checkBox11,checkBox12,checkBox13,checkBox14;
    static Boolean cb2,cb3,cb4,cb5,cb6,cb7,cb8,cb9,cb10,cb11,cb12,cb13,cb14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtre);

        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);
        checkBox5 = findViewById(R.id.checkBox5);
        checkBox6 = findViewById(R.id.checkBox6);
        checkBox7 = findViewById(R.id.checkBox7);
        checkBox8 = findViewById(R.id.checkBox8);
        checkBox9 = findViewById(R.id.checkBox9);
        checkBox10 = findViewById(R.id.checkBox10);
        checkBox11 = findViewById(R.id.checkBox11);
        checkBox12 = findViewById(R.id.checkBox12);
        checkBox13 = findViewById(R.id.checkBox13);
        checkBox14 = findViewById(R.id.checkBox14);

        cb2 = checkBox2.isChecked();
        cb3 = checkBox3.isChecked();
        cb4 = checkBox4.isChecked();
        cb5 = checkBox5.isChecked();
        cb6 = checkBox6.isChecked();
        cb7 = checkBox7.isChecked();
        cb8 = checkBox8.isChecked();
        cb9 = checkBox9.isChecked();
        cb10 = checkBox10.isChecked();
        cb11 = checkBox11.isChecked();
        cb12 = checkBox12.isChecked();
        cb13 = checkBox13.isChecked();
        cb14 = checkBox14.isChecked();

    }

}