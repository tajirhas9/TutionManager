package com.ourcuet.tutionmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.NumberPicker;

public class NewTutionRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tution_registration);

        NumberPicker NewStudentTotalDaysNumberPicker = findViewById(R.id.NewStudentTotalDaysNumberPicker);
        NumberPicker NewStudentDaysCompletedNumberPicker = findViewById(R.id.NewStudentDaysCompletedNumberPicker);

        SetUpMinMaxNumberPickers(NewStudentTotalDaysNumberPicker,1,30);
        SetUpMinMaxNumberPickers(NewStudentDaysCompletedNumberPicker,0,30);
    }

    private void SetUpMinMaxNumberPickers(NumberPicker numberPicker,int min, int max) {

        numberPicker.setMaxValue(max);
        numberPicker.setMinValue(min);
    }
}
