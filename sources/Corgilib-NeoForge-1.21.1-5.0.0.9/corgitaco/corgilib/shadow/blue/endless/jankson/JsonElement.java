package corgitaco.corgilib.shadow.blue.endless.jankson;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public abstract class JsonElement implements Cloneable {
   public abstract JsonElement clone();

   public String toJson() {
      return this.toJson(false, false, 0);
   }

   public String toJson(boolean comments, boolean newlines) {
      return this.toJson(comments, newlines, 0);
   }

   @Deprecated
   public abstract String toJson(boolean var1, boolean var2, int var3);

   public String toJson(JsonGrammar grammar, int depth) {
      StringWriter w = new StringWriter();

      try {
         this.toJson(w, grammar, depth);
         w.flush();
         return w.toString();
      } catch (IOException var5) {
         throw new RuntimeException(var5);
      }
   }

   public String toJson(JsonGrammar grammar) {
      return this.toJson(grammar, 0);
   }

   public abstract void toJson(Writer var1, JsonGrammar var2, int var3) throws IOException;
}
