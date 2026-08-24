package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.type.TypeConsolidator;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import org.jetbrains.annotations.Nullable;

public class CachedMethodInfo extends CachedExecutableInfo {
   final Method method;
   private TypeInfo returnType;
   protected MethodHandle methodHandle;

   public CachedMethodInfo(CachedClassInfo parent, Method m) {
      super(parent, m);
      this.method = m;
   }

   public Method getCached() {
      return this.method;
   }

   @Override
   public TypeInfo getReturnType() {
      if (this.returnType == null) {
         this.returnType = TypeInfo.safeOf(this.method::getGenericReturnType).consolidate(TypeConsolidator.getMapping(this.parent.type));
      }

      return this.returnType;
   }

   @Override
   public Object invoke(Context cx, Scriptable scope, @Nullable Object instance, Object... args) throws Throwable {
      CachedParameters parameters = this.getParameters();
      if (parameters.isVarArg()) {
         if (!this.method.isAccessible()) {
            this.method.setAccessible(true);
         }

         return this.method.invoke(this.isStatic ? null : instance, this.transformArgs(cx, null, parameters, args));
      } else {
         MethodHandle mh = this.methodHandle;
         if (mh == null) {
            if (!this.method.isAccessible()) {
               this.method.setAccessible(true);
            }

            mh = this.methodHandle = cx.factory.getMethodHandlesLookup().unreflect(this.method);
         }

         return mh.invokeWithArguments(this.transformArgs(cx, this.isStatic ? null : instance, parameters, args));
      }
   }

   public static class Accessible {
      CachedMethodInfo info;
      MethodSignature signature;
      String name = "";
      boolean hidden = false;

      public CachedMethodInfo getInfo() {
         return this.info;
      }

      public MethodSignature getSignature() {
         return this.signature;
      }

      public String getName() {
         return this.name;
      }

      boolean isHidden() {
         return this.hidden;
      }
   }
}
