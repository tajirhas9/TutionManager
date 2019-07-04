package com.ourcuet.tutionmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ListOfDaysActivity extends AppCompatActivity {

    Integer StudentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_days);

        Intent intent = getIntent();
        StudentID = Integer.parseInt(intent.getStringExtra("StudentID"));

    }
}
