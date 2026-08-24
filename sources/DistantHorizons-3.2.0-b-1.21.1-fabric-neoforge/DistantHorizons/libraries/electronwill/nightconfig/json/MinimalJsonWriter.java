package DistantHorizons.libraries.electronwill.nightconfig.json;

import DistantHorizons.libraries.electronwill.nightconfig.core.NullObject;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.CharacterOutput;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.ConfigWriter;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.Utils;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.WriterOutput;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.WritingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

public final class MinimalJsonWriter implements ConfigWriter {
   static final char[] NULL_CHARS = new char[]{'n', 'u', 'l', 'l'};
   static final char[] TRUE_CHARS = new char[]{'t', 'r', 'u', 'e'};
   static final char[] FALSE_CHARS = new char[]{'f', 'a', 'l', 's', 'e'};
   static final char[] TO_ESCAPE = new char[]{'"', '\n', '\r', '\t', '\\'};
   static final char[] ESCAPED = new char[]{'"', 'n', 'r', 't', '\\'};
   static final char[] EMPTY_OBJECT = new char[]{'{', '}'};
   static final char[] EMPTY_ARRAY = new char[]{'[', ']'};

   @Override
   public void write(UnmodifiableConfig config, Writer writer) {
      this.writeConfig(config, new WriterOutput(writer));
   }

   public void writeCollection(Collection<?> collection, Writer writer) {
      this.writeCollection(collection, new WriterOutput(writer));
   }

   public void writeString(CharSequence csq, Writer writer) {
      this.writeString(csq, new WriterOutput(writer));
   }

   public void writeValue(Object value, Writer writer) {
      this.writeValue(value, new WriterOutput(writer));
   }

   private void writeConfig(UnmodifiableConfig config, CharacterOutput output) {
      if (config.isEmpty()) {
         output.write(EMPTY_OBJECT);
      } else {
         Iterator<? extends UnmodifiableConfig.Entry> it = config.entrySet().iterator();
         output.write('{');

         while (true) {
            UnmodifiableConfig.Entry entry = it.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            this.writeString(key, output);
            output.write(':');
            this.writeValue(value, output);
            if (!it.hasNext()) {
               output.write('}');
               return;
            }

            output.write(',');
         }
      }
   }

   private void writeValue(Object v, CharacterOutput output) {
      if (v == null || v == NullObject.NULL_OBJECT) {
         output.write(NULL_CHARS);
      } else if (v instanceof CharSequence) {
         this.writeString((CharSequence)v, output);
      } else if (v instanceof Enum) {
         this.writeString(((Enum)v).name(), output);
      } else if (v instanceof Number) {
         output.write(v.toString());
      } else if (v instanceof UnmodifiableConfig) {
         this.writeConfig((UnmodifiableConfig)v, output);
      } else if (v instanceof Collection) {
         this.writeCollection((Collection<?>)v, output);
      } else if (v instanceof Boolean) {
         this.writeBoolean((Boolean)v, output);
      } else if (v instanceof Object[]) {
         List<Object> list = Arrays.asList((Object[])v);
         this.writeCollection(list, output);
      } else if (v instanceof long[]) {
         output.write(Arrays.toString((long[])v));
      } else if (v instanceof int[]) {
         output.write(Arrays.toString((int[])v));
      } else if (v instanceof double[]) {
         output.write(Arrays.toString((double[])v));
      } else if (v instanceof float[]) {
         output.write(Arrays.toString((float[])v));
      } else if (v instanceof short[]) {
         output.write(Arrays.toString((short[])v));
      } else {
         if (!(v instanceof byte[])) {
            throw new WritingException("Unsupported value type: " + v.getClass());
         }

         output.write(Arrays.toString((byte[])v));
      }
   }

   private void writeCollection(Collection<?> collection, CharacterOutput output) {
      if (collection.isEmpty()) {
         output.write(EMPTY_ARRAY);
      } else {
         output.write('[');
         if (collection instanceof RandomAccess) {
            List<?> list = (List<?>)collection;
            int lastIndex = list.size() - 1;

            for (int i = 0; i < lastIndex; i++) {
               Object value = list.get(i);
               this.writeValue(value, output);
               output.write(',');
            }

            this.writeValue(list.get(lastIndex), output);
         } else {
            Iterator<?> it = collection.iterator();

            while (true) {
               Object value = it.next();
               this.writeValue(value, output);
               if (!it.hasNext()) {
                  break;
               }

               output.write(',');
            }
         }

         output.write(']');
      }
   }

   private void writeBoolean(boolean b, CharacterOutput output) {
      if (b) {
         output.write(TRUE_CHARS);
      } else {
         output.write(FALSE_CHARS);
      }
   }

   private void writeString(CharSequence csq, CharacterOutput output) {
      output.write('"');
      int length = csq.length();

      for (int i = 0; i < length; i++) {
         char c = csq.charAt(i);
         int escapeIndex = Utils.arrayIndexOf(TO_ESCAPE, c);
         if (escapeIndex == -1) {
            output.write(c);
         } else {
            char escaped = ESCAPED[escapeIndex];
            output.write('\\');
            output.write(escaped);
         }
      }

      output.write('"');
   }
}
