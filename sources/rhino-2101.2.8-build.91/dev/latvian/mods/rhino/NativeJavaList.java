package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.Deletable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

public class NativeJavaList extends NativeJavaObject {
   private static final TypeInfo REDUCE_FUNC_ARG = TypeInfo.of(BinaryOperator.class);
   public final List list;
   public final TypeInfo listType;

   public NativeJavaList(Context cx, Scriptable scope, Object jo, List list, TypeInfo type) {
      super(scope, jo, type, cx);
      this.list = list;
      this.listType = type.param(0);
   }

   @Override
   public String getClassName() {
      return "JavaList";
   }

   @Override
   public boolean has(Context cx, int index, Scriptable start) {
      return this.isWithValidIndex(index) ? true : super.has(cx, index, start);
   }

   @Override
   public boolean has(Context cx, Symbol key, Scriptable start) {
      return SymbolKey.IS_CONCAT_SPREADABLE.equals(key) ? true : super.has(cx, key, start);
   }

   @Override
   public Object get(Context cx, int index, Scriptable start) {
      return this.isWithValidIndex(index) ? cx.javaToJS(this.list.get(index), start, this.listType) : Undefined.INSTANCE;
   }

   @Override
   public Object get(Context cx, Symbol key, Scriptable start) {
      return SymbolKey.IS_CONCAT_SPREADABLE.equals(key) ? Boolean.TRUE : super.get(cx, key, start);
   }

   @Override
   public void put(Context cx, int index, Scriptable start, Object value) {
      if (index >= 0) {
         Object javaValue = cx.jsToJava(value, this.listType);
         if (index == this.list.size()) {
            this.list.add(javaValue);
         } else {
            this.ensureCapacity(index + 1);
            this.list.set(index, javaValue);
         }
      } else {
         super.put(cx, index, start, value);
      }
   }

   @Override
   public void put(Context cx, String name, Scriptable start, Object value) {
      if (this.list != null && "length".equals(name)) {
         this.setLength(cx, value);
      } else {
         super.put(cx, name, start, value);
      }
   }

   private void setLength(Context cx, Object val) {
      double d = ScriptRuntime.toNumber(cx, val);
      long longVal = ScriptRuntime.toUint32(d);
      if (longVal == d && longVal <= 2147483647L) {
         if (longVal < this.list.size()) {
            this.list.subList((int)longVal, this.list.size()).clear();
         } else {
            this.ensureCapacity((int)longVal);
         }
      } else {
         String msg = ScriptRuntime.getMessage0("msg.arraylength.bad");
         throw ScriptRuntime.rangeError(cx, msg);
      }
   }

   private void ensureCapacity(int minCapacity) {
      if (minCapacity > this.list.size()) {
         if (this.list instanceof ArrayList) {
            ((ArrayList)this.list).ensureCapacity(minCapacity);
         }

         while (minCapacity > this.list.size()) {
            this.list.add(null);
         }
      }
   }

   @Override
   public Object[] getIds(Context cx) {
      List<?> list = (List<?>)this.javaObject;
      Object[] result = new Object[list.size()];
      int i = list.size();

      while (--i >= 0) {
         result[i] = i;
      }

      return result;
   }

   private boolean isWithValidIndex(int index) {
      return index >= 0 && index < this.list.size();
   }

   @Override
   public void delete(Context cx, int index) {
      if (this.isWithValidIndex(index)) {
         Deletable.deleteObject(this.list.remove(index));
      }
   }

   @Override
   protected void initMembers(Context cx, Scriptable scope) {
      super.initMembers(cx, scope);
      TypeInfo reduceFuncArg = REDUCE_FUNC_ARG.withParams(this.listType);
      this.addCustomProperty("length", TypeInfo.INT, this::getLength);
      this.addCustomFunction("push", TypeInfo.INT, this::push, new TypeInfo[]{TypeInfo.OBJECT});
      this.addCustomFunction("pop", this.listType, this::pop);
      this.addCustomFunction("shift", this.listType, this::shift);
      this.addCustomFunction("unshift", TypeInfo.INT, this::unshift, new TypeInfo[]{TypeInfo.OBJECT});
      this.addCustomFunction("concat", this.typeInfo, this::concat, new TypeInfo[]{TypeInfo.RAW_LIST});
      this.addCustomFunction("join", TypeInfo.STRING, this::join, new TypeInfo[]{TypeInfo.STRING});
      this.addCustomFunction("reverse", TypeInfo.NONE, this::reverse);
      this.addCustomFunction("slice", TypeInfo.NONE, this::slice, new TypeInfo[]{TypeInfo.OBJECT});
      this.addCustomFunction("splice", TypeInfo.NONE, this::splice, new TypeInfo[]{TypeInfo.OBJECT});
      this.addCustomFunction("every", TypeInfo.BOOLEAN, this::every, new TypeInfo[]{TypeInfo.RAW_PREDICATE});
      this.addCustomFunction("some", TypeInfo.BOOLEAN, this::some, new TypeInfo[]{TypeInfo.RAW_PREDICATE});
      this.addCustomFunction("filter", this.typeInfo, this::filter, new TypeInfo[]{TypeInfo.RAW_PREDICATE});
      this.addCustomFunction("map", TypeInfo.RAW_LIST, this::map, new TypeInfo[]{TypeInfo.RAW_FUNCTION});
      this.addCustomFunction("reduce", this.listType, this::reduce, new TypeInfo[]{reduceFuncArg});
      this.addCustomFunction("reduceRight", this.listType, this::reduceRight, new TypeInfo[]{reduceFuncArg});
      this.addCustomFunction("find", this.listType, this::find, new TypeInfo[]{TypeInfo.RAW_PREDICATE});
      this.addCustomFunction("findIndex", TypeInfo.NONE, this::findIndex, new TypeInfo[]{TypeInfo.RAW_PREDICATE});
      this.addCustomFunction("findLast", this.listType, this::findLast, new TypeInfo[]{TypeInfo.RAW_PREDICATE});
      this.addCustomFunction("findLastIndex", TypeInfo.NONE, this::findLastIndex, new TypeInfo[]{TypeInfo.RAW_PREDICATE});
   }

   private int getLength(Context cx) {
      return this.list.size();
   }

   private int push(Context cx, Object[] args) {
      if (args.length == 1) {
         this.list.add(cx.jsToJava(args[0], this.listType));
      } else if (args.length > 1) {
         Object[] args1 = new Object[args.length];

         for (int i = 0; i < args.length; i++) {
            args1[i] = cx.jsToJava(args[i], this.listType);
         }

         this.list.addAll(Arrays.asList(args1));
      }

      return this.list.size();
   }

   private Object pop(Context cx) {
      return this.list.isEmpty() ? Undefined.INSTANCE : this.list.removeLast();
   }

   private Object shift(Context cx) {
      return this.list.isEmpty() ? Undefined.INSTANCE : this.list.removeFirst();
   }

   private int unshift(Context cx, Object[] args) {
      for (int i = args.length - 1; i >= 0; i--) {
         this.list.addFirst(cx.jsToJava(args[i], this.listType));
      }

      return this.list.size();
   }

   private Object concat(Context cx, Object[] args) {
      List<Object> list1 = new ArrayList<>(this.list);
      if (args.length > 0 && args[0] instanceof List) {
         list1.addAll((List)cx.jsToJava(args[0], this.typeInfo));
      }

      return list1;
   }

   private String join(Context cx, Object[] args) {
      if (this.list.isEmpty()) {
         return "";
      } else if (this.list.size() == 1) {
         return ScriptRuntime.toString(cx, this.list.getFirst());
      } else {
         String j = ScriptRuntime.toString(cx, args[0]);
         StringBuilder sb = new StringBuilder();

         for (int i = 0; i < this.list.size(); i++) {
            if (i > 0) {
               sb.append(j);
            }

            sb.append(ScriptRuntime.toString(cx, this.list.get(i)));
         }

         return sb.toString();
      }
   }

   private NativeJavaList reverse(Context cx) {
      if (this.list.size() > 1) {
         Collections.reverse(this.list);
      }

      return this;
   }

   private Object slice(Context cx, Object[] args) {
      throw new IllegalStateException("Not implemented yet!");
   }

   private Object splice(Context cx, Object[] args) {
      throw new IllegalStateException("Not implemented yet!");
   }

   private Object every(Context cx, Object[] args) {
      Predicate predicate = (Predicate)args[0];

      for (Object o : this.list) {
         if (!predicate.test(o)) {
            return Boolean.FALSE;
         }
      }

      return Boolean.TRUE;
   }

   private Object some(Context cx, Object[] args) {
      Predicate predicate = (Predicate)args[0];

      for (Object o : this.list) {
         if (predicate.test(o)) {
            return Boolean.TRUE;
         }
      }

      return Boolean.FALSE;
   }

   private Object filter(Context cx, Object[] args) {
      if (this.list.isEmpty()) {
         return this;
      } else {
         Predicate predicate = (Predicate)args[0];
         List<Object> list1 = new ArrayList<>();

         for (Object o : this.list) {
            if (predicate.test(o)) {
               list1.add(o);
            }
         }

         return list1;
      }
   }

   private Object map(Context cx, Object[] args) {
      if (this.list.isEmpty()) {
         return this;
      } else {
         java.util.function.Function function = (java.util.function.Function)args[0];
         List<Object> list1 = new ArrayList<>();

         for (Object o : this.list) {
            list1.add(function.apply(o));
         }

         return list1;
      }
   }

   private Object reduce(Context cx, Object[] args) {
      if (this.list.isEmpty()) {
         return Undefined.INSTANCE;
      } else if (this.list.size() == 1) {
         return this.list.getFirst();
      } else {
         BinaryOperator operator = (BinaryOperator)args[0];
         Object o = this.get(cx, 0, this);

         for (int i = 1; i < this.list.size(); i++) {
            o = operator.apply(o, this.get(cx, i, this));
         }

         return o;
      }
   }

   private Object reduceRight(Context cx, Object[] args) {
      if (this.list.isEmpty()) {
         return Undefined.INSTANCE;
      } else if (this.list.size() == 1) {
         return this.list.getFirst();
      } else {
         BinaryOperator operator = (BinaryOperator)args[0];
         Object o = this.get(cx, 0, this);

         for (int i = this.list.size() - 1; i >= 1; i--) {
            o = operator.apply(o, this.get(cx, i, this));
         }

         return o;
      }
   }

   private Object find(Context cx, Object[] args) {
      if (this.list.isEmpty()) {
         return Undefined.INSTANCE;
      } else {
         Predicate predicate = (Predicate)args[0];

         for (Object o : this.list) {
            if (predicate.test(o)) {
               return o;
            }
         }

         return Undefined.INSTANCE;
      }
   }

   private Object findIndex(Context cx, Object[] args) {
      if (this.list.isEmpty()) {
         return -1;
      } else {
         Predicate predicate = (Predicate)args[0];

         for (int i = 0; i < this.list.size(); i++) {
            if (predicate.test(this.list.get(i))) {
               return i;
            }
         }

         return -1;
      }
   }

   private Object findLast(Context cx, Object[] args) {
      if (this.list.isEmpty()) {
         return Undefined.INSTANCE;
      } else {
         Predicate predicate = (Predicate)args[0];

         for (int i = this.list.size() - 1; i >= 0; i--) {
            Object o = this.list.get(i);
            if (predicate.test(o)) {
               return o;
            }
         }

         return Undefined.INSTANCE;
      }
   }

   private Object findLastIndex(Context cx, Object[] args) {
      if (this.list.isEmpty()) {
         return -1;
      } else {
         Predicate predicate = (Predicate)args[0];

         for (int i = this.list.size() - 1; i >= 0; i--) {
            if (predicate.test(this.list.get(i))) {
               return i;
            }
         }

         return -1;
      }
   }
}
