package net.diebuddies.util.cpp;

import javax.annotation.Nonnull;

public interface VirtualFileSystem {
   @Nonnull
   VirtualFile getFile(@Nonnull String var1);

   @Nonnull
   VirtualFile getFile(@Nonnull String var1, @Nonnull String var2);
}
