package com.itsvks.jeditor.editor.completion;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import io.github.rosemoe.sora.widget.component.CompletionLayout;
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class CustomCompletionLayout implements CompletionLayout {
    
    private ListView mListView;
    private ProgressBar mProgressBar;
    private RelativeLayout mRelativeLayout;
    private EditorAutoCompletion mEditorAutoCompletion;
    
    Context context;
    
    @Override
    public void setEditorCompletion(EditorAutoCompletion completion) {
        mEditorAutoCompletion = completion;
    }
    
    @Override
    public void ensureListPositionVisible(int position, int increment) {
        mListView.post(() -> {
            while(mListView.getFirstVisiblePosition() + 1 > position && mListView.canScrollList(-1)) {
                performScrollList(increment / 2);
            }
            while(mListView.getFirstVisiblePosition() - 1 < position && mListView.canScrollList(1)) {
                performScrollList(-increment / 2);
            }
        });
    }

    @Override
    public void onApplyColorScheme(EditorColorScheme colorScheme) {
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mEditorAutoCompletion.getContext().getResources().getDisplayMetrics()));
        gd.setStroke(1, colorScheme.getColor(EditorColorScheme.COMPLETION_WND_CORNER));
        gd.setColor(colorScheme.getColor(EditorColorScheme.COMPLETION_WND_BACKGROUND));
        mRelativeLayout.setBackground(gd);
    }
    

    @Override
    public View inflate(Context context) {
        this.context = context;
        RelativeLayout layout = new RelativeLayout(context);
        mListView = new ListView(context);
        layout.addView(mListView, new LinearLayout.LayoutParams(-1, -1));
        mProgressBar = new ProgressBar(context);
        layout.addView(mProgressBar);
        var params = ((RelativeLayout.LayoutParams) mProgressBar.getLayoutParams());
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.width = params.height = (int) valueInDp(30);
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(valueInDp(10));
        layout.setBackground(gd);
        mRelativeLayout = layout;
        mListView.setDividerHeight(0);
        setLoading(true);
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            try {
                mEditorAutoCompletion.select(position);
            } catch (Exception e) {
            	Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        
        return layout;
    }
    

    @Override
    public AdapterView getCompletionList() {
        return mListView;
    }
    

    @Override
    public void setLoading(boolean state) {
        mProgressBar.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
    }
    
    public float valueInDp(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }
    
    private void performScrollList(int offset) {
        var adpView = getCompletionList();

        long down = SystemClock.uptimeMillis();
        var ev = MotionEvent.obtain(down, down, MotionEvent.ACTION_DOWN, 0, 0, 0);
        adpView.onTouchEvent(ev);
        ev.recycle();

        ev = MotionEvent.obtain(down, down, MotionEvent.ACTION_MOVE, 0, offset, 0);
        adpView.onTouchEvent(ev);
        ev.recycle();

        ev = MotionEvent.obtain(down, down, MotionEvent.ACTION_CANCEL, 0, offset, 0);
        adpView.onTouchEvent(ev);
        ev.recycle();
    }
}
