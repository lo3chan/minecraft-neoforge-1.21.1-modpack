package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.regexp.NativeRegExp;
import java.util.Comparator;

public class ArrayLikeAbstractOperations {
   public static Object iterativeMethod(
      Context cx, Object tag, String name, ArrayLikeAbstractOperations.IterativeOperation operation, Scriptable scope, Scriptable thisObj, Object[] args
   ) {
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      if (ArrayLikeAbstractOperations.IterativeOperation.FIND == operation
         || ArrayLikeAbstractOperations.IterativeOperation.FIND_INDEX == operation
         || ArrayLikeAbstractOperations.IterativeOperation.FIND_LAST == operation
         || ArrayLikeAbstractOperations.IterativeOperation.FIND_LAST_INDEX == operation) {
         ScriptRuntimeES6.requireObjectCoercible(cx, o, tag, name);
      }

      long length = NativeArray.getLengthProperty(cx, o);
      Object callbackArg = args.length > 0 ? args[0] : Undefined.INSTANCE;
      Function f = getCallbackArg(cx, callbackArg);
      Scriptable parent = ScriptableObject.getTopLevelScope(f);
      Scriptable thisArg;
      if (args.length >= 2 && args[1] != null && args[1] != Undefined.INSTANCE) {
         thisArg = ScriptRuntime.toObject(cx, scope, args[1]);
      } else {
         thisArg = parent;
      }

      Scriptable array = null;
      if (operation == ArrayLikeAbstractOperations.IterativeOperation.FILTER || operation == ArrayLikeAbstractOperations.IterativeOperation.MAP) {
         int resultLength = operation == ArrayLikeAbstractOperations.IterativeOperation.MAP ? (int)length : 0;
         array = cx.newArray(scope, resultLength);
      }

      long j = 0L;
      boolean reverse = operation == ArrayLikeAbstractOperations.IterativeOperation.FIND_LAST
         || operation == ArrayLikeAbstractOperations.IterativeOperation.FIND_LAST_INDEX;
      long start = reverse ? length - 1L : 0L;
      long end = reverse ? -1L : length;
      long increment = reverse ? -1L : 1L;

      for (long i = start; i != end; i += increment) {
         Object[] innerArgs = new Object[3];
         Object elem = getRawElem(o, i, cx);
         if (elem == Scriptable.NOT_FOUND) {
            if (operation != ArrayLikeAbstractOperations.IterativeOperation.FIND
               && operation != ArrayLikeAbstractOperations.IterativeOperation.FIND_INDEX
               && operation != ArrayLikeAbstractOperations.IterativeOperation.FIND_LAST
               && operation != ArrayLikeAbstractOperations.IterativeOperation.FIND_LAST_INDEX) {
               continue;
            }

            elem = Undefined.INSTANCE;
         }

         innerArgs[0] = elem;
         innerArgs[1] = i;
         innerArgs[2] = o;
         Object result = f.call(cx, parent, thisArg, innerArgs);
         switch (operation) {
            case EVERY:
               if (!ScriptRuntime.toBoolean(cx, result)) {
                  return Boolean.FALSE;
               }
               break;
            case FILTER:
               if (ScriptRuntime.toBoolean(cx, result)) {
                  defineElem(cx, array, j++, innerArgs[0]);
               }
            case FOR_EACH:
            default:
               break;
            case MAP:
               defineElem(cx, array, i, result);
               break;
            case SOME:
               if (ScriptRuntime.toBoolean(cx, result)) {
                  return Boolean.TRUE;
               }
               break;
            case FIND:
            case FIND_LAST:
               if (ScriptRuntime.toBoolean(cx, result)) {
                  return elem;
               }
               break;
            case FIND_INDEX:
            case FIND_LAST_INDEX:
               if (ScriptRuntime.toBoolean(cx, result)) {
                  return ScriptRuntime.wrapNumber(i);
               }
         }
      }
      return switch (operation) {
         case EVERY -> Boolean.TRUE;
         case FILTER, MAP -> array;
         default -> Undefined.INSTANCE;
         case SOME -> Boolean.FALSE;
         case FIND_INDEX, FIND_LAST_INDEX -> ScriptRuntime.wrapNumber(-1.0);
      };
   }

   static Function getCallbackArg(Context cx, Object callbackArg) {
      if (callbackArg instanceof Function f) {
         if (callbackArg instanceof NativeRegExp) {
            throw ScriptRuntime.notFunctionError(cx, callbackArg);
         } else {
            return f;
         }
      } else {
         throw ScriptRuntime.notFunctionError(cx, callbackArg);
      }
   }

   static void defineElem(Context cx, Scriptable target, long index, Object value) {
      if (index > 2147483647L) {
         String id = Long.toString(index);
         target.put(cx, id, target, value);
      } else {
         target.put(cx, (int)index, target, value);
      }
   }

   static Object getRawElem(Scriptable target, long index, Context cx) {
      return index > 2147483647L ? ScriptableObject.getProperty(target, Long.toString(index), cx) : ScriptableObject.getProperty(target, (int)index, cx);
   }

   public static long toSliceIndex(double value, long length) {
      long result;
      if (value < 0.0) {
         if (value + length < 0.0) {
            result = 0L;
         } else {
            result = (long)(value + length);
         }
      } else if (value > length) {
         result = length;
      } else {
         result = (long)value;
      }

      return result;
   }

   public static Object reduceMethod(Context cx, ArrayLikeAbstractOperations.ReduceOperation operation, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
      long length = NativeArray.getLengthProperty(cx, o);
      Object callbackArg = args.length > 0 ? args[0] : Undefined.INSTANCE;
      if (callbackArg != null && callbackArg instanceof Function f) {
         Scriptable parent = ScriptableObject.getTopLevelScope(f);
         boolean movingLeft = operation == ArrayLikeAbstractOperations.ReduceOperation.REDUCE;
         Object value = args.length > 1 ? args[1] : Scriptable.NOT_FOUND;

         for (long i = 0L; i < length; i++) {
            long index = movingLeft ? i : length - 1L - i;
            Object elem = getRawElem(o, index, cx);
            if (elem != Scriptable.NOT_FOUND) {
               if (value == Scriptable.NOT_FOUND) {
                  value = elem;
               } else {
                  Object[] innerArgs = new Object[]{value, elem, index, o};
                  value = f.call(cx, parent, parent, innerArgs);
               }
            }
         }

         if (value == Scriptable.NOT_FOUND) {
            throw ScriptRuntime.typeError0(cx, "msg.empty.array.reduce");
         } else {
            return value;
         }
      } else {
         throw ScriptRuntime.notFunctionError(cx, callbackArg);
      }
   }

   public static Comparator<Object> getSortComparator(Context cx, Scriptable scope, Object[] args) {
      return args.length > 0 && Undefined.INSTANCE != args[0]
         ? getSortComparatorFromArguments(cx, scope, args)
         : new ArrayLikeAbstractOperations.ElementComparator(new ArrayLikeAbstractOperations.StringLikeComparator(cx));
   }

   public static ArrayLikeAbstractOperations.ElementComparator getSortComparatorFromArguments(Context cx, Scriptable scope, Object[] args) {
      Callable jsCompareFunction = ScriptRuntime.getValueFunctionAndThis(cx, args[0]);
      Scriptable funThis = cx.lastStoredScriptable();
      Object[] cmpBuf = new Object[2];
      return new ArrayLikeAbstractOperations.ElementComparator((x, y) -> {
         cmpBuf[0] = x;
         cmpBuf[1] = y;
         Object ret = jsCompareFunction.call(cx, scope, funThis, cmpBuf);
         double d = ScriptRuntime.toNumber(cx, ret);
         int cmp = Double.compare(d, 0.0);
         if (cmp < 0) {
            return -1;
         } else {
            return cmp > 0 ? 1 : 0;
         }
      });
   }

   static Scriptable arraySpeciesCreate(Context cx, Scriptable scope, Scriptable o, int length) {
      if (o instanceof NativeArray) {
         Object c = ScriptableObject.getProperty(o, "constructor", cx);
         if (c instanceof Scriptable) {
            c = ScriptableObject.getProperty((Scriptable)c, SymbolKey.SPECIES, cx);
            if (c == null || c == Scriptable.NOT_FOUND) {
               c = Undefined.INSTANCE;
            }
         }

         if (!Undefined.isUndefined(c)) {
            if (c instanceof Constructable) {
               return ((Constructable)c).construct(cx, scope, new Object[]{(double)length});
            }

            throw ScriptRuntime.typeError1(cx, "msg.ctor.not.found", o);
         }
      }

      return cx.newArray(scope, length);
   }

   public record ElementComparator(Comparator<Object> child) implements Comparator<Object> {
      @Override
      public int compare(Object x, Object y) {
         if (x == Undefined.INSTANCE) {
            if (y == Undefined.INSTANCE) {
               return 0;
            } else {
               return y == Scriptable.NOT_FOUND ? -1 : 1;
            }
         } else if (x == Scriptable.NOT_FOUND) {
            return y == Scriptable.NOT_FOUND ? 0 : 1;
         } else if (y == Scriptable.NOT_FOUND) {
            return -1;
         } else {
            return y == Undefined.INSTANCE ? -1 : this.child.compare(x, y);
         }
      }
   }

   public static enum IterativeOperation {
      EVERY,
      FILTER,
      FOR_EACH,
      MAP,
      SOME,
      FIND,
      FIND_INDEX,
      FIND_LAST,
      FIND_LAST_INDEX;
   }

   public static enum ReduceOperation {
      REDUCE,
      REDUCE_RIGHT;
   }

   public record StringLikeComparator(Context cx) implements Comparator<Object> {
      @Override
      public int compare(Object x, Object y) {
         String a = ScriptRuntime.toString(this.cx, x);
         String b = ScriptRuntime.toString(this.cx, y);
         return a.compareTo(b);
      }
   }
}
