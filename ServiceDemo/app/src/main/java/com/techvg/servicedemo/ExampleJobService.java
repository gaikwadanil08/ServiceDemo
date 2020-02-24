package com.techvg.servicedemo;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ExampleJobService extends JobService {

    public static final String TAG = "ExampleJobService";
    private static final String PREF_NAME = "Count Details ";
    private boolean jobCancelled = false;

    DatabaseReference databaseReference;
    List pojoList;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    int cnt = 0;
    int count = 0;

    @Override
    public boolean onStartJob(JobParameters params) {

        Log.d(TAG, "Job Started");
        doBackgroundWork(params);

        databaseReference = FirebaseDatabase.getInstance().getReference("data");
        pojoList = new ArrayList<>();

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        cnt = sharedPreferences.getInt("count", 0);
        Log.d(TAG, "shared Count: "+cnt);

        getAmount();

        if (count > cnt) {
            Toast.makeText(getApplicationContext(), "found Count", Toast.LENGTH_LONG).show();
        }

        return true;
    }

    private void doBackgroundWork(final JobParameters params) {

        Log.d("sample", "sample");
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    Log.d(TAG, "run : " + i);
                    getAmount();
                    if (jobCancelled) {
                        return;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "Job Finished");
                jobFinished(params, false);
            }
        }).start();*/


    }

    @Override
    public boolean onStopJob(JobParameters params) {

        Log.d(TAG, "Job Cancelled before completion. ");
        jobCancelled = true;

        return true;
    }

    public void getAmount() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                pojoList.clear();

                for (DataSnapshot listSnapshot : dataSnapshot.getChildren()) {

                    count++;
                }
                Log.d("Count", " =1234= " + count);
                editor.putInt("count", count);
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
