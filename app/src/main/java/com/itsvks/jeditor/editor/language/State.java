package com.itsvks.jeditor.editor.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class State {

    public int state = 0;

    public boolean hasBraces = false;

    public List<String> identifiers = null;

    public void addIdentifier(CharSequence idt) {
        if (identifiers == null) {
            identifiers = new ArrayList<>();
        }
        if (idt instanceof String) {
            identifiers.add((String) idt);
        } else {
            identifiers.add(idt.toString());
        }
    }

    @Override
    public boolean equals(Object o) {
        // `identifiers` is ignored because it is unrelated to tokenization for next line
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state1 = (State) o;
        return state == state1.state && hasBraces == state1.hasBraces;
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, hasBraces);
    }
}
