package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class CachedClassInfo {
   private static final HashSet<String> IGNORED_DEBUG_METHODS = new HashSet<>();
   public final CachedClassStorage storage;
   public final Class<?> type;
   public final int modifiers;
   public final boolean isInterface;
   private TypeInfo typeInfo;
   private Set<String> remapPrefixes;
   private CachedClassInfo superclass;
   private List<CachedClassInfo> interfaces;
   private List<CachedConstructorInfo> constructors;
   private List<CachedFieldInfo> declaredFields;
   private List<CachedMethodInfo> declaredMethods;
   private List<CachedFieldInfo.Accessible> accessibleFields;
   private List<CachedMethodInfo.Accessible> accessibleMethods;

   public CachedClassInfo(CachedClassStorage storage, Class<?> type) {
      this.storage = storage;
      this.type = type;
      this.modifiers = type.getModifiers();
      this.isInterface = type.isInterface();
   }

   public TypeInfo getTypeInfo() {
      if (this.typeInfo == null) {
         this.typeInfo = TypeInfo.of(this.type);
      }

      return this.typeInfo;
   }

   public Set<String> getRemapPrefixes() {
      if (this.remapPrefixes == null) {
         this.remapPrefixes = new HashSet<>(0);

         for (RemapPrefixForJS r : this.type.getAnnotationsByType(RemapPrefixForJS.class)) {
            String s = r.value().trim();
            if (!s.isEmpty()) {
               this.remapPrefixes.add(s);
            }
         }

         this.remapPrefixes = Set.copyOf(this.remapPrefixes);
      }

      return this.remapPrefixes;
   }

   public CachedClassInfo getSuperclass() {
      if (this.superclass == null) {
         this.superclass = this.storage.get(this.type.getSuperclass());
      }

      return this.superclass;
   }

   public List<CachedClassInfo> getInterfaces() {
      List<CachedClassInfo> il = this.interfaces;
      if (il == null) {
         il = new ArrayList<>(0);

         for (Class<?> i : this.type.getInterfaces()) {
            il.add(this.storage.get(i));
         }

         this.interfaces = List.copyOf(il);
      }

      return il;
   }

   public List<CachedConstructorInfo> getConstructors() {
      List<CachedConstructorInfo> list = this.constructors;
      if (list == null) {
         if (!this.storage.isVisible(this.modifiers)) {
            return this.constructors = List.of();
         }

         list = new ArrayList<>(1);

         for (Constructor<?> constructor : this.type.getConstructors()) {
            int mods = constructor.getModifiers();
            if (Modifier.isPublic(mods) && !Modifier.isAbstract(mods)) {
               CachedConstructorInfo info = new CachedConstructorInfo(this, constructor);
               if (!info.isHidden) {
                  list.add(info);
               }
            }
         }

         this.constructors = List.copyOf(list);
      }

      return list;
   }

   public List<CachedFieldInfo> getDeclaredFields() {
      List<CachedFieldInfo> list = this.declaredFields;
      if (list == null) {
         if (!this.storage.isVisible(this.modifiers)) {
            return this.declaredFields = List.of();
         }

         list = new ArrayList<>();

         try {
            for (Field field : this.type.getDeclaredFields()) {
               if (this.storage.include(this.type, field)) {
                  try {
                     list.add(new CachedFieldInfo(this, field));
                  } catch (Throwable var10) {
                  }
               }
            }
         } catch (Throwable var12) {
            System.err.println("[Rhino] Failed to get declared fields for " + this.type.getName() + ": " + var12);

            try {
               for (Field fieldx : this.type.getFields()) {
                  int mods = fieldx.getModifiers();
                  if (this.storage.include(this.type, fieldx)) {
                     try {
                        if (this.storage.includeProtected && Modifier.isProtected(mods) && !fieldx.isAccessible()) {
                           fieldx.setAccessible(true);
                        }

                        list.add(new CachedFieldInfo(this, fieldx));
                     } catch (Throwable var9) {
                     }
                  }
               }
            } catch (Throwable var11) {
               System.err.println("[Rhino] Failed to get declared fields for " + this.type.getName() + " again: " + var11);
            }
         }

         this.declaredFields = List.copyOf(list);
      }

      return list;
   }

   public List<CachedMethodInfo> getDeclaredMethods() {
      List<CachedMethodInfo> list = this.declaredMethods;
      if (list == null) {
         if (!this.storage.isVisible(this.modifiers)) {
            return this.declaredMethods = List.of();
         }

         list = new ArrayList<>();

         try {
            for (Method method : this.type.getDeclaredMethods()) {
               if (this.storage.include(this.type, method)) {
                  list.add(new CachedMethodInfo(this, method));
               }
            }
         } catch (Throwable var8) {
            System.err.println("[Rhino] Failed to get declared methods for " + this.type.getName() + ": " + var8);
            list.clear();

            try {
               for (Method methodx : this.type.getMethods()) {
                  if (this.storage.include(this.type, methodx)) {
                     list.add(new CachedMethodInfo(this, methodx));
                  }
               }
            } catch (Throwable var7) {
               System.err.println("[Rhino] Failed to get declared methods for " + this.type.getName() + " again: " + var7);
            }
         }

         this.declaredMethods = List.copyOf(list);
      }

      return list;
   }

   public List<CachedFieldInfo.Accessible> getAccessibleFields(boolean cache) {
      List<CachedFieldInfo.Accessible> list = this.accessibleFields;
      if (list == null) {
         HashMap<String, CachedFieldInfo.Accessible> map = new HashMap<>();
         boolean anyHidden = false;

         for (CachedClassInfo current = this; current != this.storage.objectClass; current = current.getSuperclass()) {
            for (CachedFieldInfo info : current.getDeclaredFields()) {
               CachedFieldInfo.Accessible accessible = map.get(info.originalName);
               if (accessible == null) {
                  accessible = new CachedFieldInfo.Accessible();
                  accessible.info = info;
                  map.put(info.originalName, accessible);
               }

               if (info.isHidden) {
                  anyHidden = true;
                  accessible.hidden = true;
               }

               if (accessible.name.isEmpty()) {
                  accessible.name = info.rename;
               }
            }
         }

         if (anyHidden) {
            map.values().removeIf(CachedFieldInfo.Accessible::isHidden);
         }

         list = List.copyOf(map.values());

         for (CachedFieldInfo.Accessible v : list) {
            if (v.name.isEmpty()) {
               v.name = v.getInfo().getName();
            }
         }

         if (cache) {
            this.accessibleFields = list;
         }
      }

      return list;
   }

   public List<CachedMethodInfo.Accessible> getAccessibleMethods(boolean cache) {
      List<CachedMethodInfo.Accessible> list = this.accessibleMethods;
      if (list == null) {
         LinkedHashMap<MethodSignature, CachedMethodInfo.Accessible> map = new LinkedHashMap<>();
         boolean anyHidden = false;
         ArrayDeque<CachedClassInfo> stack = new ArrayDeque<>();
         stack.add(this);

         while (!stack.isEmpty()) {
            CachedClassInfo current = stack.pop();

            for (CachedMethodInfo info : current.getDeclaredMethods()) {
               MethodSignature signature = info.getSignature();
               CachedMethodInfo.Accessible accessible = map.get(signature);
               if (accessible == null) {
                  accessible = new CachedMethodInfo.Accessible();
                  accessible.info = info;
                  accessible.signature = signature;
                  map.put(signature, accessible);
               } else if (info.method.getReturnType() != accessible.info.method.getReturnType()
                  && accessible.info.method.getReturnType().isAssignableFrom(info.method.getReturnType())) {
                  accessible.info = info;
               }

               if (info.isHidden) {
                  anyHidden = true;
                  accessible.hidden = true;
               }

               if (accessible.name.isEmpty()) {
                  accessible.name = info.rename;
               }
            }

            stack.addAll(current.getInterfaces());
            CachedClassInfo parent = current.getSuperclass();
            if (parent != this.storage.objectClass) {
               stack.add(parent);
            }
         }

         if (anyHidden) {
            map.values().removeIf(CachedMethodInfo.Accessible::isHidden);
         }

         list = List.copyOf(map.values());

         for (CachedMethodInfo.Accessible v : list) {
            if (v.name.isEmpty()) {
               v.name = v.getInfo().getName();
            }
         }

         if (cache) {
            this.accessibleMethods = list;
         }
      }

      return list;
   }

   @Override
   public String toString() {
      return this.type.getName();
   }

   public CachedMethodInfo getMethod(String name, Class<?>[] params) throws NoSuchMethodException {
      for (CachedMethodInfo method : this.getDeclaredMethods()) {
         if (method.originalName.equals(name) && method.getParameters().typesMatch(params)) {
            return method;
         }
      }

      throw new NoSuchMethodException("Method '" + name + "' not found in class " + this.type.getName());
   }

   public void appendDebugType(StringBuilder builder) {
      int arr = 0;

      Class<?> current;
      for (current = this.type; current.isArray(); current = current.componentType()) {
         arr++;
      }

      builder.append(this.storage.getDebugClassName(current));
      if (arr > 0) {
         builder.append("[]".repeat(arr));
      }
   }

   public List<String> getDebugInfo() {
      int array = 0;

      Class<?> cl;
      for (cl = this.type; cl.isArray(); array++) {
         cl = cl.getComponentType();
      }

      StringBuilder clName = new StringBuilder(cl.getName());
      if (array > 0) {
         clName.append("[]".repeat(array));
      }

      ArrayList<String> list = new ArrayList<>();
      if (cl.isInterface()) {
         clName.insert(0, "interface ");
      } else if (cl.isAnnotation()) {
         clName.insert(0, "annotation ");
      } else if (cl.isEnum()) {
         clName.insert(0, "enum ");
      } else if (cl.isRecord()) {
         clName.insert(0, "record ");
      } else {
         clName.insert(0, "class ");
      }

      list.add(clName.toString());
      String shortName = cl.getName();
      int shortNameIndex = shortName.lastIndexOf(46);
      if (shortNameIndex > 0) {
         shortName = shortName.substring(shortNameIndex + 1);
      }

      for (CachedConstructorInfo constructor : this.getConstructors()) {
         StringBuilder builder1 = new StringBuilder("new ");
         builder1.append(shortName);
         constructor.appendDebugParams(builder1);
         list.add(builder1.toString());
      }

      for (CachedFieldInfo.Accessible field : this.getAccessibleFields(false)) {
         StringBuilder builder1 = new StringBuilder();
         if (field.getInfo().isStatic) {
            builder1.append("static ");
         }

         if (field.getInfo().isFinal) {
            builder1.append("final ");
         }

         if (field.getInfo().isNative) {
            builder1.append("native ");
         }

         this.storage.get(field.getInfo().getType().asClass()).appendDebugType(builder1);
         builder1.append(' ');
         builder1.append(field.getName());
         list.add(builder1.toString());
      }

      for (CachedMethodInfo.Accessible method : this.getAccessibleMethods(false)) {
         StringBuilder builder1x = new StringBuilder();
         if (method.getInfo().isStatic) {
            builder1x.append("static ");
         }

         if (method.getInfo().isNative) {
            builder1x.append("native ");
         }

         this.storage.get(method.getInfo().getReturnType().asClass()).appendDebugType(builder1x);
         builder1x.append(' ');
         builder1x.append(method.getName());
         method.getInfo().appendDebugParams(builder1x);
         String s = builder1x.toString();
         if (!IGNORED_DEBUG_METHODS.contains(s)) {
            list.add(s);
         }
      }

      return list;
   }

   static {
      IGNORED_DEBUG_METHODS.add("void wait()");
      IGNORED_DEBUG_METHODS.add("void wait(long, int)");
      IGNORED_DEBUG_METHODS.add("native void wait(long)");
      IGNORED_DEBUG_METHODS.add("boolean equals(Object)");
      IGNORED_DEBUG_METHODS.add("String toString()");
      IGNORED_DEBUG_METHODS.add("native int hashCode()");
      IGNORED_DEBUG_METHODS.add("native Class getClass()");
      IGNORED_DEBUG_METHODS.add("native void notify()");
      IGNORED_DEBUG_METHODS.add("native void notifyAll()");
   }
}
