package com.ourcuet.tutionmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.os.Build.ID;

public class ListOfDaysActivity extends AppCompatActivity {

    Integer StudentID;
    Integer CurrentMonth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_days);

        Intent intent = getIntent();
        StudentID = Integer.parseInt(intent.getStringExtra("StudentID"));
        ClearLayout();
        FillLayout();
    }

    private void ClearLayout() {
        LinearLayout contentHolder = findViewById(R.id.ListOfDaysContentHolder);
        contentHolder.removeAllViews();
    }

    private void FillLayout() {
        SharedPreferences sharedPreference = getSharedPreferences("TutionManagerSharedPreference" , MODE_PRIVATE);
        ArrayList<String> TutionDays = getPreStoredDayInformationFromSharedPreference(sharedPreference);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        for(int i = 0;i < TutionDays.size(); ++i) {
            String DayInString = TutionDays.get(i);

            if(NotCurrentMonth(DayInString)) {
                DisplayNewDay(getMonthName(DayInString),-1);
            }

            DisplayNewDay(DayInString,i);
        }
    }

    private ArrayList< String > getPreStoredDayInformationFromSharedPreference(SharedPreferences sharedPreferences) {
        Gson gson = new Gson();

        String keyOfSharedPreferences = "TutionDays";

        String StoredDaysString = sharedPreferences.getString(keyOfSharedPreferences , null);

        java.lang.reflect.Type type = new TypeToken<ArrayList<TutionDayInformation> >() {}.getType();

        ArrayList < TutionDayInformation > PreStoredDays;

        if(StoredDaysString != null) {
            PreStoredDays = gson.fromJson(StoredDaysString, type);
            return PreStoredDays.get(StudentID).DaysWentToTution;
        }
        else{
            return new ArrayList<>();
        }

    }

    private void DisplayNewDay(String date,int index) {
        LinearLayout contentHolder = findViewById(R.id.ListOfDaysContentHolder);

        LinearLayout newLayout = getNewTextViewLayoutHavingText(date, index);

        LinearLayout.LayoutParams LayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT);

        LayoutParams.setMargins(0,10,0,0);

        AddNewLayoutToExistingLayout(contentHolder, newLayout, LayoutParams);
    }

    private LinearLayout getNewTextViewLayoutHavingText(String string, int index) {
        LinearLayout newLayout = new LinearLayout(this);

        TextView textView = SetUpTextView(string,index);
        LinearLayout.LayoutParams LayoutParams = getLinearLayoutParametersForNewLayout();
        newLayout.addView(textView,LayoutParams);

        if(index == -1) {
            newLayout.setBackgroundColor(Color.GRAY);
        }
        else
            newLayout.setBackgroundColor(getResources().getColor(R.color.CustomWhite));

        return newLayout;
    }

    private TextView SetUpTextView(String string,int index) {
        TextView textView = new TextView(this);

        SpannableString convertedString;

        convertedString = new SpannableString(string);

        if(index == -1)
            convertedString.setSpan(new StyleSpan(Typeface.BOLD),0,convertedString.length(),0);

        textView.setText(convertedString);
        if(index == -1)
            textView.setTextSize(30f);
        else
            textView.setTextSize(20f);
        return textView;
    }

    private void AddNewLayoutToExistingLayout(LinearLayout existingLayout, LinearLayout newLayout, LinearLayout.LayoutParams LayoutParameters) {
        existingLayout.addView(newLayout,LayoutParameters);
    }

    private LinearLayout.LayoutParams getLinearLayoutParametersForNewLayout() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5,10,0,10);
        return layoutParams;
    }

    private boolean NotCurrentMonth(String day) {
        Integer month = Integer.parseInt(day.substring(3,5));
        Log.v("Month of this day", month.toString());
        if(month != CurrentMonth) {
            CurrentMonth = month;
            return true;
        }
        else
            return false;
    }

    private String getMonthName(String date) {
        String MonthName = new String();
        Log.v("date",date);
        Log.v("CurrentMonth",CurrentMonth.toString());
        switch (CurrentMonth) {
            case 1:
                MonthName = "January";
                break;
            case 2:
                MonthName = "February";
                break;
            case 3:
                MonthName = "March";
                break;
            case 4:
                MonthName = "April";
                break;
            case 5:
                MonthName = "May";
                break;
            case 6:
                MonthName = "June";
                break;
            case 7:
                MonthName = "July";
                break;
            case 8:
                MonthName = "August";
                break;
            case 9:
                MonthName = "September";
                break;
            case 10:
                MonthName = "October";
                break;
            case 11:
                MonthName = "November";
                break;
            case 12:
                MonthName = "December";
                break;
            default:
                MonthName = "UNKNOWN";
                break;
        }

        String year = date.substring(6,10);
        String response = (MonthName+" "+year);
        Log.v("Month Name", response);
        return response;
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

    private void RefreshActivity() {
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, TutionProfileActivity.class);
        intent.putExtra("StudentID",StudentID.toString());
        startActivity(intent);
        finish();
    }
}
