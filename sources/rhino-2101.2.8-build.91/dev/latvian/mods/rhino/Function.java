package dev.latvian.mods.rhino;

public interface Function extends Scriptable, Callable, Constructable {
   @Override
   Object call(Context var1, Scriptable var2, Scriptable var3, Object[] var4);

   @Override
   Scriptable construct(Context var1, Scriptable var2, Object[] var3);
}
