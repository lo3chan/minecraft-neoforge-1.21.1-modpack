package net.diebuddies.util.cpp;

import java.io.IOException;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public interface VirtualFile {
   boolean isFile();

   @Nonnull
   String getPath();

   @Nonnull
   String getName();

   @CheckForNull
   VirtualFile getParentFile();

   @Nonnull
   VirtualFile getChildFile(String var1);

   @Nonnull
   Source getSource() throws IOException;
}
