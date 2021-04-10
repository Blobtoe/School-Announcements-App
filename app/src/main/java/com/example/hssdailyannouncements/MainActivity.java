package com.example.hssdailyannouncements;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.hssdailyannouncements.fragments.AnnoucementsFragment;
import com.example.hssdailyannouncements.fragments.CalendarFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
    }
}