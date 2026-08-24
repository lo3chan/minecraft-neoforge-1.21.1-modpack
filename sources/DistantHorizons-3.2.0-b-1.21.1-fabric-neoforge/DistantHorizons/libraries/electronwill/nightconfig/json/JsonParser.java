package DistantHorizons.libraries.electronwill.nightconfig.json;

import DistantHorizons.libraries.electronwill.nightconfig.core.Config;
import DistantHorizons.libraries.electronwill.nightconfig.core.ConfigFormat;
import DistantHorizons.libraries.electronwill.nightconfig.core.concurrent.ConcurrentConfig;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.CharacterInput;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.CharsWrapper;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.ConfigParser;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.ParsingException;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.ParsingMode;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.ReaderInput;
import DistantHorizons.libraries.electronwill.nightconfig.core.io.Utils;
import DistantHorizons.libraries.electronwill.nightconfig.core.utils.FastStringReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class JsonParser implements ConfigParser<Config> {
   private static final char[] SPACES = new char[]{' ', '\t', '\n', '\r'};
   private static final char[] TRUE_LAST = new char[]{'r', 'u', 'e'};
   private static final char[] FALSE_LAST = new char[]{'a', 'l', 's', 'e'};
   private static final char[] NULL_LAST = new char[]{'u', 'l', 'l'};
   private static final char[] NUMBER_END = new char[]{',', '}', ']', ' ', '\t', '\n', '\r'};
   private final ConfigFormat<Config> configFormat;
   private boolean emptyDataAccepted = false;
   private boolean trailingDataAccepted = false;

   public JsonParser() {
      this(JsonFormat.fancyInstance());
   }

   JsonParser(ConfigFormat<Config> configFormat) {
      this.configFormat = configFormat;
   }

   @Override
   public ConfigFormat<Config> getFormat() {
      return this.configFormat;
   }

   public boolean isEmptyDataAccepted() {
      return this.emptyDataAccepted;
   }

   public JsonParser setEmptyDataAccepted(boolean emptyDataAccepted) {
      this.emptyDataAccepted = emptyDataAccepted;
      return this;
   }

   public boolean isTrailingDataAccepted() {
      return this.trailingDataAccepted;
   }

   public JsonParser setTrailingDataAccepted(boolean trailingDataAccepted) {
      this.trailingDataAccepted = trailingDataAccepted;
      return this;
   }

   public Object parseDocument(String json) {
      return this.parseDocument(new FastStringReader(json));
   }

   public Object parseDocument(Reader reader) {
      return this.parseDocument(reader, this.configFormat.createConfig());
   }

   public Object parseDocument(Reader reader, Config configModel) {
      CharacterInput input = new ReaderInput(reader);
      if (input.peek() == -1) {
         if (this.emptyDataAccepted) {
            return configModel.createSubConfig();
         } else {
            throw new ParsingException("No json data: input is empty");
         }
      } else {
         char firstChar = input.readCharAndSkip(SPACES);
         Object result;
         if (firstChar == '{') {
            result = this.parseObject(input, configModel.createSubConfig(), ParsingMode.MERGE);
         } else {
            if (firstChar != '[') {
               throw new ParsingException("Invalid first character for a json document: " + firstChar);
            }

            result = this.parseArray(input, new ArrayList(), ParsingMode.MERGE, configModel.createSubConfig());
         }

         this.checkNoTrailingData(input);
         return result;
      }
   }

   private void checkNoTrailingData(CharacterInput input) {
      if (!this.trailingDataAccepted) {
         int trailing = input.readAndSkip(SPACES);
         if (trailing >= 0) {
            input.pushBack((char)trailing);
            String msg = String.format(
               "Invalid data at the end of the JSON document: %s (use JsonParser.setTrailingDataAccepted(true) if you intend this to work)",
               input.read(6).toString()
            );
            throw new ParsingException(msg);
         }
      }
   }

   @Override
   public Config parse(Reader reader) {
      Config config = this.configFormat.createConfig();
      this.parse(reader, config, ParsingMode.MERGE);
      return config;
   }

   @Override
   public void parse(Reader reader, Config destination, ParsingMode parsingMode) {
      CharacterInput input = new ReaderInput(reader);
      if (input.peek() == -1) {
         if (!this.emptyDataAccepted) {
            throw new ParsingException("No json data: input is empty");
         }
      } else {
         char firstChar = input.readCharAndSkip(SPACES);
         if (firstChar != '{') {
            throw new ParsingException("Invalid first character for a json object: " + firstChar);
         } else {
            if (destination instanceof ConcurrentConfig) {
               ((ConcurrentConfig)destination).bulkUpdate(view -> {
                  parsingMode.prepareParsing(view);
                  this.parseObject(input, view, parsingMode);
               });
            } else {
               parsingMode.prepareParsing(destination);
               this.parseObject(input, destination, parsingMode);
            }

            this.checkNoTrailingData(input);
         }
      }
   }

   public <T> List<T> parseList(String json) {
      return this.parseList(new FastStringReader(json));
   }

   public <T> List<T> parseList(Reader reader) {
      List<Object> list = new ArrayList<>();
      this.parseList(reader, list, ParsingMode.MERGE, this.configFormat.createConfig());
      return (List<T>)list;
   }

   public void parseList(Reader reader, List<?> destination, ParsingMode parsingMode) {
      this.parseList(reader, destination, parsingMode, this.configFormat.createConfig());
   }

   public void parseList(Reader reader, List<?> destination, ParsingMode parsingMode, Config configModel) {
      CharacterInput input = new ReaderInput(reader);
      if (input.peek() == -1) {
         if (!this.emptyDataAccepted) {
            throw new ParsingException("No json data: input is empty");
         }
      } else {
         char firstChar = input.readCharAndSkip(SPACES);
         if (firstChar != '[') {
            throw new ParsingException("Invalid first character for a json array: " + firstChar);
         } else {
            this.parseArray(input, destination, parsingMode, configModel);
            this.checkNoTrailingData(input);
         }
      }
   }

   private <T extends Config> T parseObject(CharacterInput input, T config, ParsingMode parsingMode) {
      char kfirst = input.readCharAndSkip(SPACES);
      if (kfirst == '}') {
         return config;
      } else if (kfirst != '"') {
         throw new ParsingException("Invalid beginning of a key: " + kfirst);
      } else {
         this.parseKVPair(input, config, parsingMode);

         while (true) {
            char vsep = input.readCharAndSkip(SPACES);
            if (vsep == '}') {
               return config;
            }

            if (vsep != ',') {
               throw new ParsingException("Invalid value separator: " + vsep);
            }

            kfirst = input.readCharAndSkip(SPACES);
            if (kfirst != '"') {
               throw new ParsingException("Invalid beginning of a key: " + kfirst);
            }

            this.parseKVPair(input, config, parsingMode);
         }
      }
   }

   private void parseKVPair(CharacterInput input, Config config, ParsingMode parsingMode) {
      List<String> key = Collections.singletonList(this.parseString(input));
      char sep = input.readCharAndSkip(SPACES);
      if (sep != ':') {
         throw new ParsingException("Invalid key-value separator: " + sep);
      } else {
         char vfirst = input.readCharAndSkip(SPACES);
         Object value = this.parseValue(input, vfirst, parsingMode, config);
         parsingMode.put(config, key, value);
      }
   }

   private <T> List<T> parseArray(CharacterInput input, List<T> list, ParsingMode parsingMode, Config parentConfig) {
      boolean first = true;

      char valueFirst;
      char next;
      do {
         valueFirst = input.readCharAndSkip(SPACES);
         if (first && valueFirst == ']') {
            return list;
         }

         first = false;
         T value = (T)this.parseValue(input, valueFirst, parsingMode, parentConfig);
         list.add(value);
         next = input.readCharAndSkip(SPACES);
         if (next == ']') {
            return list;
         }
      } while (next == ',');

      throw new ParsingException("Invalid value separator: " + valueFirst);
   }

   private Object parseValue(CharacterInput input, char firstChar, ParsingMode parsingMode, Config parentConfig) {
      switch (firstChar) {
         case '"':
            return this.parseString(input);
         case '[':
            return this.parseArray(input, new ArrayList(), parsingMode, parentConfig);
         case 'f':
            return this.parseFalse(input);
         case 'n':
            return this.parseNull(input);
         case 't':
            return this.parseTrue(input);
         case '{':
            return this.parseObject(input, parentConfig.createSubConfig(), parsingMode);
         default:
            input.pushBack(firstChar);
            return this.parseNumber(input);
      }
   }

   private Number parseNumber(CharacterInput input) {
      CharsWrapper chars = input.readCharsUntil(NUMBER_END);
      if (!chars.contains('.') && !chars.contains('e') && !chars.contains('E')) {
         long l = Utils.parseLong(chars, 10);
         int small = (int)l;
         return (Number)(l == small ? small : l);
      } else {
         return Utils.parseDouble(chars);
      }
   }

   private boolean parseTrue(CharacterInput input) {
      CharsWrapper chars = input.readChars(3);
      if (!chars.contentEquals(TRUE_LAST)) {
         throw new ParsingException("Invalid value: t" + chars + " - expected boolean true");
      } else {
         return true;
      }
   }

   private boolean parseFalse(CharacterInput input) {
      CharsWrapper chars = input.readChars(4);
      if (!chars.contentEquals(FALSE_LAST)) {
         throw new ParsingException("Invalid value: f" + chars + " - expected boolean false");
      } else {
         return false;
      }
   }

   private Object parseNull(CharacterInput input) {
      CharsWrapper chars = input.readChars(3);
      if (!chars.contentEquals(NULL_LAST)) {
         throw new ParsingException("Invaid value: n" + chars + " - expected null");
      } else {
         return null;
      }
   }

   private String parseString(CharacterInput input) {
      StringBuilder builder = new StringBuilder();
      boolean escape = false;

      char c;
      while ((c = input.readChar()) != '"' || escape) {
         if (escape) {
            builder.append(this.unescape(c, input));
            escape = false;
         } else if (c == '\\') {
            escape = true;
         } else {
            builder.append(c);
         }
      }

      return builder.toString();
   }

   private char unescape(char c, CharacterInput input) {
      switch (c) {
         case '"':
         case '/':
         case '\\':
            return c;
         case 'b':
            return '\b';
         case 'f':
            return '\f';
         case 'n':
            return '\n';
         case 'r':
            return '\r';
         case 't':
            return '\t';
         case 'u':
            CharsWrapper chars = input.readChars(4);
            return (char)Utils.parseInt(chars, 16);
         default:
            throw new ParsingException("Invalid escapement: \\" + c);
      }
   }
}
