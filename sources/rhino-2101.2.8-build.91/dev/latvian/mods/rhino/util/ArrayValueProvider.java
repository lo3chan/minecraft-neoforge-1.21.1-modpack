package dev.latvian.mods.rhino.util;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.EvaluatorException;
import dev.latvian.mods.rhino.NativeArray;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public interface ArrayValueProvider {
   ArrayValueProvider EMPTY = new ArrayValueProvider() {
      @Override
      public int getLength(Context cx) {
         return 0;
      }

      @Override
      public Object getArrayValue(Context cx, int index) {
         return null;
      }

      @Override
      public Object getErrorSource(Context cx) {
         return null;
      }
   };

   int getLength(Context var1);

   Object getArrayValue(Context var1, int var2);

   Object getErrorSource(Context var1);

   default Object createArray(Context cx, TypeInfo target) {
      int len = this.getLength(cx);
      Object arr = target.newArray(len);

      for (int i = 0; i < len; i++) {
         try {
            Array.set(arr, i, cx.jsToJava(this.getArrayValue(cx, i), target));
         } catch (EvaluatorException var7) {
            return cx.reportConversionError(this.getErrorSource(cx), target);
         }
      }

      return arr;
   }

   default Object createList(Context cx, TypeInfo target) {
      int len = this.getLength(cx);
      if (len == 0) {
         return List.of();
      } else if (len == 1) {
         try {
            return List.of(cx.jsToJava(this.getArrayValue(cx, 0), target));
         } catch (EvaluatorException var7) {
            return cx.reportConversionError(this.getErrorSource(cx), target);
         }
      } else {
         ArrayList<Object> list = new ArrayList<>(len);

         for (int i = 0; i < len; i++) {
            try {
               list.add(cx.jsToJava(this.getArrayValue(cx, i), target));
            } catch (EvaluatorException var8) {
               return cx.reportConversionError(this.getErrorSource(cx), target);
            }
         }

         return list;
      }
   }

   default Object createSet(Context cx, TypeInfo target) {
      int len = this.getLength(cx);
      if (len == 0) {
         return Set.of();
      } else if (len == 1) {
         try {
            return Set.of(cx.jsToJava(this.getArrayValue(cx, 0), target));
         } catch (EvaluatorException var7) {
            return cx.reportConversionError(this.getErrorSource(cx), target);
         }
      } else {
         HashSet<Object> set = new HashSet<>(len);

         for (int i = 0; i < len; i++) {
            try {
               set.add(cx.jsToJava(this.getArrayValue(cx, i), target));
            } catch (EvaluatorException var8) {
               return cx.reportConversionError(this.getErrorSource(cx), target);
            }
         }

         return set;
      }
   }

   static ArrayValueProvider fromNativeArray(NativeArray array) {
      return (ArrayValueProvider)(array.getLength() == 0L ? EMPTY : new ArrayValueProvider.FromNativeArray(array));
   }

   static ArrayValueProvider fromJavaList(List<?> list, Object errorSource) {
      return (ArrayValueProvider)(list.isEmpty() ? EMPTY : new ArrayValueProvider.FromJavaList(list, errorSource));
   }

   static ArrayValueProvider fromIterable(Iterable<?> iterable) {
      int len;
      if (iterable instanceof Collection<?> c) {
         len = c.size();
      } else {
         len = 0;

         for (Object ignored : iterable) {
            len++;
         }
      }

      return (ArrayValueProvider)(len == 0 ? EMPTY : new ArrayValueProvider.FromIterator(len, iterable.iterator(), iterable));
   }

   public record FromIterator(int length, Iterator<?> iterator, Object errorSource) implements ArrayValueProvider {
      @Override
      public int getLength(Context cx) {
         return this.length;
      }

      @Override
      public Object getArrayValue(Context cx, int index) {
         return this.iterator.next();
      }

      @Override
      public Object getErrorSource(Context cx) {
         return this.errorSource;
      }
   }

   public record FromJavaArray(Object array, int length) implements ArrayValueProvider {
      @Override
      public int getLength(Context cx) {
         return this.length;
      }

      @Override
      public Object getArrayValue(Context cx, int index) {
         return Array.get(this.array, index);
      }

      @Override
      public Object getErrorSource(Context cx) {
         return this.array;
      }
   }

   public record FromJavaList(List<?> list, Object errorSource) implements ArrayValueProvider {
      @Override
      public int getLength(Context cx) {
         return this.list.size();
      }

      @Override
      public Object getArrayValue(Context cx, int index) {
         return this.list.get(index);
      }

      @Override
      public Object getErrorSource(Context cx) {
         return this.errorSource;
      }
   }

   public record FromNativeArray(NativeArray array) implements ArrayValueProvider {
      @Override
      public int getLength(Context cx) {
         return (int)this.array.getLength();
      }

      @Override
      public Object getArrayValue(Context cx, int index) {
         return this.array.get(cx, index, this.array);
      }

      @Override
      public Object getErrorSource(Context cx) {
         return this.array;
      }
   }

   public record FromObject(Object object) implements ArrayValueProvider {
      public static final ArrayValueProvider.FromObject FROM_NULL = new ArrayValueProvider.FromObject(null);

      @Override
      public int getLength(Context cx) {
         return 1;
      }

      @Override
      public Object getArrayValue(Context cx, int index) {
         return this.object;
      }

      @Override
      public Object getErrorSource(Context cx) {
         return this.object;
      }
   }

   public record FromPlainJavaArray(Object[] array) implements ArrayValueProvider {
      @Override
      public int getLength(Context cx) {
         return this.array.length;
      }

      @Override
      public Object getArrayValue(Context cx, int index) {
         return this.array[index];
      }

      @Override
      public Object getErrorSource(Context cx) {
         return this.array;
      }
   }
}
