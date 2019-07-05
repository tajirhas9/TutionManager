package com.ourcuet.tutionmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class ChangeInformationActivity extends AppCompatActivity {

    Integer StudentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_information);
        NumberPicker StudentTotalDaysNumberPicker = findViewById(R.id.UpdateStudentTotalDaysNumberPicker);
        NumberPicker StudentDaysCompletedNumberPicker = findViewById(R.id.UpdateStudentDaysCompletedNumberPicker);
        Button addButton = findViewById(R.id.UpdateButton);

        SetUpMinMaxNumberPickers(StudentTotalDaysNumberPicker,1,30);
        SetUpMinMaxNumberPickers(StudentDaysCompletedNumberPicker,0,30);

        Intent intent = getIntent();

        StudentID = Integer.parseInt(intent.getStringExtra("StudentID"));

        UpdateUI();
        SetOnClickListener(addButton);
    }

        private void UpdateUI() {
            TutionInfo tutionInfo = getTutionInfoFromSharedPreference();

            EditText StudentNameField = findViewById(R.id.UpdateStudentNameField);
            TextView InstitutionField = findViewById(R.id.UpdateInstitutionField);
            NumberPicker TotalDaysField = findViewById(R.id.UpdateStudentTotalDaysNumberPicker);
            NumberPicker DaysCompletedField = findViewById(R.id.UpdateStudentDaysCompletedNumberPicker);

            StudentNameField.setText(tutionInfo.StudentName);
            InstitutionField.setText(tutionInfo.Institution);
            TotalDaysField.setValue(tutionInfo.TotalDays);
            DaysCompletedField.setValue(tutionInfo.DaysCompleted);

            Button button = findViewById(R.id.UpdateButton);
            button.setText("Update Information");
        }

        //gets only one TutionInfo object with index value StudentID
        private TutionInfo getTutionInfoFromSharedPreference() {
            SharedPreferences sharedPreference = getSharedPreferences("TutionManagerSharedPreference" , MODE_PRIVATE);

            Gson gson = new Gson();

            String StoredListString = sharedPreference.getString("TutionList", null);

            java.lang.reflect.Type type = new TypeToken<ArrayList< TutionInfo > >(){}.getType();

            ArrayList < TutionInfo > PreStoredList;
            PreStoredList = gson.fromJson(StoredListString, type);

            return PreStoredList.get(StudentID);
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
        EditText StudentNameField = findViewById(R.id.UpdateStudentNameField);
        EditText InstitutionField = findViewById(R.id.UpdateInstitutionField);
        NumberPicker TotalDaysNumberPicker = findViewById(R.id.UpdateStudentTotalDaysNumberPicker);
        NumberPicker DaysCompletedNumberPicker = findViewById(R.id.UpdateStudentDaysCompletedNumberPicker);

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
        EditText StudentNameField = findViewById(R.id.UpdateStudentNameField);
        EditText InstitutionField = findViewById(R.id.UpdateInstitutionField);
        NumberPicker TotalDaysNumberPicker = findViewById(R.id.UpdateStudentTotalDaysNumberPicker);
        NumberPicker DaysCompletedNumberPicker = findViewById(R.id.UpdateStudentDaysCompletedNumberPicker);

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

        ArrayList< TutionInfo > TutionList = new ArrayList<>();

        // Get prestored list from sharedPreference

        SharedPreferences sharedPreference = getSharedPreferences("TutionManagerSharedPreference" , MODE_PRIVATE);

        TutionList = getPreStoredInformationFromSharedPreference(sharedPreference);

        ArrayList <TutionDayInformation> TutionDays = getPreStoredDayInformationFromSharedPreference(sharedPreference);


        // Overwrite sharedPreference

        sharedPreference.edit().clear();

        TutionList.get(StudentID).StudentName = tutionInfo.StudentName;
        TutionList.get(StudentID).Institution = tutionInfo.Institution;
        TutionList.get(StudentID).TotalDays = tutionInfo.TotalDays;
        TutionList.get(StudentID).DaysCompleted= tutionInfo.DaysCompleted;

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

    private void OverwriteSharedPreference(SharedPreferences sharedPreference, ArrayList < TutionInfo > information, ArrayList<TutionDayInformation> TutionDays) {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreference.edit();

        //Apply to tutionDays
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
        MakeToast("Student Information Updated");
        RedirectToTutionProfileActivity(StudentID);
    }

        private void RedirectToTutionProfileActivity(Integer id) {
            Intent intent = new Intent(this,TutionProfileActivity.class);
            intent.putExtra("StudentID", id.toString());
            Log.v("StudentID ", id.toString());
            startActivity(intent);

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
