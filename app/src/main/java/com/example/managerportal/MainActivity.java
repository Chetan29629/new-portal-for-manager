package com.example.managerportal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Handler class is used here to provide 4 seconds delay to pass on Login Activity
//       When this code runs, the Handler schedules the Runnable task to execute after 4 seconds.
//       After the delay, the run() method inside the Runnable is executed.
//       The startActivity method is called, transitioning the user from the MainActivity to the Login activity.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               startActivity(new Intent(MainActivity.this, Login.class));
            }
        },4000);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}