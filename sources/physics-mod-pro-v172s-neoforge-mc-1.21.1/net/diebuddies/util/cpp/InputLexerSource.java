package net.diebuddies.util.cpp;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import javax.annotation.Nonnull;

public class InputLexerSource extends LexerSource {
   @Deprecated
   public InputLexerSource(@Nonnull InputStream input) {
      this(input, Charset.defaultCharset());
   }

   public InputLexerSource(@Nonnull InputStream input, Charset charset) {
      this(new InputStreamReader(input, charset));
   }

   public InputLexerSource(@Nonnull Reader input, boolean ppvalid) {
      super(input, true);
   }

   public InputLexerSource(@Nonnull Reader input) {
      this(input, true);
   }

   @Override
   public String getPath() {
      return "<standard-input>";
   }

   @Override
   public String getName() {
      return "standard input";
   }

   @Override
   public String toString() {
      return String.valueOf(this.getPath());
   }
}
