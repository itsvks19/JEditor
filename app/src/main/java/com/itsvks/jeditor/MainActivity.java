package com.itsvks.jeditor;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.*;

import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.snackbar.Snackbar;
// import com.itsaky.androidide.logsender.LogSender;
import com.itsvks.jeditor.activities.EditorActivity;
import com.itsvks.jeditor.databinding.ActivityMainBinding;
import com.itsvks.jeditor.handler.CrashHandler;

import com.itsvks.jeditor.utils.AppUtils;
import com.itsvks.jeditor.R;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Timer timer = new Timer();
    private TimerTask main;
    private AppUtils utils = new AppUtils(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // LogSender.startLogging(this);
        CrashHandler.INSTANCE.init(this);
        // Inflate and get instance of binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // set content view to binding's root
        setContentView(binding.getRoot());
        // utils.setupDynamicColors(this);
        utils.setupSplashActionBar(this);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        main =
                new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent splash = new Intent();
                                        splash.setClass(
                                                getApplicationContext(), EditorActivity.class);
                                        startActivity(splash);
                                        finish();
                                    }
                                });
                    }
                };
        timer.schedule(main, 3000);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        int currentNightMode = configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                break;
        }
    }
}

