package com.itsvks.jeditor.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.AttrRes;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.itsvks.jeditor.R;

import io.github.rosemoe.sora.text.ContentCreator;
import io.github.rosemoe.sora.widget.CodeEditor;

import java.io.IOException;

public class AppUtils {

    Context ctx;

    public AppUtils(Context ctx) {
        this.ctx = ctx;
    }

    public void showShortSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    public void showLongSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public void showIndefiniteSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).show();
    }

    public void setupDynamicColors(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (DynamicColors.isDynamicColorAvailable()) {
                DynamicColors.applyToActivityIfAvailable(activity);
            } else {
                showShortToast("Dynamic Colors not available");
            }
        }
    }

    public void showShortToast(String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
    }

    public void setupSplashActionBar(AppCompatActivity activity) {
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().hide();
        }
    }

    public void setupEditorActionBar(AppCompatActivity activity) {
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setElevation(10);
            // activity.getSupportActionBar().setBackgroundDrawable(actionBarBackground());

        }
    }

    public void setupCrashActionBar(AppCompatActivity activity) {
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setElevation(10);
            // activity.getSupportActionBar().setBackgroundDrawable(actionBarBackground());
            activity.getSupportActionBar().setTitle("Crash logs");
            // activity.getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            // activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setIcon(R.drawable.script_text);
        }
    }

    public void setupPreferencesActionBar(AppCompatActivity activity) {
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setElevation(10);
            // activity.getSupportActionBar().setBackgroundDrawable(actionBarBackground());
            activity.getSupportActionBar().setTitle("Preferences");
            // activity.getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            // activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setIcon(R.drawable.cog);
        }
    }

    public GradientDrawable actionBarBackground() {
        GradientDrawable gd = new GradientDrawable();

        // gd.setCornerRadius((int) valueInDp(8));
        // gd.setStroke(1, 0xFF1E1E2D);
        if (isDarkMode()) {
            gd.setColor(R.color.md_theme_dark_primaryContainer);
        } else {
            gd.setColor(R.color.md_theme_light_primaryContainer);
        }
        return gd;
    }

    public float valueInDp(float value) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, ctx.getResources().getDisplayMetrics());
    }

    public TypedValue getTypedValueForAttr(@AttrRes int attrRes) {
        TypedValue typedValue = new TypedValue();
        ctx.getTheme().resolveAttribute(attrRes, typedValue, true);
        return typedValue;
    }

    public boolean isDarkMode() {
        int currentNightMode =
                ctx.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }
}
