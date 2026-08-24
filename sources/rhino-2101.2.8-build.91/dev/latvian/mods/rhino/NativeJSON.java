package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.json.JsonParser;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.Map.Entry;

public class NativeJSON extends ScriptableObject {
   protected static final int MAX_STRINGIFY_GAP_LENGTH = 10;

   static void init(Scriptable scope, boolean sealed, Context cx) {
      register(new NativeJSON(), scope, sealed, cx);
   }

   static void register(NativeJSON obj, Scriptable scope, boolean sealed, Context cx) {
      obj.setPrototype(getObjectPrototype(scope, cx));
      obj.setParentScope(scope);
      obj.defineProperty(cx, scope, "toSource", 0, (lcx, lscope, thisObj, args) -> "JSON", 2, 3);
      obj.defineProperty(cx, scope, "parse", 2, (lcx, lscope, thisObj, args) -> obj.jsonParse(lcx, lscope, args), 2, 3);
      obj.defineProperty(cx, scope, "stringify", 3, (lcx, lscope, thisObj, args) -> obj.jsonStringify(lcx, args), 2, 3);
      obj.defineProperty(cx, SymbolKey.TO_STRING_TAG, "JSON", 3);
      if (sealed) {
         obj.sealObject(cx);
      }

      defineProperty(scope, "JSON", obj, 2, cx);
   }

   private Object jsonParse(Context cx, Scriptable scope, Object[] args) {
      String jtext = ScriptRuntime.toString(cx, args, 0);
      Object reviver = args.length > 1 ? args[1] : null;
      return reviver instanceof Callable ? parse(cx, scope, jtext, (Callable)reviver) : parse(cx, scope, jtext);
   }

   private Object jsonStringify(Context cx, Object[] args) {
      Object value = null;
      Object replacer = null;
      Object space = null;
      switch (args.length) {
         case 3:
            space = args[2];
         case 2:
            replacer = args[1];
         case 1:
            value = args[0];
         case 0:
         default:
            return args.length != 0 && !doesNotSerialize(value) ? this.stringifyJSON(value, replacer, space, cx) : Undefined.INSTANCE;
      }
   }

   private static Object parse(Context cx, Scriptable scope, String jtext) {
      try {
         return new JsonParser(scope).parseValue(cx, jtext);
      } catch (JsonParser.ParseException var4) {
         throw ScriptRuntime.constructError(cx, "SyntaxError", var4.getMessage());
      }
   }

   public static Object parse(Context cx, Scriptable scope, String jtext, Callable reviver) {
      Object unfiltered = parse(cx, scope, jtext);
      Scriptable root = cx.newObject(scope);
      root.put(cx, "", root, unfiltered);
      return walk(cx, scope, reviver, root, "");
   }

   private static Object walk(Context cx, Scriptable scope, Callable reviver, Scriptable holder, Object name) {
      Object property;
      if (name instanceof Number) {
         property = holder.get(cx, ((Number)name).intValue(), holder);
      } else {
         property = holder.get(cx, (String)name, holder);
      }

      if (property instanceof Scriptable val) {
         if (val instanceof NativeArray) {
            long len = ((NativeArray)val).getLength();

            for (long i = 0L; i < len; i++) {
               if (i > 2147483647L) {
                  String id = Long.toString(i);
                  Object newElement = walk(cx, scope, reviver, val, id);
                  if (newElement == Undefined.INSTANCE) {
                     val.delete(cx, id);
                  } else {
                     val.put(cx, id, val, newElement);
                  }
               } else {
                  int idx = (int)i;
                  Object newElement = walk(cx, scope, reviver, val, idx);
                  if (newElement == Undefined.INSTANCE) {
                     val.delete(cx, idx);
                  } else {
                     val.put(cx, idx, val, newElement);
                  }
               }
            }
         } else {
            Object[] keys = val.getIds(cx);

            for (Object p : keys) {
               Object newElement = walk(cx, scope, reviver, val, p);
               if (newElement == Undefined.INSTANCE) {
                  if (p instanceof Number) {
                     val.delete(cx, ((Number)p).intValue());
                  } else {
                     val.delete(cx, (String)p);
                  }
               } else if (p instanceof Number) {
                  val.put(cx, ((Number)p).intValue(), val, newElement);
               } else {
                  val.put(cx, (String)p, val, newElement);
               }
            }
         }
      }

      return reviver.call(cx, scope, holder, new Object[]{name, property});
   }

   public static String stringify(Object value, Object replacer, Object space, Context cx) {
      StringBuilder builder = new StringBuilder();
      stringify0(cx, value, builder);
      return builder.toString();
   }

   static boolean doesNotSerialize(Object v) {
      return Undefined.isUndefined(v) || v == Scriptable.NOT_FOUND || v instanceof Callable || v instanceof Symbol;
   }

   private static void escape(StringBuilder builder, String string) {
      builder.append('"');
      builder.append(string.replace("\"", "\\\""));
      builder.append('"');
   }

   private static void stringify0(Context cx, Object v, StringBuilder builder) {
      if (v == null || v instanceof Boolean) {
         builder.append(v);
      } else if (v instanceof Number || v instanceof NativeNumber) {
         double d = ScriptRuntime.toNumber(cx, v);
         builder.append(Double.isFinite(d) ? ScriptRuntime.toString(cx, d) : "null");
      } else if (v instanceof CharSequence) {
         escape(builder, v.toString());
      } else if (v instanceof NativeString) {
         escape(builder, ScriptRuntime.toString(cx, v));
      } else if (v instanceof NativeObject obj) {
         builder.append('{');
         boolean first = true;

         for (Object id : obj.getIds(cx)) {
            Object value = id instanceof Integer index ? obj.get(cx, index, obj) : obj.get(cx, String.valueOf(id), obj);
            value = Wrapper.unwrapped(value);
            if (!doesNotSerialize(value)) {
               if (first) {
                  first = false;
               } else {
                  builder.append(',');
               }

               escape(builder, String.valueOf(id));
               builder.append(':');
               stringify0(cx, value, builder);
            }
         }

         builder.append('}');
      } else if (v instanceof Map<?, ?> map) {
         builder.append('{');
         boolean first = true;

         for (Entry<?, ?> entry : map.entrySet()) {
            if (!doesNotSerialize(entry.getValue())) {
               if (first) {
                  first = false;
               } else {
                  builder.append(',');
               }

               escape(builder, String.valueOf(entry.getKey()));
               builder.append(':');
               stringify0(cx, entry.getValue(), builder);
            }
         }

         builder.append('}');
      } else if (v.getClass().isArray()) {
         builder.append('[');
         int length = Array.getLength(v);

         for (int i = 0; i < length; i++) {
            if (i > 0) {
               builder.append(',');
            }

            stringify0(cx, Array.get(v, i), builder);
         }

         builder.append(']');
      } else if (v instanceof Iterable<?> itr) {
         builder.append('[');
         boolean first = true;

         for (Object value : itr) {
            if (first) {
               first = false;
            } else {
               builder.append(',');
            }

            stringify0(cx, value, builder);
         }

         builder.append(']');
      } else if (doesNotSerialize(v)) {
         builder.append("null");
      } else {
         stringify0(cx, cx.getCachedClassStorage(false).get(Wrapper.unwrapped(v).getClass()).getDebugInfo(), builder);
      }
   }

   protected NativeJSON() {
   }

   @Override
   public String getClassName() {
      return "JSON";
   }

   public String stringifyJSON(Object value, Object replacer, Object space, Context cx) {
      return stringify(value, replacer, space, cx);
   }
}
