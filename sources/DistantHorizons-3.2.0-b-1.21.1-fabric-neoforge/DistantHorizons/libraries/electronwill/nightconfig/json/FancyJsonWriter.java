package DistantHorizons.libraries.electronwill.nightconfig.json;

import DistantHorizons.libraries.electronwill.nightconfig.core.NullObject;
import DistantHorizons.libraries.electronwill.nightconfig.core.UnmodifiableConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.CharacterOutput;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.ConfigWriter;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.IndentStyle;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.NewlineStyle;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.Utils;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.WriterOutput;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.WritingException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public final class FancyJsonWriter implements ConfigWriter {
   private static final char[] ENTRY_SEPARATOR = new char[]{':', ' '};
   private static final char[] VALUE_SEPARATOR = new char[]{',', ' '};
   private Predicate<UnmodifiableConfig> indentObjectElementsPredicate = c -> true;
   private Predicate<Collection<?>> indentArrayElementsPredicate = c -> true;
   private boolean newlineAfterObjectStart;
   private char[] newline = NewlineStyle.system().chars;
   private char[] indent = IndentStyle.TABS.chars;
   private int currentIndentLevel;

   @Override
   public void write(UnmodifiableConfig config, Writer writer) {
      this.currentIndentLevel = 0;
      this.writeObject(config, new WriterOutput(writer));
   }

   private void writeObject(UnmodifiableConfig config, CharacterOutput output) {
      if (config.isEmpty()) {
         output.write(MinimalJsonWriter.EMPTY_OBJECT);
      } else {
         Iterator<? extends UnmodifiableConfig.Entry> it = config.entrySet().iterator();
         output.write('{');
         if (this.newlineAfterObjectStart) {
            output.write(this.newline);
         }

         boolean indentElements = this.indentObjectElementsPredicate.test(config);
         if (indentElements) {
            output.write(this.newline);
            this.increaseIndentLevel();
         }

         while (true) {
            UnmodifiableConfig.Entry entry = it.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (indentElements) {
               this.writeIndent(output);
            }

            this.writeString(key, output);
            output.write(ENTRY_SEPARATOR);
            this.writeValue(value, output);
            if (!it.hasNext()) {
               if (indentElements) {
                  output.write(this.newline);
               }

               if (indentElements) {
                  this.decreaseIndentLevel();
                  this.writeIndent(output);
               }

               output.write('}');
               return;
            }

            output.write(',');
            if (indentElements) {
               output.write(this.newline);
            }
         }
      }
   }

   private void writeValue(Object v, CharacterOutput output) {
      if (v == null || v == NullObject.NULL_OBJECT) {
         output.write(MinimalJsonWriter.NULL_CHARS);
      } else if (v instanceof CharSequence) {
         this.writeString((CharSequence)v, output);
      } else if (v instanceof Enum) {
         this.writeString(((Enum)v).name(), output);
      } else if (v instanceof Number) {
         output.write(v.toString());
      } else if (v instanceof UnmodifiableConfig) {
         this.writeObject((UnmodifiableConfig)v, output);
      } else if (v instanceof Collection) {
         this.writeArray((Collection<?>)v, output);
      } else if (v instanceof Boolean) {
         this.writeBoolean((Boolean)v, output);
      } else if (v instanceof Object[]) {
         this.writeArray(Arrays.asList((Object[])v), output);
      } else {
         if (!v.getClass().isArray()) {
            throw new WritingException("Unsupported value type: " + v.getClass());
         }

         this.writeArray(v, output);
      }
   }

   private void writeArray(Collection<?> collection, CharacterOutput output) {
      if (collection.isEmpty()) {
         output.write(MinimalJsonWriter.EMPTY_ARRAY);
      } else {
         Iterator<?> it = collection.iterator();
         output.write('[');
         if (this.newlineAfterObjectStart) {
            output.write(this.newline);
         }

         boolean indentElements = this.indentArrayElementsPredicate.test(collection);
         if (indentElements) {
            output.write(this.newline);
            this.increaseIndentLevel();
         }

         while (true) {
            Object value = it.next();
            if (indentElements) {
               this.writeIndent(output);
            }

            this.writeValue(value, output);
            if (!it.hasNext()) {
               if (indentElements) {
                  output.write(this.newline);
               }

               if (indentElements) {
                  this.decreaseIndentLevel();
                  this.writeIndent(output);
               }

               output.write(']');
               return;
            }

            output.write(VALUE_SEPARATOR);
            if (indentElements) {
               output.write(this.newline);
            }
         }
      }
   }

   private void writeArray(Object array, CharacterOutput output) {
      int length = Array.getLength(array);
      List<Object> list = new ArrayList<>(length);

      for (int i = 0; i < length; i++) {
         list.add(Array.get(array, i));
      }

      this.writeArray(list, output);
   }

   private void writeBoolean(boolean b, CharacterOutput output) {
      if (b) {
         output.write(MinimalJsonWriter.TRUE_CHARS);
      } else {
         output.write(MinimalJsonWriter.FALSE_CHARS);
      }
   }

   private void writeString(CharSequence s, CharacterOutput output) {
      output.write('"');
      int length = s.length();

      for (int i = 0; i < length; i++) {
         char c = s.charAt(i);
         int escapeIndex = Utils.arrayIndexOf(MinimalJsonWriter.TO_ESCAPE, c);
         if (escapeIndex == -1) {
            output.write(c);
         } else {
            char escaped = MinimalJsonWriter.ESCAPED[escapeIndex];
            output.write('\\');
            output.write(escaped);
         }
      }

      output.write('"');
   }

   private void increaseIndentLevel() {
      this.currentIndentLevel++;
   }

   private void decreaseIndentLevel() {
      this.currentIndentLevel--;
   }

   private void writeIndent(CharacterOutput output) {
      for (int i = 0; i < this.currentIndentLevel; i++) {
         output.write(this.indent);
      }
   }

   public FancyJsonWriter setIndentObjectElementsPredicate(Predicate<UnmodifiableConfig> indentObjectElementsPredicate) {
      this.indentObjectElementsPredicate = indentObjectElementsPredicate;
      return this;
   }

   public FancyJsonWriter setIndentArrayElementsPredicate(Predicate<Collection<?>> indentArrayElementsPredicate) {
      this.indentArrayElementsPredicate = indentArrayElementsPredicate;
      return this;
   }

   public FancyJsonWriter setNewlineAfterObjectStart(boolean newlineAfterObjectStart) {
      this.newlineAfterObjectStart = newlineAfterObjectStart;
      return this;
   }

   public FancyJsonWriter setIndent(IndentStyle indentStyle) {
      this.indent = indentStyle.chars;
      return this;
   }

   public FancyJsonWriter setIndent(String indent) {
      this.indent = indent.toCharArray();
      return this;
   }

   public FancyJsonWriter setNewline(NewlineStyle newlineStyle) {
      this.newline = newlineStyle.chars;
      return this;
   }

   public FancyJsonWriter setNewline(String newlineString) {
      this.newline = newlineString.toCharArray();
      return this;
   }
}
