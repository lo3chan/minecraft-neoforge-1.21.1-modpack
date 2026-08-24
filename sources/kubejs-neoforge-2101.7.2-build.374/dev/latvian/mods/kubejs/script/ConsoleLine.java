package dev.latvian.mods.kubejs.script;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.util.LogType;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ConsoleLine implements Supplier<JsonElement> {
   public static final ConsoleLine[] EMPTY_ARRAY = new ConsoleLine[0];
   public static final StreamCodec<FriendlyByteBuf, ConsoleLine> STREAM_CODEC = new StreamCodec<FriendlyByteBuf, ConsoleLine>() {
      public ConsoleLine decode(FriendlyByteBuf buf) {
         ConsoleJS console = ScriptType.VALUES[buf.readByte()].console;
         long timestamp = buf.readVarLong();
         String message = buf.readUtf();
         ConsoleLine line = new ConsoleLine(console, timestamp, message);
         line.type = LogType.VALUES[buf.readByte()];
         line.group = "";
         line.sourceLines = buf.readList(SourceLine::read);
         line.stackTrace = buf.readList(FriendlyByteBuf::readUtf);
         return line;
      }

      public void encode(FriendlyByteBuf buf, ConsoleLine line) {
         buf.writeByte(line.console.scriptType.ordinal());
         buf.writeVarLong(line.timestamp);
         buf.writeUtf(line.message);
         buf.writeByte(line.type.ordinal());
         buf.writeCollection(line.sourceLines, SourceLine::write);
         buf.writeCollection(line.stackTrace, FriendlyByteBuf::writeUtf);
      }
   };
   public final ConsoleJS console;
   public final long timestamp;
   public String message;
   public LogType type = LogType.INFO;
   public String group = "";
   public Collection<SourceLine> sourceLines = Set.of();
   public Path externalFile = null;
   public List<String> stackTrace = List.of();
   private String cachedText;
   private JsonObject customData;

   public ConsoleLine(ConsoleJS console, long timestamp, String message) {
      this.console = console;
      this.timestamp = timestamp;
      this.message = message;
   }

   public String getText() {
      if (this.cachedText == null) {
         StringBuilder builder = new StringBuilder();
         if (!this.sourceLines.isEmpty()) {
            for (SourceLine line : this.sourceLines) {
               if (!line.isUnknown()) {
                  builder.append(line.source()).append('#').append(line.line()).append(':').append(' ');
                  break;
               }
            }
         }

         if (!this.group.isEmpty()) {
            builder.append(this.group);
         }

         builder.append(this.message);
         this.cachedText = builder.toString();
      }

      return this.cachedText;
   }

   public ConsoleLine withSourceLine(String source, int line) {
      if (source == null) {
         source = "";
      }

      if (!source.isEmpty() && source.startsWith(this.console.scriptType.nameStrip)) {
         source = source.substring(this.console.scriptType.nameStrip.length());
      }

      if (line < 0) {
         line = 0;
      }

      return source.isEmpty() && line == 0 ? this : this.withSourceLine(SourceLine.of(source, line));
   }

   public ConsoleLine withSourceLine(SourceLine sourceLine) {
      if (sourceLine.isUnknown()) {
         return this;
      } else if (this.sourceLines.isEmpty()) {
         this.sourceLines = Set.of(sourceLine);
         return this;
      } else {
         if (this.sourceLines.size() == 1) {
            SourceLine line0 = this.sourceLines.iterator().next();
            this.sourceLines = new LinkedHashSet<>();
            this.sourceLines.add(line0);
         }

         this.sourceLines.add(sourceLine);
         return this;
      }
   }

   public ConsoleLine withExternalFile(Path path) {
      this.externalFile = path;
      this.sourceLines = Set.of(SourceLine.of(path.getFileName().toString(), 0));
      return this;
   }

   @Override
   public String toString() {
      return this.getText();
   }

   public ConsoleLine customData(String key, JsonElement data, boolean override) {
      if (this.customData == null) {
         this.customData = new JsonObject();
      }

      if (override || !this.customData.has(key)) {
         this.customData.add(key, data);
      }

      return this;
   }

   public JsonObject toJson() {
      JsonObject json = new JsonObject();
      json.addProperty("type", this.type.id);
      json.addProperty("message", this.getText());
      json.addProperty("timestamp", this.timestamp);
      if (this.customData != null) {
         json.add("custom_data", this.customData);
      }

      JsonArray ssls = new JsonArray();
      JsonArray asls = new JsonArray();
      json.add("script_source_lines", ssls);
      json.add("all_source_lines", asls);

      for (SourceLine l : this.sourceLines) {
         JsonObject sourceLine = new JsonObject();
         sourceLine.addProperty("source", l.source());
         sourceLine.addProperty("line", l.line());
         asls.add(sourceLine);
         if (l.source().endsWith(".js")) {
            ssls.add(sourceLine);
         }
      }

      JsonArray st = new JsonArray();
      json.add("stack_trace", st);

      for (String s : this.stackTrace) {
         st.add(s);
      }

      return json;
   }

   public JsonElement get() {
      return this.toJson();
   }
}
