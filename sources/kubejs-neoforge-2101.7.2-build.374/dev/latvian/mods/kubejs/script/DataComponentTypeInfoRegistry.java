package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.rhino.type.TypeInfo;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import net.minecraft.core.component.DataComponentType;

@FunctionalInterface
public interface DataComponentTypeInfoRegistry {
   void register(DataComponentType<?> type, TypeInfo typeInfo);

   default void scanClass(Class<?> clz) {
      try {
         for (Field field : clz.getDeclaredFields()) {
            if (field.getType() == DataComponentType.class
               && Modifier.isPublic(field.getModifiers())
               && Modifier.isStatic(field.getModifiers())
               && field.getGenericType() instanceof ParameterizedType t) {
               DataComponentType<?> key = (DataComponentType<?>)field.get(null);
               TypeInfo typeInfo = TypeInfo.of(t.getActualTypeArguments()[0]);
               this.register(key, typeInfo);
            }
         }
      } catch (Exception var9) {
         var9.printStackTrace();
      }
   }
}
