package com.example.hssdailyannouncements;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;

public class DownloadAnnouncements extends AsyncTask<Context, Void, File> {

    Context context;
    String webpageUrl = "https://www.sd48howesound.org/apps/pages/index.jsp?uREC_ID=1094706&type=d&pREC_ID=1369697";
    String outDir;
    String PDFUrl;
    String fileName = "annoucements.pdf";
    File pdf;
    Boolean downloadFailure = false;

    @Override
    protected File doInBackground(Context... contexts) {
        context = contexts[0];
        outDir = context.getCacheDir().toString();

        try {
            //load the hss daily annoucement webpage
            Document doc = Jsoup.connect(webpageUrl).get();
            //get the link elements by getting elements with class
            Elements links = doc.select(".attachment-type-pdf");
            //return the first link in the list (latest pdf)
            PDFUrl = links.first().attr("href");
        } catch (Exception e) {
            e.printStackTrace();
            PDFUrl = "";
        }

        Log.d("log", "GOT PDF URL");

        //if we successfully got the pdf url
        if (PDFUrl != "") {

            //download the pdf to cache
            PRDownloader.download(
                    PDFUrl,
                    outDir,
                    fileName
            ).build().start(new OnDownloadListener() {

                @Override
                public void onDownloadComplete() {
                    pdf = new File(outDir, fileName);
                }

                @Override
                public void onError(Error error) {
                    downloadFailure = true;
                    Log.d("log", error.getServerErrorMessage());
                }
            });

            Log.d("log", "STARTED DOWNLOAD");

            //wait for file to be downloaded
            while (pdf == null) {

                //if the download failed for whatever reason
                if (downloadFailure == true) {
                    Log.d("log", "DOWNLOAD FAILED");
                    break;
                }
            }
        }
        return pdf;
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);

        MainActivity.showAnnouncements(file, context);
    }
}
