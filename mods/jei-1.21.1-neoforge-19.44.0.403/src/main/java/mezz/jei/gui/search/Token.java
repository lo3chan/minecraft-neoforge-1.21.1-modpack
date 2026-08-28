/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.gui.search;

public record Token(String text, boolean exclusion) {
    public boolean isEmpty() {
        return this.text.isEmpty();
    }
}

