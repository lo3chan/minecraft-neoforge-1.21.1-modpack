package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.Iterator;

public final class NativeIterator extends ScriptableObject {
   public static final String ITERATOR_PROPERTY_NAME = "__iterator__";
   private static final Object ITERATOR_TAG = "Iterator";
   private static final String STOP_ITERATION = "StopIteration";
   private static final String CLASS_NAME = "Iterator";
   private IdEnumeration objectIterator;

   static void init(Context cx, ScriptableObject scope, boolean sealed) {
      LambdaConstructor constructor = new LambdaConstructor(cx, scope, "Iterator", 2, NativeIterator::jsConstructorCall, NativeIterator::jsConstructor);
      constructor.setPrototypePropertyAttributes(7);
      NativeIterator proto = new NativeIterator();
      constructor.setPrototypeScriptable(proto, cx);
      constructor.definePrototypeMethod(cx, scope, "next", 0, NativeIterator::js_next);
      constructor.definePrototypeMethod(cx, scope, "__iterator__", 1, NativeIterator::js_iteratorMethod);
      ScriptableObject.defineProperty(scope, "Iterator", constructor, 2, cx);
      if (sealed) {
         constructor.sealObject(cx);
         ((ScriptableObject)constructor.getPrototypeProperty(cx)).sealObject(cx);
      }

      ES6Generator.init(scope, sealed, cx);
      NativeObject obj = new NativeIterator.StopIteration(cx);
      obj.setPrototype(getObjectPrototype(scope, cx));
      obj.setParentScope(scope);
      if (sealed) {
         obj.sealObject(cx);
      }

      defineProperty(scope, "StopIteration", obj, 2, cx);
      scope.associateValue(ITERATOR_TAG, obj);
   }

   public static Object getStopIterationObject(Scriptable scope, Context cx) {
      Scriptable top = getTopLevelScope(scope);
      return getTopScopeValue(top, ITERATOR_TAG, cx);
   }

   private static Object jsConstructorCall(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Scriptable target = requireIteratorTarget(cx, scope, args);
      boolean keyOnly = isKeyOnly(cx, args);
      Iterator<?> iterator = getJavaIterator(target);
      if (iterator != null) {
         Scriptable topScope = getTopLevelScope(scope);
         return cx.wrap(topScope, new NativeIterator.WrappedJavaIterator(cx, iterator, topScope), TypeInfo.of(NativeIterator.WrappedJavaIterator.class));
      } else {
         Scriptable jsIterator = ScriptRuntime.toIterator(cx, scope, target, keyOnly);
         return jsIterator != null ? jsIterator : createNativeIterator(cx, scope, target, keyOnly);
      }
   }

   private static Scriptable jsConstructor(Context cx, Scriptable scope, Object[] args) {
      Scriptable target = requireIteratorTarget(cx, scope, args);
      boolean keyOnly = isKeyOnly(cx, args);
      return createNativeIterator(cx, scope, target, keyOnly);
   }

   private static Scriptable requireIteratorTarget(Context cx, Scriptable scope, Object[] args) {
      if (args.length != 0 && args[0] != null && args[0] != Undefined.INSTANCE) {
         return ScriptRuntime.toObject(cx, scope, args[0]);
      } else {
         Object argument = args.length == 0 ? Undefined.INSTANCE : args[0];
         throw ScriptRuntime.typeError1(cx, "msg.no.properties", ScriptRuntime.toString(cx, argument));
      }
   }

   private static boolean isKeyOnly(Context cx, Object[] args) {
      return args.length > 1 && ScriptRuntime.toBoolean(cx, args[1]);
   }

   private static Scriptable createNativeIterator(Context cx, Scriptable scope, Scriptable target, boolean keyOnly) {
      IdEnumeration objectIterator = ScriptRuntime.enumInit(cx, scope, target, keyOnly ? 3 : 5);
      objectIterator.enumNumbers = true;
      NativeIterator result = new NativeIterator(objectIterator);
      result.setPrototype(getClassPrototype(scope, result.getClassName(), cx));
      result.setParentScope(scope);
      return result;
   }

   private static Iterator<?> getJavaIterator(Object obj) {
      if (obj instanceof Wrapper) {
         Object unwrapped = ((Wrapper)obj).unwrap();
         Iterator<?> iterator = null;
         if (unwrapped instanceof Iterator) {
            iterator = (Iterator<?>)unwrapped;
         }

         if (unwrapped instanceof Iterable) {
            iterator = ((Iterable)unwrapped).iterator();
         }

         return iterator;
      } else {
         return null;
      }
   }

   private NativeIterator() {
   }

   private NativeIterator(IdEnumeration objectIterator) {
      this.objectIterator = objectIterator;
   }

   @Override
   public String getClassName() {
      return "Iterator";
   }

   private static NativeIterator realThis(Context cx, Scriptable thisObj) {
      return LambdaConstructor.convertThisObject(cx, thisObj, NativeIterator.class);
   }

   private static Object js_next(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      NativeIterator iterator = realThis(cx, thisObj);
      return iterator.objectIterator.nextExec(cx, scope);
   }

   private static Object js_iteratorMethod(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return realThis(cx, thisObj);
   }

   public static class StopIteration extends NativeObject {
      private Object value = Undefined.INSTANCE;

      public StopIteration(Context cx) {
         super(cx.factory);
      }

      public StopIteration(Context cx, Object val) {
         this(cx);
         this.value = val;
      }

      public Object getValue() {
         return this.value;
      }

      @Override
      public String getClassName() {
         return "StopIteration";
      }

      @Override
      public boolean hasInstance(Context cx, Scriptable instance) {
         return instance instanceof NativeIterator.StopIteration;
      }
   }

   public static class WrappedJavaIterator {
      private final Context localContext;
      private final Iterator<?> iterator;
      private final Scriptable scope;

      WrappedJavaIterator(Context cx, Iterator<?> iterator, Scriptable scope) {
         this.localContext = cx;
         this.iterator = iterator;
         this.scope = scope;
      }

      public Object next() {
         if (!this.iterator.hasNext()) {
            throw new JavaScriptException(this.localContext, NativeIterator.getStopIterationObject(this.scope, this.localContext), null, 0);
         } else {
            return this.iterator.next();
         }
      }

      public Object __iterator__(boolean b) {
         return this;
      }
   }
}
