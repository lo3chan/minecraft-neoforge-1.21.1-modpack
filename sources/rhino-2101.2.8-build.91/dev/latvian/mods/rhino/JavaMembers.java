package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.ClassVisibilityContext;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class JavaMembers {
   private final Class<?> cl;
   private final Map<String, Object> members;
   private final Map<String, Object> staticMembers;
   NativeJavaMethod ctors;
   private Map<String, FieldAndMethods> fieldAndMethods;
   private Map<String, FieldAndMethods> staticFieldAndMethods;

   public static String javaSignature(Class<?> type) {
      if (!type.isArray()) {
         return type.getName();
      } else {
         int arrayDimension = 0;

         do {
            arrayDimension++;
            type = type.getComponentType();
         } while (type.isArray());

         String name = type.getName();
         String suffix = "[]";
         if (arrayDimension == 1) {
            return name.concat(suffix);
         } else {
            int length = name.length() + arrayDimension * suffix.length();
            StringBuilder sb = new StringBuilder(length);
            sb.append(name);

            while (arrayDimension != 0) {
               arrayDimension--;
               sb.append(suffix);
            }

            return sb.toString();
         }
      }
   }

   public static String liveConnectSignature(List<Class<?>> argTypes) {
      int N = argTypes.size();
      if (N == 0) {
         return "()";
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append('(');

         for (int i = 0; i != N; i++) {
            if (i != 0) {
               sb.append(',');
            }

            sb.append(javaSignature(argTypes.get(i)));
         }

         sb.append(')');
         return sb.toString();
      }
   }

   private static MemberBox findGetter(boolean isStatic, Map<String, Object> ht, String prefix, String propertyName) {
      String getterName = prefix.concat(propertyName);
      return ht.containsKey(getterName) && ht.get(getterName) instanceof NativeJavaMethod njmGet ? extractGetMethod(njmGet.methods, isStatic) : null;
   }

   private static MemberBox extractGetMethod(MemberBox[] methods, boolean isStatic) {
      for (MemberBox method : methods) {
         if (method.parameters().count() == 0 && (!isStatic || method.isStatic())) {
            if (method.getReturnType() != TypeInfo.PRIMITIVE_VOID) {
               return method;
            }
            break;
         }
      }

      return null;
   }

   private static MemberBox extractSetMethod(TypeInfo type0, MemberBox[] methods, boolean isStatic) {
      Class<?> type = type0.asClass();

      for (int pass = 1; pass <= 2; pass++) {
         for (MemberBox method : methods) {
            if (!isStatic || method.isStatic()) {
               List<Class<?>> params = method.parameters().types();
               if (params.size() == 1) {
                  if (pass == 1) {
                     if (params.getFirst() == type) {
                        return method;
                     }
                  } else {
                     if (pass != 2) {
                        Kit.codeBug();
                     }

                     if (((Class)params.getFirst()).isAssignableFrom(type)) {
                        return method;
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   private static MemberBox extractSetMethod(MemberBox[] methods, boolean isStatic) {
      for (MemberBox method : methods) {
         if ((!isStatic || method.isStatic()) && method.getReturnType() == TypeInfo.PRIMITIVE_VOID && method.parameters().count() == 1) {
            return method;
         }
      }

      return null;
   }

   public static JavaMembers lookupClass(Context cx, Scriptable scope, Class<?> dynamicType, Class<?> staticType, boolean includeProtected) {
      Map<Class<?>, JavaMembers> ct = cx.getClassCacheMap();
      Class<?> cl = dynamicType;

      while (true) {
         JavaMembers members = ct.get(cl);
         if (members != null) {
            if (cl != dynamicType) {
               ct.put(dynamicType, members);
            }

            return members;
         }

         try {
            members = new JavaMembers(cl, includeProtected, cx, ScriptableObject.getTopLevelScope(scope));
         } catch (SecurityException var10) {
            if (staticType != null && staticType.isInterface()) {
               cl = staticType;
               staticType = null;
               continue;
            }

            Class<?> parent = cl.getSuperclass();
            if (parent == null) {
               if (!cl.isInterface()) {
                  throw var10;
               }

               parent = ScriptRuntime.ObjectClass;
            }

            cl = parent;
            continue;
         }

         ct.put(cl, members);
         if (cl != dynamicType) {
            ct.put(dynamicType, members);
         }

         return members;
      }
   }

   JavaMembers(Class<?> cl, boolean includeProtected, Context cx, Scriptable scope) {
      if (!cx.visibleToScripts(cl.getName(), ClassVisibilityContext.MEMBER)) {
         throw Context.reportRuntimeError1("msg.access.prohibited", cl.getName(), cx);
      } else {
         this.members = new HashMap<>();
         this.staticMembers = new HashMap<>();
         this.cl = cl;
         this.reflect(scope, includeProtected, cx);
      }
   }

   public boolean has(Context cx, String name, boolean isStatic) {
      Map<String, Object> ht = isStatic ? this.staticMembers : this.members;
      Object obj = ht.get(name);
      return obj != null ? true : this.findExplicitFunction(cx, name, isStatic) != null;
   }

   public Object get(Scriptable scope, String name, Object javaObject, boolean isStatic, Context cx) {
      Map<String, Object> ht = isStatic ? this.staticMembers : this.members;
      Object member = ht.get(name);
      if (!isStatic && member == null && cx.factory.getInstanceStaticFallback()) {
         member = this.staticMembers.get(name);
      }

      if (member == null) {
         member = this.getExplicitFunction(scope, name, javaObject, isStatic, cx);
         if (member == null) {
            return Scriptable.NOT_FOUND;
         }
      }

      if (member instanceof Scriptable) {
         return member;
      } else {
         Object rval;
         TypeInfo type;
         try {
            if (member instanceof BeanProperty bp) {
               if (bp.getter == null) {
                  return Scriptable.NOT_FOUND;
               }

               rval = bp.getter.invoke(javaObject, ScriptRuntime.EMPTY_OBJECTS, cx, scope);
               type = bp.getter.getReturnType();
            } else {
               CachedFieldInfo fieldInfo = (CachedFieldInfo)member;
               rval = fieldInfo.get(cx, isStatic ? null : javaObject);
               type = fieldInfo.getType();
            }
         } catch (Throwable var12) {
            throw Context.throwAsScriptRuntimeEx(var12, cx);
         }

         scope = ScriptableObject.getTopLevelScope(scope);
         return cx.wrap(scope, rval, type);
      }
   }

   public void put(Scriptable scope, String name, Object javaObject, Object value, boolean isStatic, Context cx) {
      Map<String, Object> ht = isStatic ? this.staticMembers : this.members;
      Object member = ht.get(name);
      if (!isStatic && member == null && cx.factory.getInstanceStaticFallback()) {
         member = this.staticMembers.get(name);
      }

      if (member == null) {
         throw this.reportMemberNotFound(name, cx);
      } else {
         if (member instanceof FieldAndMethods fam) {
            member = fam.fieldInfo;
         }

         if (member instanceof BeanProperty bp) {
            if (bp.setter == null) {
               throw this.reportMemberNotFound(name, cx);
            }

            if (bp.setters != null && value != null) {
               Object[] args = new Object[]{value};
               cx.callSync(bp.setters, ScriptableObject.getTopLevelScope(scope), scope, args);
            } else {
               Object[] args = new Object[]{cx.jsToJava(value, (TypeInfo)bp.setter.parameters().typeInfos().getFirst())};

               try {
                  bp.setter.invoke(javaObject, args, cx, scope);
               } catch (Exception var15) {
                  throw Context.throwAsScriptRuntimeEx(var15, cx);
               }
            }
         } else {
            if (!(member instanceof CachedFieldInfo fieldInfo)) {
               if (member == null) {
                  throw Context.reportRuntimeError3("msg.java.internal.private.set", name, String.valueOf(javaObject), this.cl.getName(), cx);
               }

               throw Context.reportRuntimeError2("msg.java.method.assign", name, this.cl.getName(), cx);
            }

            if (fieldInfo.isFinal) {
               throw Context.throwAsScriptRuntimeEx(new IllegalAccessException("Can't modify final field " + fieldInfo.getName()), cx);
            }

            TypeInfo type = fieldInfo.getType();
            if (scope instanceof NativeJavaObject nativeJavaObject) {
               type = type.consolidate(nativeJavaObject.getTypeMapping());
            }

            try {
               fieldInfo.set(cx, javaObject, cx.jsToJava(value, type));
            } catch (IllegalArgumentException var13) {
               throw Context.reportRuntimeError3(
                  "msg.java.internal.field.type", value.getClass().getName(), fieldInfo.getType(), javaObject.getClass().getName(), cx
               );
            } catch (Throwable var14) {
               throw Context.throwAsScriptRuntimeEx(var14, cx);
            }
         }
      }
   }

   public Object[] getIds(boolean isStatic) {
      Map<String, Object> map = isStatic ? this.staticMembers : this.members;
      return map.keySet().toArray(ScriptRuntime.EMPTY_OBJECTS);
   }

   private MemberBox findExplicitFunction(Context cx, String name, boolean isStatic) {
      int sigStart = name.indexOf(40);
      if (sigStart < 0) {
         return null;
      } else {
         Map<String, Object> ht = isStatic ? this.staticMembers : this.members;
         MemberBox[] methodsOrCtors = null;
         boolean isCtor = isStatic && sigStart == 0;
         if (isCtor) {
            methodsOrCtors = this.ctors.methods;
         } else {
            String trueName = name.substring(0, sigStart);
            Object obj = ht.get(trueName);
            if (!isStatic && obj == null && cx.factory.getInstanceStaticFallback()) {
               obj = this.staticMembers.get(trueName);
            }

            if (obj instanceof NativeJavaMethod njm) {
               methodsOrCtors = njm.methods;
            }
         }

         if (methodsOrCtors != null) {
            for (MemberBox methodsOrCtor : methodsOrCtors) {
               List<Class<?>> type = methodsOrCtor.parameters().types();
               String sig = liveConnectSignature(type);
               if (sigStart + sig.length() == name.length() && name.regionMatches(sigStart, sig, 0, sig.length())) {
                  return methodsOrCtor;
               }
            }
         }

         return null;
      }
   }

   private Object getExplicitFunction(Scriptable scope, String name, Object javaObject, boolean isStatic, Context cx) {
      Map<String, Object> ht = isStatic ? this.staticMembers : this.members;
      Object member = null;
      MemberBox methodOrCtor = this.findExplicitFunction(cx, name, isStatic);
      if (methodOrCtor != null) {
         Scriptable prototype = ScriptableObject.getFunctionPrototype(scope, cx);
         if (methodOrCtor.isCtor()) {
            NativeJavaConstructor fun = new NativeJavaConstructor(methodOrCtor);
            fun.setPrototype(prototype);
            member = fun;
            ht.put(name, fun);
         } else {
            String trueName = methodOrCtor.getName();
            member = ht.get(trueName);
            if (member instanceof NativeJavaMethod && ((NativeJavaMethod)member).methods.length > 1) {
               NativeJavaMethod fun = new NativeJavaMethod(methodOrCtor, name);
               fun.setPrototype(prototype);
               ht.put(name, fun);
               member = fun;
            }
         }
      }

      return member;
   }

   private void reflect(Scriptable scope, boolean includeProtected, Context cx) {
      if (this.cl.isAnnotationPresent(HideFromJS.class)) {
         this.ctors = new NativeJavaMethod(new MemberBox[0], this.cl.getSimpleName());
      } else {
         CachedClassStorage storage = cx.getCachedClassStorage(includeProtected);
         CachedClassInfo classInfo = storage.get(this.cl);

         for (CachedMethodInfo.Accessible methodInfo0 : classInfo.getAccessibleMethods(includeProtected)) {
            CachedMethodInfo methodInfo = methodInfo0.getInfo();
            String name = methodInfo0.getName();
            Map<String, Object> ht = methodInfo.isStatic ? this.staticMembers : this.members;
            Object value = ht.get(name);
            if (value == null) {
               ht.put(name, methodInfo);
            } else {
               ObjArray overloadedMethods;
               if (value instanceof ObjArray) {
                  overloadedMethods = (ObjArray)value;
               } else {
                  if (!(value instanceof CachedMethodInfo)) {
                     Kit.codeBug();
                  }

                  overloadedMethods = new ObjArray();
                  overloadedMethods.add(value);
                  ht.put(name, overloadedMethods);
               }

               overloadedMethods.add(methodInfo);
            }
         }

         for (int tableCursor = 0; tableCursor != 2; tableCursor++) {
            boolean isStatic = tableCursor == 0;
            Map<String, Object> ht = isStatic ? this.staticMembers : this.members;

            for (Entry<String, Object> entry : ht.entrySet()) {
               Object value = entry.getValue();
               MemberBox[] methodBoxes;
               if (value instanceof CachedMethodInfo methodInfo) {
                  methodBoxes = new MemberBox[1];
                  methodBoxes[0] = new MemberBox(methodInfo);
               } else {
                  ObjArray overloadedMethods = (ObjArray)value;
                  int N = overloadedMethods.size();
                  if (N < 2) {
                     Kit.codeBug();
                  }

                  methodBoxes = new MemberBox[N];

                  for (int i = 0; i != N; i++) {
                     CachedMethodInfo method = (CachedMethodInfo)overloadedMethods.get(i);
                     methodBoxes[i] = new MemberBox(method);
                  }
               }

               NativeJavaMethod fun = new NativeJavaMethod(methodBoxes);
               if (scope != null) {
                  ScriptRuntime.setFunctionProtoAndParent(cx, scope, fun);
               }

               ht.put(entry.getKey(), fun);
            }
         }

         for (CachedFieldInfo.Accessible fieldInfo0 : classInfo.getAccessibleFields(includeProtected)) {
            CachedFieldInfo fieldInfo = fieldInfo0.getInfo();
            String name = fieldInfo0.getName();

            try {
               Map<String, Object> ht = fieldInfo.isStatic ? this.staticMembers : this.members;
               Object member = ht.get(name);
               if (member == null) {
                  ht.put(name, fieldInfo);
               } else if (member instanceof NativeJavaMethod method) {
                  FieldAndMethods fam = new FieldAndMethods(scope, method.methods, fieldInfo, cx);
                  Map<String, FieldAndMethods> fmht = fieldInfo.isStatic ? this.staticFieldAndMethods : this.fieldAndMethods;
                  if (fmht == null) {
                     fmht = new HashMap<>();
                     if (fieldInfo.isStatic) {
                        this.staticFieldAndMethods = fmht;
                     } else {
                        this.fieldAndMethods = fmht;
                     }
                  }

                  fmht.put(name, fam);
                  ht.put(name, fam);
               } else if (member instanceof CachedFieldInfo oldFieldInfo) {
                  if (oldFieldInfo.getDeclaringClass().type.isAssignableFrom(fieldInfo.getDeclaringClass().type)) {
                     ht.put(name, fieldInfo);
                  }
               } else {
                  Kit.codeBug();
               }
            } catch (SecurityException var26) {
               Context.reportWarning("Could not access field " + name + " of class " + this.cl.getName() + " due to lack of privileges.", cx);
            }
         }

         for (int tableCursor = 0; tableCursor != 2; tableCursor++) {
            boolean isStatic = tableCursor == 0;
            Map<String, Object> ht = isStatic ? this.staticMembers : this.members;
            Map<String, BeanProperty> toAdd = new HashMap<>();

            for (String name : ht.keySet()) {
               boolean memberIsGetMethod = name.startsWith("get");
               boolean memberIsSetMethod = name.startsWith("set");
               boolean memberIsIsMethod = name.startsWith("is");
               if (memberIsGetMethod || memberIsIsMethod || memberIsSetMethod) {
                  String nameComponent = name.substring(memberIsIsMethod ? 2 : 3);
                  if (nameComponent.length() != 0) {
                     String beanPropertyName = nameComponent;
                     char ch0 = nameComponent.charAt(0);
                     if (Character.isUpperCase(ch0)) {
                        if (nameComponent.length() == 1) {
                           beanPropertyName = nameComponent.toLowerCase(Locale.ROOT);
                        } else {
                           char ch1 = nameComponent.charAt(1);
                           if (!Character.isUpperCase(ch1)) {
                              beanPropertyName = Character.toLowerCase(ch0) + nameComponent.substring(1);
                           }
                        }
                     }

                     if (!toAdd.containsKey(beanPropertyName)) {
                        Object v = ht.get(beanPropertyName);
                        if (v == null) {
                           MemberBox getter = findGetter(isStatic, ht, "get", nameComponent);
                           if (getter == null) {
                              getter = findGetter(isStatic, ht, "is", nameComponent);
                           }

                           MemberBox setter = null;
                           NativeJavaMethod setters = null;
                           String setterName = "set".concat(nameComponent);
                           if (ht.containsKey(setterName) && ht.get(setterName) instanceof NativeJavaMethod njmSet) {
                              if (getter != null) {
                                 TypeInfo type = getter.getReturnType();
                                 setter = extractSetMethod(type, njmSet.methods, isStatic);
                              } else {
                                 setter = extractSetMethod(njmSet.methods, isStatic);
                              }

                              if (njmSet.methods.length > 1) {
                                 setters = njmSet;
                              }
                           }

                           BeanProperty bp = new BeanProperty(getter, setter, setters);
                           toAdd.put(beanPropertyName, bp);
                        }
                     }
                  }
               }
            }

            ht.putAll(toAdd);
         }

         List<CachedConstructorInfo> constructors = classInfo.getConstructors();
         MemberBox[] ctorMembers = new MemberBox[constructors.size()];

         for (int i = 0; i != constructors.size(); i++) {
            ctorMembers[i] = new MemberBox(constructors.get(i));
         }

         this.ctors = new NativeJavaMethod(ctorMembers, this.cl.getSimpleName());
      }
   }

   public List<Constructor<?>> getAccessibleConstructors() {
      List<Constructor<?>> constructorsList = new ArrayList<>();

      for (Constructor<?> c : this.cl.getConstructors()) {
         if (!c.isAnnotationPresent(HideFromJS.class) && Modifier.isPublic(c.getModifiers())) {
            constructorsList.add(c);
         }
      }

      return constructorsList;
   }

   @Deprecated(
      forRemoval = true
   )
   public Collection<JavaMembers.FieldInfo> getAccessibleFields(Context cx, boolean includeProtected) {
      List<CachedFieldInfo.Accessible> list0 = cx.getCachedClassStorage(includeProtected).get(this.cl).getAccessibleFields(includeProtected);
      ArrayList<JavaMembers.FieldInfo> list = new ArrayList<>(list0.size());

      for (CachedFieldInfo.Accessible f : list0) {
         list.add(new JavaMembers.FieldInfo(f));
      }

      return list;
   }

   @Deprecated(
      forRemoval = true
   )
   public Collection<JavaMembers.MethodInfo> getAccessibleMethods(Context cx, boolean includeProtected) {
      List<CachedMethodInfo.Accessible> list0 = cx.getCachedClassStorage(includeProtected).get(this.cl).getAccessibleMethods(includeProtected);
      ArrayList<JavaMembers.MethodInfo> list = new ArrayList<>(list0.size());

      for (CachedMethodInfo.Accessible m : list0) {
         list.add(new JavaMembers.MethodInfo(m));
      }

      return list;
   }

   public Map<String, FieldAndMethods> getFieldAndMethodsObjects(Scriptable scope, Object javaObject, boolean isStatic, Context cx) {
      Map<String, FieldAndMethods> ht = isStatic ? this.staticFieldAndMethods : this.fieldAndMethods;
      if (ht == null) {
         return null;
      } else {
         int len = ht.size();
         HashMap<String, FieldAndMethods> result = new HashMap<>(len);

         for (FieldAndMethods fam : ht.values()) {
            FieldAndMethods famNew = new FieldAndMethods(scope, fam.methods, fam.fieldInfo, cx);
            famNew.javaObject = javaObject;
            result.put(fam.fieldInfo.getName(), famNew);
         }

         return result;
      }
   }

   RuntimeException reportMemberNotFound(String memberName, Context cx) {
      return Context.reportRuntimeError2("msg.java.member.not.found", this.cl.getName(), memberName, cx);
   }

   @Deprecated(
      forRemoval = true
   )
   public static class FieldInfo {
      public final CachedFieldInfo.Accessible cached;
      public final Field field;
      public final String name;

      public FieldInfo(CachedFieldInfo.Accessible cached) {
         this.cached = cached;
         this.field = cached.getInfo().field;
         this.name = cached.getName();
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public static class MethodInfo {
      public final CachedMethodInfo.Accessible cached;
      public final Method method;
      public final String name;
      public final boolean hidden;

      public MethodInfo(CachedMethodInfo.Accessible cached) {
         this.cached = cached;
         this.method = cached.getInfo().method;
         this.name = cached.getName();
         this.hidden = cached.isHidden();
      }
   }
}
