package amp_libs.org.tomlj;

import amp_libs.org.antlr.v4.runtime.CharStream;
import amp_libs.org.antlr.v4.runtime.CharStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public final class Toml {
   private static final Pattern simpleKeyPattern = Pattern.compile("^[A-Za-z0-9_-]+$");

   private Toml() {
   }

   public static TomlParseResult parse(String input) {
      return parse(input, TomlVersion.LATEST);
   }

   public static TomlParseResult parse(String input, TomlVersion version) {
      CharStream stream = CharStreams.fromString(input);
      return Parser.parse(stream, version.canonical);
   }

   public static TomlParseResult parse(Path file) throws IOException {
      return parse(file, TomlVersion.LATEST);
   }

   public static TomlParseResult parse(Path file, TomlVersion version) throws IOException {
      CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
      decoder.onMalformedInput(CodingErrorAction.REPORT);
      decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
      InputStreamReader reader = new InputStreamReader(Files.newInputStream(file), decoder);
      return parse(reader, version);
   }

   public static TomlParseResult parse(InputStream is) throws IOException {
      return parse(is, TomlVersion.LATEST);
   }

   public static TomlParseResult parse(InputStream is, TomlVersion version) throws IOException {
      CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
      decoder.onMalformedInput(CodingErrorAction.REPORT);
      decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
      return parse(new InputStreamReader(is, decoder), version);
   }

   public static TomlParseResult parse(Reader reader) throws IOException {
      return parse(reader, TomlVersion.LATEST);
   }

   public static TomlParseResult parse(Reader reader, TomlVersion version) throws IOException {
      CharStream stream = CharStreams.fromReader(reader);
      return Parser.parse(stream, version.canonical);
   }

   public static TomlParseResult parse(ReadableByteChannel channel) throws IOException {
      return parse(channel, TomlVersion.LATEST);
   }

   public static TomlParseResult parse(ReadableByteChannel channel, TomlVersion version) throws IOException {
      CharStream stream = CharStreams.fromChannel(channel, StandardCharsets.UTF_8, 4096, CodingErrorAction.REPORT, "<unknown>", -1L);
      return Parser.parse(stream, version.canonical);
   }

   public static List<String> parseDottedKey(String dottedKey) {
      Objects.requireNonNull(dottedKey);
      return Parser.parseDottedKey(dottedKey);
   }

   public static String joinKeyPath(List<String> path) {
      Objects.requireNonNull(path);
      StringJoiner joiner = new StringJoiner(".");

      for (String key : path) {
         if (simpleKeyPattern.matcher(key).matches()) {
            joiner.add(key);
         } else {
            joiner.add("\"" + tomlEscape(key) + '"');
         }
      }

      return joiner.toString();
   }

   public static String canonicalDottedKey(String dottedKey) {
      return joinKeyPath(parseDottedKey(dottedKey));
   }

   public static StringBuilder tomlEscape(String text) {
      StringBuilder out = new StringBuilder();

      for (int i = 0; i < text.length(); i++) {
         int codepoint = text.codePointAt(i);
         if (Character.charCount(codepoint) > 1) {
            out.append("\\U").append(String.format("%08x", codepoint));
            i++;
         } else {
            char ch = Character.toChars(codepoint)[0];
            if (ch == '\'') {
               out.append("\\'");
            } else if (ch == '"') {
               out.append("\\\"");
            } else if (ch == '\\') {
               out.append("\\\\");
            } else if (ch >= ' ' && ch < 127) {
               out.append(ch);
            } else {
               switch (ch) {
                  case '\b':
                     out.append("\\b");
                     break;
                  case '\t':
                     out.append("\\t");
                     break;
                  case '\n':
                     out.append("\\n");
                     break;
                  case '\u000b':
                  default:
                     out.append("\\u").append(String.format("%04x", codepoint));
                     break;
                  case '\f':
                     out.append("\\f");
                     break;
                  case '\r':
                     out.append("\\r");
               }
            }
         }
      }

      return out;
   }

   public static boolean equals(TomlArray array1, TomlArray array2) {
      if (array1.size() != array2.size()) {
         return false;
      } else {
         for (int i = 0; i < array1.size(); i++) {
            Object value1 = array1.get(i);
            Object value2 = array2.get(i);
            Optional<TomlType> tomlType1 = TomlType.typeFor(value1);

            assert tomlType1.isPresent();

            Optional<TomlType> tomlType2 = TomlType.typeFor(value2);

            assert tomlType2.isPresent();

            if (tomlType1.get() != tomlType2.get()) {
               return false;
            }

            if (tomlType1.get().equals(TomlType.TABLE)) {
               if (!equals((TomlTable)value1, (TomlTable)value2)) {
                  return false;
               }
            } else if (tomlType1.get().equals(TomlType.ARRAY)) {
               if (!equals((TomlArray)value1, (TomlArray)value2)) {
                  return false;
               }
            } else if (!value1.equals(value2)) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean equals(TomlTable table1, TomlTable table2) {
      if (table1.entrySet().size() != table2.entrySet().size()) {
         return false;
      } else {
         for (Entry<String, Object> entry : table1.entrySet()) {
            String key = entry.getKey();
            if (!table2.keySet().contains(key)) {
               return false;
            }

            Object value1 = entry.getValue();
            Optional<Entry<String, Object>> value2Entry = table2.entrySet().stream().filter(entry2 -> entry2.getKey().equals(key)).findFirst();
            if (!value2Entry.isPresent()) {
               return false;
            }

            Object value2 = value2Entry.get().getValue();
            Optional<TomlType> tomlType1 = TomlType.typeFor(value1);

            assert tomlType1.isPresent();

            Optional<TomlType> tomlType2 = TomlType.typeFor(value2);

            assert tomlType2.isPresent();

            if (tomlType1.get() != tomlType2.get()) {
               return false;
            }

            if (tomlType1.get().equals(TomlType.TABLE)) {
               if (!equals((TomlTable)value1, (TomlTable)value2)) {
                  return false;
               }
            } else if (tomlType1.get().equals(TomlType.ARRAY)) {
               if (!equals((TomlArray)value1, (TomlArray)value2)) {
                  return false;
               }
            } else if (!value1.equals(value2)) {
               return false;
            }
         }

         return true;
      }
   }
}
