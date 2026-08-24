package dev.latvian.mods.rhino.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record JSOrTypeInfo(List<TypeInfo> types) implements TypeInfo {
   @Override
   public Class<?> asClass() {
      return TypeInfo.class;
   }

   @Override
   public TypeInfo or(TypeInfo info) {
      if (info instanceof JSOrTypeInfo(List var6)) {
         ArrayList<TypeInfo> list = new ArrayList<>(this.types.size() + var6.size());
         list.addAll(this.types);
         list.addAll(var6);
         return new JSOrTypeInfo(List.copyOf(list));
      } else {
         ArrayList<TypeInfo> list = new ArrayList<>(this.types.size() + 1);
         list.addAll(this.types);
         list.add(info);
         return new JSOrTypeInfo(List.copyOf(list));
      }
   }

   @Override
   public String toString() {
      return TypeStringContext.DEFAULT.toString(this);
   }

   @Override
   public void append(TypeStringContext ctx, StringBuilder sb) {
      for (int i = 0; i < this.types.size(); i++) {
         if (i != 0) {
            ctx.appendSpace(sb);
            sb.append('|');
            ctx.appendSpace(sb);
         }

         ctx.append(sb, this.types.get(i));
      }
   }

   @Override
   public void collectContainedComponentClasses(Collection<Class<?>> classes) {
      for (TypeInfo type : this.types) {
         type.collectContainedComponentClasses(classes);
      }
   }
}
