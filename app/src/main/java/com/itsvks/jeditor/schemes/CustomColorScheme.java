package com.itsvks.jeditor.schemes;

import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

public class CustomColorScheme extends EditorColorScheme {

    public CustomColorScheme() {
        super(true);
    }

    @Override
    public void applyDefault() {
        super.applyDefault();
        setColor(HIGHLIGHTED_DELIMITERS_BACKGROUND, 0x80263238);
        setColor(CURRENT_LINE, 0x00000050);
        setColor(MATCHED_TEXT_BACKGROUND, 0xFFF8E71C);
        setColor(ATTRIBUTE_VALUE, 0xFFF78C6C);
        setColor(ATTRIBUTE_NAME, 0xFFC792EA);
        setColor(LINE_DIVIDER, 0);
        setColor(LINE_NUMBER, 0xFF546E7A);
        setColor(ANNOTATION, 0xff4ec9b0);
        setColor(FUNCTION_NAME, 0xFF82AAFF);
        setColor(IDENTIFIER_NAME, 0xFFFFCB6B);
        setColor(IDENTIFIER_VAR, 0xFFC3E88D);
        setColor(LITERAL, 0xFFC3E88D);
        setColor(OPERATOR, 0xFF89DDFF);
        setColor(COMMENT, 0xFF546E7A);
        setColor(KEYWORD, 0xFFC792EA);
        setColor(WHOLE_BACKGROUND, 0xff263238);
        setColor(COMPLETION_WND_BACKGROUND, 0xff1e1e1e);
        setColor(COMPLETION_WND_CORNER, 0xff999999);
        setColor(TEXT_NORMAL, 0xFFC3E88D);
        setColor(LINE_NUMBER_BACKGROUND, 0xFF263238);
        setColor(SCROLL_BAR_THUMB, 0xff3e3e42);
        setColor(SCROLL_BAR_THUMB_PRESSED, 0xff9e9e9e);
        setColor(SELECTED_TEXT_BACKGROUND, 0xff3676b8);
        setColor(SELECTION_INSERT, 0xffffffff);
        setColor(SELECTION_HANDLE, 0xffffffff);
        setColor(BLOCK_LINE, 0xFF82AAFF);
        setColor(BLOCK_LINE_CURRENT, 0);
        setColor(NON_PRINTABLE_CHAR, 0xffdddddd);
        setColor(TEXT_SELECTED, 0xffffffff);
        setColor(HIGHLIGHTED_DELIMITERS_FOREGROUND, 0x89DDFF4C);
    }
}
