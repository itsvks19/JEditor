package com.itsvks.jeditor.editor.formatter;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.google.googlejavaformat.java.JavaFormatterOptions;

public class GoogleJavaFormatter {
    private String source;

    public GoogleJavaFormatter(String source) {
        this.source = source;
    }

    public String format() {
        var options =
                JavaFormatterOptions.builder()
                        .style(JavaFormatterOptions.Style.AOSP)
                        .formatJavadoc(true)
                        .build();

        var formatter = new Formatter(options);
        try {
            return formatter.formatSourceAndFixImports(source);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return source;
    }
}

