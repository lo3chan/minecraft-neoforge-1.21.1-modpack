package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.type.TypeInfo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public final class MemberBox {
   transient CachedExecutableInfo executableInfo;
   transient Object delegateTo;
   public transient WrappedExecutable wrappedExecutable;
   private transient Function asGetterFunction;
   private transient Function asSetterFunction;

   private static CachedMethodInfo searchAccessibleMethod(CachedMethodInfo method, Class<?>[] params) {
      if (Modifier.isPublic(method.modifiers) && !method.isStatic) {
         CachedClassInfo c = method.getDeclaringClass();
         if (!Modifier.isPublic(c.modifiers)) {
            String name = method.getName();
            List<CachedClassInfo> intfs = c.getInterfaces();
            int i = 0;

            for (int N = intfs.size(); i != N; i++) {
               CachedClassInfo intf = intfs.get(i);
               if (Modifier.isPublic(intf.modifiers)) {
                  try {
                     return intf.getMethod(name, params);
                  } catch (NoSuchMethodException var10) {
                  }
               }
            }

            while (true) {
               c = c.getSuperclass();
               if (c == null) {
                  break;
               }

               if (Modifier.isPublic(c.modifiers)) {
                  try {
                     CachedMethodInfo m = c.getMethod(name, params);
                     if (Modifier.isPublic(m.modifiers) && !m.isStatic) {
                        return m;
                     }
                  } catch (NoSuchMethodException var9) {
                  }
               }
            }
         }
      }

      return null;
   }

   MemberBox(CachedExecutableInfo executableInfo) {
      this.executableInfo = executableInfo;
   }

   MemberBox(WrappedExecutable wrappedExecutable) {
      CachedExecutableInfo executable = wrappedExecutable.unwrap();
      if (executable != null) {
         this.executableInfo = executable;
      } else {
         this.wrappedExecutable = wrappedExecutable;
      }
   }

   @Nullable
   public CachedExecutableInfo getInfo() {
      return this.executableInfo;
   }

   public CachedParameters parameters() {
      return this.executableInfo == null ? CachedParameters.EMPTY : this.executableInfo.getParameters();
   }

   boolean isMethod() {
      return this.executableInfo instanceof CachedMethodInfo;
   }

   boolean isCtor() {
      return this.executableInfo instanceof CachedConstructorInfo;
   }

   boolean isStatic() {
      return this.executableInfo.isStatic;
   }

   String getName() {
      return this.wrappedExecutable != null ? this.wrappedExecutable.toString() : this.executableInfo.getName();
   }

   TypeInfo getReturnType() {
      return this.wrappedExecutable != null ? this.wrappedExecutable.getReturnType() : this.executableInfo.getReturnType();
   }

   String toJavaDeclaration() {
      return this.getReturnType() + " " + this.getName() + JavaMembers.liveConnectSignature(this.parameters().types());
   }

   @Override
   public String toString() {
      return this.getName();
   }

   boolean isSameGetterFunction(Context cx, Object function) {
      Object f = this.asGetterFunction == null ? Undefined.INSTANCE : this.asGetterFunction;
      return ScriptRuntime.shallowEq(cx, function, f);
   }

   boolean isSameSetterFunction(Context cx, Object function) {
      Object f = this.asSetterFunction == null ? Undefined.INSTANCE : this.asSetterFunction;
      return ScriptRuntime.shallowEq(cx, function, f);
   }

   Function asGetterFunction(Context cx, final String name, Scriptable scope) {
      if (this.asGetterFunction == null) {
         final MemberBox self = this;
         this.asGetterFunction = new BaseFunction(scope, ScriptableObject.getFunctionPrototype(scope, cx)) {
            @Override
            public Object call(Context cx, Scriptable scopex, Scriptable thisObj, Object[] args) {
               Object getterThis;
               Object[] callArgs;
               if (self.delegateTo == null) {
                  getterThis = thisObj;
                  callArgs = ScriptRuntime.EMPTY_OBJECTS;
               } else {
                  getterThis = self.delegateTo;
                  callArgs = new Object[]{thisObj};
               }

               return self.invoke(getterThis, callArgs, cx, scopex);
            }

            @Override
            public String getFunctionName() {
               return name;
            }
         };
      }

      return this.asGetterFunction;
   }

   Function asSetterFunction(Context cx, final String name, Scriptable scope) {
      if (this.asSetterFunction == null) {
         final MemberBox self = this;
         this.asSetterFunction = new BaseFunction(scope, ScriptableObject.getFunctionPrototype(scope, cx)) {
            @Override
            public Object call(Context cx, Scriptable scopex, Scriptable thisObj, Object[] args) {
               Object value = args.length > 0 ? args[0] : Undefined.INSTANCE;
               Object setterThis;
               Object[] callArgs;
               if (self.delegateTo == null) {
                  setterThis = thisObj;
                  callArgs = new Object[]{value};
               } else {
                  setterThis = self.delegateTo;
                  callArgs = new Object[]{thisObj, value};
               }

               return self.invoke(setterThis, callArgs, cx, scopex);
            }

            @Override
            public String getFunctionName() {
               return name;
            }
         };
      }

      return this.asSetterFunction;
   }

   Object invoke(Object target, Object[] args, Context cx, Scriptable scope) {
      if (this.wrappedExecutable != null) {
         try {
            return this.wrappedExecutable.invoke(cx, scope, target, args);
         } catch (Throwable var7) {
            throw Context.throwAsScriptRuntimeEx(var7, cx);
         }
      } else {
         try {
            return this.executableInfo.invoke(cx, scope, target, args);
         } catch (InvocationTargetException var8) {
            Throwable e = var8;

            do {
               e = ((InvocationTargetException)e).getTargetException();
            } while (e instanceof InvocationTargetException);

            throw Context.throwAsScriptRuntimeEx(e, cx);
         } catch (Throwable var9) {
            throw Context.throwAsScriptRuntimeEx(var9, cx);
         }
      }
   }

   Object newInstance(Object[] args, Context cx, Scriptable scope) {
      if (this.wrappedExecutable != null) {
         try {
            return this.wrappedExecutable.construct(cx, scope, args);
         } catch (Throwable var5) {
            throw Context.throwAsScriptRuntimeEx(var5, cx);
         }
      } else {
         try {
            return this.executableInfo.invoke(cx, scope, null, args);
         } catch (Throwable var6) {
            throw Context.throwAsScriptRuntimeEx(var6, cx);
         }
      }
   }
}
