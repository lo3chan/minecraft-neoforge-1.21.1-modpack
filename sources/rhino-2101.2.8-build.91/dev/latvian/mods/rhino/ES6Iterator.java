package dev.latvian.mods.rhino;

public abstract class ES6Iterator extends ScriptableObject {
   public static final String NEXT_METHOD = "next";
   public static final String DONE_PROPERTY = "done";
   public static final String RETURN_PROPERTY = "return";
   public static final String VALUE_PROPERTY = "value";
   public static final String RETURN_METHOD = "return";
   protected boolean exhausted = false;
   private String tag;

   protected static void init(ScriptableObject scope, boolean sealed, ScriptableObject prototype, String tag, Context cx) {
      if (scope != null) {
         prototype.setParentScope(scope);
         prototype.setPrototype(getObjectPrototype(scope, cx));
      }

      LambdaFunction next = new LambdaFunction(cx, scope, "next", 0, ES6Iterator::js_next, false);
      prototype.defineProperty(cx, "next", next, 2);
      LambdaFunction iterator = new LambdaFunction(cx, scope, "[Symbol.iterator]", 1, ES6Iterator::js_iterator, false);
      prototype.defineProperty(cx, SymbolKey.ITERATOR, iterator, 2);
      prototype.defineProperty(cx, SymbolKey.TO_STRING_TAG, prototype.getClassName(), 3);
      if (sealed) {
         prototype.sealObject(cx);
      }

      if (scope != null) {
         scope.associateValue(tag, prototype);
      }
   }

   static Scriptable makeIteratorResult(Context cx, Scriptable scope, Boolean done) {
      return makeIteratorResult(cx, scope, done, Undefined.INSTANCE);
   }

   static Scriptable makeIteratorResult(Context cx, Scriptable scope, Boolean done, Object value) {
      Scriptable iteratorResult = cx.newObject(scope);
      ScriptableObject.putProperty(iteratorResult, "value", value, cx);
      ScriptableObject.putProperty(iteratorResult, "done", done, cx);
      return iteratorResult;
   }

   protected ES6Iterator() {
   }

   protected ES6Iterator(Scriptable scope, String tag, Context cx) {
      this.tag = tag;
      Scriptable top = ScriptableObject.getTopLevelScope(scope);
      this.setParentScope(top);
      ScriptableObject prototype = (ScriptableObject)ScriptableObject.getTopScopeValue(top, tag, cx);
      this.setPrototype(prototype);
   }

   private static ES6Iterator realThis(Context cx, Scriptable thisObj) {
      return LambdaConstructor.convertThisObject(cx, thisObj, ES6Iterator.class);
   }

   private static Object js_next(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      ES6Iterator iterator = realThis(cx, thisObj);
      return iterator.next(cx, scope);
   }

   private static Object js_iterator(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return thisObj;
   }

   protected abstract boolean isDone(Context var1, Scriptable var2);

   protected abstract Object nextValue(Context var1, Scriptable var2);

   protected Object next(Context cx, Scriptable scope) {
      Object value = Undefined.INSTANCE;
      boolean done = this.isDone(cx, scope) || this.exhausted;
      if (!done) {
         value = this.nextValue(cx, scope);
      } else {
         this.exhausted = true;
      }

      return makeIteratorResult(cx, scope, done, value);
   }

   protected String getTag() {
      return this.tag;
   }
}
