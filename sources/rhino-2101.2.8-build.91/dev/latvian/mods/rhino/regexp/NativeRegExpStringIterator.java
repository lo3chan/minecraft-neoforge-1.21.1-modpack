package dev.latvian.mods.rhino.regexp;

import dev.latvian.mods.rhino.Callable;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.ES6Iterator;
import dev.latvian.mods.rhino.ScriptRuntime;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.ScriptableObject;
import dev.latvian.mods.rhino.Undefined;

public final class NativeRegExpStringIterator extends ES6Iterator {
   private static final long serialVersionUID = 1L;
   private static final String ITERATOR_TAG = "RegExpStringIterator";
   private Scriptable regexp;
   private String string;
   private boolean global;
   private boolean fullUnicode;
   private boolean nextDone;
   private Object next = null;

   public static void init(ScriptableObject scope, boolean sealed, Context cx) {
      ES6Iterator.init(scope, sealed, new NativeRegExpStringIterator(), "RegExpStringIterator", cx);
   }

   private NativeRegExpStringIterator() {
   }

   public NativeRegExpStringIterator(Context cx, Scriptable scope, Scriptable regexp, String string, boolean global, boolean fullUnicode) {
      super(scope, "RegExpStringIterator", cx);
      this.regexp = regexp;
      this.string = string;
      this.global = global;
      this.fullUnicode = fullUnicode;
      this.nextDone = false;
   }

   @Override
   public String getClassName() {
      return "RegExp String Iterator";
   }

   @Override
   protected boolean isDone(Context cx, Scriptable scope) {
      if (this.nextDone) {
         return true;
      } else {
         this.next = this.regExpExec(cx, scope);
         if (this.next == null) {
            this.next = Undefined.INSTANCE;
            this.nextDone = true;
            return true;
         } else if (!this.global) {
            this.nextDone = true;
            return false;
         } else {
            String matchStr = ScriptRuntime.toString(cx, ScriptRuntime.getObjectIndex(cx, scope, this.next, 0.0));
            if (matchStr.isEmpty()) {
               long thisIndex = ScriptRuntime.toLength(cx, ScriptRuntime.getObjectProp(cx, scope, this.regexp, "lastIndex"));
               long nextIndex = ScriptRuntime.advanceStringIndex(this.string, thisIndex, this.fullUnicode);
               ScriptRuntime.setObjectProp(cx, scope, this.regexp, "lastIndex", nextIndex);
            }

            return false;
         }
      }
   }

   @Override
   protected Object nextValue(Context cx, Scriptable scope) {
      return this.next;
   }

   private Object regExpExec(Context cx, Scriptable scope) {
      Object execMethod = ScriptRuntime.getObjectProp(cx, scope, this.regexp, "exec");
      return execMethod instanceof Callable
         ? ((Callable)execMethod).call(cx, scope, this.regexp, new Object[]{this.string})
         : NativeRegExp.js_exec(cx, scope, this.regexp, new Object[]{this.string});
   }

   @Override
   protected String getTag() {
      return "RegExpStringIterator";
   }
}
