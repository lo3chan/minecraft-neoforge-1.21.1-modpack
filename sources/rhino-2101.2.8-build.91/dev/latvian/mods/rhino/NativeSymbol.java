package dev.latvian.mods.rhino;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class NativeSymbol extends ScriptableObject implements Symbol {
   public static final String CLASS_NAME = "Symbol";
   private static final Object GLOBAL_TABLE_KEY = new Object();
   private static final Object CONSTRUCTOR_SLOT = new Object();
   private final SymbolKey key;
   private final NativeSymbol.SymbolKind kind;
   private final NativeSymbol symbolData;

   public static void init(Context cx, Scriptable scope, boolean sealed) {
      LambdaConstructor ctor = new LambdaConstructor(cx, scope, "Symbol", 0, 0, NativeSymbol::js_constructor) {
         @Override
         public Scriptable construct(Context cx, Scriptable scopex, Object[] args) {
            if (cx.getThreadLocal(NativeSymbol.CONSTRUCTOR_SLOT) == null) {
               throw ScriptRuntime.typeError1(cx, "msg.no.new", this.getFunctionName());
            } else {
               return (Scriptable)this.call(cx, scopex, null, args);
            }
         }

         @Override
         public Object call(Context cx, Scriptable scopex, Scriptable thisObj, Object[] args) {
            Scriptable obj = this.targetConstructor.construct(cx, scopex, args);
            obj.setPrototype(this.getClassPrototype(cx));
            obj.setParentScope(scopex);
            return obj;
         }
      };
      ctor.setPrototypePropertyAttributes(7);
      ctor.defineConstructorMethod(cx, scope, "for", 1, (lcx, lscope, thisObj, args) -> js_for(lcx, lscope, args, ctor), 2, 3);
      ctor.defineConstructorMethod(cx, scope, "keyFor", 1, NativeSymbol::js_keyFor, 2, 3);
      ctor.definePrototypeMethod(cx, scope, "toString", 0, NativeSymbol::js_toString, 2, 3);
      ctor.definePrototypeMethod(cx, scope, "valueOf", 0, NativeSymbol::js_valueOf, 2, 3);
      ctor.definePrototypeMethod(cx, scope, SymbolKey.TO_PRIMITIVE, 1, NativeSymbol::js_valueOf, 3, 3);
      ctor.definePrototypeProperty(cx, SymbolKey.TO_STRING_TAG, "Symbol", 3);
      ctor.definePrototypeProperty(cx, "description", NativeSymbol::js_description, 3);
      ScriptableObject.defineProperty(scope, "Symbol", ctor, 2, cx);
      cx.putThreadLocal(CONSTRUCTOR_SLOT, Boolean.TRUE);

      try {
         createStandardSymbol(cx, scope, ctor, "iterator", SymbolKey.ITERATOR);
         createStandardSymbol(cx, scope, ctor, "species", SymbolKey.SPECIES);
         createStandardSymbol(cx, scope, ctor, "toStringTag", SymbolKey.TO_STRING_TAG);
         createStandardSymbol(cx, scope, ctor, "hasInstance", SymbolKey.HAS_INSTANCE);
         createStandardSymbol(cx, scope, ctor, "isConcatSpreadable", SymbolKey.IS_CONCAT_SPREADABLE);
         createStandardSymbol(cx, scope, ctor, "isRegExp", SymbolKey.IS_REGEXP);
         createStandardSymbol(cx, scope, ctor, "toPrimitive", SymbolKey.TO_PRIMITIVE);
         createStandardSymbol(cx, scope, ctor, "match", SymbolKey.MATCH);
         createStandardSymbol(cx, scope, ctor, "matchAll", SymbolKey.MATCH_ALL);
         createStandardSymbol(cx, scope, ctor, "replace", SymbolKey.REPLACE);
         createStandardSymbol(cx, scope, ctor, "search", SymbolKey.SEARCH);
         createStandardSymbol(cx, scope, ctor, "split", SymbolKey.SPLIT);
         createStandardSymbol(cx, scope, ctor, "unscopables", SymbolKey.UNSCOPABLES);
      } finally {
         cx.removeThreadLocal(CONSTRUCTOR_SLOT);
      }

      if (sealed) {
         ctor.sealObject(cx);
      }
   }

   NativeSymbol(SymbolKey key, NativeSymbol.SymbolKind kind) {
      this.key = key;
      this.symbolData = this;
      this.kind = kind;
   }

   public NativeSymbol(NativeSymbol s) {
      this.key = s.key;
      this.symbolData = s.symbolData;
      this.kind = s.kind;
   }

   NativeSymbol.SymbolKind getKind() {
      return this.kind;
   }

   public static NativeSymbol construct(Context cx, Scriptable scope, Object[] args) {
      cx.putThreadLocal(CONSTRUCTOR_SLOT, Boolean.TRUE);

      NativeSymbol var3;
      try {
         var3 = (NativeSymbol)cx.newObject(scope, "Symbol", args);
      } finally {
         cx.removeThreadLocal(CONSTRUCTOR_SLOT);
      }

      return var3;
   }

   @Override
   public String getClassName() {
      return "Symbol";
   }

   private static NativeSymbol createRegisteredSymbol(Context cx, Scriptable scope, LambdaConstructor ctor, String name) {
      NativeSymbol sym = new NativeSymbol(new SymbolKey(name), NativeSymbol.SymbolKind.REGISTERED);
      sym.setPrototype(ctor.getClassPrototype(cx));
      sym.setParentScope(scope);
      return sym;
   }

   private static void createStandardSymbol(Context cx, Scriptable scope, LambdaConstructor ctor, String name, SymbolKey key) {
      NativeSymbol sym = new NativeSymbol(key, NativeSymbol.SymbolKind.BUILT_IN);
      sym.setPrototype(ctor.getClassPrototype(cx));
      sym.setParentScope(scope);
      ctor.defineProperty(cx, name, sym, 7);
   }

   private static NativeSymbol getSelf(Context cx, Scriptable thisObj) {
      return LambdaConstructor.convertThisObject(cx, thisObj, NativeSymbol.class);
   }

   private static NativeSymbol js_constructor(Context cx, Scriptable scope, Object[] args) {
      String desc = null;
      if (args.length > 0 && !Undefined.isUndefined(args[0])) {
         desc = ScriptRuntime.toString(cx, args[0]);
      }

      return args.length > 1
         ? new NativeSymbol((SymbolKey)args[1], NativeSymbol.SymbolKind.REGULAR)
         : new NativeSymbol(new SymbolKey(desc), NativeSymbol.SymbolKind.REGULAR);
   }

   private static Object js_toString(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return getSelf(cx, thisObj).toString();
   }

   private static Object js_valueOf(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return getSelf(cx, thisObj).symbolData;
   }

   private static Object js_description(Scriptable thisObj) {
      return ((NativeSymbol)thisObj).getKey().getDescription();
   }

   private static Object js_for(Context cx, Scriptable scope, Object[] args, LambdaConstructor constructor) {
      String name = args.length > 0 ? ScriptRuntime.toString(cx, args[0]) : ScriptRuntime.toString(cx, Undefined.INSTANCE);
      Map<String, NativeSymbol> table = getGlobalMap(scope);
      NativeSymbol ret = table.get(name);
      if (ret == null) {
         ret = createRegisteredSymbol(cx, scope, constructor, name);
         table.put(name, ret);
      }

      return ret;
   }

   private static Object js_keyFor(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      if ((args.length > 0 ? args[0] : Undefined.INSTANCE) instanceof NativeSymbol sym) {
         Map<String, NativeSymbol> table = getGlobalMap(scope);

         for (Entry<String, NativeSymbol> e : table.entrySet()) {
            if (e.getValue().key == sym.key) {
               return e.getKey();
            }
         }

         return Undefined.INSTANCE;
      } else {
         throw ScriptRuntime.throwCustomError(cx, scope, "TypeError", "Not a Symbol");
      }
   }

   @Override
   public String toString() {
      return this.key.toString();
   }

   private static boolean isStrictMode(Context cx) {
      return cx != null && cx.isStrictMode();
   }

   @Override
   public void put(Context cx, String name, Scriptable start, Object value) {
      if (!this.isSymbol()) {
         super.put(cx, name, start, value);
      } else if (isStrictMode(cx)) {
         throw ScriptRuntime.typeError0(cx, "msg.no.assign.symbol.strict");
      }
   }

   @Override
   public void put(Context cx, int index, Scriptable start, Object value) {
      if (!this.isSymbol()) {
         super.put(cx, index, start, value);
      } else if (isStrictMode(cx)) {
         throw ScriptRuntime.typeError0(cx, "msg.no.assign.symbol.strict");
      }
   }

   @Override
   public void put(Context cx, Symbol key, Scriptable start, Object value) {
      if (!this.isSymbol()) {
         super.put(cx, key, start, value);
      } else if (isStrictMode(cx)) {
         throw ScriptRuntime.typeError0(cx, "msg.no.assign.symbol.strict");
      }
   }

   public boolean isSymbol() {
      return this.symbolData == this;
   }

   @Override
   public MemberType getTypeOf() {
      return this.isSymbol() ? MemberType.SYMBOL : super.getTypeOf();
   }

   @Override
   public int hashCode() {
      return this.key.hashCode();
   }

   @Override
   public boolean equals(Object x) {
      return this.key.equals(x);
   }

   SymbolKey getKey() {
      return this.key;
   }

   private static Map<String, NativeSymbol> getGlobalMap(Scriptable scope) {
      ScriptableObject top = (ScriptableObject)getTopLevelScope(scope);
      Map<String, NativeSymbol> map = (Map<String, NativeSymbol>)top.getAssociatedValue(GLOBAL_TABLE_KEY);
      if (map == null) {
         map = new HashMap<>();
         top.associateValue(GLOBAL_TABLE_KEY, map);
      }

      return map;
   }

   static enum SymbolKind {
      REGULAR,
      BUILT_IN,
      REGISTERED;
   }
}
