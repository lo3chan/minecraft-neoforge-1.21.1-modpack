/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 */
package mezz.jei.common.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class TextHistory {
    private static final int MAX_HISTORY = 100;
    private final List<String> history = new LinkedList<String>();

    public boolean add(String currentText) {
        if (!currentText.isEmpty()) {
            this.history.remove(currentText);
            this.history.add(currentText);
            if (this.history.size() > 100) {
                this.history.removeFirst();
            }
            return true;
        }
        return false;
    }

    public Optional<String> get(Direction direction, String currentText) {
        return switch (direction.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.getNext(currentText);
            case 1 -> this.getPrevious(currentText);
        };
    }

    public Optional<String> getPrevious(String currentText) {
        int historyIndex = this.history.indexOf(currentText);
        if (historyIndex < 0) {
            historyIndex = this.add(currentText) ? this.history.size() - 1 : this.history.size();
        }
        if (historyIndex <= 0) {
            return Optional.empty();
        }
        String value = this.history.get(historyIndex - 1);
        return Optional.of(value);
    }

    public Optional<String> getNext(String currentText) {
        int historyIndex = this.history.indexOf(currentText);
        if (historyIndex < 0) {
            return Optional.empty();
        }
        String historyString = historyIndex + 1 < this.history.size() ? this.history.get(historyIndex + 1) : "";
        return Optional.of(historyString);
    }

    public static enum Direction {
        NEXT,
        PREVIOUS;

    }
}

