package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.type.TypeInfo;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashSet;

public class InterfaceAdapter {
   private final Object proxyHelper;

   static Object create(Context cx, Class<?> cl, ScriptableObject object) {
      if (!cl.isInterface()) {
         throw new IllegalArgumentException();
      } else {
         Scriptable topScope = cx.getTopCallOrThrow();
         InterfaceAdapter adapter = (InterfaceAdapter)cx.getInterfaceAdapter(cl);
         if (adapter == null) {
            if (object instanceof Callable) {
               Method[] methods = cl.getMethods();
               HashSet<String> functionalMethodNames = new HashSet<>();
               HashSet<String> defaultMethodNames = new HashSet<>();

               for (Method method : methods) {
                  if (isFunctionalMethodCandidate(method)) {
                     functionalMethodNames.add(method.getName());
                     if (functionalMethodNames.size() > 1) {
                        break;
                     }
                  } else {
                     defaultMethodNames.add(method.getName());
                  }
               }

               boolean canConvert = functionalMethodNames.size() == 1 || functionalMethodNames.isEmpty() && defaultMethodNames.size() == 1;
               if (!canConvert) {
                  if (functionalMethodNames.isEmpty() && defaultMethodNames.isEmpty()) {
                     throw Context.reportRuntimeError1("msg.no.empty.interface.conversion", cl.getName(), cx);
                  }

                  throw Context.reportRuntimeError1("msg.no.function.interface.conversion", cl.getName(), cx);
               }
            }

            adapter = new InterfaceAdapter(cx, cl);
            cx.cacheInterfaceAdapter(cl, adapter);
         }

         return VMBridge.newInterfaceProxy(adapter.proxyHelper, adapter, object, topScope, cx);
      }
   }

   private static boolean isFunctionalMethodCandidate(Method method) {
      return !method.getName().equals("equals") && !method.getName().equals("hashCode") && !method.getName().equals("toString")
         ? Modifier.isAbstract(method.getModifiers())
         : false;
   }

   private InterfaceAdapter(Context cx, Class<?> cl) {
      this.proxyHelper = VMBridge.getInterfaceProxyHelper(cx, new Class[]{cl});
   }

   public Object invoke(Context cx, Object target, Scriptable topScope, Object thisObject, Method method, Object[] args) {
      if (!(target instanceof Callable functionx)) {
         Scriptable s = (Scriptable)target;
         String methodName = method.getName();
         Object value = ScriptableObject.getProperty(s, methodName, cx);
         if (value == Scriptable.NOT_FOUND) {
            Context.reportWarning(ScriptRuntime.getMessage1("msg.undefined.function.interface", methodName), cx);
            Class<?> resultType = method.getReturnType();
            if (resultType == void.class) {
               return null;
            }

            return cx.jsToJava(null, TypeInfo.safeOf(method::getGenericReturnType));
         }

         if (!(value instanceof Callable functionx)) {
            throw Context.reportRuntimeError1("msg.not.function.interface", methodName, cx);
         }
      }

      if (args != null && args.length != 0) {
         int i = 0;

         for (int N = args.length; i != N; i++) {
            Object arg = args[i];
            if (!(arg instanceof String) && !(arg instanceof Number) && !(arg instanceof Boolean)) {
               args[i] = cx.wrap(topScope, arg);
            }
         }
      } else {
         args = ScriptRuntime.EMPTY_OBJECTS;
      }

      Scriptable thisObj = cx.wrapAsJavaObject(topScope, thisObject, TypeInfo.NONE);
      Object result = cx.callSync(functionx, topScope, thisObj, args);
      Type javaResultType = void.class;

      try {
         javaResultType = method.getGenericReturnType();
      } catch (Throwable var12) {
      }

      if (javaResultType == void.class) {
         result = null;
      } else {
         result = cx.jsToJava(result, TypeInfo.of(javaResultType));
      }

      return result;
   }
}
