/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package net.diebuddies.util.cpp;

import javax.annotation.Nonnull;
import net.diebuddies.util.cpp.VirtualFile;

public interface VirtualFileSystem {
    @Nonnull
    public VirtualFile getFile(@Nonnull String var1);

    @Nonnull
    public VirtualFile getFile(@Nonnull String var1, @Nonnull String var2);
}

