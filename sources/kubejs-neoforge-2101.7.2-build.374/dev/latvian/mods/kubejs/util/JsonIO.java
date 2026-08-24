package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.rhino.Context;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;

public class JsonIO {
   @Nullable
   public static Object toObject(@Nullable JsonElement json) {
      return JsonUtils.toObject(json);
   }

   public static String toString(JsonElement json) {
      return JsonUtils.toString(json);
   }

   public static String toPrettyString(JsonElement json) {
      return JsonUtils.toPrettyString(json);
   }

   public static JsonElement parseRaw(@Nullable String string) {
      return JsonUtils.fromString(string);
   }

   public static Object parse(String string) {
      return UtilsJS.wrap(parseRaw(string), JSObjectType.ANY);
   }

   @Nullable
   public static Object toPrimitive(@Nullable JsonElement element) {
      return JsonUtils.toPrimitive(element);
   }

   @Nullable
   public static JsonElement readJson(Path path) throws IOException {
      if (!Files.notExists(path) && Files.isRegularFile(path)) {
         JsonElement var2;
         try (BufferedReader fileReader = Files.newBufferedReader(path)) {
            var2 = JsonParser.parseReader(fileReader);
         }

         return var2;
      } else {
         return null;
      }
   }

   public static String readString(Path path) throws IOException {
      return toString(readJson(path));
   }

   @Nullable
   public static Map<?, ?> read(Context cx, Path path) throws IOException {
      return cx.optionalMapOf(readJson(path));
   }

   public static void write(Path path, @Nullable JsonElement json) throws IOException {
      if (json != null && !(json instanceof JsonNull)) {
         if (Files.notExists(path.getParent())) {
            Files.createDirectories(path.getParent());
         }

         Files.writeString(path, JsonUtils.toPrettyString(json));
      } else {
         Files.deleteIfExists(path);
      }
   }

   public static JsonArray toArray(JsonElement element) {
      return switch (element) {
         case JsonArray a -> a;
         case null, default -> {
            JsonArray a = new JsonArray();
            a.add(element);
            yield a;
         }
      };
   }

   public static void writeJsonHash(DataOutputStream stream, @Nullable JsonElement element) throws IOException {
      switch (element) {
         case null:
            stream.writeByte(45);
            break;
         case JsonNull jsonNull:
            stream.writeByte(45);
            break;
         case JsonArray arr:
            stream.writeByte(91);

            for (JsonElement e : arr) {
               writeJsonHash(stream, e);
            }
            break;
         case JsonObject obj:
            stream.writeByte(123);

            for (Entry<String, JsonElement> e : obj.entrySet()) {
               stream.writeBytes(e.getKey());
               writeJsonHash(stream, e.getValue());
            }
            break;
         case JsonPrimitive primitive:
            stream.writeByte(61);
            if (primitive.isBoolean()) {
               stream.writeBoolean(element.getAsBoolean());
            } else if (primitive.isNumber()) {
               stream.writeDouble(element.getAsDouble());
            } else {
               stream.writeBytes(element.getAsString());
            }
            break;
         default:
            stream.writeByte(63);
            stream.writeInt(element.hashCode());
      }
   }

   public static byte[] getJsonHashBytes(JsonElement json) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      try {
         writeJsonHash(new DataOutputStream(baos), json);
      } catch (IOException var4) {
         var4.printStackTrace();
         int h = json.hashCode();
         return new byte[]{(byte)(h >> 24), (byte)(h >> 16), (byte)(h >> 8), (byte)h};
      }

      return baos.toByteArray();
   }

   public static String getJsonHashString(JsonElement json) {
      try {
         MessageDigest messageDigest = Objects.requireNonNull(MessageDigest.getInstance("MD5"));
         return new BigInteger(HexFormat.of().formatHex(messageDigest.digest(getJsonHashBytes(json))), 16).toString(36);
      } catch (Exception var2) {
         return "%08x".formatted(json.hashCode());
      }
   }
}
