package com.geoit.climbapp;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

@Deprecated
public class OverpassAsyncTask extends AsyncTask<String, Float, File> {

    private final static String URL = "";

    private final static String pw = "";

    private boolean isFinished = false;


    @Override
    protected File doInBackground(String... params) {
        publishProgress(0.0f);

        
        publishProgress(0.0f);
        return null;
    }


    @Override
    protected void onPostExecute(File f) {
        super.onPostExecute(f);
    }

    public boolean isFinished() {
        return isFinished;
    }

}





