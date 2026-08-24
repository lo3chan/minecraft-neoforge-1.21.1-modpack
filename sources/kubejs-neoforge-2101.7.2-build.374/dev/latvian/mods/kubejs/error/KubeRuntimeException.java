package dev.latvian.mods.kubejs.error;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.MutedError;
import dev.latvian.mods.rhino.RhinoException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class KubeRuntimeException extends RuntimeException implements MutedError {
   private SourceLine sourceLine = SourceLine.UNKNOWN;
   private Map<String, Object> customData;

   public KubeRuntimeException(String m) {
      super(m);
   }

   public KubeRuntimeException(String m, Throwable cause) {
      super(m, cause);
   }

   public KubeRuntimeException(Throwable cause) {
      super(cause);
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      String message = this.getLocalizedMessage();
      if (message != null) {
         sb.append(message);
      } else {
         sb.append(this.getClass().getName());
      }

      for (Throwable c = this.getCause(); c != null; c = c.getCause()) {
         sb.append(" - ").append(c);
      }

      return sb.toString();
   }

   public KubeRuntimeException source(SourceLine sourceLine) {
      if (this.sourceLine.isUnknown()) {
         this.sourceLine = sourceLine;
      }

      return this;
   }

   public KubeRuntimeException customData(String key, Object data) {
      if (this.customData == null) {
         this.customData = new LinkedHashMap<>();
      }

      this.customData.put(key, data);
      return this;
   }

   public void apply(ConsoleLine line) {
      for (Throwable c = this; c != null; c = c.getCause()) {
         if (c instanceof KubeRuntimeException ex) {
            line.withSourceLine(ex.sourceLine);
            if (ex.customData != null) {
               for (Entry<String, Object> entry : ex.customData.entrySet()) {
                  line.customData(
                     entry.getKey(), (JsonElement)(entry.getValue() == null ? JsonNull.INSTANCE : new JsonPrimitive(entry.getValue().toString())), false
                  );
               }
            }
         }

         if (c instanceof RhinoException exx && exx.lineSource() != null) {
            line.withSourceLine(exx.lineSource(), exx.lineNumber());
         }
      }
   }
}
