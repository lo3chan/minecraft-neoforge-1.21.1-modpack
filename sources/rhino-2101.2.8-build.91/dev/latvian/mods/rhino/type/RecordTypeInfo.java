package dev.latvian.mods.rhino.type;

import dev.latvian.mods.rhino.Callable;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeArray;
import dev.latvian.mods.rhino.NativeJavaList;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.NativeMap;
import dev.latvian.mods.rhino.RhinoException;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.wrap.TypeWrapperFactory;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class RecordTypeInfo extends ClassTypeInfo implements TypeWrapperFactory<Object> {
   private static final Map<Class<?>, Object> GLOBAL_DEFAULT_VALUES = new IdentityHashMap<>();
   private static final TypeInfo CONSUMER_TYPE_INFO = TypeInfo.RAW_CONSUMER.withParams(TypeInfo.RAW_MAP.withParams(TypeInfo.STRING, TypeInfo.NONE));
   private RecordTypeInfo.Data data;
   private JSObjectTypeInfo objectTypeInfo;
   private JSFixedArrayTypeInfo arrayTypeInfo;

   public static <T> void setGlobalDefaultValue(Class<T> type, T value) {
      GLOBAL_DEFAULT_VALUES.put(type, value);
   }

   RecordTypeInfo(Class<?> type) {
      super(type);
   }

   public synchronized RecordTypeInfo.Data getData() {
      if (this.data == null) {
         RecordComponent[] rc = this.asClass().getRecordComponents();
         RecordTypeInfo.Component[] components = new RecordTypeInfo.Component[rc.length];
         HashMap<String, RecordTypeInfo.Component> componentMap = new HashMap<>();
         Object[] defaultArguments = new Object[rc.length];

         for (int i = 0; i < rc.length; i++) {
            Type gt = rc[i].getGenericType();
            RemapForJS rename = rc[i].getAccessor().getDeclaredAnnotation(RemapForJS.class);
            RecordTypeInfo.Component c = new RecordTypeInfo.Component(i, rename != null ? rename.value() : rc[i].getName(), TypeInfo.of(gt));
            components[i] = c;
            componentMap.put(c.name, c);
            defaultArguments[i] = c.type.createDefaultValue();
            if (defaultArguments[i] == null) {
               defaultArguments[i] = GLOBAL_DEFAULT_VALUES.getOrDefault(rc[i].getType(), null);
            }
         }

         this.data = new RecordTypeInfo.Data(components, Map.copyOf(componentMap), defaultArguments);
      }

      return this.data;
   }

   public JSObjectTypeInfo getObjectTypeInfo() {
      if (this.objectTypeInfo == null) {
         RecordTypeInfo.Data data = this.getData();
         ArrayList<JSOptionalParam> list = new ArrayList<>(data.components.length);

         for (RecordTypeInfo.Component c : data.components) {
            list.add(new JSOptionalParam(c.name, c.type, true));
         }

         this.objectTypeInfo = new JSObjectTypeInfo(List.copyOf(list));
      }

      return this.objectTypeInfo;
   }

   public JSFixedArrayTypeInfo getArrayTypeInfo() {
      if (this.arrayTypeInfo == null) {
         RecordTypeInfo.Data data = this.getData();
         ArrayList<JSOptionalParam> list = new ArrayList<>(data.components.length);

         for (RecordTypeInfo.Component c : data.components) {
            list.add(new JSOptionalParam(c.name, c.type, true));
         }

         this.arrayTypeInfo = new JSFixedArrayTypeInfo(List.copyOf(list));
      }

      return this.arrayTypeInfo;
   }

   public TypeInfo createCombinedType(TypeInfo... preference) {
      ArrayList<TypeInfo> types = new ArrayList<>(2 + preference.length);
      types.addAll(Arrays.asList(preference));
      types.add(this.getObjectTypeInfo());
      types.add(this.getArrayTypeInfo());
      return new JSOrTypeInfo(types);
   }

   @Override
   public Map<String, RecordTypeInfo.Component> recordComponents() {
      return this.getData().componentMap;
   }

   private Object createInstance0(Context cx, Object original, Object[] args) {
      MethodHandle constructor = cx.factory.getRecordConstructor(this.asClass());
      if (constructor == null) {
         throw Context.reportRuntimeError("Unable to find record '" + this.asClass().getName() + "' constructor", cx);
      } else {
         try {
            return constructor.invokeWithArguments(args);
         } catch (RhinoException var6) {
            return cx.reportConversionError(original, this);
         } catch (Throwable var7) {
            throw Context.throwAsScriptRuntimeEx(var7, cx);
         }
      }
   }

   public Object createInstance(Context cx, Map<?, ?> map) {
      RecordTypeInfo.Data data = this.getData();
      Object[] defaultRecordProperties = cx.factory.getDefaultRecordProperties(this.asClass());
      Object[] args0 = defaultRecordProperties == null ? data.defaultArguments : defaultRecordProperties;
      Object[] args = (Object[])args0.clone();

      for (Entry<?, ?> entry : map.entrySet()) {
         RecordTypeInfo.Component c = data.componentMap.get(String.valueOf(entry.getKey()));
         if (c != null) {
            if (args[c.index] instanceof Optional) {
               args[c.index] = Optional.ofNullable(cx.jsToJava(entry.getValue(), c.type.param(0)));
            } else {
               args[c.index] = cx.jsToJava(entry.getValue(), c.type);
            }
         }
      }

      return this.createInstance0(cx, map, args);
   }

   public Object createInstance(Context cx, Object... objects) {
      RecordTypeInfo.Data data = this.getData();
      Object[] defaultRecordProperties = cx.factory.getDefaultRecordProperties(this.asClass());
      Object[] args0 = defaultRecordProperties == null ? data.defaultArguments : defaultRecordProperties;
      Object[] args = (Object[])args0.clone();
      int alen = Math.min(args0.length, objects.length);

      for (int i = 0; i < alen; i++) {
         if (args[i] instanceof Optional) {
            args[i] = Optional.ofNullable(cx.jsToJava(objects[i], data.components[i].type.param(0)));
         } else {
            args[i] = cx.jsToJava(objects[i], data.components[i].type);
         }
      }

      return this.createInstance0(cx, args, args);
   }

   @Override
   public Object wrap(Context cx, Object from, TypeInfo target) {
      if (this.asClass().isInstance(from)) {
         return from;
      } else if (from instanceof NativeArray || from instanceof NativeJavaList) {
         Object[] arr = (Object[])cx.arrayOf(from, TypeInfo.NONE);
         return this.createInstance(cx, arr);
      } else if (from instanceof Map || from instanceof NativeJavaObject || from instanceof NativeMap) {
         Map map = (Map)cx.mapOf(from, TypeInfo.STRING, TypeInfo.NONE);
         return this.createInstance(cx, map);
      } else if (from instanceof Callable) {
         HashMap<String, Object> map = new HashMap<>(2);
         Consumer<Map<String, ?>> consumer = (Consumer<Map<String, ?>>)cx.jsToJava(from, CONSUMER_TYPE_INFO);
         consumer.accept(map);
         return this.createInstance(cx, map);
      } else {
         return cx.reportConversionError(from, target);
      }
   }

   static {
      setGlobalDefaultValue(Optional.class, Optional.empty());
      setGlobalDefaultValue(List.class, List.of());
      setGlobalDefaultValue(Set.class, Set.of());
      setGlobalDefaultValue(Map.class, Map.of());
   }

   public record Component(int index, String name, TypeInfo type) {
   }

   public record Data(RecordTypeInfo.Component[] components, Map<String, RecordTypeInfo.Component> componentMap, Object[] defaultArguments) {
      private Object[] createMHArgs() {
         Object[] args = new Object[this.components.length + 1];
         System.arraycopy(this.components, 0, args, 1, this.components.length);
         return args;
      }
   }
}
