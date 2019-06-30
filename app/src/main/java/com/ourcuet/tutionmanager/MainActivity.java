package com.ourcuet.tutionmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       Button AddNewStudentButton = findViewById(R.id.AddNewStudent);
        AddNewStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RedirectToRegistrationActivity();
            }
        });

        Log.v("App says","Welcome to Tution Manager");
        DisplayTutionList();
    }

    private void DisplayTutionList() {

        ArrayList < TutionInfo > TutionList = new ArrayList<>();

        // Get prestored list from sharedPreference

        SharedPreferences sharedPreference = getSharedPreferences("TutionManagerSharedPreference" , MODE_PRIVATE);

        TutionList = getPreStoredInformationFromSharedPreference(sharedPreference);


        //Iterate over the TutionList

        TutionInfo tution = new TutionInfo();

        if(TutionList.isEmpty()) {
            DisplayNewTution("No Tution Added",-1);
        }
        else {
            for(int i = 0; i < TutionList.size(); ++i) {
                tution = TutionList.get(i);

                DisplayNewTution(tution.StudentName, tution.TutionID);
            }
        }
    }

    private void DisplayNewTution(String name, Integer ID) {
        LinearLayout contentHolder = findViewById(R.id.ContentHolder);

        LinearLayout newLayout = getNewTextViewLayoutHavingText(name,ID);

        LinearLayout.LayoutParams LayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT);

        LayoutParams.setMargins(0,10,0,0);

        AddNewLayoutToExistingLayout(contentHolder, newLayout, LayoutParams);
    }

    private LinearLayout getNewTextViewLayoutHavingText(String string, Integer ID) {
        LinearLayout newLayout = new LinearLayout(this);

        TextView textView = SetUpTextView(string,ID);
        LinearLayout.LayoutParams LayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        newLayout.addView(textView,LayoutParams);
        return newLayout;
    }

    private TextView SetUpTextView(String string,final Integer ID) {
        TextView textView = new TextView(this);
        textView.setText(string);
        textView.setTextSize(20f);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ID != -1)
                    RedirectToTutionProfileActivity(ID);
            }
        });
        return textView;
    }

    private void AddNewLayoutToExistingLayout(LinearLayout existingLayout, LinearLayout newLayout, LinearLayout.LayoutParams LayoutParameters) {
        existingLayout.addView(newLayout,LayoutParameters);
    }

    public ArrayList< TutionInfo > getPreStoredInformationFromSharedPreference(SharedPreferences sharedPreference) {

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


    private void RedirectToRegistrationActivity() {
        Intent intent = new Intent(this,NewTutionRegistrationActivity.class);
        startActivity(intent);
    }

    private void RedirectToTutionProfileActivity(Integer id) {
        id = id - 1;
        Intent intent = new Intent(this,TutionProfileActivity.class);
        intent.putExtra("StudentID", id.toString());         //As array index starts from zero.NewTutionRegistrationActivity.getLatestAvailableID() gives id starting from 1
        Log.v("StudentID ", id.toString());
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
