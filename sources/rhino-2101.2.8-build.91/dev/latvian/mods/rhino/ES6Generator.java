package dev.latvian.mods.rhino;

public final class ES6Generator extends ScriptableObject {
   private static final Object GENERATOR_TAG = "Generator";
   private NativeFunction function;
   private Object savedState;
   private String lineSource;
   private int lineNumber;
   private ES6Generator.State state = ES6Generator.State.SUSPENDED_START;
   private Object delegee;

   static ES6Generator init(ScriptableObject scope, boolean sealed, Context cx) {
      ES6Generator prototype = new ES6Generator();
      if (scope != null) {
         prototype.setParentScope(scope);
         prototype.setPrototype(getObjectPrototype(scope, cx));
      }

      prototype.defineProperty(cx, "next", new LambdaFunction(cx, scope, "next", 1, ES6Generator::js_next, false), 2);
      prototype.defineProperty(cx, "return", new LambdaFunction(cx, scope, "return", 1, ES6Generator::js_return, false), 2);
      prototype.defineProperty(cx, "throw", new LambdaFunction(cx, scope, "throw", 1, ES6Generator::js_throw, false), 2);
      prototype.defineProperty(cx, SymbolKey.ITERATOR, new LambdaFunction(cx, scope, "[Symbol.iterator]", 0, ES6Generator::js_iterator, false), 2);
      if (sealed) {
         prototype.sealObject(cx);
      }

      if (scope != null) {
         scope.associateValue(GENERATOR_TAG, prototype);
      }

      return prototype;
   }

   private ES6Generator() {
   }

   public ES6Generator(Scriptable scope, NativeFunction function, Object savedState, Context cx) {
      this.function = function;
      this.savedState = savedState;
      Scriptable top = ScriptableObject.getTopLevelScope(scope);
      this.setParentScope(top);
      ES6Generator prototype = (ES6Generator)ScriptableObject.getTopScopeValue(top, GENERATOR_TAG, cx);
      this.setPrototype(prototype);
   }

   @Override
   public String getClassName() {
      return "Generator";
   }

   private static ES6Generator realThis(Context cx, Scriptable thisObj) {
      return LambdaConstructor.convertThisObject(cx, thisObj, ES6Generator.class);
   }

   private static Object js_next(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      ES6Generator generator = realThis(cx, thisObj);
      Object value = args.length >= 1 ? args[0] : Undefined.INSTANCE;
      return generator.delegee == null ? generator.resumeLocal(cx, scope, value) : generator.resumeDelegee(cx, scope, value);
   }

   private static Object js_return(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      ES6Generator generator = realThis(cx, thisObj);
      Object value = args.length >= 1 ? args[0] : Undefined.INSTANCE;
      return generator.delegee == null ? generator.resumeAbruptLocal(cx, scope, 2, value) : generator.resumeDelegeeReturn(cx, scope, value);
   }

   private static Object js_throw(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      ES6Generator generator = realThis(cx, thisObj);
      Object value = args.length >= 1 ? args[0] : Undefined.INSTANCE;
      return generator.delegee == null ? generator.resumeAbruptLocal(cx, scope, 1, value) : generator.resumeDelegeeThrow(cx, scope, value);
   }

   private static Object js_iterator(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return thisObj;
   }

   private Scriptable resumeDelegee(Context cx, Scriptable scope, Object value) {
      try {
         Object[] nextArgs = Undefined.INSTANCE.equals(value) ? ScriptRuntime.EMPTY_OBJECTS : new Object[]{value};
         Callable nextFn = ScriptRuntime.getPropFunctionAndThis(cx, scope, this.delegee, "next");
         Scriptable nextThis = cx.lastStoredScriptable();
         Object nr = cx.callSync(nextFn, scope, nextThis, nextArgs);
         Scriptable nextResult = ScriptableObject.ensureScriptable(nr, cx);
         if (ScriptRuntime.isIteratorDone(cx, nextResult)) {
            this.delegee = null;
            return this.resumeLocal(cx, scope, ScriptableObject.getProperty(nextResult, "value", cx));
         } else {
            return nextResult;
         }
      } catch (RhinoException var9) {
         this.delegee = null;
         return this.resumeAbruptLocal(cx, scope, 1, var9);
      }
   }

   private Scriptable resumeDelegeeThrow(Context cx, Scriptable scope, Object value) {
      boolean returnCalled = false;

      try {
         Callable throwFn = ScriptRuntime.getPropFunctionAndThis(cx, scope, this.delegee, "throw");
         Scriptable nextThis = cx.lastStoredScriptable();
         Object throwResult = cx.callSync(throwFn, scope, nextThis, new Object[]{value});
         if (ScriptRuntime.isIteratorDone(cx, throwResult)) {
            try {
               returnCalled = true;
               this.callReturnOptionally(cx, scope, Undefined.INSTANCE);
            } finally {
               this.delegee = null;
            }

            return this.resumeLocal(cx, scope, ScriptRuntime.getObjectProp(cx, scope, throwResult, "value"));
         } else {
            return ensureScriptable(throwResult, cx);
         }
      } catch (RhinoException var21) {
         Scriptable throwResultx;
         try {
            if (returnCalled) {
               return this.resumeAbruptLocal(cx, scope, 1, var21);
            }

            try {
               this.callReturnOptionally(cx, scope, Undefined.INSTANCE);
               return this.resumeAbruptLocal(cx, scope, 1, var21);
            } catch (RhinoException var19) {
               throwResultx = this.resumeAbruptLocal(cx, scope, 1, var19);
            }
         } finally {
            this.delegee = null;
         }

         return throwResultx;
      }
   }

   private Scriptable resumeDelegeeReturn(Context cx, Scriptable scope, Object value) {
      try {
         Object retResult = this.callReturnOptionally(cx, scope, value);
         if (retResult != null) {
            if (ScriptRuntime.isIteratorDone(cx, retResult)) {
               this.delegee = null;
               return this.resumeAbruptLocal(cx, scope, 2, ScriptRuntime.getObjectPropNoWarn(cx, scope, retResult, "value"));
            } else {
               return ensureScriptable(retResult, cx);
            }
         } else {
            this.delegee = null;
            return this.resumeAbruptLocal(cx, scope, 2, value);
         }
      } catch (RhinoException var5) {
         this.delegee = null;
         return this.resumeAbruptLocal(cx, scope, 1, var5);
      }
   }

   private Scriptable resumeLocal(Context cx, Scriptable scope, Object value) {
      if (this.state == ES6Generator.State.COMPLETED) {
         return ES6Iterator.makeIteratorResult(cx, scope, Boolean.TRUE);
      } else if (this.state == ES6Generator.State.EXECUTING) {
         throw ScriptRuntime.typeError0(cx, "msg.generator.executing");
      } else {
         Scriptable result = ES6Iterator.makeIteratorResult(cx, scope, Boolean.FALSE);
         this.state = ES6Generator.State.EXECUTING;

         try {
            Object r = this.function.resumeGenerator(cx, scope, 0, this.savedState, value);
            if (!(r instanceof ES6Generator.YieldStarResult ysResult)) {
               ScriptableObject.putProperty(result, "value", r, cx);
               return result;
            } else {
               this.state = ES6Generator.State.SUSPENDED_YIELD;

               try {
                  this.delegee = ScriptRuntime.callIterator(ysResult.getResult(), cx, scope);
               } catch (RhinoException var24) {
                  return this.resumeAbruptLocal(cx, scope, 1, var24);
               }

               Scriptable delResult;
               try {
                  delResult = this.resumeDelegee(cx, scope, Undefined.INSTANCE);
               } finally {
                  this.state = ES6Generator.State.EXECUTING;
               }

               if (ScriptRuntime.isIteratorDone(cx, delResult)) {
                  this.state = ES6Generator.State.COMPLETED;
               }

               return delResult;
            }
         } catch (GeneratorState.GeneratorClosedException var25) {
            this.state = ES6Generator.State.COMPLETED;
            return result;
         } catch (JavaScriptException var26) {
            this.state = ES6Generator.State.COMPLETED;
            if (var26.getValue() instanceof NativeIterator.StopIteration) {
               ScriptableObject.putProperty(result, "value", ((NativeIterator.StopIteration)var26.getValue()).getValue(), cx);
               return result;
            } else {
               this.lineNumber = var26.lineNumber();
               this.lineSource = var26.lineSource();
               if (var26.getValue() instanceof RhinoException) {
                  throw (RhinoException)var26.getValue();
               } else {
                  throw var26;
               }
            }
         } catch (RhinoException var27) {
            this.lineNumber = var27.lineNumber();
            this.lineSource = var27.lineSource();
            throw var27;
         } finally {
            if (this.state == ES6Generator.State.COMPLETED) {
               ScriptableObject.putProperty(result, "done", Boolean.TRUE, cx);
            } else {
               this.state = ES6Generator.State.SUSPENDED_YIELD;
            }
         }
      }
   }

   private Scriptable resumeAbruptLocal(Context cx, Scriptable scope, int op, Object value) {
      if (this.state == ES6Generator.State.EXECUTING) {
         throw ScriptRuntime.typeError0(cx, "msg.generator.executing");
      } else {
         if (this.state == ES6Generator.State.SUSPENDED_START) {
            this.state = ES6Generator.State.COMPLETED;
         }

         Scriptable result = ES6Iterator.makeIteratorResult(cx, scope, Boolean.FALSE);
         if (this.state == ES6Generator.State.COMPLETED) {
            if (op == 1) {
               throw new JavaScriptException(cx, value, this.lineSource, this.lineNumber);
            } else {
               ScriptableObject.putProperty(result, "value", value, cx);
               ScriptableObject.putProperty(result, "done", Boolean.TRUE, cx);
               return result;
            }
         } else {
            this.state = ES6Generator.State.EXECUTING;
            Object throwValue = value;
            if (op == 2) {
               if (!(value instanceof GeneratorState.GeneratorClosedException)) {
                  throwValue = new GeneratorState.GeneratorClosedException(value);
               }
            } else if (value instanceof JavaScriptException) {
               throwValue = ((JavaScriptException)value).getValue();
            } else if (value instanceof RhinoException) {
               throwValue = ScriptRuntime.wrapException(cx, scope, (Throwable)value);
            }

            try {
               Object r = this.function.resumeGenerator(cx, scope, op, this.savedState, throwValue);
               ScriptableObject.putProperty(result, "value", r, cx);
               this.state = ES6Generator.State.SUSPENDED_YIELD;
            } catch (GeneratorState.GeneratorClosedException var13) {
               this.state = ES6Generator.State.COMPLETED;
               ScriptableObject.putProperty(result, "value", var13.getValue(), cx);
            } catch (JavaScriptException var14) {
               this.state = ES6Generator.State.COMPLETED;
               if (!(var14.getValue() instanceof NativeIterator.StopIteration)) {
                  this.lineNumber = var14.lineNumber();
                  this.lineSource = var14.lineSource();
                  if (var14.getValue() instanceof RhinoException) {
                     throw (RhinoException)var14.getValue();
                  }

                  throw var14;
               }

               ScriptableObject.putProperty(result, "value", ((NativeIterator.StopIteration)var14.getValue()).getValue(), cx);
            } catch (RhinoException var15) {
               this.state = ES6Generator.State.COMPLETED;
               this.lineNumber = var15.lineNumber();
               this.lineSource = var15.lineSource();
               throw var15;
            } finally {
               if (this.state == ES6Generator.State.COMPLETED) {
                  this.delegee = null;
                  ScriptableObject.putProperty(result, "done", Boolean.TRUE, cx);
               }
            }

            return result;
         }
      }
   }

   private Object callReturnOptionally(Context cx, Scriptable scope, Object value) {
      Object[] retArgs = Undefined.INSTANCE.equals(value) ? ScriptRuntime.EMPTY_OBJECTS : new Object[]{value};
      Object retFnObj = ScriptRuntime.getObjectPropNoWarn(cx, scope, this.delegee, "return");
      if (!Undefined.INSTANCE.equals(retFnObj)) {
         if (!(retFnObj instanceof Callable)) {
            throw ScriptRuntime.typeError2(cx, "msg.isnt.function", "return", ScriptRuntime.typeof(cx, retFnObj));
         } else {
            return cx.callSync((Callable)retFnObj, scope, ensureScriptable(this.delegee, cx), retArgs);
         }
      } else {
         return null;
      }
   }

   static enum State {
      SUSPENDED_START,
      SUSPENDED_YIELD,
      EXECUTING,
      COMPLETED;
   }

   public static final class YieldStarResult {
      private final Object result;

      public YieldStarResult(Object result) {
         this.result = result;
      }

      Object getResult() {
         return this.result;
      }
   }
}
