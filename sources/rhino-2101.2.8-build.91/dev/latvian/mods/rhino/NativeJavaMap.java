package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.Deletable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NativeJavaMap extends NativeJavaObject {
   public final Map map;
   public final TypeInfo mapKeyType;
   public final TypeInfo mapValueType;
   private static final Object INVALID_KEY = new Object();
   private static final Callable symbol_iterator = (cx, scope, thisObj, args) -> {
      if (thisObj instanceof NativeJavaMap njm) {
         return new NativeJavaMap.NativeJavaMapIterator(cx, scope, njm);
      } else {
         throw ScriptRuntime.typeError1(cx, "msg.incompat.call", SymbolKey.ITERATOR);
      }
   };

   static void init(ScriptableObject scope, boolean sealed, Context cx) {
      NativeJavaMap.NativeJavaMapIterator.init(scope, sealed, cx);
   }

   public NativeJavaMap(Context cx, Scriptable scope, Object jo, Map map, TypeInfo type) {
      super(scope, jo, type, cx);
      this.map = map;
      this.mapKeyType = type.param(0);
      this.mapValueType = type.param(1);
   }

   @Override
   public String getClassName() {
      return "JavaMap";
   }

   private Object toMapKey(Context cx, Object jsKey) {
      try {
         return cx.jsToJava(jsKey, this.mapKeyType);
      } catch (Exception var4) {
         return INVALID_KEY;
      }
   }

   private boolean safeHas(Object key) {
      if (key == INVALID_KEY) {
         return false;
      } else {
         try {
            return this.map.containsKey(key);
         } catch (NullPointerException | ClassCastException var3) {
            return false;
         }
      }
   }

   @Override
   public boolean has(Context cx, String name, Scriptable start) {
      return this.safeHas(this.toMapKey(cx, name)) ? true : super.has(cx, name, start);
   }

   @Override
   public boolean has(Context cx, int index, Scriptable start) {
      return this.safeHas(this.toMapKey(cx, index)) ? true : super.has(cx, index, start);
   }

   @Override
   public boolean has(Context cx, Symbol key, Scriptable start) {
      return SymbolKey.ITERATOR.equals(key);
   }

   @Override
   public Object get(Context cx, String name, Scriptable start) {
      Object key = this.toMapKey(cx, name);
      return this.safeHas(key) ? cx.javaToJS(this.map.get(key), start, this.mapValueType) : super.get(cx, name, start);
   }

   @Override
   public Object get(Context cx, int index, Scriptable start) {
      Object key = this.toMapKey(cx, index);
      return this.safeHas(key) ? cx.javaToJS(this.map.get(key), start, this.mapValueType) : super.get(cx, index, start);
   }

   @Override
   public Object get(Context cx, Symbol key, Scriptable start) {
      return SymbolKey.ITERATOR.equals(key) ? symbol_iterator : super.get(cx, key, start);
   }

   @Override
   public void put(Context cx, String name, Scriptable start, Object value) {
      Object key = this.toMapKey(cx, name);
      if (key != INVALID_KEY) {
         try {
            this.map.put(key, cx.jsToJava(value, this.mapValueType));
            return;
         } catch (NullPointerException | ClassCastException var7) {
         }
      }

      super.put(cx, name, start, value);
   }

   @Override
   public void put(Context cx, int index, Scriptable start, Object value) {
      Object key = this.toMapKey(cx, index);
      if (key != INVALID_KEY) {
         try {
            this.map.put(key, cx.jsToJava(value, this.mapValueType));
            return;
         } catch (NullPointerException | ClassCastException var7) {
         }
      }

      super.put(cx, index, start, value);
   }

   @Override
   public Object[] getIds(Context cx) {
      List<Object> ids = new ArrayList<>(this.map.size());

      for (Object key : this.map.keySet()) {
         if (key instanceof Integer) {
            ids.add(key);
         } else {
            ids.add(ScriptRuntime.toString(cx, key));
         }
      }

      return ids.toArray();
   }

   @Override
   public void delete(Context cx, String name) {
      Object key = this.toMapKey(cx, name);
      if (this.safeHas(key)) {
         Deletable.deleteObject(this.map.remove(key));
      }
   }

   @Override
   public void delete(Context cx, int index) {
      Object key = this.toMapKey(cx, index);
      if (this.safeHas(key)) {
         Deletable.deleteObject(this.map.remove(key));
      }
   }

   @Override
   protected void initMembers(Context cx, Scriptable scope) {
      super.initMembers(cx, scope);
      this.addCustomFunction("hasOwnProperty", TypeInfo.BOOLEAN, this::hasOwnProperty, new TypeInfo[]{TypeInfo.STRING});
   }

   private boolean hasOwnProperty(Context cx, Object[] args) {
      return this.safeHas(this.toMapKey(cx, ScriptRuntime.toString(cx, args[0])));
   }

   private static final class NativeJavaMapIterator extends ES6Iterator {
      private static final String ITERATOR_TAG = "JavaMapIterator";
      private final Iterator<Entry> iterator;
      private final TypeInfo keyType;
      private final TypeInfo valueType;

      static void init(ScriptableObject scope, boolean sealed, Context cx) {
         init(scope, sealed, new NativeJavaMap.NativeJavaMapIterator(), "JavaMapIterator", cx);
      }

      private NativeJavaMapIterator() {
         this.iterator = Collections.emptyIterator();
         this.keyType = TypeInfo.NONE;
         this.valueType = TypeInfo.NONE;
      }

      NativeJavaMapIterator(Context cx, Scriptable scope, NativeJavaMap njm) {
         super(scope, "JavaMapIterator", cx);
         this.iterator = njm.map.entrySet().iterator();
         this.keyType = njm.mapKeyType;
         this.valueType = njm.mapValueType;
      }

      @Override
      public String getClassName() {
         return "Java Map Iterator";
      }

      @Override
      protected boolean isDone(Context cx, Scriptable scope) {
         return !this.iterator.hasNext();
      }

      @Override
      protected Object nextValue(Context cx, Scriptable scope) {
         if (!this.iterator.hasNext()) {
            return cx.newArray(scope, new Object[]{Undefined.INSTANCE, Undefined.INSTANCE});
         } else {
            Entry e = this.iterator.next();
            Object key = cx.javaToJS(e.getKey(), scope, this.keyType);
            Object value = cx.javaToJS(e.getValue(), scope, this.valueType);
            return cx.newArray(scope, new Object[]{key, value});
         }
      }

      @Override
      protected String getTag() {
         return "JavaMapIterator";
      }
   }
}
