package com.itsvks.jeditor.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.view.menu.MenuBuilder;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itsvks.jeditor.R;
import com.itsvks.jeditor.BaseActivity;
import com.itsvks.jeditor.databinding.ActivityCrashBinding;
import com.itsvks.jeditor.editor.utils.EditorUtils;
import com.itsvks.jeditor.handler.CrashHandler;
import com.itsvks.jeditor.utils.AppUtils;

import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.schemes.SchemeEclipse;
import io.github.rosemoe.sora.widget.schemes.SchemeVS2019;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class CrashActivity extends BaseActivity {

    private ActivityCrashBinding binding;
    private CodeEditor editor = null;
    private EditorUtils eu = new EditorUtils(this);
    private AppUtils utils = new AppUtils(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashHandler.INSTANCE.init(this);
        binding = ActivityCrashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // utils.setupDynamicColors(this);
        utils.setupCrashActionBar(this);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        editor = binding.editor;
        if (utils.isDarkMode()) {
            editor.setColorScheme(new SchemeVS2019());
        } else {
            editor.setColorScheme(new SchemeEclipse());
        }
        eu.setupCrashEditor(editor);
        getCrashFile();
        if (editor.getText().toString() == "") {
            final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Information");
            builder.setIcon(R.drawable.information);
            builder.setMessage("I have nothing to show you...");
            builder.setCancelable(false);
            builder.setPositiveButton("Okay", (d, w) -> nothing());
            // builder.setNegativeButton("Cancel", null);
            builder.show();
        }
    }

    public void getCrashFile() {
        FileInputStream fis = null;
        try {
            fis = openFileInput("crash-info.log");
            var br = new BufferedReader(new InputStreamReader(fis));
            var sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            if (!sb.toString().isEmpty()) {
                utils.showShortToast("Success");
                editor.setText(
                        sb
                                + "\n\nPlease send these logs to the developer,\nit will help the developer to fix issues\nand improve this app.",
                        null);
            }
        } catch (Exception e) {
            utils.showShortSnackbar(editor, "Failed: " + e);
            e.printStackTrace();
            super.onBackPressed();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu instanceof MenuBuilder) {
            MenuBuilder builder = (MenuBuilder) menu;
            builder.setOptionalIconsVisible(true);
        }
		getMenuInflater().inflate(R.menu.crash_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        var id = item.getItemId();
        if (id == R.id.exit) {
            finishAffinity();
        } else if (id == R.id.clear) {
            FileOutputStream fos = null;
            try {
                fos = openFileOutput("crash-info.log", Context.MODE_PRIVATE);
                utils.showShortSnackbar(binding.getRoot(), "Succeeded");
                editor.setText("Crash log cleared!\n\nGo back and come after a crash.");
            } catch (Exception e) {
                utils.showShortSnackbar(binding.getRoot(), "Failed: " + e);
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void nothing() {
        super.onBackPressed();
    }
}
