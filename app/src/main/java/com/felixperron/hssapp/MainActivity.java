package com.felixperron.hssapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.felixperron.hssapp.fragments.AnnoucementsFragment;
import com.felixperron.hssapp.fragments.CalendarFragment;
import com.felixperron.hssapp.fragments.PrivacyNoticeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        bottomNav = findViewById(R.id.bottomNav);

        //Start the announcements fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AnnoucementsFragment annoucementsFragment = new AnnoucementsFragment();
        fragmentTransaction.replace(R.id.main_content, annoucementsFragment);
        fragmentTransaction.commit();

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //Start the announcements fragment
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                switch (item.getItemId()) {
                    // if the announcements button is pressed
                    case R.id.announcements_button:
                        AnnoucementsFragment annoucementsFragment = new AnnoucementsFragment();
                        fragmentTransaction.replace(R.id.main_content, annoucementsFragment);
                        fragmentTransaction.commit();
                        bottomNav.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                        break;

                    // if the calendar button is pressed
                    case R.id.calendar_button:
                        CalendarFragment calendarFragment = new CalendarFragment();
                        fragmentTransaction.replace(R.id.main_content, calendarFragment);
                        fragmentTransaction.commit();
                        bottomNav.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                        break;

                        /*
                    // if the cafe button is pressed
                    case R.id.cafe_button:
                        CafeFragment cafeFragment = new CafeFragment();
                        fragmentTransaction.replace(R.id.main_content, cafeFragment);
                        fragmentTransaction.commit();
                        bottomNav.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                        break; */
                }
                return true;
            }
        });

        if (getSharedPreferences("PREFERENCES", MODE_PRIVATE).getBoolean("isFirstRun", true)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage("This app uses Google Analytics to track the number of users using it. This information is solely used for analytics and is never sold or shared to anyone.");
            alertBuilder.setTitle("Privacy Notice");
            alertBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getSharedPreferences("PREFERENCES", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();
                }
            });
            alertBuilder.setCancelable(false);
            alertBuilder.create().show();
        }
    }
}