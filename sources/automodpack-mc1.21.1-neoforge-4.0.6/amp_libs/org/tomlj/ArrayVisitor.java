package amp_libs.org.tomlj;

import amp_libs.org.tomlj.internal.TomlParser;
import amp_libs.org.tomlj.internal.TomlParserBaseVisitor;

final class ArrayVisitor extends TomlParserBaseVisitor<MutableTomlArray> {
   private final TomlVersion version;
   private final MutableTomlArray array;

   public ArrayVisitor(TomlVersion version) {
      this.version = version;
      this.array = MutableTomlArray.create(version);
   }

   public MutableTomlArray visitArrayValue(TomlParser.ArrayValueContext ctx) {
      TomlParser.ValContext valContext = ctx.val();
      if (valContext != null) {
         Object value = valContext.accept(new ValueVisitor(this.version));
         if (value != null) {
            TomlPosition position = new TomlPosition(ctx);

            try {
               this.array.append(value, position);
            } catch (TomlInvalidTypeException var6) {
               throw new TomlParseError(var6.getMessage(), position);
            }
         }
      }

      return this.array;
   }

   protected MutableTomlArray aggregateResult(MutableTomlArray aggregate, MutableTomlArray nextResult) {
      return aggregate == null ? null : nextResult;
   }

   protected MutableTomlArray defaultResult() {
      return this.array;
   }
}
