package net.blay09.mods.balm.notoml;

import com.google.common.collect.Table;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class NotomlSerializer {
   public static void serialize(Writer writer, Notoml notoml) throws IOException {
      writer.write(serializeToString(notoml));
   }

   private static String serializeToString(Notoml notoml) {
      StringBuilder sb = new StringBuilder();
      Table<String, String, Object> data = notoml.getProperties();
      Table<String, String, String> comments = notoml.getComments();

      for (String category : data.rowKeySet().stream().sorted().toList()) {
         String categoryComment = (String)comments.get(category, "");
         if (categoryComment != null && !categoryComment.isEmpty()) {
            sb.append("\n").append("# ").append(categoryComment).append("\n");
         }

         if (!category.isEmpty()) {
            sb.append("[").append(category).append("]").append("\n");
         }

         Map<String, Object> categoryProperties = data.row(category);

         for (String property : categoryProperties.keySet().stream().sorted().toList()) {
            String propertyComment = (String)comments.get(category, property);
            if (propertyComment != null && !propertyComment.isEmpty()) {
               sb.append("\n").append("# ").append(propertyComment).append("\n");
            }

            sb.append(property).append(" = ");
            Object value = categoryProperties.get(property);
            if (value instanceof String stringValue) {
               if (stringValue.contains("\n")) {
                  sb.append("\"\"\"\n").append(value).append("\n\"\"\"");
               } else {
                  sb.append("\"").append(stringValue.replace("\"", "\\\"")).append("\"");
               }
            } else if (value instanceof ResourceLocation resourceLocationValue) {
               sb.append("\"").append(resourceLocationValue).append("\"");
            } else if (value instanceof Collection<?> listValue) {
               serializeList(listValue, sb);
            } else if (value instanceof Enum<?> enumValue) {
               sb.append("\"").append(enumValue.name()).append("\"");
            } else {
               sb.append(value);
            }

            sb.append("\n");
         }

         sb.append("\n");
      }

      return sb.toString();
   }

   private static String serializeList(Collection<?> list, StringBuilder sb) {
      sb.append("[ ");
      boolean newLines = list.size() > 3;
      boolean first = true;

      for (Object value : list) {
         if (!first) {
            sb.append(", ");
         }

         first = false;
         if (newLines) {
            sb.append("\n");
         }

         if (newLines) {
            sb.append("    ");
         }

         if (value instanceof String stringValue) {
            sb.append("\"").append(stringValue.replace("\"", "\\\"")).append("\"");
         } else if (value instanceof ResourceLocation resourceLocationValue) {
            sb.append("\"").append(resourceLocationValue).append("\"");
         } else if (value instanceof Enum<?> enumValue) {
            sb.append("\"").append(enumValue.name()).append("\"");
         } else {
            sb.append(value);
         }
      }

      if (newLines) {
         sb.append("\n]");
      } else {
         sb.append(" ]");
      }

      return sb.toString();
   }
}
