package com.itsvks.jeditor.editor.language;

import static io.github.rosemoe.sora.lang.styling.StylesUtils.checkNoCompletion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.github.rosemoe.sora.lang.QuickQuoteHandler;
import io.github.rosemoe.sora.lang.QuickQuoteHandler.HandleResult;
import io.github.rosemoe.sora.lang.styling.Styles;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.TextRange;

public class JavaQuoteHandler implements QuickQuoteHandler {

    @NonNull
    @Override
    public HandleResult onHandleTyping(
            @NonNull String candidateCharacter,
            @NonNull Content text,
            @NonNull TextRange cursor,
            @Nullable Styles style) {
        if (!checkNoCompletion(style, cursor.getStart())
                && !checkNoCompletion(style, cursor.getEnd())
                && "\"".equals(candidateCharacter)
                && cursor.getStart().line == cursor.getEnd().line) {
            text.insert(cursor.getStart().line, cursor.getStart().column, "\"");
            text.insert(cursor.getEnd().line, cursor.getEnd().column + 1, "\"");
            return new HandleResult(
                    true,
                    new TextRange(
                            text.getIndexer().getCharPosition(cursor.getStartIndex() + 1),
                            text.getIndexer().getCharPosition(cursor.getEndIndex() + 1)));
        }
        return HandleResult.NOT_CONSUMED;
    }
}
