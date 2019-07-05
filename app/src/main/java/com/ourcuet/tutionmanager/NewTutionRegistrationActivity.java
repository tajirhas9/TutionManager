package com.ourcuet.tutionmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Stack;

public class NewTutionRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tution_registration);

        NumberPicker NewStudentTotalDaysNumberPicker = findViewById(R.id.NewStudentTotalDaysNumberPicker);
        NumberPicker NewStudentDaysCompletedNumberPicker = findViewById(R.id.NewStudentDaysCompletedNumberPicker);
        Button addButton = findViewById(R.id.AddButton);

        SetUpMinMaxNumberPickers(NewStudentTotalDaysNumberPicker,1,30);
        SetUpMinMaxNumberPickers(NewStudentDaysCompletedNumberPicker,0,30);

        NewStudentTotalDaysNumberPicker.setValue(12);

        SetOnClickListener(addButton);
    }

    private void SetOnClickListener(Button addButton) {

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(formValidationSuccessful()) {

                    TutionInfo tutionInfo = GetNewTutionInformation();

                    uploadToSharedPreference(tutionInfo);

                    Redirect();
                }
            }
        });
    }

    private boolean formValidationSuccessful() {
        EditText StudentNameField = findViewById(R.id.StudentNameField);
        EditText InstitutionField = findViewById(R.id.InstitutionField);
        NumberPicker TotalDaysNumberPicker = findViewById(R.id.NewStudentTotalDaysNumberPicker);
        NumberPicker DaysCompletedNumberPicker = findViewById(R.id.NewStudentDaysCompletedNumberPicker);

        String StudentName = StudentNameField.getText().toString();
        String Institution = InstitutionField.getText().toString();
        Integer TotalDays = TotalDaysNumberPicker.getValue();
        Integer DaysCompleted = DaysCompletedNumberPicker.getValue();

        if(StudentName != null && Institution != null && TotalDays != null && DaysCompleted != null) {
            return true;
        }
        else {
            return false;
        }
    }

    private TutionInfo GetNewTutionInformation() {
        EditText StudentNameField = findViewById(R.id.StudentNameField);
        EditText InstitutionField = findViewById(R.id.InstitutionField);
        NumberPicker TotalDaysNumberPicker = findViewById(R.id.NewStudentTotalDaysNumberPicker);
        NumberPicker DaysCompletedNumberPicker = findViewById(R.id.NewStudentDaysCompletedNumberPicker);

        String StudentName = StudentNameField.getText().toString();
        String Institution = InstitutionField.getText().toString();
        Integer TotalDays = TotalDaysNumberPicker.getValue();
        Integer DaysCompleted = DaysCompletedNumberPicker.getValue();

        TutionInfo tutionInfo = new TutionInfo();

        tutionInfo.StudentName = StudentName;
        tutionInfo.Institution = Institution;
        tutionInfo.TotalDays = TotalDays;
        tutionInfo.DaysCompleted = DaysCompleted;
        tutionInfo.TutionID = getLatestAvailableID();

        return tutionInfo;
    }

    private void uploadToSharedPreference(TutionInfo tutionInfo) {

        ArrayList< TutionInfo > TutionList;

        // Get prestored list from sharedPreference

        SharedPreferences sharedPreference = getSharedPreferences("TutionManagerSharedPreference" , MODE_PRIVATE);

        TutionList = getPreStoredInformationFromSharedPreference(sharedPreference);

        ArrayList <TutionDayInformation> TutionDays = getPreStoredDayInformationFromSharedPreference(sharedPreference);

        // Overwrite sharedPreference

        sharedPreference.edit().clear();

        TutionList.add(tutionInfo);

        TutionDays.add(NewTutionDayInformation(tutionInfo.TutionID));

        OverwriteSharedPreference(sharedPreference,TutionList,TutionDays);


    }

    public ArrayList< TutionInfo > getPreStoredInformationFromSharedPreference(SharedPreferences sharedPreference) {

        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreference.edit();

        String StoredListString = sharedPreference.getString("TutionList", null);

        java.lang.reflect.Type type = new TypeToken<ArrayList< TutionInfo > >(){}.getType();

        ArrayList < TutionInfo > PreStoredList;

        if(StoredListString != null)
            PreStoredList = gson.fromJson(StoredListString, type);

        else
            PreStoredList = new ArrayList<>();

        return  PreStoredList;
    }

    private ArrayList< TutionDayInformation > getPreStoredDayInformationFromSharedPreference(SharedPreferences sharedPreferences) {
        Gson gson = new Gson();

        String keyOfSharedPreferences = "TutionDays";

        String StoredDaysString = sharedPreferences.getString(keyOfSharedPreferences , null);

        java.lang.reflect.Type type = new TypeToken<ArrayList<TutionDayInformation> >() {}.getType();

        ArrayList < TutionDayInformation > PreStoredDays;

        if(StoredDaysString != null)
            PreStoredDays = gson.fromJson(StoredDaysString , type);
        else
            PreStoredDays = new ArrayList<>();

        return PreStoredDays;
    }

    private TutionDayInformation NewTutionDayInformation(Integer tutionID) {
        SimpleDateFormat dateFormater = new SimpleDateFormat("dd/M/yyyy");
        TutionDayInformation tutionDayInformation = new TutionDayInformation();
        tutionDayInformation.tutionID = tutionID;
        tutionDayInformation.DaysWentToTution = new ArrayList<>();

        return tutionDayInformation;
    }

    private void OverwriteSharedPreference(SharedPreferences sharedPreference, ArrayList < TutionInfo > information, ArrayList<TutionDayInformation> TutionDays) {
        Gson gson = new Gson();

        SharedPreferences.Editor editor = sharedPreference.edit();

        editor.clear();
        editor.putString("TutionList", gson.toJson(information));
        editor.apply();

        //Apply to add days

        editor.putString("TutionDays", gson.toJson(TutionDays));
        editor.apply();

    }

    private void SetUpMinMaxNumberPickers(NumberPicker numberPicker,int min, int max) {

        numberPicker.setMaxValue(max);
        numberPicker.setMinValue(min);
    }

    private Integer getLatestAvailableID() {
        ArrayList < TutionInfo > TutionList = new ArrayList<>();

        // Get prestored list from sharedPreference

        SharedPreferences sharedPreference = getSharedPreferences("TutionManagerSharedPreference" , MODE_PRIVATE);

        TutionList = getPreStoredInformationFromSharedPreference(sharedPreference);

        return (TutionList.size()+1);
    }

    private void Redirect() {
        MakeToast("New Student Added");
        ReturnToMainActivity();
    }

    private void MakeToast(String message) {
        Toast.makeText(getApplicationContext() , message, Toast.LENGTH_SHORT).show();
    }

    private void ReturnToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
