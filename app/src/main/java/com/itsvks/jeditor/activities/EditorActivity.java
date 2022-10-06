package com.itsvks.jeditor.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itsvks.jeditor.BaseActivity;
import com.itsvks.jeditor.R;
import com.itsvks.jeditor.databinding.ActivityEditorBinding;
import com.itsvks.jeditor.editor.formatter.GoogleJavaFormatter;
import com.itsvks.jeditor.editor.language.JavaLanguage;
import com.itsvks.jeditor.editor.utils.EditorUtils;
import com.itsvks.jeditor.handler.CrashHandler;
import com.itsvks.jeditor.managers.PreferencesManager;
import com.itsvks.jeditor.schemes.CustomColorScheme;
import com.itsvks.jeditor.utils.AppUtils;

import io.github.rosemoe.sora.event.ContentChangeEvent;
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme;
import io.github.rosemoe.sora.text.ContentCreator;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.EditorSearcher;
import io.github.rosemoe.sora.widget.SymbolInputView;
import io.github.rosemoe.sora.widget.component.Magnifier;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.function.Supplier;
import java.util.regex.PatternSyntaxException;
import org.eclipse.tm4e.core.registry.IThemeSource;

public class EditorActivity extends BaseActivity {

    private String formattedCode;

    private ActivityEditorBinding binding;
    private CodeEditor editor;
    private SymbolInputView symbolInput;
    private EditorUtils eu = new EditorUtils(this);
    private AppUtils utils = new AppUtils(this);
    private PreferencesActivity prefs = new PreferencesActivity();
    MenuItem undo = null;
    MenuItem redo = null;
    MenuItem save = null;
    MenuItem closeFile = null;
    Supplier<Integer> port = () -> randomPort();

    final Runnable updt = () -> updateBtnPos();

    private Uri filePath = null;
    private String currentFileName = null;
    private File currentFile = null;

    private static final int PICK_FILE = 998;
    private static final int CREATE_FILE = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Crash Handler for app
        CrashHandler.INSTANCE.init(this);
        binding = ActivityEditorBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        utils.setupEditorActionBar(this);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        updateSearchBtnState();
        editor = binding.editor;
        symbolInput = binding.symbolInput;
        eu.setupEditor(editor);
        editor.setEditorLanguage(new JavaLanguage());
        editor.setColorScheme(new CustomColorScheme());

        /* try {
            setEditorTmTheme(getAssets().open("Material-Theme.tmTheme"), "Material-Theme.tmTheme");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            var language =
                    TextMateLanguage.create(
                            IGrammarSource.fromInputStream(
                                    getAssets().open("textmate/java/syntaxes/java.tmLanguage.json"),
                                    "java.tmLanguage.json",
                                    null),
                            new InputStreamReader(
                                    getAssets().open("textmate/java/language-configuration.json")),
                            ((TextMateColorScheme) editor.getColorScheme()).getThemeSource());
            
        } catch (IOException e) {
            e.printStackTrace();
        } */

        openAssetsFile("Sample.java");
        getSupportActionBar().setSubtitle("Sample.java");
        editor.subscribeEvent(
                ContentChangeEvent.class,
                ((event, subscribe) -> {
                    handleUndoRedo(event);
                    if (currentFileName != null) {
                        getSupportActionBar().setSubtitle(currentFileName + "*");
                    }
                }));
        symbolInput.bindEditor(editor);
        eu.setupSymbols(symbolInput);
        updateBtnPos();
        prefsEditor();
        binding.searchEditor.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence cs, int i1, int i2, int i3) {}

                    @Override
                    public void onTextChanged(CharSequence cs, int i1, int i2, int i3) {
                        updateSearchBtnState();
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (!editable.toString().isEmpty()) {
                            try {
                                binding.editor
                                        .getSearcher()
                                        .search(
                                                editable.toString(),
                                                new EditorSearcher.SearchOptions(true, true));
                            } catch (PatternSyntaxException e) {
                                e.printStackTrace();
                            }
                        } else {
                            binding.editor.getSearcher().stopSearch();
                        }
                    }
                });
        binding.replaceEditor.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence cs, int i1, int i2, int i3) {}

                    @Override
                    public void onTextChanged(CharSequence cs, int i1, int i2, int i3) {
                        updateSearchBtnState();
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {}
                });
    }

    public int randomPort() {
        ServerSocket serverSocket;
        int port = 0;
        try {
            serverSocket = new ServerSocket(0);
            port = serverSocket.getLocalPort();
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return port;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu instanceof MenuBuilder) {
            MenuBuilder builder = (MenuBuilder) menu;
            builder.setOptionalIconsVisible(true);
        }
        getMenuInflater().inflate(R.menu.main_menu, menu);
        undo = menu.findItem(R.id.undo);
        redo = menu.findItem(R.id.redo);
        save = menu.findItem(R.id.save);
        closeFile = menu.findItem(R.id.close_file);
        undo.setEnabled(false);
        redo.setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        var id = item.getItemId();
        updateBtnPos();
        if (id == R.id.open_crash_log) {
            startActivity(new Intent(this, CrashActivity.class));
        } else if (id == R.id.clear_crash_log) {
            FileOutputStream fos = null;
            try {
                fos = openFileOutput("crash-info.log", Context.MODE_PRIVATE);
                utils.showShortToast("Success");
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
        } else if (id == R.id.about_us) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.about_us)
                    .setIcon(R.drawable.information)
                    .setMessage("This is my information.")
                    .setCancelable(false)
                    .setPositiveButton(
                            "Okay", (d, w) -> utils.showLongToast("Thank you for knowing me."))
                    // .setNegativeButton("Cancel", null)
                    .show();
        } else if (id == R.id.settings) {
            startActivity(new Intent(this, PreferencesActivity.class));
        } else if (id == R.id.undo) {
            undo();
        } else if (id == R.id.redo) {
            redo();
        } else if (id == R.id.search_pm) {
            search_pm();
            if (binding.searchPanel.getVisibility() == View.GONE) {
                item.setChecked(false);
            } else {
                item.setChecked(true);
            }
        } else if (id == R.id.search_am) {
            search_am();
        } else if (id == R.id.open_file) {
            openFile(Uri.fromFile(Environment.getExternalStorageDirectory()));
        } else if (id == R.id.new_file) {
            createFile(Uri.fromFile(Environment.getExternalStorageDirectory()), "NewFile.java");
        } else if (id == R.id.save) {
            if (filePath != null) {
                saveFile(filePath, binding.editor.getText().toString());
                if (getSupportActionBar().getSubtitle().toString().endsWith("*")) {
                    getSupportActionBar().setSubtitle(currentFileName);
                }
                item.setEnabled(false);
                item.getIcon().setAlpha(130);
                utils.showShortSnackbar(binding.getRoot(), "Saved to " + currentFile);
            } else {
                utils.showShortSnackbar(binding.getRoot(), "Error");
            }
        } else if (id == R.id.close_file) {
            if (filePath != null) {
                saveFile(filePath, binding.editor.getText().toString());
            }
            filePath = null;
            currentFileName = null;
            getSupportActionBar().setSubtitle("");
            editor.setText("");
            utils.showShortSnackbar(binding.getRoot(), "Closed");
        } else if (id == R.id.format) {
            try {
                var formatter = new GoogleJavaFormatter(editor.getText().toString());
                formattedCode = formatter.format();
                editor.setText(formattedCode);
            } catch (NoClassDefFoundError e) {
                utils.showShortToast("Failed");
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Really?")
                .setIcon(R.drawable.logout)
                .setMessage("Do you really want to exit?")
                .setCancelable(true)
                .setPositiveButton("No", (d, w) -> d.dismiss())
                .setNegativeButton("Yes", (d, w) -> really())
                .show();
    }

    public void really() {
        finishAffinity();
    }

    public CodeEditor getEditor() {
        return this.editor;
    }

    public void setEditor(io.github.rosemoe.sora.widget.CodeEditor editor) {
        this.editor = editor;
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefsEditor();
    }

    @Override
    protected void onStart() {
        super.onStart();
        prefsEditor();
    }

    public void setEditorTmTheme(InputStream is, String fileName) {
        try {
            var colorScheme = editor.getColorScheme();
            IThemeSource themeSource;
            if (!(colorScheme instanceof TextMateColorScheme)) {
                themeSource = IThemeSource.fromInputStream(is, fileName, null);

                colorScheme = TextMateColorScheme.create(themeSource);
            }

            editor.setColorScheme(colorScheme);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openAssetsFile(String name) {
        new Thread() {
            @Override
            public void run() {
                try {
                    var text = ContentCreator.fromStream(getAssets().open(name));
                    runOnUiThread(() -> editor.setText(text, null));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        updateBtnPos();
    }

    private void updateBtnPos() {
        if (undo == null || redo == null || save == null || closeFile == null) {
            return;
        }
        if (editor.canUndo()) {
            undo.setEnabled(true);
            undo.getIcon().setAlpha(255);
        } else {
            undo.setEnabled(false);
            undo.getIcon().setAlpha(130);
        }
        if (editor.canRedo()) {
            redo.setEnabled(true);
            redo.getIcon().setAlpha(255);
        } else {
            redo.setEnabled(false);
            redo.getIcon().setAlpha(130);
        }
        if (filePath == null) {
            closeFile.setEnabled(false);
            closeFile.getIcon().setAlpha(130);
        } else {
            closeFile.setEnabled(true);
            closeFile.getIcon().setAlpha(255);
        }
        if (!getSupportActionBar().getSubtitle().toString().endsWith("*")) {
            save.setEnabled(false);
            save.getIcon().setAlpha(130);
        } else {
            save.setEnabled(true);
            save.getIcon().setAlpha(255);
        }
    }

    private void handleUndoRedo(@NonNull ContentChangeEvent event) {
        if (event.getAction() == ContentChangeEvent.ACTION_INSERT) {
            new Handler(Looper.getMainLooper()).postDelayed(updt, 10);
        } else if (event.getAction() == ContentChangeEvent.ACTION_DELETE) {
            new Handler(Looper.getMainLooper()).postDelayed(updt, 10);
        } else if (event.getAction() == ContentChangeEvent.ACTION_SET_NEW_TEXT) {
            new Handler(Looper.getMainLooper()).postDelayed(updt, 10);
        }
    }

    public void prefsEditor() {
        editor.setWordwrap(prefs.getBoolean(PreferencesManager.KEY_WORDWRAP, false, this));
        editor.setLineNumberEnabled(prefs.getBoolean(PreferencesManager.KEY_SHOWLINE, true, this));
        editor.setPinLineNumber(prefs.getBoolean(PreferencesManager.KEY_PINLINE, false, this));
        editor.getProps().useICULibToSelectWords =
                prefs.getBoolean(PreferencesManager.KEY_USEICU, false, this);
        editor.getComponent(Magnifier.class)
                .setEnabled(prefs.getBoolean(PreferencesManager.KEY_MAGNIFIER, true, this));
        editor.setLigatureEnabled(prefs.getBoolean(PreferencesManager.KEY_LIGATURE, false, this));
    }

    public void redo() {
        if (editor.canRedo()) {
            editor.redo();
        }
    }

    public void undo() {
        if (editor.canUndo()) {
            editor.undo();
        }
    }

    public void search_pm() {
        if (binding.searchPanel.getVisibility() == View.GONE) {
            binding.replaceEditor.setText("");
            binding.searchEditor.setText("");
            binding.editor.getSearcher().stopSearch();
            binding.searchPanel.setVisibility(View.VISIBLE);
        } else {
            binding.editor.getSearcher().stopSearch();
            binding.searchPanel.setVisibility(View.GONE);
        }
    }

    public void search_am() {
        binding.replaceEditor.setText("");
        binding.searchEditor.setText("");
        binding.editor.getSearcher().stopSearch();
        binding.editor.beginSearchMode();
    }

    public void gotoNext(View v) {
        try {
            binding.editor.getSearcher().gotoNext();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void gotoLast(View v) {
        try {
            binding.editor.getSearcher().gotoPrevious();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void replace(View v) {
        try {
            if (binding.replaceEditor.getText().toString() != "") {
                binding.editor
                        .getSearcher()
                        .replaceThis(binding.replaceEditor.getText().toString());
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void replaceAll(View v) {
        try {
            if (binding.replaceEditor.getText().toString() != "") {
                binding.editor.getSearcher().replaceAll(binding.replaceEditor.getText().toString());
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void openFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, PICK_FILE);
    }

    private void createFile(Uri pickerInitialUri, String name) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, name);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, CREATE_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == PICK_FILE && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                // Perform operations on the document using its URI.
                readFile(uri);
            }
        }
        if (requestCode == CREATE_FILE && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                // Perform operations on the document using its URI.
                readFile(uri);
            }
        }
    }

    public void readFile(Uri uri) {
        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(uri);
            var br = new BufferedReader(new InputStreamReader(is));
            var sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            filePath = uri;
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            int fileName = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            currentFileName = cursor.getString(fileName);
            currentFile = new File(filePath.getPath());
            if (!isJavaFile()) {
                filePath = null;
                editor.setText("Not a Java file");
                editor.setEditable(false);
                editor.setTextSize(18);
                editor.setScalable(false);
                getSupportActionBar().setSubtitle("");
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Error")
                        .setMessage("Reason: Not a Java file")
                        .setIcon(R.drawable.close)
                        .setCancelable(false)
                        .setPositiveButton(
                                "Okay",
                                (d, w) -> {
                                    d.dismiss();
                                })
                        .show();
            } else {
                editor.setEditable(true);
                editor.setScalable(true);
                editor.setText(sb);
                getSupportActionBar().setSubtitle(currentFileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        updateBtnPos();
    }

    public void saveFile(Uri uri, String data) {
        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
            fos.write(data.getBytes());
            fos.close();
            pfd.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateBtnPos();
    }

    public boolean isJavaFile() {
        return (currentFileName.endsWith(".java")
                || currentFileName.endsWith(".Java")
                || currentFileName.endsWith(".JAVA")
                || currentFileName.endsWith(".jav"));
    }

    public void updateSearchBtnState() {
        if (binding.replaceEditor.getText().toString().isEmpty()) {
            binding.replace.setEnabled(false);
            binding.replaceAll.setEnabled(false);
        } else if (!binding.replaceEditor.getText().toString().isEmpty()
                && binding.searchEditor.getText().toString().isEmpty()) {
            binding.replace.setEnabled(false);
            binding.replaceAll.setEnabled(false);
        } else {
            binding.replace.setEnabled(true);
            binding.replaceAll.setEnabled(true);
        }
        if (binding.searchEditor.getText().toString().isEmpty()
                && binding.searchEditor.getText().toString().isEmpty()) {
            binding.last.setEnabled(false);
            binding.next.setEnabled(false);
        } else {
            binding.last.setEnabled(true);
            binding.next.setEnabled(true);
        }
    }
}
