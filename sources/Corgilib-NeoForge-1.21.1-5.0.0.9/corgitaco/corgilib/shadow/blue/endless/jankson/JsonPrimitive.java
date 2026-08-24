package corgitaco.corgilib.shadow.blue.endless.jankson;

import corgitaco.corgilib.shadow.blue.endless.jankson.api.Escaper;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import javax.annotation.Nonnull;

public class JsonPrimitive extends JsonElement {
   public static JsonPrimitive TRUE = new JsonPrimitive(Boolean.TRUE);
   public static JsonPrimitive FALSE = new JsonPrimitive(Boolean.FALSE);
   @Nonnull
   private Object value;

   private JsonPrimitive() {
   }

   public JsonPrimitive(@Nonnull Object value) {
      if (value instanceof Character) {
         this.value = "" + (Character)value;
      } else if (value instanceof Long) {
         this.value = value;
      } else if (value instanceof Double) {
         this.value = value;
      } else if (value instanceof BigInteger) {
         this.value = ((BigInteger)value).toString(16);
      } else if (value instanceof Float) {
         this.value = (double)((Float)value).floatValue();
      } else if (value instanceof Number) {
         this.value = ((Number)value).longValue();
      } else if (value instanceof CharSequence) {
         this.value = value.toString();
      } else {
         if (!(value instanceof Boolean)) {
            throw new IllegalArgumentException("Object of type '" + value.getClass().getCanonicalName() + "' not allowed as a JsonPrimitive");
         }

         this.value = value;
      }
   }

   @Nonnull
   public String asString() {
      return this.value == null ? "null" : this.value.toString();
   }

   public boolean asBoolean(boolean defaultValue) {
      return this.value instanceof Boolean ? (Boolean)this.value : defaultValue;
   }

   public byte asByte(byte defaultValue) {
      return this.value instanceof Number ? ((Number)this.value).byteValue() : defaultValue;
   }

   public char asChar(char defaultValue) {
      if (this.value instanceof Number) {
         return (char)((Number)this.value).intValue();
      } else if (this.value instanceof Character) {
         return (Character)this.value;
      } else if (this.value instanceof String) {
         return ((String)this.value).length() == 1 ? ((String)this.value).charAt(0) : defaultValue;
      } else {
         return defaultValue;
      }
   }

   public short asShort(short defaultValue) {
      return this.value instanceof Number ? ((Number)this.value).shortValue() : defaultValue;
   }

   public int asInt(int defaultValue) {
      return this.value instanceof Number ? ((Number)this.value).intValue() : defaultValue;
   }

   public long asLong(long defaultValue) {
      return this.value instanceof Number ? ((Number)this.value).longValue() : defaultValue;
   }

   public float asFloat(float defaultValue) {
      return this.value instanceof Number ? ((Number)this.value).floatValue() : defaultValue;
   }

   public double asDouble(double defaultValue) {
      return this.value instanceof Number ? ((Number)this.value).doubleValue() : defaultValue;
   }

   public BigInteger asBigInteger(BigInteger defaultValue) {
      if (this.value instanceof Number) {
         return BigInteger.valueOf(((Number)this.value).longValue());
      } else {
         return this.value instanceof String ? new BigInteger((String)this.value, 16) : defaultValue;
      }
   }

   public BigDecimal asBigDecimal(BigDecimal defaultValue) {
      if (this.value instanceof Number) {
         return BigDecimal.valueOf(((Number)this.value).doubleValue());
      } else {
         return this.value instanceof String ? new BigDecimal((String)this.value) : defaultValue;
      }
   }

   @Nonnull
   @Override
   public String toString() {
      return this.toJson();
   }

   @Nonnull
   public Object getValue() {
      return this.value;
   }

   @Override
   public boolean equals(Object other) {
      if (other == null) {
         return false;
      } else {
         return other instanceof JsonPrimitive ? Objects.equals(this.value, ((JsonPrimitive)other).value) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.value.hashCode();
   }

   @Override
   public String toJson(boolean comments, boolean newlines, int depth) {
      return this.toJson(JsonGrammar.builder().withComments(comments).printWhitespace(newlines).build(), depth);
   }

   @Override
   public void toJson(Writer writer, JsonGrammar grammar, int depth) throws IOException {
      if (this.value == null) {
         writer.write("null");
      } else if (this.value instanceof Double && grammar.bareSpecialNumerics) {
         double d = (Double)this.value;
         if (Double.isNaN(d)) {
            writer.write("NaN");
         } else if (!Double.isInfinite(d)) {
            writer.write(this.value.toString());
         } else if (d < 0.0) {
            writer.write("-Infinity");
         } else {
            writer.write("Infinity");
         }
      } else if (this.value instanceof Number) {
         writer.write(this.value.toString());
      } else if (this.value instanceof Boolean) {
         writer.write(this.value.toString());
      } else {
         writer.write(34);
         writer.write(Escaper.escapeString(this.value.toString()));
         writer.write(34);
      }
   }

   public JsonPrimitive clone() {
      JsonPrimitive result = new JsonPrimitive();
      result.value = this.value;
      return result;
   }

   public static JsonPrimitive of(@Nonnull String s) {
      JsonPrimitive result = new JsonPrimitive();
      result.value = s;
      return result;
   }

   public static JsonPrimitive of(@Nonnull BigInteger n) {
      JsonPrimitive result = new JsonPrimitive();
      result.value = n.toString(16);
      return result;
   }

   public static JsonPrimitive of(@Nonnull BigDecimal n) {
      JsonPrimitive result = new JsonPrimitive();
      result.value = n.toString();
      return result;
   }

   public static JsonPrimitive of(@Nonnull Double d) {
      JsonPrimitive result = new JsonPrimitive();
      result.value = d;
      return result;
   }

   public static JsonPrimitive of(@Nonnull Long l) {
      JsonPrimitive result = new JsonPrimitive();
      result.value = l;
      return result;
   }

   public static JsonPrimitive of(@Nonnull Boolean b) {
      JsonPrimitive result = new JsonPrimitive();
      result.value = b;
      return result;
   }
}
