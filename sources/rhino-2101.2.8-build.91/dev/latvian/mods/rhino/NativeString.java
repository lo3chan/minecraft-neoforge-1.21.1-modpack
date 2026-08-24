package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.regexp.RegExp;
import java.text.Collator;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

final class NativeString extends ScriptableObject implements Wrapper {
   private static final long serialVersionUID = 920268368584188687L;
   private static final String CLASS_NAME = "String";
   private final CharSequence string;

   static void init(Scriptable scope, boolean sealed, Context cx) {
      LambdaConstructor c = new LambdaConstructor(cx, scope, "String", 1, NativeString::js_constructorFunc, NativeString::js_constructor);
      c.setPrototypePropertyAttributes(7);
      c.setPrototypeScriptable(new NativeString(""), cx);
      defConsMethod(cx, c, scope, "fromCharCode", 1, NativeString::js_fromCharCode);
      defConsMethod(cx, c, scope, "fromCodePoint", 1, NativeString::js_fromCodePoint);
      defConsMethod(cx, c, scope, "raw", 1, NativeString::js_raw);
      defConsMethod(cx, c, scope, "charAt", 1, wrapConstructor(NativeString::js_charAt));
      defConsMethod(cx, c, scope, "charCodeAt", 1, wrapConstructor(NativeString::js_charCodeAt));
      defConsMethod(cx, c, scope, "indexOf", 2, wrapConstructor(NativeString::js_indexOf));
      defConsMethod(cx, c, scope, "lastIndexOf", 2, wrapConstructor(NativeString::js_lastIndexOf));
      defConsMethod(cx, c, scope, "split", 3, wrapConstructor(NativeString::js_split));
      defConsMethod(cx, c, scope, "substring", 3, wrapConstructor(NativeString::js_substring));
      defConsMethod(cx, c, scope, "toLowerCase", 1, wrapConstructor(NativeString::js_toLowerCase));
      defConsMethod(cx, c, scope, "toUpperCase", 1, wrapConstructor(NativeString::js_toUpperCase));
      defConsMethod(cx, c, scope, "substr", 3, wrapConstructor(NativeString::js_substr));
      defConsMethod(cx, c, scope, "concat", 2, wrapConstructor(NativeString::js_concat));
      defConsMethod(cx, c, scope, "slice", 3, wrapConstructor(NativeString::js_slice));
      defConsMethod(cx, c, scope, "equalsIgnoreCase", 2, wrapConstructor(NativeString::js_equalsIgnoreCase));
      defConsMethod(cx, c, scope, "match", 2, wrapConstructor(NativeString::js_match));
      defConsMethod(cx, c, scope, "search", 2, wrapConstructor(NativeString::js_search));
      defConsMethod(cx, c, scope, "replace", 2, wrapConstructor(NativeString::js_replace));
      defConsMethod(cx, c, scope, "replaceAll", 2, wrapConstructor(NativeString::js_replaceAll));
      defConsMethod(cx, c, scope, "localeCompare", 2, wrapConstructor(NativeString::js_localeCompare));
      defConsMethod(cx, c, scope, "toLocaleLowerCase", 1, wrapConstructor(NativeString::js_toLocaleLowerCase));
      defProtoMethod(cx, c, scope, SymbolKey.ITERATOR, 0, NativeString::js_iterator);
      defProtoMethod(cx, c, scope, "toString", 0, NativeString::js_toString);
      defProtoMethod(cx, c, scope, "toSource", 0, NativeString::js_toSource);
      defProtoMethod(cx, c, scope, "valueOf", 0, NativeString::js_toString);
      defProtoMethodWithoutProto(cx, c, scope, "charAt", 1, NativeString::js_charAt);
      defProtoMethodWithoutProto(cx, c, scope, "charCodeAt", 1, NativeString::js_charCodeAt);
      defProtoMethodWithoutProto(cx, c, scope, "indexOf", 1, NativeString::js_indexOf);
      defProtoMethodWithoutProto(cx, c, scope, "lastIndexOf", 1, NativeString::js_lastIndexOf);
      defProtoMethodWithoutProto(cx, c, scope, "split", 2, NativeString::js_split);
      defProtoMethodWithoutProto(cx, c, scope, "substring", 2, NativeString::js_substring);
      defProtoMethodWithoutProto(cx, c, scope, "toLowerCase", 0, NativeString::js_toLowerCase);
      defProtoMethodWithoutProto(cx, c, scope, "toUpperCase", 0, NativeString::js_toUpperCase);
      defProtoMethodWithoutProto(cx, c, scope, "substr", 2, NativeString::js_substr);
      defProtoMethodWithoutProto(cx, c, scope, "concat", 1, NativeString::js_concat);
      defProtoMethodWithoutProto(cx, c, scope, "slice", 2, NativeString::js_slice);
      defProtoMethod(cx, c, scope, "bold", 0, NativeString::js_bold);
      defProtoMethod(cx, c, scope, "italics", 0, NativeString::js_italics);
      defProtoMethod(cx, c, scope, "fixed", 0, NativeString::js_fixed);
      defProtoMethod(cx, c, scope, "strike", 0, NativeString::js_strike);
      defProtoMethod(cx, c, scope, "small", 0, NativeString::js_small);
      defProtoMethod(cx, c, scope, "big", 0, NativeString::js_big);
      defProtoMethod(cx, c, scope, "blink", 0, NativeString::js_blink);
      defProtoMethod(cx, c, scope, "sup", 0, NativeString::js_sup);
      defProtoMethod(cx, c, scope, "sub", 0, NativeString::js_sub);
      defProtoMethod(cx, c, scope, "fontsize", 0, NativeString::js_fontsize);
      defProtoMethod(cx, c, scope, "fontcolor", 0, NativeString::js_fontcolor);
      defProtoMethod(cx, c, scope, "link", 0, NativeString::js_link);
      defProtoMethod(cx, c, scope, "anchor", 0, NativeString::js_anchor);
      defProtoMethod(cx, c, scope, "equals", 1, NativeString::js_equals);
      defProtoMethod(cx, c, scope, "equalsIgnoreCase", 1, NativeString::js_equalsIgnoreCase);
      defProtoMethodWithoutProto(cx, c, scope, "match", 1, NativeString::js_match);
      defProtoMethodWithoutProto(cx, c, scope, "matchAll", 1, NativeString::js_matchAll);
      defProtoMethodWithoutProto(cx, c, scope, "search", 1, NativeString::js_search);
      defProtoMethodWithoutProto(cx, c, scope, "replace", 2, NativeString::js_replace);
      defProtoMethodWithoutProto(cx, c, scope, "replaceAll", 2, NativeString::js_replaceAll);
      defProtoMethod(cx, c, scope, "at", 1, NativeString::js_at);
      defProtoMethodWithoutProto(cx, c, scope, "localeCompare", 1, NativeString::js_localeCompare);
      defProtoMethodWithoutProto(cx, c, scope, "toLocaleLowerCase", 0, NativeString::js_toLocaleLowerCase);
      defProtoMethodWithoutProto(cx, c, scope, "toLocaleUpperCase", 0, NativeString::js_toLocaleUpperCase);
      defProtoMethod(cx, c, scope, "trim", 0, NativeString::js_trim);
      defProtoMethod(cx, c, scope, "trimLeft", 0, NativeString::js_trimLeft);
      defProtoMethod(cx, c, scope, "trimStart", 0, NativeString::js_trimLeft);
      defProtoMethod(cx, c, scope, "trimRight", 0, NativeString::js_trimRight);
      defProtoMethod(cx, c, scope, "trimEnd", 0, NativeString::js_trimRight);
      defProtoMethod(cx, c, scope, "includes", 1, NativeString::js_includes);
      defProtoMethod(cx, c, scope, "startsWith", 1, NativeString::js_startsWith);
      defProtoMethod(cx, c, scope, "endsWith", 1, NativeString::js_endsWith);
      defProtoMethod(cx, c, scope, "normalize", 0, NativeString::js_normalize);
      defProtoMethod(cx, c, scope, "repeat", 1, NativeString::js_repeat);
      defProtoMethod(cx, c, scope, "codePointAt", 1, NativeString::js_codePointAt);
      defProtoMethod(cx, c, scope, "padStart", 1, NativeString::js_padStart);
      defProtoMethod(cx, c, scope, "padEnd", 1, NativeString::js_padEnd);
      defProtoMethod(cx, c, scope, "isWellFormed", 0, NativeString::js_isWellFormed);
      defProtoMethod(cx, c, scope, "toWellFormed", 0, NativeString::js_toWellFormed);
      if (sealed) {
         c.sealObject(cx);
      }

      ScriptableObject.defineProperty(scope, "String", c, 2, cx);
   }

   private static void defConsMethod(Context cx, LambdaConstructor c, Scriptable scope, String name, int length, Callable target) {
      c.defineConstructorMethod(cx, scope, name, length, target, 2);
   }

   private static void defProtoMethod(Context cx, LambdaConstructor c, Scriptable scope, String name, int length, Callable target) {
      c.definePrototypeMethod(cx, scope, name, length, target, 2, 3);
   }

   private static void defProtoMethod(Context cx, LambdaConstructor c, Scriptable scope, SymbolKey key, int length, Callable target) {
      LambdaFunction f = new LambdaFunction(cx, scope, "[Symbol.iterator]", length, target, false);
      f.setStandardPropertyAttributes(3);
      c.definePrototypeProperty(cx, key, f, 2);
   }

   private static void defProtoMethodWithoutProto(Context cx, LambdaConstructor c, Scriptable scope, String name, int length, Callable target) {
      c.definePrototypeMethod(cx, scope, name, length, target, 2, 3);
   }

   NativeString(CharSequence s) {
      this.string = s;
      this.defineProperty("length", s::length, null, 7);
      this.defineProperty("namespace", () -> {
         String str = s.toString();
         int colon = str.indexOf(58);
         return colon == -1 ? "minecraft" : str.substring(0, colon);
      }, null, 7);
      this.defineProperty("path", () -> {
         String str = s.toString();
         int colon = str.indexOf(58);
         return colon == -1 ? str : str.substring(colon + 1);
      }, null, 7);
   }

   @Override
   public String getClassName() {
      return "String";
   }

   @Override
   public Object unwrap() {
      return this.string;
   }

   @Override
   public MemberType getTypeOf() {
      return MemberType.STRING;
   }

   private static Callable wrapConstructor(Callable target) {
      return (cx, scope, origThis, origArgs) -> {
         Scriptable thisObj;
         Object[] newArgs;
         if (origArgs.length > 0) {
            thisObj = ScriptRuntime.toObject(cx, scope, ScriptRuntime.toCharSequence(cx, origArgs[0]));
            newArgs = new Object[origArgs.length - 1];
            System.arraycopy(origArgs, 1, newArgs, 0, newArgs.length);
         } else {
            thisObj = ScriptRuntime.toObject(cx, scope, ScriptRuntime.toCharSequence(cx, origThis));
            newArgs = origArgs;
         }

         return target.call(cx, scope, thisObj, newArgs);
      };
   }

   private static Scriptable js_constructor(Context cx, Scriptable scope, Object[] args) {
      CharSequence s;
      if (args.length == 0) {
         s = "";
      } else {
         s = ScriptRuntime.toCharSequence(cx, args[0]);
      }

      return new NativeString(s);
   }

   private static Object js_constructorFunc(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      CharSequence s;
      if (args.length == 0) {
         s = "";
      } else if (ScriptRuntime.isSymbol(args[0])) {
         s = args[0].toString();
      } else {
         s = ScriptRuntime.toCharSequence(cx, args[0]);
      }

      return s instanceof String ? s : s.toString();
   }

   private static Object js_fromCharCode(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      int n = args.length;
      if (n < 1) {
         return "";
      } else {
         char[] chars = new char[n];

         for (int i = 0; i != n; i++) {
            chars[i] = ScriptRuntime.toUint16(cx, args[i]);
         }

         return new String(chars);
      }
   }

   private static Object js_fromCodePoint(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      int n = args.length;
      if (n < 1) {
         return "";
      } else {
         int[] codePoints = new int[n];

         for (int i = 0; i != n; i++) {
            Object arg = args[i];
            int codePoint = ScriptRuntime.toInt32(cx, arg);
            double num = ScriptRuntime.toNumber(cx, arg);
            if (!ScriptRuntime.eqNumber(cx, num, codePoint) || !Character.isValidCodePoint(codePoint)) {
               throw ScriptRuntime.rangeError(cx, "Invalid code point " + ScriptRuntime.toString(cx, arg));
            }

            codePoints[i] = codePoint;
         }

         return new String(codePoints, 0, n);
      }
   }

   private static Object js_charAt(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return charAt(cx, thisObj, args, false);
   }

   private static Object js_charCodeAt(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return charAt(cx, thisObj, args, true);
   }

   private static Object charAt(Context cx, Scriptable thisObj, Object[] args, boolean getCode) {
      CharSequence target = ScriptRuntime.toCharSequence(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "charAt"));
      double pos = ScriptRuntime.toInteger(cx, args, 0);
      if (!(pos < 0.0) && !(pos >= target.length())) {
         char c = target.charAt((int)pos);
         return !getCode ? String.valueOf(c) : ScriptRuntime.wrapNumber(c);
      } else {
         return !getCode ? "" : ScriptRuntime.NaNobj;
      }
   }

   private static Object js_indexOf(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      String target = ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "indexOf"));
      String searchStr = ScriptRuntime.toString(cx, args, 0);
      double position = ScriptRuntime.toInteger(cx, args, 1);
      if (searchStr.isEmpty()) {
         return position > target.length() ? target.length() : (int)position;
      } else if (position > target.length()) {
         return -1;
      } else {
         if (position < 0.0) {
            position = 0.0;
         }

         return target.indexOf(searchStr, (int)position);
      }
   }

   private static Object js_startsWith(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      String target = ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "startsWith"));
      checkValidRegex(cx, args, 0, "startsWith");
      String searchStr = ScriptRuntime.toString(cx, args, 0);
      double position = ScriptRuntime.toInteger(cx, args, 1);
      if (position < 0.0) {
         position = 0.0;
      } else if (position > target.length()) {
         position = target.length();
      }

      return target.startsWith(searchStr, (int)position);
   }

   private static Object js_endsWith(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      String target = ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "endsWith"));
      checkValidRegex(cx, args, 0, "endsWith");
      String searchStr = ScriptRuntime.toString(cx, args, 0);
      double position = ScriptRuntime.toInteger(cx, args, 1);
      if (position < 0.0) {
         position = 0.0;
      } else if (Double.isNaN(position) || position > target.length()) {
         position = target.length();
      }

      if (args.length == 0 || args.length == 1 || args.length == 2 && Undefined.isUndefined(args[1])) {
         position = target.length();
      }

      return target.substring(0, (int)position).endsWith(searchStr);
   }

   private static Object js_includes(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      String target = ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "includes"));
      String searchStr = ScriptRuntime.toString(cx, args, 0);
      checkValidRegex(cx, args, 0, "includes");
      int position = (int)ScriptRuntime.toInteger(cx, args, 1);
      return target.indexOf(searchStr, position) != -1;
   }

   private static void checkValidRegex(Context cx, Object[] args, int pos, String functionName) {
      if (args.length > pos && args[pos] instanceof Scriptable arg) {
         RegExp reProxy = cx.getRegExp();
         if (reProxy != null && reProxy.isRegExp(arg) && ScriptableObject.isTrue(ScriptableObject.getProperty(arg, SymbolKey.MATCH, cx), cx)) {
            throw ScriptRuntime.typeError2(cx, "msg.first.arg.not.regexp", "String", functionName);
         }
      }
   }

   private static Object js_split(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      String thisStr = ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "split"));
      return cx.getRegExp().js_split(cx, scope, thisStr, args);
   }

   private static NativeString realThis(Context cx, Scriptable thisObj) {
      return LambdaConstructor.convertThisObject(cx, thisObj, NativeString.class);
   }

   private static Object js_iterator(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return new NativeStringIterator(cx, scope, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "[Symbol.iterator]"));
   }

   private static Object js_toString(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      CharSequence cs = realThis(cx, thisObj).string;
      return cs instanceof String ? cs : cs.toString();
   }

   private static Object js_toSource(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      CharSequence s = realThis(cx, thisObj).string;
      return "(new String(\"" + ScriptRuntime.escapeString(s.toString(), '"') + "\"))";
   }

   private static String tagify(Context cx, Scriptable thisObj, String functionName, String tag, String attribute, Object[] args) {
      String str = ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", functionName));
      StringBuilder result = new StringBuilder();
      result.append('<').append(tag);
      if (attribute != null && !attribute.isEmpty()) {
         String attributeValue = ScriptRuntime.toString(cx, args, 0);
         attributeValue = attributeValue.replace("\"", "&quot;");
         result.append(' ').append(attribute).append("=\"").append(attributeValue).append('"');
      }

      result.append('>').append(str).append("</").append(tag).append('>');
      return result.toString();
   }

   public CharSequence toCharSequence() {
      return this.string;
   }

   @Override
   public String toString() {
      return this.string instanceof String ? (String)this.string : this.string.toString();
   }

   @Override
   public Object get(Context cx, int index, Scriptable start) {
      return 0 <= index && index < this.string.length() ? String.valueOf(this.string.charAt(index)) : super.get(cx, index, start);
   }

   @Override
   public void put(Context cx, int index, Scriptable start, Object value) {
      if (0 > index || index >= this.string.length()) {
         super.put(cx, index, start, value);
      }
   }

   @Override
   public boolean has(Context cx, int index, Scriptable start) {
      return 0 <= index && index < this.string.length() ? true : super.has(cx, index, start);
   }

   @Override
   public int getAttributes(Context cx, int index) {
      return 0 <= index && index < this.string.length() ? 5 : super.getAttributes(cx, index);
   }

   @Override
   Object[] getIds(Context cx, boolean getNonEnumerable, boolean getSymbols) {
      Object[] sids = super.getIds(cx, getNonEnumerable, getSymbols);
      Object[] a = new Object[sids.length + this.string.length()];

      int i;
      for (i = 0; i < this.string.length(); i++) {
         a[i] = i;
      }

      System.arraycopy(sids, 0, a, i, sids.length);
      return a;
   }

   @Override
   protected ScriptableObject getOwnPropertyDescriptor(Context cx, Object id) {
      if (!(id instanceof Symbol)) {
         ScriptRuntime.StringIdOrIndex s = ScriptRuntime.toStringIdOrIndex(cx, id);
         if (s.stringId == null && 0 <= s.index && s.index < this.string.length()) {
            String value = String.valueOf(this.string.charAt(s.index));
            return this.defaultIndexPropertyDescriptor(cx, value);
         }
      }

      return super.getOwnPropertyDescriptor(cx, id);
   }

   private ScriptableObject defaultIndexPropertyDescriptor(Context cx, Object value) {
      Scriptable scope = this.getParentScope();
      if (scope == null) {
         scope = this;
      }

      ScriptableObject desc = new NativeObject(cx.factory);
      ScriptRuntime.setBuiltinProtoAndParent(cx, scope, desc, TopLevel.Builtins.Object);
      desc.defineProperty(cx, "value", value, 0);
      desc.defineProperty(cx, "writable", Boolean.FALSE, 0);
      desc.defineProperty(cx, "enumerable", Boolean.TRUE, 0);
      desc.defineProperty(cx, "configurable", Boolean.FALSE, 0);
      return desc;
   }

   private static Object js_match(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Object o = ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "match");
      Object regexp = args.length > 0 ? args[0] : Undefined.INSTANCE;
      RegExp regExpProxy = cx.getRegExp();
      if (regexp != null && !Undefined.isUndefined(regexp)) {
         Object matcher = ScriptRuntime.getObjectElem(cx, scope, regexp, SymbolKey.MATCH);
         if (matcher != null && !Undefined.isUndefined(matcher)) {
            if (!(matcher instanceof Callable)) {
               throw ScriptRuntime.notFunctionError(cx, regexp, matcher, SymbolKey.MATCH.getName());
            }

            return ((Callable)matcher).call(cx, scope, ScriptRuntime.toObject(cx, scope, regexp), new Object[]{o});
         }
      }

      String s = ScriptRuntime.toString(cx, o);
      String regexpToString = Undefined.isUndefined(regexp) ? "" : ScriptRuntime.toString(cx, regexp);
      String flags = null;
      Object compiledRegExp = regExpProxy.compileRegExp(cx, regexpToString, flags);
      Scriptable rx = regExpProxy.wrapRegExp(cx, scope, compiledRegExp);
      Object method = ScriptRuntime.getObjectElem(cx, scope, rx, SymbolKey.MATCH);
      if (!(method instanceof Callable)) {
         throw ScriptRuntime.notFunctionError(cx, rx, method, SymbolKey.MATCH.getName());
      } else {
         return ((Callable)method).call(cx, scope, rx, new Object[]{s});
      }
   }

   private static Object js_lastIndexOf(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      String target = ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "lastIndexOf"));
      String search = ScriptRuntime.toString(cx, args, 0);
      double end = ScriptRuntime.toNumber(cx, args, 1);
      if (Double.isNaN(end) || end > target.length()) {
         end = target.length();
      } else if (end < 0.0) {
         end = 0.0;
      }

      return target.lastIndexOf(search, (int)end);
   }

   private static Object js_substring(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      CharSequence target = ScriptRuntime.toCharSequence(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "substring"));
      int length = target.length();
      double start = ScriptRuntime.toInteger(cx, args, 0);
      if (start < 0.0) {
         start = 0.0;
      } else if (start > length) {
         start = length;
      }

      double end;
      if (args.length > 1 && args[1] != Undefined.INSTANCE) {
         end = ScriptRuntime.toInteger(cx, args[1]);
         if (end < 0.0) {
            end = 0.0;
         } else if (end > length) {
            end = length;
         }

         if (end < start) {
            double temp = start;
            start = end;
            end = temp;
         }
      } else {
         end = length;
      }

      return target.subSequence((int)start, (int)end);
   }

   private static Object js_toLowerCase(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      String thisStr = ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "toLowerCase"));
      return thisStr.toLowerCase(Locale.ROOT);
   }

   private static Object js_toUpperCase(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      String thisStr = ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "toUpperCase"));
      return thisStr.toUpperCase(Locale.ROOT);
   }

   int getLength() {
      return this.string.length();
   }

   private static CharSequence js_substr(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      CharSequence target = ScriptRuntime.toCharSequence(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "substr"));
      if (args.length < 1) {
         return target;
      } else {
         double begin = ScriptRuntime.toInteger(cx, args[0]);
         int length = target.length();
         if (begin < 0.0) {
            begin += length;
            if (begin < 0.0) {
               begin = 0.0;
            }
         } else if (begin > length) {
            begin = length;
         }

         double end = length;
         if (args.length > 1) {
            Object lengthArg = args[1];
            if (!Undefined.isUndefined(lengthArg)) {
               end = ScriptRuntime.toInteger(cx, lengthArg);
               if (end < 0.0) {
                  end = 0.0;
               }

               end += begin;
               if (end > length) {
                  end = length;
               }
            }
         }

         return target.subSequence((int)begin, (int)end);
      }
   }

   private static String js_concat(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      String target = ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "concat"));
      int N = args.length;
      if (N == 0) {
         return target;
      } else if (N == 1) {
         String arg = ScriptRuntime.toString(cx, args[0]);
         return target.concat(arg);
      } else {
         int size = target.length();
         String[] argsAsStrings = new String[N];

         for (int i = 0; i != N; i++) {
            String s = ScriptRuntime.toString(cx, args[i]);
            argsAsStrings[i] = s;
            size += s.length();
         }

         StringBuilder result = new StringBuilder(size);
         result.append(target);

         for (int i = 0; i != N; i++) {
            result.append(argsAsStrings[i]);
         }

         return result.toString();
      }
   }

   private static Object js_slice(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      CharSequence target = ScriptRuntime.toCharSequence(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "slice"));
      double begin = args.length < 1 ? 0.0 : ScriptRuntime.toInteger(cx, args[0]);
      int length = target.length();
      if (begin < 0.0) {
         begin += length;
         if (begin < 0.0) {
            begin = 0.0;
         }
      } else if (begin > length) {
         begin = length;
      }

      double end;
      if (args.length >= 2 && args[1] != Undefined.INSTANCE) {
         end = ScriptRuntime.toInteger(cx, args[1]);
         if (end < 0.0) {
            end += length;
            if (end < 0.0) {
               end = 0.0;
            }
         } else if (end > length) {
            end = length;
         }

         if (end < begin) {
            end = begin;
         }
      } else {
         end = length;
      }

      return target.subSequence((int)begin, (int)end);
   }

   private static Object js_at(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      String str = ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "at"));
      Object targetArg = args.length >= 1 ? args[0] : Undefined.INSTANCE;
      int len = str.length();
      int relativeIndex = (int)ScriptRuntime.toInteger(cx, targetArg);
      int k = relativeIndex >= 0 ? relativeIndex : len + relativeIndex;
      return k >= 0 && k < len ? str.substring(k, k + 1) : Undefined.INSTANCE;
   }

   private static Object js_equals(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      String s1 = ScriptRuntime.toString(cx, thisObj);
      String s2 = ScriptRuntime.toString(cx, args, 0);
      return s1.equals(s2);
   }

   private static Object js_equalsIgnoreCase(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      String s1 = ScriptRuntime.toString(cx, thisObj);
      String s2 = ScriptRuntime.toString(cx, args, 0);
      return s1.equalsIgnoreCase(s2);
   }

   private static Object js_search(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "search");
      return cx.getRegExp().action(cx, scope, thisObj, args, 3);
   }

   private static Object js_replace(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "replace");
      return cx.getRegExp().action(cx, scope, thisObj, args, 2);
   }

   private static Object js_replaceAll(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "replaceAll");
      return cx.getRegExp().action(cx, scope, thisObj, args, 4);
   }

   private static Object js_matchAll(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Object o = ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "matchAll");
      Object regexp = args.length > 0 ? args[0] : Undefined.INSTANCE;
      RegExp regExpProxy = cx.getRegExp();
      if (regexp != null && !Undefined.isUndefined(regexp)) {
         boolean isRegExp = regexp instanceof Scriptable && regExpProxy.isRegExp((Scriptable)regexp);
         if (isRegExp) {
            Object flags = ScriptRuntime.getObjectProp(cx, scope, regexp, "flags");
            ScriptRuntimeES6.requireObjectCoercible(cx, flags, "String", "matchAll");
            String flagsStr = ScriptRuntime.toString(cx, flags);
            if (!flagsStr.contains("g")) {
               throw ScriptRuntime.typeError0(cx, "msg.str.match.all.no.global.flag");
            }
         }

         Object matcher = ScriptRuntime.getObjectElem(cx, scope, regexp, SymbolKey.MATCH_ALL);
         if (matcher != null && !Undefined.isUndefined(matcher)) {
            if (!(matcher instanceof Callable)) {
               throw ScriptRuntime.notFunctionError(cx, regexp, matcher, SymbolKey.MATCH_ALL.getName());
            }

            return ((Callable)matcher).call(cx, scope, ScriptRuntime.toObject(cx, scope, regexp), new Object[]{o});
         }
      }

      String s = ScriptRuntime.toString(cx, o);
      String regexpToString = Undefined.isUndefined(regexp) ? "" : ScriptRuntime.toString(cx, regexp);
      Object compiledRegExp = regExpProxy.compileRegExp(cx, regexpToString, "g");
      Scriptable rx = regExpProxy.wrapRegExp(cx, scope, compiledRegExp);
      Object method = ScriptRuntime.getObjectElem(cx, scope, rx, SymbolKey.MATCH_ALL);
      if (!(method instanceof Callable)) {
         throw ScriptRuntime.notFunctionError(cx, rx, method, SymbolKey.MATCH_ALL.getName());
      } else {
         return ((Callable)method).call(cx, scope, rx, new Object[]{s});
      }
   }

   private static Object js_localeCompare(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      String thisStr = ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "localeCompare"));
      Collator collator = Collator.getInstance();
      collator.setStrength(3);
      collator.setDecomposition(1);
      return collator.compare(thisStr, ScriptRuntime.toString(cx, args, 0));
   }

   private static Object js_toLocaleLowerCase(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      String thisStr = ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "toLocaleLowerCase"));
      return thisStr.toLowerCase(Locale.ROOT);
   }

   private static Object js_toLocaleUpperCase(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      String thisStr = ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "toLocaleUpperCase"));
      return thisStr.toUpperCase(Locale.ROOT);
   }

   private static Object js_trim(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      String str = ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "trim"));
      char[] chars = str.toCharArray();
      int start = 0;

      while (start < chars.length && ScriptRuntime.isJSWhitespaceOrLineTerminator(chars[start])) {
         start++;
      }

      int end = chars.length;

      while (end > start && ScriptRuntime.isJSWhitespaceOrLineTerminator(chars[end - 1])) {
         end--;
      }

      return str.substring(start, end);
   }

   private static Object js_trimLeft(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      String str = ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "trimLeft"));
      char[] chars = str.toCharArray();
      int start = 0;

      while (start < chars.length && ScriptRuntime.isJSWhitespaceOrLineTerminator(chars[start])) {
         start++;
      }

      int end = chars.length;
      return str.substring(start, end);
   }

   private static Object js_trimRight(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      String str = ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "trimRight"));
      char[] chars = str.toCharArray();
      int start = 0;
      int end = chars.length;

      while (end > start && ScriptRuntime.isJSWhitespaceOrLineTerminator(chars[end - 1])) {
         end--;
      }

      return str.substring(start, end);
   }

   private static Object js_normalize(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      if (args.length != 0 && !Undefined.isUndefined(args[0])) {
         String formStr = ScriptRuntime.toString(cx, args, 0);
         Form form;
         if (Form.NFD.name().equals(formStr)) {
            form = Form.NFD;
         } else if (Form.NFKC.name().equals(formStr)) {
            form = Form.NFKC;
         } else if (Form.NFKD.name().equals(formStr)) {
            form = Form.NFKD;
         } else {
            if (!Form.NFC.name().equals(formStr)) {
               throw ScriptRuntime.rangeError(cx, "The normalization form should be one of 'NFC', 'NFD', 'NFKC', 'NFKD'.");
            }

            form = Form.NFC;
         }

         return Normalizer.normalize(ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "normalize")), form);
      } else {
         return Normalizer.normalize(ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "normalize")), Form.NFC);
      }
   }

   private static String js_repeat(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      String str = ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "repeat"));
      double cnt = ScriptRuntime.toInteger(cx, args, 0);
      if (cnt < 0.0 || cnt == 1.0 / 0.0) {
         throw ScriptRuntime.rangeError(cx, "Invalid count value");
      } else if (cnt != 0.0 && !str.isEmpty()) {
         long size = str.length() * (long)cnt;
         if (!(cnt > 2.147483647E9) && size <= 2147483647L) {
            StringBuilder retval = new StringBuilder((int)size);
            retval.append(str);
            int i = 1;

            int icnt;
            for (icnt = (int)cnt; i <= icnt / 2; i *= 2) {
               retval.append((CharSequence)retval);
            }

            if (i < icnt) {
               retval.append(retval, 0, str.length() * (icnt - i));
            }

            return retval.toString();
         } else {
            throw ScriptRuntime.rangeError(cx, "Invalid size or count value");
         }
      } else {
         return "";
      }
   }

   private static Object js_codePointAt(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      String str = ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "codePointAt"));
      double cnt = ScriptRuntime.toInteger(cx, args, 0);
      return !(cnt < 0.0) && !(cnt >= str.length()) ? str.codePointAt((int)cnt) : Undefined.INSTANCE;
   }

   private static String pad(Context cx, Scriptable thisObj, String functionName, Object[] args, boolean atStart) {
      String pad = ScriptRuntime.toString(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", functionName));
      long intMaxLength = ScriptRuntime.toLength(cx, args, 0);
      if (intMaxLength <= pad.length()) {
         return pad;
      } else {
         String filler = " ";
         if (args.length >= 2 && !Undefined.isUndefined(args[1])) {
            filler = ScriptRuntime.toString(cx, args[1]);
            if (filler.isEmpty()) {
               return pad;
            }
         }

         int fillLen = (int)(intMaxLength - pad.length());
         StringBuilder concat = new StringBuilder();

         do {
            concat.append(filler);
         } while (concat.length() < fillLen);

         concat.setLength(fillLen);
         return atStart ? concat.append(pad).toString() : concat.insert(0, pad).toString();
      }
   }

   private static Object js_padStart(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return pad(cx, thisObj, "padStart", args, true);
   }

   private static Object js_padEnd(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return pad(cx, thisObj, "padEnd", args, false);
   }

   private static Object js_raw(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Object arg0 = args.length > 0 ? args[0] : Undefined.INSTANCE;
      Scriptable cooked = ScriptRuntime.toObject(cx, scope, arg0);
      Object rawValue = ScriptRuntime.getObjectProp(cx, cooked, "raw");
      Scriptable raw = ScriptRuntime.toObject(cx, scope, rawValue);
      long rawLength = NativeArray.getLengthProperty(cx, raw);
      if (rawLength > 2147483647L) {
         throw ScriptRuntime.rangeError(cx, "raw.length > 2147483647");
      } else {
         int literalSegments = (int)rawLength;
         if (literalSegments <= 0) {
            return "";
         } else {
            StringBuilder elements = new StringBuilder();
            int nextIndex = 0;

            while (true) {
               Object next = ScriptRuntime.getObjectIndex(cx, raw, nextIndex);
               String nextSeg = ScriptRuntime.toString(cx, next);
               elements.append(nextSeg);
               if (++nextIndex == literalSegments) {
                  return elements;
               }

               if (args.length > nextIndex) {
                  next = args[nextIndex];
                  String nextSub = ScriptRuntime.toString(cx, next);
                  elements.append(nextSub);
               }
            }
         }
      }
   }

   private static Object js_isWellFormed(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      CharSequence str = ScriptRuntime.toCharSequence(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "isWellFormed"));
      int len = str.length();
      boolean foundLeadingSurrogate = false;

      for (int i = 0; i < len; i++) {
         char c = str.charAt(i);
         if (Character.isHighSurrogate(c)) {
            if (foundLeadingSurrogate) {
               return false;
            }

            foundLeadingSurrogate = true;
         } else if (Character.isLowSurrogate(c)) {
            if (!foundLeadingSurrogate) {
               return false;
            }

            foundLeadingSurrogate = false;
         } else if (foundLeadingSurrogate) {
            return false;
         }
      }

      return !foundLeadingSurrogate;
   }

   private static Object js_toWellFormed(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      CharSequence str = ScriptRuntime.toCharSequence(cx, ScriptRuntimeES6.requireObjectCoercible(cx, thisObj, "String", "toWellFormed"));
      Map<Integer, Boolean> surrogates = new HashMap<>();
      int len = str.length();
      char prev = 0;
      int firstSurrogateIndex = -1;

      for (int i = 0; i < len; i++) {
         char c = str.charAt(i);
         if (Character.isHighSurrogate(prev) && Character.isLowSurrogate(c)) {
            surrogates.put(i - 1, Boolean.TRUE);
            surrogates.put(i, Boolean.TRUE);
         } else if (Character.isHighSurrogate(c) || Character.isLowSurrogate(c)) {
            surrogates.put(i, Boolean.FALSE);
            if (firstSurrogateIndex == -1) {
               firstSurrogateIndex = i;
            }
         }

         prev = c;
      }

      if (surrogates.isEmpty()) {
         return str.toString();
      } else {
         StringBuilder sb = new StringBuilder(str.subSequence(0, firstSurrogateIndex));

         for (int i = firstSurrogateIndex; i < len; i++) {
            char c = str.charAt(i);
            Boolean pairOrNormal = surrogates.get(i);
            if (pairOrNormal != null && !pairOrNormal) {
               sb.append('�');
            } else {
               sb.append(c);
            }
         }

         return sb.toString();
      }
   }

   private static Object js_bold(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return tagify(cx, thisObj, "bold", "b", null, args);
   }

   private static Object js_italics(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return tagify(cx, thisObj, "italics", "i", null, args);
   }

   private static Object js_fixed(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return tagify(cx, thisObj, "fixed", "tt", null, args);
   }

   private static Object js_strike(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return tagify(cx, thisObj, "strike", "strike", null, args);
   }

   private static Object js_small(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return tagify(cx, thisObj, "small", "small", null, args);
   }

   private static Object js_big(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return tagify(cx, thisObj, "big", "big", null, args);
   }

   private static Object js_blink(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return tagify(cx, thisObj, "blink", "blink", null, args);
   }

   private static Object js_sup(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return tagify(cx, thisObj, "sup", "sup", null, args);
   }

   private static Object js_sub(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return tagify(cx, thisObj, "sub", "sub", null, args);
   }

   private static Object js_fontsize(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return tagify(cx, thisObj, "fontsize", "font", "size", args);
   }

   private static Object js_fontcolor(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return tagify(cx, thisObj, "fontcolor", "font", "color", args);
   }

   private static Object js_link(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return tagify(cx, thisObj, "link", "a", "href", args);
   }

   private static Object js_anchor(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return tagify(cx, thisObj, "anchor", "a", "name", args);
   }
}
