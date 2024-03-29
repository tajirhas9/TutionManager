package com.ourcuet.tutionmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TutionProfileActivity extends AppCompatActivity {

    Integer StudentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tution_profile);

        Intent intent = getIntent();
        String StudentIDString = intent.getStringExtra("StudentID");
        Log.d("Student ID" , StudentIDString);
        StudentID = Integer.parseInt(StudentIDString);

        if(StudentID == null) {
            MakeToast("Student not available");
            ReturnToMainActivity();
        }

        FillLayout();

        SetUpIncreaseButton();
        SetUpDecreaseButton();
        SetUpResetButton();
        SetUpChangeButton();
        SetUpDeleteButton();
        SetUpDaysCoveredButton();
    }

    private void FillLayout() {
        TutionInfo tutionInfo = getTutionInfoFromSharedPreference();

        TextView StudentNameField = findViewById(R.id.StudentNameField);
        TextView InstitutionField = findViewById(R.id.InstitutionField);
        TextView TotalDaysField = findViewById(R.id.TotalDays);
        TextView DaysCompletedField = findViewById(R.id.DaysCompleted);

        StudentNameField.setText(tutionInfo.StudentName);
        InstitutionField.setText(tutionInfo.Institution);
        TotalDaysField.setText(tutionInfo.TotalDays.toString());
        DaysCompletedField.setText(tutionInfo.DaysCompleted.toString());
    }


    //gets only one TutionInfo object with index value StudentID
    private TutionInfo getTutionInfoFromSharedPreference() {
        SharedPreferences sharedPreference = getSharedPreferences("TutionManagerSharedPreference" , MODE_PRIVATE);

        Gson gson = new Gson();

        String StoredListString = sharedPreference.getString("TutionList", null);

        Log.v("StoredListString", StoredListString);

        java.lang.reflect.Type type = new TypeToken<ArrayList< TutionInfo > >(){}.getType();

        ArrayList < TutionInfo > PreStoredList;
        PreStoredList = gson.fromJson(StoredListString, type);

        return PreStoredList.get(StudentID);
    }

    private void SetUpChangeButton() {
        Button IncreaseButton = findViewById(R.id.ChangeInformation);

        IncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RedirectToChangeInformationActivity();
            }
        });
    }

    private void SetUpIncreaseButton() {
        Button IncreaseButton = findViewById(R.id.increase);

        IncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If updated,then refresh. If not updated, don't refresh
                if(UpdateDaysCompletedByOne(1))
                    RefreshActivity();
            }
        });
    }

    private void SetUpDecreaseButton() {
        Button IncreaseButton = findViewById(R.id.decrease);

        IncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if updated,then refresh. Else, don't refresh
                if(UpdateDaysCompletedByOne(-1))
                    RefreshActivity();
            }
        });
    }

    private void SetUpResetButton() {
        Button IncreaseButton = findViewById(R.id.reset);

        IncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetDaysCompletedByOne();

                RefreshActivity();
            }
        });
    }

    private void SetUpDeleteButton() {
        Button DeleteButton = findViewById(R.id.DeleteStudent);

        DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAlertDialogue();
            }
        });
    }

    private void SetUpDaysCoveredButton() {
        Button DaysCoveredButton = findViewById(R.id.ShowDaysButton);

        DaysCoveredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RedirectToListOfDaysActivity();
            }
        });
    }

    private boolean UpdateDaysCompletedByOne(Integer value) {

        SharedPreferences sharedPreference = getSharedPreferences("TutionManagerSharedPreference" , MODE_PRIVATE);

        ArrayList < TutionInfo > TutionList = getPreStoredTutionInformationFromSharedPreference(sharedPreference);
        ArrayList <TutionDayInformation> TutionDays = getPreStoredDayInformationFromSharedPreference(sharedPreference);

        TutionList.get(StudentID).DaysCompleted = TutionList.get(StudentID).DaysCompleted + value;

        if(TutionList.get(StudentID).DaysCompleted >= 0) {

            if(value == 1) {                                                                        // + Button
                TutionDays.get(StudentID).DaysWentToTution.add(getCurrentDateInStringFormat());
            }
            else if(TutionDays.get(StudentID).DaysWentToTution.size() > 0) {                        // - Button
                int lastIndex = TutionDays.get(StudentID).DaysWentToTution.size()-1;
                TutionDays.get(StudentID).DaysWentToTution.remove(lastIndex);
            }

            OverwriteSharedPreference(sharedPreference, TutionList,TutionDays,getLatestAvailableID());
            return true;
        }
        else {
            MakeToast("You have not completed any day this month");
            return false;
        }
    }

    private void ResetDaysCompletedByOne() {
        SharedPreferences sharedPreference = getSharedPreferences("TutionManagerSharedPreference" , MODE_PRIVATE);

        ArrayList < TutionInfo > TutionList = getPreStoredTutionInformationFromSharedPreference(sharedPreference);

        ArrayList<TutionDayInformation> TutionDays = getPreStoredDayInformationFromSharedPreference(sharedPreference);


        TutionList.get(StudentID).DaysCompleted = 0;

        OverwriteSharedPreference(sharedPreference, TutionList,TutionDays, getLatestAvailableID());
    }

    private void ShowAlertDialogue() {
        new AlertDialog.Builder(this)
                .setTitle("Remove this Student?")
                .setMessage("Are you sure you want to remove this student from your Tution List?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RemoveStudent();
                        Log.v("Response" , "True");
                        ReturnToMainActivity();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MakeToast("Student is not removed");
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    //Returns overall tutionlist

    public ArrayList< TutionInfo > getPreStoredTutionInformationFromSharedPreference(SharedPreferences sharedPreference) {

        Gson gson = new Gson();

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

    private void OverwriteSharedPreference(SharedPreferences sharedPreference, ArrayList < TutionInfo > information, ArrayList<TutionDayInformation> TutionDays, Integer nextID) {
        Gson gson = new Gson();

        SharedPreferences.Editor editor = sharedPreference.edit();

        editor.clear();
        editor.putString("TutionList", gson.toJson(information));
        editor.apply();

        //Apply to add days

        editor.putString("TutionDays", gson.toJson(TutionDays));
        editor.apply();

        //Increase NextID

        editor.putString("NextID",gson.toJson(nextID));
        editor.apply();

    }

    private boolean RemoveStudent() {
        SharedPreferences sharedPreference = getSharedPreferences("TutionManagerSharedPreference" , MODE_PRIVATE);

        ArrayList < TutionInfo > TutionList = getPreStoredTutionInformationFromSharedPreference(sharedPreference);
        ArrayList <TutionDayInformation> TutionDays = getPreStoredDayInformationFromSharedPreference(sharedPreference);
        Integer nextID = getLatestAvailableID();

        TutionList.remove(TutionList.get(StudentID));
        TutionDays.remove(TutionDays.get(StudentID));
        Log.v("TutionList" , TutionList.toString());
        OverwriteSharedPreference(sharedPreference, TutionList,TutionDays,nextID);

        return true;
    }


    private Integer getLatestAvailableID() {
        ArrayList < TutionInfo > TutionList = new ArrayList<>();

        // Get prestored list from sharedPreference

        SharedPreferences sharedPreference = getSharedPreferences("TutionManagerSharedPreference" , MODE_PRIVATE);

        return Integer.parseInt(sharedPreference.getString("NextID",Integer.valueOf(0).toString()));

    }

    private String getCurrentDateInStringFormat() {
        DateFormat dateFormater = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return dateFormater.format(date);
    }

    private void MakeToast(String message) {
        Toast.makeText(getApplicationContext() , message, Toast.LENGTH_SHORT).show();
    }
    private void ReturnToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void RedirectToChangeInformationActivity() {
        Intent intent = new Intent(this, ChangeInformationActivity.class);
        intent.putExtra("StudentID",StudentID.toString());
        startActivity(intent);
    }

    private void RedirectToListOfDaysActivity() {
        Intent intent = new Intent(this, ListOfDaysActivity.class);
        intent.putExtra("StudentID",StudentID.toString());
        startActivity(intent);
    }

    private void RefreshActivity() {
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
