package amp_libs.org.tomlj;

import amp_libs.org.tomlj.internal.TomlParser;
import amp_libs.org.tomlj.internal.TomlParserBaseVisitor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class LineVisitor extends TomlParserBaseVisitor<MutableTomlTable> {
   private final TomlVersion version;
   private final ErrorReporter errorReporter;
   private final MutableTomlTable rootTable;
   private MutableTomlTable currentTable;
   private final Map<MutableTomlTable, TomlPosition> openTables;

   LineVisitor(TomlVersion version, ErrorReporter errorReporter) {
      this.version = version;
      this.errorReporter = errorReporter;
      this.rootTable = new MutableTomlTable(version, TomlPosition.positionAt(1, 1));
      this.currentTable = this.rootTable;
      this.openTables = new HashMap<>();
   }

   public MutableTomlTable visitKeyval(TomlParser.KeyvalContext ctx) {
      TomlParser.KeyContext keyContext = ctx.key();
      TomlParser.ValContext valContext = ctx.val();
      if (keyContext != null && valContext != null) {
         try {
            List<String> path = keyContext.accept(new KeyVisitor(this.version));
            if (path != null && !path.isEmpty()) {
               if (!this.version.after(TomlVersion.V0_4_0) && path.size() > 1) {
                  throw new TomlParseError("Dotted keys are not supported", new TomlPosition(keyContext));
               } else {
                  Object value = valContext.accept(new ValueVisitor(this.version));
                  if (value != null) {
                     this.currentTable.set(path, value, new TomlPosition(ctx)).forEach(entry -> this.openTables.putIfAbsent(entry.getKey(), entry.getValue()));
                  }

                  return this.rootTable;
               }
            } else {
               return this.rootTable;
            }
         } catch (TomlParseError var6) {
            this.errorReporter.reportError(var6);
            return this.rootTable;
         }
      } else {
         return this.rootTable;
      }
   }

   public MutableTomlTable visitStandardTable(TomlParser.StandardTableContext ctx) {
      this.defineOpenTables();
      TomlParser.KeyContext keyContext = ctx.key();
      if (keyContext == null) {
         this.errorReporter.reportError(new TomlParseError("Empty table key", new TomlPosition(ctx)));
         return this.rootTable;
      } else {
         List<String> path = keyContext.accept(new KeyVisitor(this.version));
         if (path == null) {
            return this.rootTable;
         } else {
            try {
               this.currentTable = this.rootTable.createTable(path, new TomlPosition(ctx));
            } catch (TomlParseError var5) {
               this.errorReporter.reportError(var5);
            }

            return this.rootTable;
         }
      }
   }

   public MutableTomlTable visitArrayTable(TomlParser.ArrayTableContext ctx) {
      this.defineOpenTables();
      TomlParser.KeyContext keyContext = ctx.key();
      if (keyContext == null) {
         this.errorReporter.reportError(new TomlParseError("Empty table key", new TomlPosition(ctx)));
         return this.rootTable;
      } else {
         List<String> path = keyContext.accept(new KeyVisitor(this.version));
         if (path == null) {
            return this.rootTable;
         } else {
            try {
               this.currentTable = this.rootTable.createTableArray(path, new TomlPosition(ctx));
            } catch (TomlParseError var5) {
               this.errorReporter.reportError(var5);
            }

            return this.rootTable;
         }
      }
   }

   protected MutableTomlTable aggregateResult(MutableTomlTable aggregate, MutableTomlTable nextResult) {
      return aggregate == null ? null : nextResult;
   }

   protected MutableTomlTable defaultResult() {
      return this.rootTable;
   }

   private void defineOpenTables() {
      this.openTables.forEach(MutableTomlTable::define);
      this.openTables.clear();
   }
}
