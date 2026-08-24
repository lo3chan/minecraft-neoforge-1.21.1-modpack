package dev.latvian.mods.rhino;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class VMBridge {
   public static boolean tryToMakeAccessible(Object target, AccessibleObject accessible) {
      if (accessible.canAccess(target)) {
         return true;
      } else {
         try {
            accessible.setAccessible(true);
         } catch (Exception var3) {
         }

         return accessible.canAccess(target);
      }
   }

   public static Object getInterfaceProxyHelper(Context cx, Class<?>[] interfaces) {
      ClassLoader loader = interfaces[0].getClassLoader();
      Class<?> cl = Proxy.getProxyClass(loader, interfaces);

      try {
         return cl.getConstructor(InvocationHandler.class);
      } catch (NoSuchMethodException var6) {
         throw new IllegalStateException(var6);
      }
   }

   public static Object newInterfaceProxy(Object proxyHelper, InterfaceAdapter adapter, Object target, Scriptable topScope, Context cx) {
      Constructor<?> c = (Constructor<?>)proxyHelper;

      try {
         return c.newInstance(new VMBridge.AdapterInvocationHandler(cx, adapter, target, topScope));
      } catch (InvocationTargetException var7) {
         throw Context.throwAsScriptRuntimeEx(var7, cx);
      } catch (InstantiationException | IllegalAccessException var8) {
         throw new IllegalStateException(var8);
      }
   }

   public record AdapterInvocationHandler(Context cx, InterfaceAdapter adapter, Object target, Scriptable topScope) implements InvocationHandler {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         if (method.getDeclaringClass() == Object.class) {
            String methodName = method.getName();
            switch (methodName) {
               case "equals":
                  return proxy == args[0];
               case "hashCode":
                  return this.target.hashCode();
               case "toString":
                  return "Proxy[" + this.target + "]";
            }
         }

         return method.isDefault() && !this.hasJSImplementation(method)
            ? InvocationHandler.invokeDefault(proxy, method, args)
            : this.adapter.invoke(this.cx, this.target, this.topScope, proxy, method, args);
      }

      private boolean hasJSImplementation(Method method) {
         return this.target instanceof Callable
            ? false
            : this.target instanceof Scriptable s && ScriptableObject.getProperty(s, method.getName(), this.cx) instanceof Callable;
      }
   }
}
