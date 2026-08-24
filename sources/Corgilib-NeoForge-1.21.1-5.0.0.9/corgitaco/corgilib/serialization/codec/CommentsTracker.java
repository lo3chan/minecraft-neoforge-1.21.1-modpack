package corgitaco.corgilib.serialization.codec;

import org.jetbrains.annotations.Nullable;

public interface CommentsTracker {
   void addComment(String var1, String var2);

   @Nullable
   String getComment(String var1);
}
