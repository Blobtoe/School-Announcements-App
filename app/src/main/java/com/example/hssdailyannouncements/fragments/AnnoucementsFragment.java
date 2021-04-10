package com.example.hssdailyannouncements.fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hssdailyannouncements.R;
import com.github.barteksc.pdfviewer.PDFView;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class AnnoucementsFragment extends Fragment {

    String REQUESTURL = "https://www.sd48howesound.org/apps/pages/index.jsp?uREC_ID=1094706&type=d&pREC_ID=1369697";
    String fileName = "announcements.pdf";

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

        //if we already have them downloaded, then use that file.
        if (new File(getContext().getCacheDir().toString(),fileName).exists()) {
            showAnnouncements(true);
        }
        //else download it
        else {
            //download the latest announcement pdf from the school website
            downloadAnnouncements();
        }

        //re-download and show latest announcement on refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //download the latest announcement pdf
                downloadAnnouncements();
            }
        });

        //return the inflated layout
        return view;
    }

    //downloads the announcements.
    void downloadAnnouncements() {
        //run download in background because Jsoup cant run on ui thread
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                String PdfUrl = "";

                try {
                    //load the hss daily announcement webpage
                    Document doc = Jsoup.connect(REQUESTURL).timeout(5000).get();
                    //get the link elements by getting elements with class
                    Elements links = doc.select(".attachment-type-pdf");
                    //return the first link in the list (latest pdf)
                    PdfUrl = links.first().attr("href");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (PdfUrl != "") {
                    //make http request to the school website
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(PdfUrl).build();
                    client.newCall(request).enqueue(new Callback() {

                        //save the response to a file
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            //create new file object
                            File file = new File(getContext().getCacheDir(), fileName);

                            //save response to file
                            BufferedSink sink = Okio.buffer(Okio.sink(file));
                            sink.writeAll(response.body().source());
                            sink.close();

                            //show the pdf
                            showAnnouncements(true);
                        }

                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            showAnnouncements(false);
                        }
                    });
                }
                else {
                    Log.d("debug", "Failed to get pdf url");
                }
            }
        });
    }

    //show the announcements from the pdf file
    void showAnnouncements(boolean downloadSuccess) {
        File file = new File(getContext().getCacheDir(), fileName);

        //stop the refreshing animation on the swipe layout
        swipeRefreshLayout.setRefreshing(false);

        //if the download was unsuccessful
        if (!downloadSuccess) {
            Toast.makeText(getContext(), "Failed to fetch latest announcements.", Toast.LENGTH_LONG).show();
            return;
        }

        //show the pdf
        pdfView.fromFile(file).load();
    }
}