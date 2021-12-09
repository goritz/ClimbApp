package com.geoit.climbapp.overpass;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class TaskExecutor {


    private boolean isRunning=false;

        private final Executor executor = Executors.newSingleThreadExecutor(); // change according to your requirements
        private final Handler handler = new Handler(Looper.getMainLooper());

        public interface Callback<R> {
            void onComplete(R result);
        }

        public <R> void executeAsync(Callable<R> callable, Callback<R> callback) {
            isRunning=true;
            executor.execute(() -> {
                final R result;
                try {
                    result = callable.call();


                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onComplete(result);
                            isRunning=false;

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }

    public boolean isRunning() {
        return isRunning;
    }
}
