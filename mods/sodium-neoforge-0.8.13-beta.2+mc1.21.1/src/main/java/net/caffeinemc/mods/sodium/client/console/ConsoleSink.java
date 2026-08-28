/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package net.caffeinemc.mods.sodium.client.console;

import net.caffeinemc.mods.sodium.client.console.message.MessageLevel;
import org.jetbrains.annotations.NotNull;

public interface ConsoleSink {
    public void logMessage(@NotNull MessageLevel var1, @NotNull String var2, boolean var3, double var4);
}

