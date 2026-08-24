package dev.latvian.mods.rhino;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Map;

public class CachedClassStorage {
   public static final CachedClassStorage GLOBAL_PUBLIC = new CachedClassStorage(false);
   public static final CachedClassStorage GLOBAL_PROTECTED = new CachedClassStorage(true);
   private final Map<Class<?>, CachedClassInfo> map = new IdentityHashMap<>();
   public final CachedClassInfo objectClass = new CachedClassInfo(this, Object.class);
   public final boolean includeProtected;

   public CachedClassStorage(boolean includeProtected) {
      this.includeProtected = includeProtected;
   }

   public synchronized CachedClassInfo get(Class<?> type) {
      return type != null && type != Object.class ? this.map.computeIfAbsent(type, c -> new CachedClassInfo(this, (Class<?>)c)) : this.objectClass;
   }

   public boolean isVisible(int modifiers) {
      return Modifier.isPublic(modifiers) || this.includeProtected && Modifier.isProtected(modifiers);
   }

   public boolean include(Class<?> type, Member member) {
      return this.isVisible(member.getModifiers()) && member.getDeclaringClass() == type;
   }

   public String getDebugClassName(Class<?> type) {
      String name = type.getName();
      if (name.startsWith("java.lang.") || name.startsWith("java.util.")) {
         return name.substring(10);
      } else {
         return name.startsWith("java.function.") ? name.substring(14) : name;
      }
   }
}
