package com.itsvks.jeditor;

import android.app.Application;
import android.content.res.Configuration;
import com.google.android.material.color.DynamicColors;
import com.itsvks.jeditor.handler.CrashHandler;

public class JEditorApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
		DynamicColors.applyToActivitiesIfAvailable(this);
    }
	
	public boolean isDarkMode() {
        int currentNightMode =
                getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }
}

