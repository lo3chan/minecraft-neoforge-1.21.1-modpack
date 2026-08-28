/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 *  javax.annotation.Nonnull
 */
package net.diebuddies.util.cpp;

import java.io.IOException;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import net.diebuddies.util.cpp.Source;

public interface VirtualFile {
    public boolean isFile();

    @Nonnull
    public String getPath();

    @Nonnull
    public String getName();

    @CheckForNull
    public VirtualFile getParentFile();

    @Nonnull
    public VirtualFile getChildFile(String var1);

    @Nonnull
    public Source getSource() throws IOException;
}

