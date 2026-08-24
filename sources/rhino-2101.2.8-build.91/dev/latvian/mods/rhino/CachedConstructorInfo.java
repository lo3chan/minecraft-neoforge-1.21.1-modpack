package dev.latvian.mods.rhino;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import org.jetbrains.annotations.Nullable;

public class CachedConstructorInfo extends CachedExecutableInfo {
   final Constructor<?> constructor;
   protected MethodHandle methodHandle;

   public CachedConstructorInfo(CachedClassInfo parent, Constructor<?> constructor) {
      super(parent, constructor);
      this.constructor = constructor;
   }

   public Constructor<?> getCached() {
      return this.constructor;
   }

   @Override
   public Object invoke(Context cx, Scriptable scope, @Nullable Object instance, Object... args) throws Throwable {
      CachedParameters parameters = this.getParameters();
      if (parameters.isVarArg()) {
         if (!this.constructor.isAccessible()) {
            this.constructor.setAccessible(true);
         }

         return this.constructor.newInstance(this.transformArgs(cx, null, parameters, args));
      } else {
         MethodHandle mh = this.methodHandle;
         if (mh == null) {
            mh = this.methodHandle = cx.factory.getMethodHandlesLookup().unreflectConstructor(this.constructor);
         }

         return mh.invokeWithArguments(this.transformArgs(cx, null, parameters, args));
      }
   }

   public static class Accessible {
      CachedConstructorInfo info;
      MethodSignature signature;
      String name = "";

      public CachedConstructorInfo getInfo() {
         return this.info;
      }

      public MethodSignature getSignature() {
         return this.signature;
      }

      public String getName() {
         return this.name;
      }
   }
}
