package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class CachedMemberInfo {
   public final CachedClassInfo parent;
   final AccessibleObject member;
   public final String originalName;
   String rename;
   public final int modifiers;
   public final boolean isStatic;
   final boolean isHidden;
   public final boolean isFinal;
   public final boolean isNative;

   public CachedMemberInfo(CachedClassInfo parent, AccessibleObject member, String originalName, int modifiers) {
      this.parent = parent;
      this.member = member;
      this.originalName = originalName;
      this.rename = "";
      this.modifiers = modifiers;
      this.isStatic = Modifier.isStatic(modifiers);
      this.isHidden = member instanceof Field && Modifier.isTransient(modifiers) || member.isAnnotationPresent(HideFromJS.class);
      this.isFinal = Modifier.isFinal(modifiers);
      this.isNative = Modifier.isNative(modifiers);
      RemapForJS remap = member.getAnnotation(RemapForJS.class);
      if (remap != null) {
         this.rename = remap.value().trim();
      }

      if (this.rename.isEmpty()) {
         for (String s : parent.getRemapPrefixes()) {
            if (originalName.startsWith(s)) {
               this.rename = originalName.substring(s.length()).trim();
               break;
            }
         }
      }
   }

   public AccessibleObject getCached() {
      return this.member;
   }

   public String getName() {
      return this.rename.isEmpty() ? this.originalName : this.rename;
   }

   public CachedClassInfo getDeclaringClass() {
      return this.parent;
   }

   @Override
   public String toString() {
      return this.parent.getTypeInfo() + "#" + this.originalName;
   }
}
