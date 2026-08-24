package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.util.DefaultValueTypeHint;

public class FieldAndMethods extends NativeJavaMethod {
   public transient CachedFieldInfo fieldInfo;
   public transient Object javaObject;

   FieldAndMethods(Scriptable scope, MemberBox[] methods, CachedFieldInfo fieldInfo, Context cx) {
      super(methods);
      this.fieldInfo = fieldInfo;
      this.setParentScope(scope);
      this.setPrototype(getFunctionPrototype(scope, cx));
   }

   @Override
   public Object getDefaultValue(Context cx, DefaultValueTypeHint hint) {
      if (hint == DefaultValueTypeHint.FUNCTION) {
         return this;
      } else {
         Object rval;
         try {
            rval = this.fieldInfo.get(cx, this.javaObject);
         } catch (Throwable var5) {
            throw Context.reportRuntimeError3(
               "msg.java.internal.private.get", this.fieldInfo.getName(), String.valueOf(this.javaObject), this.fieldInfo.parent.type.getName(), cx
            );
         }

         rval = cx.wrap(this, rval, this.fieldInfo.getType());
         if (rval instanceof Scriptable) {
            rval = ((Scriptable)rval).getDefaultValue(cx, hint);
         }

         return rval;
      }
   }
}
