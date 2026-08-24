package dev.latvian.mods.rhino.type;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class VariableTypeInfo extends TypeInfoBase {
   private final TypeVariable<?> raw;
   private TypeInfo mainBound;

   VariableTypeInfo(TypeVariable<?> typeVariable) {
      this.raw = typeVariable;
   }

   @Override
   public Class<?> asClass() {
      return this.getMainBound().asClass();
   }

   @Override
   public boolean shouldConvert() {
      return this.asClass() != Object.class;
   }

   public String getName() {
      return this.raw.getName();
   }

   public TypeInfo getMainBound() {
      if (this.mainBound == null) {
         Type bound = this.raw.getBounds()[0];
         if (bound == Object.class) {
            this.mainBound = NONE;
         } else {
            this.mainBound = TypeInfo.of(bound);
         }
      }

      return this.mainBound;
   }

   public TypeInfo[] getBounds() {
      Type[] rawBounds = this.raw.getBounds();
      return rawBounds.length == 1 && rawBounds[0] == Object.class
         ? TypeInfo.EMPTY_ARRAY
         : Arrays.stream(rawBounds).filter(t -> t != Object.class).map(TypeInfo::of).toArray(TypeInfo[]::new);
   }

   @Override
   public String toString() {
      return this.getName();
   }

   @NotNull
   @Override
   public TypeInfo consolidate(@NotNull Map<VariableTypeInfo, TypeInfo> mapping) {
      return mapping.getOrDefault(this, this);
   }

   @Override
   public boolean isFunctionalInterface() {
      return this.getMainBound().isFunctionalInterface();
   }
}
