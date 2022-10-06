package com.itsvks.jeditor.editor.utils;

import android.content.Context;
import android.graphics.Typeface;

import com.itsvks.jeditor.activities.PreferencesActivity;
import com.itsvks.jeditor.editor.completion.CustomCompletionItemAdapter;
import com.itsvks.jeditor.editor.completion.CustomCompletionLayout;
import com.itsvks.jeditor.managers.PreferencesManager;
import com.itsvks.jeditor.schemes.CustomColorScheme;

import io.github.rosemoe.sora.lang.EmptyLanguage;
import io.github.rosemoe.sora.langs.java.JavaLanguage;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.SymbolInputView;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.rosemoe.sora.widget.component.Magnifier;
import io.github.rosemoe.sora.widget.schemes.SchemeEclipse;

public class EditorUtils {

    Context context;
    // PreferencesActivity prefs = new PreferencesActivity();
    // private String tab = "    ";

    public EditorUtils(Context context) {
        this.context = context;
    }

    public void setupEditor(CodeEditor editor) {

        editor.setTypefaceText(jetBrainsMono());
        editor.setTypefaceLineNumber(jetBrainsMono());
        editor.setCursorAnimationEnabled(true);
        editor.setCursorBlinkPeriod(600);
        editor.setLnTip("LINE: ");
        editor.setBlockLineWidth(1);
        editor.setDisplayLnPanel(true);
        editor.setAutoCompletionItemAdapter(new CustomCompletionItemAdapter());
        editor.getComponent(EditorAutoCompletion.class)
                .setAdapter(new CustomCompletionItemAdapter());
        editor.getComponent(EditorAutoCompletion.class).setLayout(new CustomCompletionLayout());
        editor.setCompletionWndPositionMode(CodeEditor.WINDOW_POS_MODE_FULL_WIDTH_ALWAYS);
        // editor.setText("// Let's code with JEditor");
        editor.setLineNumberEnabled(true);
        // editor.setEditorLanguage(new JavaLanguage());
    }

    public void setupCrashEditor(CodeEditor editor) {
        editor.setTypefaceText(jetBrainsMono());
        editor.setTypefaceLineNumber(jetBrainsMono());
        editor.setEditorLanguage(new EmptyLanguage());
        editor.setTextSize(18);
        editor.setEditable(false);
        editor.setLnTip("LINE: ");
    }

    public void setupSymbols(SymbolInputView symbolInput) {
        symbolInput.addSymbols(
                new String[] {
                    "➜", "{", "}", "(", ")", ";", "=", "\"", "|", "&", "!", "[", "]", "<", ">", "+",
                    "-", "/", "*", "?", ":", "_"
                },
                new String[] {
                    "\t", "{}", "}", "()", ")", ";", "=", "\"\"", "|", "&", "!", "[]", "]", "<>",
                    ">", "+", "-", "/", "*", "?", ":", "_"
                });
        symbolInput.forEachButton(
                (b) -> {
                    b.setTypeface(jetBrainsMono());
                });
    }

    public Typeface jetBrainsMono() {
        return Typeface.createFromAsset(context.getAssets(), "JetBrainsMono-Regular.ttf");
    }
}
