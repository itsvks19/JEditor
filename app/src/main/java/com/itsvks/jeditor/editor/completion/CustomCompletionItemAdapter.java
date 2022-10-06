package com.itsvks.jeditor.editor.completion;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itsvks.jeditor.databinding.CustomCompletionResultItemBinding;

import io.github.rosemoe.sora.widget.component.EditorCompletionAdapter;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import org.eclipse.lsp4j.CompletionItem;

public class CustomCompletionItemAdapter extends EditorCompletionAdapter {

    private CustomCompletionResultItemBinding binding;
    private CompletionItem item;

    @Override
    public int getItemHeight() {
        return (int)
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        48,
                        getContext().getResources().getDisplayMetrics());
    }

    @Override
    protected View getView(int pos, View view, ViewGroup parent, boolean isCurrentCursorPosition) {
        binding =
                CustomCompletionResultItemBinding.inflate(
                        LayoutInflater.from(getContext()), parent, false);
        view = binding.getRoot();
        var item = getItem(pos);

        binding.resultItemLabel.setText(item.label);
        binding.resultItemLabel.setTextColor(
                getThemeColor(EditorColorScheme.COMPLETION_WND_TEXT_PRIMARY));

        binding.resultItemDesc.setText(item.desc);
        binding.resultItemDesc.setTextColor(
                getThemeColor(EditorColorScheme.COMPLETION_WND_TEXT_SECONDARY));

        view.setTag(pos);
        if (isCurrentCursorPosition) {
            view.setBackgroundColor(getThemeColor(EditorColorScheme.COMPLETION_WND_ITEM_CURRENT));
        } else {
            view.setBackgroundColor(0);
        }
        binding.resultItemImage.setText(item.desc.subSequence(0, 1));

        return view;
    }
}
