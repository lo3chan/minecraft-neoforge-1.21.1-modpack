package amp_libs.org.tomlj;

import amp_libs.org.antlr.v4.runtime.CharStream;
import amp_libs.org.antlr.v4.runtime.CharStreams;
import amp_libs.org.antlr.v4.runtime.CommonTokenStream;
import amp_libs.org.antlr.v4.runtime.tree.ParseTree;
import amp_libs.org.checkerframework.checker.nullness.qual.Nullable;
import amp_libs.org.tomlj.internal.TomlLexer;
import amp_libs.org.tomlj.internal.TomlParser;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

final class Parser {
   private Parser() {
   }

   static TomlParseResult parse(CharStream stream, TomlVersion version) {
      TomlLexer lexer = new TomlLexer(stream);
      TomlParser parser = new TomlParser(new CommonTokenStream(lexer));
      parser.removeErrorListeners();
      final AccumulatingErrorListener errorListener = new AccumulatingErrorListener();
      parser.addErrorListener(errorListener);
      ParseTree tree = parser.toml();
      final TomlTable table = tree.accept(new LineVisitor(version, errorListener));
      return new TomlParseResult() {
         @Override
         public int size() {
            return table.size();
         }

         @Override
         public boolean isEmpty() {
            return table.isEmpty();
         }

         @Override
         public Set<String> keySet() {
            return table.keySet();
         }

         @Override
         public Set<List<String>> keyPathSet(boolean includeTables) {
            return table.keyPathSet(includeTables);
         }

         @Override
         public Set<Entry<String, Object>> entrySet() {
            return table.entrySet();
         }

         @Override
         public Set<Entry<List<String>, Object>> entryPathSet(boolean includeTables) {
            return table.entryPathSet(includeTables);
         }

         @Nullable
         @Override
         public Object get(List<String> path) {
            return table.get(path);
         }

         @Nullable
         @Override
         public TomlPosition inputPositionOf(List<String> path) {
            return table.inputPositionOf(path);
         }

         @Override
         public Map<String, Object> toMap() {
            return table.toMap();
         }

         @Override
         public List<TomlParseError> errors() {
            return errorListener.errors();
         }
      };
   }

   static List<String> parseDottedKey(String dottedKey) {
      TomlLexer lexer = new TomlLexer(CharStreams.fromString(dottedKey));
      lexer.mode(2);
      TomlParser parser = new TomlParser(new CommonTokenStream(lexer));
      parser.removeErrorListeners();
      AccumulatingErrorListener errorListener = new AccumulatingErrorListener();
      parser.addErrorListener(errorListener);
      List<String> keyList = parser.tomlKey().accept(new KeyVisitor(TomlVersion.HEAD));
      List<TomlParseError> errors = errorListener.errors();
      if (!errors.isEmpty()) {
         TomlParseError e = errors.get(0);
         throw new IllegalArgumentException("Invalid key: " + e.getMessage(), e);
      } else {
         return keyList;
      }
   }
}
