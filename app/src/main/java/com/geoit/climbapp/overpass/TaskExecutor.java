package com.geoit.climbapp.overpass;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class TaskExecutor {


    private boolean isRunning = false;

    private final Executor executor = Executors.newSingleThreadExecutor(); // change according to your requirements
    private final Handler handler = new Handler(Looper.getMainLooper());

    public interface Callback<R> {
        void onComplete(R result);

        void onTimeout();

        void onError();
    }

    public <R> void executeAsync(Callable<R> callable, Callback<R> callback) {
        isRunning = true;
        executor.execute(() -> {
            final R result;
            try {
                result = callable.call();


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onComplete(result);
                        isRunning = false;

                    }
                });
            } catch (SocketTimeoutException te) {
                te.printStackTrace();
                Log.w("Request", "TimeOut at " + te.getMessage(), te.getCause());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onTimeout();
                        isRunning = false;

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Log.w("Request", "OverpassException at " + e.getMessage(), e.getCause());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError();
                        isRunning = false;

                    }
                });
            }
        });
    }

    public boolean isRunning() {
        return isRunning;
    }
}
