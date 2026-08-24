package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.util.DefaultValueTypeHint;

public interface Scriptable {
   Object NOT_FOUND = UniqueTag.NOT_FOUND;

   String getClassName();

   Object get(Context var1, String var2, Scriptable var3);

   Object get(Context var1, int var2, Scriptable var3);

   boolean has(Context var1, String var2, Scriptable var3);

   boolean has(Context var1, int var2, Scriptable var3);

   void put(Context var1, String var2, Scriptable var3, Object var4);

   void put(Context var1, int var2, Scriptable var3, Object var4);

   void delete(Context var1, String var2);

   void delete(Context var1, int var2);

   Scriptable getPrototype(Context var1);

   void setPrototype(Scriptable var1);

   Scriptable getParentScope();

   void setParentScope(Scriptable var1);

   Object[] getIds(Context var1);

   default Object[] getAllIds(Context cx) {
      return this.getIds(cx);
   }

   Object getDefaultValue(Context var1, DefaultValueTypeHint var2);

   boolean hasInstance(Context var1, Scriptable var2);

   default MemberType getTypeOf() {
      return MemberType.OBJECT;
   }
}
