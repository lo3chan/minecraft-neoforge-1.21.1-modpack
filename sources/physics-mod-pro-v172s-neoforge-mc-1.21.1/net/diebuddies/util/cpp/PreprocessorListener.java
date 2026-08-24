package net.diebuddies.util.cpp;

import javax.annotation.Nonnull;

public interface PreprocessorListener {
   void handleWarning(@Nonnull Source var1, int var2, int var3, @Nonnull String var4) throws LexerException;

   void handleError(@Nonnull Source var1, int var2, int var3, @Nonnull String var4) throws LexerException;

   void handleSourceChange(@Nonnull Source var1, @Nonnull PreprocessorListener.SourceChangeEvent var2);

   public static enum SourceChangeEvent {
      SUSPEND,
      PUSH,
      POP,
      RESUME;
   }
}
