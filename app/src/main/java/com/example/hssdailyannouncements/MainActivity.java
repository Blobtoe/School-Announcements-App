package com.example.hssdailyannouncements;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    static PDFView pdfView;
    static SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get the UI elements
        pdfView = findViewById(R.id.pdfView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        //re-download and show latest announcement on refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //download the latest announcement pdf
                new DownloadAnnouncements().execute(getApplicationContext());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //download the latest announcement pdf
        new DownloadAnnouncements().execute(getApplicationContext());
    }

    //show the announcements from the pdf file
    public static void showAnnouncements(File file, Context context) {
        //stop the refreshing animation on the swipe layout
        swipeRefreshLayout.setRefreshing(false);

        //if the download was unsuccessful
        if (file == null) {
            Toast.makeText(context, "Failed to fetch latest announcements.", Toast.LENGTH_LONG).show();
            return;
        }

        //show the pdf
        pdfView.fromFile(file).load();
        Log.d("log", "DOWNLOADED NEW PDF");
    }
}