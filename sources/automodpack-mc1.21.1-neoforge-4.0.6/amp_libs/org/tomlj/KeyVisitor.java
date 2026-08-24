package amp_libs.org.tomlj;

import amp_libs.org.tomlj.internal.TomlParser;
import amp_libs.org.tomlj.internal.TomlParserBaseVisitor;
import java.util.ArrayList;
import java.util.List;

final class KeyVisitor extends TomlParserBaseVisitor<List<String>> {
   private final TomlVersion version;
   private final List<String> keys = new ArrayList<>();

   public KeyVisitor(TomlVersion version) {
      this.version = version;
   }

   public List<String> visitUnquotedKey(TomlParser.UnquotedKeyContext ctx) {
      this.keys.add(ctx.getText());
      return this.keys;
   }

   public List<String> visitQuotedKey(TomlParser.QuotedKeyContext ctx) {
      StringBuilder builder = ctx.accept(new QuotedStringVisitor(this.version));
      this.keys.add(builder.toString());
      return this.keys;
   }

   protected List<String> aggregateResult(List<String> aggregate, List<String> nextResult) {
      return aggregate == null ? null : nextResult;
   }

   protected List<String> defaultResult() {
      return this.keys;
   }
}
