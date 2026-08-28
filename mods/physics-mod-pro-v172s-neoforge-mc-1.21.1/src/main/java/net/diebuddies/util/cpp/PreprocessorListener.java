/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package net.diebuddies.util.cpp;

import javax.annotation.Nonnull;
import net.diebuddies.util.cpp.LexerException;
import net.diebuddies.util.cpp.Source;

public interface PreprocessorListener {
    public void handleWarning(@Nonnull Source var1, int var2, int var3, @Nonnull String var4) throws LexerException;

    public void handleError(@Nonnull Source var1, int var2, int var3, @Nonnull String var4) throws LexerException;

    public void handleSourceChange(@Nonnull Source var1, @Nonnull SourceChangeEvent var2);

    public static enum SourceChangeEvent {
        SUSPEND,
        PUSH,
        POP,
        RESUME;

    }
}

