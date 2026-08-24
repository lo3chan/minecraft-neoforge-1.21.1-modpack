package amp_libs.org.tomlj;

import amp_libs.org.tomlj.internal.TomlParser;
import amp_libs.org.tomlj.internal.TomlParserBaseVisitor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class InlineTableVisitor extends TomlParserBaseVisitor<MutableTomlTable> {
   private final TomlVersion version;
   private final MutableTomlTable table;
   private final Map<MutableTomlTable, TomlPosition> openTables;

   public InlineTableVisitor(TomlVersion version, TomlPosition position) {
      this.version = version;
      this.table = new MutableTomlTable(version, position);
      this.openTables = new HashMap<>();
   }

   public MutableTomlTable visitKeyval(TomlParser.KeyvalContext ctx) {
      TomlParser.KeyContext keyContext = ctx.key();
      TomlParser.ValContext valContext = ctx.val();
      if (keyContext != null && valContext != null) {
         List<String> path = keyContext.accept(new KeyVisitor(this.version));
         if (path != null && !path.isEmpty()) {
            Object value = valContext.accept(new ValueVisitor(this.version));
            if (value != null) {
               this.table.set(path, value, new TomlPosition(ctx)).forEach(entry -> this.openTables.putIfAbsent(entry.getKey(), entry.getValue()));
            }
         }
      }

      return this.table;
   }

   protected MutableTomlTable aggregateResult(MutableTomlTable aggregate, MutableTomlTable nextResult) {
      return this.table;
   }

   protected MutableTomlTable defaultResult() {
      return this.table;
   }

   public void defineOpenTables() {
      this.openTables.forEach(MutableTomlTable::define);
      this.openTables.clear();
   }
}
