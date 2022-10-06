package com.itsvks.jeditor.editor.panel;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.itsvks.jeditor.R;

import io.github.rosemoe.sora.event.EventReceiver;
import io.github.rosemoe.sora.event.HandleStateChangeEvent;
import io.github.rosemoe.sora.event.ScrollEvent;
import io.github.rosemoe.sora.event.SelectionChangeEvent;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.EditorTouchEventHandler;
import io.github.rosemoe.sora.widget.component.EditorBuiltinComponent;
import io.github.rosemoe.sora.widget.component.EditorTextActionWindow;
/**
 * Coming soon...
 *
 */
public class CustomTextComposePanel extends EditorTextActionWindow
        implements View.OnClickListener,
                EventReceiver<SelectionChangeEvent>,
                EditorBuiltinComponent {
    private static final long DELAY = 200;
    private final CodeEditor editor;
    private final ImageButton pasteBtn;
    private final ImageButton copyBtn;
    private final ImageButton cutBtn;
    private final View rootView;
    private final EditorTouchEventHandler handler;
    private long lastScroll;
    private int lastPosition;
    private boolean enabled = true;

    public CustomTextComposePanel(CodeEditor editor) {
        super(editor);
        this.editor = editor;
        handler = editor.getEventHandler();
        // Since popup window does provide decor view, we have to pass null to this method
        @SuppressLint("InflateParams")
        View root =
                LayoutInflater.from(editor.getContext()).inflate(R.layout.custom_text_compose_panel, null);
        ImageButton selectAll = root.findViewById(R.id.panel_btn_select_all);
        ImageButton cut = root.findViewById(R.id.panel_btn_cut);
        ImageButton copy = root.findViewById(R.id.panel_btn_copy);
        pasteBtn = root.findViewById(R.id.panel_btn_paste);
        copyBtn = copy;
        cutBtn = cut;
        selectAll.setOnClickListener(this);
        cut.setOnClickListener(this);
        copy.setOnClickListener(this);
        pasteBtn.setOnClickListener(this);
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(10 * editor.getDpUnit());
        gd.setColor(0xffffffff);
        root.setBackground(gd);
        setContentView(root);
        setSize(0, (int) (this.editor.getDpUnit() * 48));
        rootView = root;
        editor.subscribeEvent(SelectionChangeEvent.class, this);
    }
}

