package com.example.hssdailyannouncements.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hssdailyannouncements.R;
import com.example.hssdailyannouncements.utils.DownloadAnnouncements;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class AnnoucementsFragment extends Fragment {

    static PDFView pdfView;
    static SwipeRefreshLayout swipeRefreshLayout;

    public AnnoucementsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_annoucements, container, false);

        //get the UI elements
        pdfView = view.findViewById(R.id.pdfView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        //re-download and show latest announcement on refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //download the latest announcement pdf
                new DownloadAnnouncements().execute(getContext());
            }
        });

        //return the inflated layout
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getAnnouncements();
    }

    //gets the announcements.

    void getAnnouncements() {
        //if we already have them downloaded, then use that file.
        if (new File(getContext().getCacheDir().toString(),"announcements.pdf").exists()) {
            showAnnouncements(new File(getContext().getCacheDir().toString(),"announcements.pdf"), getContext());
        }
        //else download it
        else {
            //download the latest announcement pdf from the school website
            new DownloadAnnouncements().execute(getContext());
        }
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
        Log.d("log", "SHOWING NEW PDF");
    }
}