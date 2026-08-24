package dev.latvian.mods.rhino.type;

import java.util.Collection;
import java.util.List;

public record JSFunctionTypeInfo(List<JSOptionalParam> params, TypeInfo returnType) implements TypeInfo {
   @Override
   public Class<?> asClass() {
      return TypeInfo.class;
   }

   @Override
   public String toString() {
      return TypeStringContext.DEFAULT.toString(this);
   }

   @Override
   public void append(TypeStringContext ctx, StringBuilder sb) {
      sb.append('(');
      boolean first = true;

      for (JSOptionalParam param : this.params) {
         if (first) {
            first = false;
         } else {
            sb.append(',');
            ctx.appendSpace(sb);
         }

         param.append(ctx, sb);
      }

      sb.append(')');
      ctx.appendSpace(sb);
      sb.append('=');
      sb.append('>');
      ctx.appendSpace(sb);
      ctx.append(sb, this.returnType);
   }

   @Override
   public void collectContainedComponentClasses(Collection<Class<?>> classes) {
      for (JSOptionalParam param : this.params) {
         param.type().collectContainedComponentClasses(classes);
      }

      this.returnType.collectContainedComponentClasses(classes);
   }
}
