package dev.latvian.mods.rhino;

import java.util.ArrayList;

public class NativePromise extends ScriptableObject {
   private NativePromise.State state = NativePromise.State.PENDING;
   private Object result = null;
   private boolean handled = false;
   private ArrayList<NativePromise.Reaction> fulfillReactions = new ArrayList<>();
   private ArrayList<NativePromise.Reaction> rejectReactions = new ArrayList<>();

   public static void init(Context cx, Scriptable scope, boolean sealed) {
      LambdaConstructor constructor = new LambdaConstructor(cx, scope, "Promise", 1, 2, NativePromise::constructor);
      constructor.setPrototypePropertyAttributes(7);
      constructor.defineConstructorMethod(cx, scope, "resolve", 1, NativePromise::resolve, 2, 3);
      constructor.defineConstructorMethod(cx, scope, "reject", 1, NativePromise::reject, 2, 3);
      constructor.defineConstructorMethod(cx, scope, "all", 1, NativePromise::all, 2, 3);
      constructor.defineConstructorMethod(cx, scope, "allSettled", 1, NativePromise::allSettled, 2, 3);
      constructor.defineConstructorMethod(cx, scope, "race", 1, NativePromise::race, 2, 3);
      constructor.defineConstructorMethod(cx, scope, "any", 1, NativePromise::any, 2, 3);
      constructor.defineConstructorMethod(cx, scope, "withResolvers", 0, NativePromise::withResolvers, 2, 3);
      constructor.defineConstructorMethod(cx, scope, "try", 1, NativePromise::promiseTry, 2, 3);
      ScriptRuntimeES6.addSymbolSpecies(cx, scope, constructor);
      constructor.definePrototypeMethod(cx, scope, "then", 2, (lcx, lscope, thisObj, args) -> {
         NativePromise self = LambdaConstructor.convertThisObject(lcx, thisObj, NativePromise.class);
         return self.then(lcx, lscope, constructor, args);
      }, 2, 3);
      constructor.definePrototypeMethod(cx, scope, "catch", 1, NativePromise::doCatch, 2, 3);
      constructor.definePrototypeMethod(cx, scope, "finally", 1, (lcx, lscope, thisObj, args) -> doFinally(lcx, lscope, thisObj, constructor, args), 2, 3);
      constructor.definePrototypeProperty(cx, SymbolKey.TO_STRING_TAG, "Promise", 3);
      ScriptableObject.defineProperty(scope, "Promise", constructor, 2, cx);
      if (sealed) {
         constructor.sealObject(cx);
      }
   }

   private static Scriptable constructor(Context cx, Scriptable scope, Object[] args) {
      if (args.length >= 1 && args[0] instanceof Callable executor) {
         NativePromise promise = new NativePromise();
         NativePromise.ResolvingFunctions resolving = new NativePromise.ResolvingFunctions(cx, scope, promise);
         Scriptable thisObj = Undefined.SCRIPTABLE_INSTANCE;
         if (!cx.isStrictMode() && cx.hasTopCallScope()) {
            thisObj = cx.getTopCallScope();
         }

         try {
            executor.call(cx, scope, thisObj, new Object[]{resolving.resolve, resolving.reject});
         } catch (RhinoException var8) {
            resolving.reject.call(cx, scope, thisObj, new Object[]{getErrorObject(cx, scope, var8)});
         }

         return promise;
      } else {
         throw ScriptRuntime.typeError0(cx, "msg.function.expected");
      }
   }

   @Override
   public String getClassName() {
      return "Promise";
   }

   Object getResult() {
      return this.result;
   }

   private static Object resolve(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      if (!ScriptRuntime.isObject(thisObj)) {
         throw ScriptRuntime.typeError1(cx, "msg.arg.not.object", ScriptRuntime.typeof(cx, thisObj).toString());
      } else {
         Object arg = args.length > 0 ? args[0] : Undefined.INSTANCE;
         return resolveInternal(cx, scope, thisObj, arg);
      }
   }

   private static Object resolveInternal(Context cx, Scriptable scope, Object constructor, Object arg) {
      if (arg instanceof NativePromise) {
         Object argConstructor = ScriptRuntime.getObjectProp(cx, scope, arg, "constructor");
         if (argConstructor == constructor) {
            return arg;
         }
      }

      NativePromise.Capability cap = new NativePromise.Capability(cx, scope, constructor);
      cap.resolve.call(cx, scope, Undefined.SCRIPTABLE_INSTANCE, new Object[]{arg});
      return cap.promise;
   }

   private static Object reject(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      if (!ScriptRuntime.isObject(thisObj)) {
         throw ScriptRuntime.typeError1(cx, "msg.arg.not.object", ScriptRuntime.typeof(cx, thisObj).toString());
      } else {
         Object arg = args.length > 0 ? args[0] : Undefined.INSTANCE;
         NativePromise.Capability cap = new NativePromise.Capability(cx, scope, thisObj);
         cap.reject.call(cx, scope, Undefined.SCRIPTABLE_INSTANCE, new Object[]{arg});
         return cap.promise;
      }
   }

   private static Object all(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return doAll(cx, scope, thisObj, args, true);
   }

   private static Object allSettled(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return doAll(cx, scope, thisObj, args, false);
   }

   private static Object doAll(Context cx, Scriptable scope, Scriptable thisObj, Object[] args, boolean failFast) {
      NativePromise.Capability cap = new NativePromise.Capability(cx, scope, thisObj);
      Object arg = args.length > 0 ? args[0] : Undefined.INSTANCE;

      IteratorLikeIterable iterable;
      try {
         Object maybeIterable = ScriptRuntime.callIterator(arg, cx, scope);
         iterable = new IteratorLikeIterable(cx, scope, maybeIterable);
      } catch (RhinoException var15) {
         cap.reject.call(cx, scope, Undefined.SCRIPTABLE_INSTANCE, new Object[]{getErrorObject(cx, scope, var15)});
         return cap.promise;
      }

      IteratorLikeIterable.Itr iterator = iterable.iterator();

      try {
         NativePromise.PromiseAllResolver resolver = new NativePromise.PromiseAllResolver(iterator, thisObj, cap, failFast);

         Object var10;
         try {
            var10 = resolver.resolve(cx, scope);
         } finally {
            if (!iterator.isDone()) {
               iterable.close();
            }
         }

         return var10;
      } catch (RhinoException var17) {
         cap.reject.call(cx, scope, Undefined.SCRIPTABLE_INSTANCE, new Object[]{getErrorObject(cx, scope, var17)});
         return cap.promise;
      }
   }

   private static Object any(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      NativePromise.Capability cap = new NativePromise.Capability(cx, scope, thisObj);
      Object arg = args.length > 0 ? args[0] : Undefined.INSTANCE;

      IteratorLikeIterable iterable;
      try {
         Object maybeIterable = ScriptRuntime.callIterator(arg, cx, scope);
         iterable = new IteratorLikeIterable(cx, scope, maybeIterable);
      } catch (RhinoException var14) {
         cap.reject.call(cx, scope, Undefined.SCRIPTABLE_INSTANCE, new Object[]{getErrorObject(cx, scope, var14)});
         return cap.promise;
      }

      IteratorLikeIterable.Itr iterator = iterable.iterator();

      try {
         NativePromise.PromiseAnyRejector rejector = new NativePromise.PromiseAnyRejector(iterator, thisObj, cap);

         Object var9;
         try {
            var9 = rejector.reject(cx, scope);
         } finally {
            if (!iterator.isDone()) {
               iterable.close();
            }
         }

         return var9;
      } catch (RhinoException var16) {
         cap.reject.call(cx, scope, Undefined.SCRIPTABLE_INSTANCE, new Object[]{getErrorObject(cx, scope, var16)});
         return cap.promise;
      }
   }

   private static Object race(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      NativePromise.Capability cap = new NativePromise.Capability(cx, scope, thisObj);
      Object arg = args.length > 0 ? args[0] : Undefined.INSTANCE;

      IteratorLikeIterable iterable;
      try {
         Object maybeIterable = ScriptRuntime.callIterator(arg, cx, scope);
         iterable = new IteratorLikeIterable(cx, scope, maybeIterable);
      } catch (RhinoException var13) {
         cap.reject.call(cx, scope, Undefined.SCRIPTABLE_INSTANCE, new Object[]{getErrorObject(cx, scope, var13)});
         return cap.promise;
      }

      IteratorLikeIterable.Itr iterator = iterable.iterator();

      try {
         Object re;
         try {
            re = performRace(cx, scope, iterator, thisObj, cap);
         } finally {
            if (!iterator.isDone()) {
               iterable.close();
            }
         }

         return re;
      } catch (RhinoException var15) {
         cap.reject.call(cx, scope, Undefined.SCRIPTABLE_INSTANCE, new Object[]{getErrorObject(cx, scope, var15)});
         return cap.promise;
      }
   }

   private static Object performRace(Context cx, Scriptable scope, IteratorLikeIterable.Itr iterator, Scriptable thisObj, NativePromise.Capability cap) {
      Callable resolve = ScriptRuntime.getPropFunctionAndThis(cx, scope, thisObj, "resolve");
      Scriptable localThis = cx.lastStoredScriptable();

      while (true) {
         Object nextVal = Undefined.INSTANCE;
         boolean nextOk = false;

         boolean hasNext;
         try {
            hasNext = iterator.hasNext();
            if (hasNext) {
               nextVal = iterator.next();
            }

            nextOk = true;
         } finally {
            if (!nextOk) {
               iterator.setDone(true);
            }
         }

         if (!hasNext) {
            return cap.promise;
         }

         Object nextPromise = resolve.call(cx, scope, localThis, new Object[]{nextVal});
         Callable thenFunc = ScriptRuntime.getPropFunctionAndThis(cx, scope, nextPromise, "then");
         thenFunc.call(cx, scope, cx.lastStoredScriptable(), new Object[]{cap.resolve, cap.reject});
      }
   }

   private static Object withResolvers(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      if (!ScriptRuntime.isObject(thisObj)) {
         throw ScriptRuntime.typeError1(cx, "msg.arg.not.object", ScriptRuntime.typeof(cx, thisObj).toString());
      } else {
         NativePromise.Capability cap = new NativePromise.Capability(cx, scope, thisObj);
         Scriptable result = cx.newObject(scope);
         result.put(cx, "promise", result, cap.promise);
         result.put(cx, "resolve", result, cap.resolve);
         result.put(cx, "reject", result, cap.reject);
         return result;
      }
   }

   private static Object promiseTry(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      if (!ScriptRuntime.isObject(thisObj)) {
         throw ScriptRuntime.typeError1(cx, "msg.arg.not.object", ScriptRuntime.typeof(cx, thisObj).toString());
      } else if (args.length >= 1 && args[0] instanceof Callable func) {
         NativePromise.Capability cap = new NativePromise.Capability(cx, scope, thisObj);
         Object[] funcArgs = new Object[args.length - 1];
         System.arraycopy(args, 1, funcArgs, 0, funcArgs.length);

         try {
            Object result = func.call(cx, scope, Undefined.SCRIPTABLE_INSTANCE, funcArgs);
            cap.resolve.call(cx, scope, Undefined.SCRIPTABLE_INSTANCE, new Object[]{result});
         } catch (RhinoException var8) {
            cap.reject.call(cx, scope, Undefined.SCRIPTABLE_INSTANCE, new Object[]{getErrorObject(cx, scope, var8)});
         }

         return cap.promise;
      } else {
         throw ScriptRuntime.typeError0(cx, "msg.function.expected");
      }
   }

   private Object then(Context cx, Scriptable scope, LambdaConstructor defaultConstructor, Object[] args) {
      Constructable constructable = AbstractEcmaObjectOperations.speciesConstructor(cx, this, defaultConstructor);
      NativePromise.Capability capability = new NativePromise.Capability(cx, scope, constructable);
      Callable onFulfilled = null;
      if (args.length >= 1 && args[0] instanceof Callable) {
         onFulfilled = (Callable)args[0];
      }

      Callable onRejected = null;
      if (args.length >= 2 && args[1] instanceof Callable) {
         onRejected = (Callable)args[1];
      }

      NativePromise.Reaction fulfillReaction = new NativePromise.Reaction(capability, NativePromise.ReactionType.FULFILL, onFulfilled);
      NativePromise.Reaction rejectReaction = new NativePromise.Reaction(capability, NativePromise.ReactionType.REJECT, onRejected);
      if (this.state == NativePromise.State.PENDING) {
         this.fulfillReactions.add(fulfillReaction);
         this.rejectReactions.add(rejectReaction);
      } else if (this.state == NativePromise.State.FULFILLED) {
         cx.enqueueMicrotask(() -> fulfillReaction.invoke(cx, scope, this.result));
      } else {
         assert this.state == NativePromise.State.REJECTED;

         this.markHandled(cx);
         cx.enqueueMicrotask(() -> rejectReaction.invoke(cx, scope, this.result));
      }

      return capability.promise;
   }

   private void markHandled(Context cx) {
      if (!this.handled) {
         cx.getUnhandledPromiseTracker().promiseHandled(this);
         this.handled = true;
      }
   }

   private static Object doCatch(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      Object arg = args.length > 0 ? args[0] : Undefined.INSTANCE;
      Scriptable coercedThis = ScriptRuntime.toObject(cx, scope, thisObj);
      Callable thenFunc = ScriptRuntime.getPropFunctionAndThis(cx, scope, coercedThis, "then");
      return thenFunc.call(cx, scope, cx.lastStoredScriptable(), new Object[]{Undefined.INSTANCE, arg});
   }

   private static Object doFinally(Context cx, Scriptable scope, Scriptable thisObj, LambdaConstructor defaultConstructor, Object[] args) {
      if (!ScriptRuntime.isObject(thisObj)) {
         throw ScriptRuntime.typeError1(cx, "msg.arg.not.object", ScriptRuntime.typeof(cx, thisObj).toString());
      } else {
         Object onFinally = args.length > 0 ? args[0] : Undefined.SCRIPTABLE_INSTANCE;
         Object thenFinally = onFinally;
         Object catchFinally = onFinally;
         Constructable constructor = AbstractEcmaObjectOperations.speciesConstructor(cx, thisObj, defaultConstructor);
         if (onFinally instanceof Callable callableOnFinally) {
            thenFinally = makeThenFinally(cx, scope, constructor, callableOnFinally);
            catchFinally = makeCatchFinally(cx, scope, constructor, callableOnFinally);
         }

         Callable thenFunc = ScriptRuntime.getPropFunctionAndThis(cx, scope, thisObj, "then");
         Scriptable to = cx.lastStoredScriptable();
         return thenFunc.call(cx, scope, to, new Object[]{thenFinally, catchFinally});
      }
   }

   private static Callable makeThenFinally(Context ccx, Scriptable scope, Object constructor, Callable onFinally) {
      return new LambdaFunction(ccx, scope, 1, (cx, ls, thisObj, args) -> {
         Object value = args.length > 0 ? args[0] : Undefined.INSTANCE;
         LambdaFunction valueThunk = new LambdaFunction(cx, scope, 0, (vc, vs, vt, va) -> value);
         Object result = onFinally.call(cx, ls, Undefined.SCRIPTABLE_INSTANCE, ScriptRuntime.EMPTY_OBJECTS);
         Object promise = resolveInternal(cx, scope, constructor, result);
         Callable thenFunc = ScriptRuntime.getPropFunctionAndThis(cx, scope, promise, "then");
         return thenFunc.call(cx, scope, cx.lastStoredScriptable(), new Object[]{valueThunk});
      });
   }

   private static Callable makeCatchFinally(Context ccx, Scriptable scope, Object constructor, Callable onFinally) {
      return new LambdaFunction(ccx, scope, 1, (cx, ls, thisObj, args) -> {
         Object reason = args.length > 0 ? args[0] : Undefined.INSTANCE;
         LambdaFunction reasonThrower = new LambdaFunction(cx, scope, 0, (vc, vs, vt, va) -> {
            throw new JavaScriptException(vc, reason, null, 0);
         });
         Object result = onFinally.call(cx, ls, Undefined.SCRIPTABLE_INSTANCE, ScriptRuntime.EMPTY_OBJECTS);
         Object promise = resolveInternal(cx, scope, constructor, result);
         Callable thenFunc = ScriptRuntime.getPropFunctionAndThis(cx, scope, promise, "then");
         return thenFunc.call(cx, scope, cx.lastStoredScriptable(), new Object[]{reasonThrower});
      });
   }

   private Object fulfillPromise(Context cx, Scriptable scope, Object value) {
      assert this.state == NativePromise.State.PENDING;

      this.result = value;
      ArrayList<NativePromise.Reaction> reactions = this.fulfillReactions;
      this.fulfillReactions = new ArrayList<>();
      if (!this.rejectReactions.isEmpty()) {
         this.rejectReactions = new ArrayList<>();
      }

      this.state = NativePromise.State.FULFILLED;

      for (NativePromise.Reaction r : reactions) {
         cx.enqueueMicrotask(() -> r.invoke(cx, scope, value));
      }

      return Undefined.INSTANCE;
   }

   private Object rejectPromise(Context cx, Scriptable scope, Object reason) {
      assert this.state == NativePromise.State.PENDING;

      this.result = reason;
      ArrayList<NativePromise.Reaction> reactions = this.rejectReactions;
      this.rejectReactions = new ArrayList<>();
      if (!this.fulfillReactions.isEmpty()) {
         this.fulfillReactions = new ArrayList<>();
      }

      this.state = NativePromise.State.REJECTED;
      cx.getUnhandledPromiseTracker().promiseRejected(this);

      for (NativePromise.Reaction r : reactions) {
         cx.enqueueMicrotask(() -> r.invoke(cx, scope, reason));
      }

      if (!reactions.isEmpty()) {
         this.markHandled(cx);
      }

      return Undefined.INSTANCE;
   }

   private void callThenable(Context cx, Scriptable scope, Object resolution, Callable thenFunc) {
      NativePromise.ResolvingFunctions resolving = new NativePromise.ResolvingFunctions(cx, scope, this);
      Scriptable thisObj = resolution instanceof Scriptable ? (Scriptable)resolution : Undefined.SCRIPTABLE_INSTANCE;

      try {
         thenFunc.call(cx, scope, thisObj, new Object[]{resolving.resolve, resolving.reject});
      } catch (RhinoException var8) {
         resolving.reject.call(cx, scope, Undefined.SCRIPTABLE_INSTANCE, new Object[]{getErrorObject(cx, scope, var8)});
      }
   }

   private static Object getErrorObject(Context cx, Scriptable scope, RhinoException re) {
      if (re instanceof JavaScriptException) {
         return ((JavaScriptException)re).getValue();
      } else {
         TopLevel.NativeErrors constructor = TopLevel.NativeErrors.Error;
         if (re instanceof EcmaError ee) {
            String var5 = ee.getName();
            switch (var5) {
               case "EvalError":
                  constructor = TopLevel.NativeErrors.EvalError;
                  break;
               case "RangeError":
                  constructor = TopLevel.NativeErrors.RangeError;
                  break;
               case "ReferenceError":
                  constructor = TopLevel.NativeErrors.ReferenceError;
                  break;
               case "SyntaxError":
                  constructor = TopLevel.NativeErrors.SyntaxError;
                  break;
               case "TypeError":
                  constructor = TopLevel.NativeErrors.TypeError;
                  break;
               case "URIError":
                  constructor = TopLevel.NativeErrors.URIError;
                  break;
               case "InternalError":
                  constructor = TopLevel.NativeErrors.InternalError;
                  break;
               case "JavaException":
                  constructor = TopLevel.NativeErrors.JavaException;
            }
         }

         return ScriptRuntime.newNativeError(cx, scope, constructor, new Object[]{re.getMessage()});
      }
   }

   private static class Capability {
      Object promise;
      private Object rawResolve = Undefined.INSTANCE;
      Callable resolve;
      private Object rawReject = Undefined.INSTANCE;
      Callable reject;

      Capability(Context topCx, Scriptable topScope, Object pc) {
         if (!(pc instanceof Constructable)) {
            throw ScriptRuntime.typeError0(topCx, "msg.constructor.expected");
         } else {
            LambdaFunction executorFunc = new LambdaFunction(topCx, topScope, 2, (cx, scope, thisObj, args) -> this.executor(cx, args));
            this.promise = ((Constructable)pc).construct(topCx, topScope, new Object[]{executorFunc});
            if (!(this.rawResolve instanceof Callable)) {
               throw ScriptRuntime.typeError0(topCx, "msg.function.expected");
            } else {
               this.resolve = (Callable)this.rawResolve;
               if (!(this.rawReject instanceof Callable)) {
                  throw ScriptRuntime.typeError0(topCx, "msg.function.expected");
               } else {
                  this.reject = (Callable)this.rawReject;
               }
            }
         }
      }

      private Object executor(Context cx, Object[] args) {
         if (Undefined.isUndefined(this.rawResolve) && Undefined.isUndefined(this.rawReject)) {
            if (args.length > 0) {
               this.rawResolve = args[0];
            }

            if (args.length > 1) {
               this.rawReject = args[1];
            }

            return Undefined.INSTANCE;
         } else {
            throw ScriptRuntime.typeError0(cx, "msg.promise.capability.state");
         }
      }
   }

   private static class PromiseAllResolver {
      private static final int MAX_PROMISES = 2097152;
      final ArrayList<Object> values = new ArrayList<>();
      int remainingElements = 1;
      IteratorLikeIterable.Itr iterator;
      Scriptable thisObj;
      NativePromise.Capability capability;
      boolean failFast;

      PromiseAllResolver(IteratorLikeIterable.Itr iter, Scriptable thisObj, NativePromise.Capability cap, boolean failFast) {
         this.iterator = iter;
         this.thisObj = thisObj;
         this.capability = cap;
         this.failFast = failFast;
      }

      Object resolve(Context topCx, Scriptable topScope) {
         int index = 0;
         Callable resolve = ScriptRuntime.getPropFunctionAndThis(topCx, topScope, this.thisObj, "resolve");

         for (Scriptable storedThis = topCx.lastStoredScriptable(); index != 2097152; index++) {
            Object nextVal = Undefined.INSTANCE;
            boolean nextOk = false;

            boolean hasNext;
            try {
               hasNext = this.iterator.hasNext();
               if (hasNext) {
                  nextVal = this.iterator.next();
               }

               nextOk = true;
            } finally {
               if (!nextOk) {
                  this.iterator.setDone(true);
               }
            }

            if (!hasNext) {
               if (--this.remainingElements == 0) {
                  this.finalResolution(topCx, topScope);
               }

               return this.capability.promise;
            }

            this.values.add(Undefined.INSTANCE);
            Object nextPromise = resolve.call(topCx, topScope, storedThis, new Object[]{nextVal});
            NativePromise.PromiseElementResolver eltResolver = new NativePromise.PromiseElementResolver(index);
            LambdaFunction resolveFunc = new LambdaFunction(topCx, topScope, 1, (cx, scope, thisObj, args) -> {
               Object value = args.length > 0 ? args[0] : Undefined.INSTANCE;
               if (!this.failFast) {
                  Scriptable elementResult = cx.newObject(scope);
                  elementResult.put(cx, "status", elementResult, "fulfilled");
                  elementResult.put(cx, "value", elementResult, value);
                  value = elementResult;
               }

               return eltResolver.resolve(cx, scope, value, this);
            });
            Callable rejectFunc = this.capability.reject;
            if (!this.failFast) {
               LambdaFunction resolveSettledRejection = new LambdaFunction(topCx, topScope, 1, (cx, scope, thisObj, args) -> {
                  Scriptable result = cx.newObject(scope);
                  result.put(cx, "status", result, " rejected");
                  result.put(cx, "reason", result, args.length > 0 ? args[0] : Undefined.INSTANCE);
                  return eltResolver.resolve(cx, scope, result, this);
               });
               resolveSettledRejection.setStandardPropertyAttributes(3);
               rejectFunc = resolveSettledRejection;
            }

            this.remainingElements++;
            Callable thenFunc = ScriptRuntime.getPropFunctionAndThis(topCx, topScope, nextPromise, "then");
            thenFunc.call(topCx, topScope, topCx.lastStoredScriptable(), new Object[]{resolveFunc, rejectFunc});
         }

         throw ScriptRuntime.rangeError(topCx, ScriptRuntime.getMessage0("msg.promise.all.toobig"));
      }

      void finalResolution(Context cx, Scriptable scope) {
         Scriptable newArray = cx.newArray(scope, this.values.toArray());
         this.capability.resolve.call(cx, scope, Undefined.SCRIPTABLE_INSTANCE, new Object[]{newArray});
      }
   }

   private static class PromiseAnyRejector {
      private static final int MAX_PROMISES = 2097152;
      final ArrayList<Object> errors = new ArrayList<>();
      int remainingElements = 1;
      IteratorLikeIterable.Itr iterator;
      Scriptable thisObj;
      NativePromise.Capability capability;

      PromiseAnyRejector(IteratorLikeIterable.Itr iter, Scriptable thisObj, NativePromise.Capability cap) {
         this.iterator = iter;
         this.thisObj = thisObj;
         this.capability = cap;
      }

      Object reject(Context topCx, Scriptable topScope) {
         int index = 0;
         Callable resolve = ScriptRuntime.getPropFunctionAndThis(topCx, topScope, this.thisObj, "resolve");

         for (Scriptable storedThis = topCx.lastStoredScriptable(); index != 2097152; index++) {
            Object nextVal = Undefined.INSTANCE;
            boolean nextOk = false;

            boolean hasNext;
            try {
               hasNext = this.iterator.hasNext();
               if (hasNext) {
                  nextVal = this.iterator.next();
               }

               nextOk = true;
            } finally {
               if (!nextOk) {
                  this.iterator.setDone(true);
               }
            }

            if (!hasNext) {
               if (--this.remainingElements == 0) {
                  Scriptable newArray = topCx.newArray(topScope, this.errors.toArray());
                  NativeError error = (NativeError)topCx.newObject(topScope, "AggregateError", new Object[]{newArray});
                  throw new JavaScriptException(topCx, error, null, 0);
               }

               return this.capability.promise;
            }

            this.errors.add(Undefined.INSTANCE);
            Object nextPromise = resolve.call(topCx, topScope, storedThis, new Object[]{nextVal});
            NativePromise.PromiseElementResolver eltResolver = new NativePromise.PromiseElementResolver(index);
            LambdaFunction rejectFunc = new LambdaFunction(topCx, topScope, 1, (cx, scope, thisObj, args) -> {
               Object value = args.length > 0 ? args[0] : Undefined.INSTANCE;
               return eltResolver.reject(cx, scope, value, this);
            });
            this.remainingElements++;
            Callable thenFunc = ScriptRuntime.getPropFunctionAndThis(topCx, topScope, nextPromise, "then");
            thenFunc.call(topCx, topScope, topCx.lastStoredScriptable(), new Object[]{this.capability.resolve, rejectFunc});
         }

         throw ScriptRuntime.rangeError(topCx, ScriptRuntime.getMessage0("msg.promise.any.toobig"));
      }

      void finalRejection(Context cx, Scriptable scope) {
         Scriptable newArray = cx.newArray(scope, this.errors.toArray());
         NativeError error = (NativeError)cx.newObject(scope, "AggregateError", new Object[]{newArray});
         this.capability.reject.call(cx, scope, Undefined.SCRIPTABLE_INSTANCE, new Object[]{error});
      }
   }

   private static class PromiseElementResolver {
      private boolean alreadyCalled = false;
      private final int index;

      PromiseElementResolver(int ix) {
         this.index = ix;
      }

      Object resolve(Context cx, Scriptable scope, Object result, NativePromise.PromiseAllResolver resolver) {
         if (this.alreadyCalled) {
            return Undefined.INSTANCE;
         } else {
            this.alreadyCalled = true;
            resolver.values.set(this.index, result);
            if (--resolver.remainingElements == 0) {
               resolver.finalResolution(cx, scope);
            }

            return Undefined.INSTANCE;
         }
      }

      Object reject(Context cx, Scriptable scope, Object result, NativePromise.PromiseAnyRejector rejector) {
         if (this.alreadyCalled) {
            return Undefined.INSTANCE;
         } else {
            this.alreadyCalled = true;
            rejector.errors.set(this.index, result);
            if (--rejector.remainingElements == 0) {
               rejector.finalRejection(cx, scope);
            }

            return Undefined.INSTANCE;
         }
      }
   }

   private static class Reaction {
      NativePromise.Capability capability;
      NativePromise.ReactionType reaction = NativePromise.ReactionType.REJECT;
      Callable handler;

      Reaction(NativePromise.Capability cap, NativePromise.ReactionType type, Callable handler) {
         this.capability = cap;
         this.reaction = type;
         this.handler = handler;
      }

      void invoke(Context cx, Scriptable scope, Object arg) {
         try {
            Object result = null;
            if (this.handler == null) {
               switch (this.reaction) {
                  case FULFILL:
                     result = arg;
                     break;
                  case REJECT:
                     this.capability.reject.call(cx, scope, Undefined.SCRIPTABLE_INSTANCE, new Object[]{arg});
                     return;
               }
            } else {
               result = this.handler.call(cx, scope, Undefined.SCRIPTABLE_INSTANCE, new Object[]{arg});
            }

            this.capability.resolve.call(cx, scope, Undefined.SCRIPTABLE_INSTANCE, new Object[]{result});
         } catch (RhinoException var5) {
            this.capability.reject.call(cx, scope, Undefined.SCRIPTABLE_INSTANCE, new Object[]{NativePromise.getErrorObject(cx, scope, var5)});
         }
      }
   }

   static enum ReactionType {
      FULFILL,
      REJECT;
   }

   private static class ResolvingFunctions {
      private boolean alreadyResolved = false;
      LambdaFunction resolve;
      LambdaFunction reject;

      ResolvingFunctions(Context ccx, Scriptable topScope, NativePromise promise) {
         this.resolve = new LambdaFunction(
            ccx, topScope, 1, (cx, scope, thisObj, args) -> this.resolve(cx, scope, promise, args.length > 0 ? args[0] : Undefined.INSTANCE)
         );
         this.reject = new LambdaFunction(
            ccx, topScope, 1, (cx, scope, thisObj, args) -> this.reject(cx, scope, promise, args.length > 0 ? args[0] : Undefined.INSTANCE)
         );
      }

      private Object reject(Context cx, Scriptable scope, NativePromise promise, Object reason) {
         if (this.alreadyResolved) {
            return Undefined.INSTANCE;
         } else {
            this.alreadyResolved = true;
            return promise.rejectPromise(cx, scope, reason);
         }
      }

      private Object resolve(Context cx, Scriptable scope, NativePromise promise, Object resolution) {
         if (this.alreadyResolved) {
            return Undefined.INSTANCE;
         } else {
            this.alreadyResolved = true;
            if (resolution == promise) {
               Object err = ScriptRuntime.newNativeError(cx, scope, TopLevel.NativeErrors.TypeError, new Object[]{"No promise self-resolution"});
               return promise.rejectPromise(cx, scope, err);
            } else if (!ScriptRuntime.isObject(resolution)) {
               return promise.fulfillPromise(cx, scope, resolution);
            } else {
               Scriptable sresolution = ScriptableObject.ensureScriptable(resolution, cx);
               Object thenObj = ScriptableObject.getProperty(sresolution, "then", cx);
               if (!(thenObj instanceof Callable)) {
                  return promise.fulfillPromise(cx, scope, resolution);
               } else {
                  cx.enqueueMicrotask(() -> promise.callThenable(cx, scope, resolution, (Callable)thenObj));
                  return Undefined.INSTANCE;
               }
            }
         }
      }
   }

   static enum State {
      PENDING,
      FULFILLED,
      REJECTED;
   }
}
