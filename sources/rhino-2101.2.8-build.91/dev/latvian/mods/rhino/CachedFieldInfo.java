package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.type.TypeConsolidator;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.jetbrains.annotations.Nullable;

public class CachedFieldInfo extends CachedMemberInfo {
   final Field field;
   private TypeInfo type;
   private MethodHandle getterMethodHandle;
   private MethodHandle setterMethodHandle;

   public CachedFieldInfo(CachedClassInfo parent, Field f) {
      super(parent, f, f.getName(), f.getModifiers());
      this.field = f;
   }

   public Field getCached() {
      return this.field;
   }

   public TypeInfo getType() {
      if (this.type == null) {
         this.type = TypeInfo.safeOf(this.field::getGenericType).consolidate(TypeConsolidator.getMapping(this.parent.type));
      }

      return this.type;
   }

   public Object get(Context cx, @Nullable Object instance) throws Throwable {
      MethodHandle mh = this.getterMethodHandle;
      if (mh == null) {
         if (this.parent.storage.includeProtected && Modifier.isProtected(this.modifiers) && !this.field.isAccessible()) {
            this.field.setAccessible(true);
         }

         mh = this.getterMethodHandle = cx.factory.getMethodHandlesLookup().unreflectGetter(this.field);
      }

      return this.isStatic ? mh.invokeWithArguments() : mh.invokeWithArguments(instance);
   }

   public void set(Context cx, @Nullable Object instance, Object value) throws Throwable {
      MethodHandle mh = this.setterMethodHandle;
      if (mh == null) {
         if (this.parent.storage.includeProtected && Modifier.isProtected(this.modifiers) && !this.field.isAccessible()) {
            this.field.setAccessible(true);
         }

         mh = this.setterMethodHandle = cx.factory.getMethodHandlesLookup().unreflectSetter(this.field);
      }

      if (this.isStatic) {
         mh.invokeWithArguments(value);
      } else {
         mh.invokeWithArguments(instance, value);
      }
   }

   public static class Accessible {
      CachedFieldInfo info;
      String name = "";
      boolean hidden = false;

      public CachedFieldInfo getInfo() {
         return this.info;
      }

      public String getName() {
         return this.name;
      }

      boolean isHidden() {
         return this.hidden;
      }
   }
}
