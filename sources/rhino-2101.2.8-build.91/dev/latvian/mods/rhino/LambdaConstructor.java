package dev.latvian.mods.rhino;

import java.util.function.BiConsumer;

public class LambdaConstructor extends LambdaFunction {
   public static final int CONSTRUCTOR_FUNCTION = 1;
   public static final int CONSTRUCTOR_NEW = 2;
   public static final int CONSTRUCTOR_DEFAULT = 3;
   protected final transient Constructable targetConstructor;
   private final int flags;

   public static <T> T convertThisObject(Context cx, Scriptable thisObj, Class<T> targetClass) {
      if (!targetClass.isInstance(thisObj)) {
         throw ScriptRuntime.typeError0(cx, "msg.this.not.instance");
      } else {
         return (T)thisObj;
      }
   }

   public LambdaConstructor(Context cx, Scriptable scope, String name, int length, Constructable target) {
      super(cx, scope, name, length, null);
      this.targetConstructor = target;
      this.flags = 3;
   }

   public LambdaConstructor(Context cx, Scriptable scope, String name, int length, int flags, Constructable target) {
      super(cx, scope, name, length, null);
      this.targetConstructor = target;
      this.flags = flags;
   }

   public LambdaConstructor(Context cx, Scriptable scope, String name, int length, Callable targetCall, Constructable targetConstructor) {
      super(cx, scope, name, length, targetCall);
      this.targetConstructor = targetConstructor;
      this.flags = 3;
   }

   @Override
   public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      if ((this.flags & 1) == 0) {
         throw ScriptRuntime.typeError1(cx, "msg.constructor.no.function", this.getFunctionName());
      } else {
         return this.target == null ? this.targetConstructor.construct(cx, scope, args) : this.target.call(cx, scope, thisObj, args);
      }
   }

   @Override
   public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
      if ((this.flags & 2) == 0) {
         throw ScriptRuntime.typeError1(cx, "msg.no.new", this.getFunctionName());
      } else {
         Scriptable obj = this.targetConstructor.construct(cx, scope, args);
         obj.setPrototype(this.getClassPrototype(cx));
         obj.setParentScope(scope);
         return obj;
      }
   }

   public void setPrototypeScriptable(ScriptableObject proto, Context cx) {
      proto.setParentScope(this.getParentScope());
      this.setPrototypeProperty(proto);
      Scriptable objectProto = getObjectPrototype(this, cx);
      if (proto != objectProto) {
         proto.setPrototype(objectProto);
      }

      proto.defineProperty(cx, "constructor", this, 2);
   }

   public void definePrototypeMethod(Context cx, Scriptable scope, String name, int length, Callable target) {
      LambdaFunction f = new LambdaFunction(cx, scope, name, length, target, false);
      ScriptableObject proto = this.getPrototypeScriptable(cx);
      proto.defineProperty(cx, name, f, 0);
   }

   public void definePrototypeMethod(Context cx, Scriptable scope, String name, int length, Callable target, int attributes, int propertyAttributes) {
      LambdaFunction f = new LambdaFunction(cx, scope, name, length, target, false);
      f.setStandardPropertyAttributes(propertyAttributes);
      ScriptableObject proto = this.getPrototypeScriptable(cx);
      proto.defineProperty(cx, name, f, attributes);
   }

   public void definePrototypeMethod(Context cx, Scriptable scope, SymbolKey name, int length, Callable target, int attributes, int propertyAttributes) {
      LambdaFunction f = new LambdaFunction(cx, scope, "[" + name.getName() + "]", length, target, false);
      f.setStandardPropertyAttributes(propertyAttributes);
      ScriptableObject proto = this.getPrototypeScriptable(cx);
      proto.defineProperty(cx, name, f, attributes);
   }

   public void definePrototypeProperty(Context cx, String name, Object value, int attributes) {
      ScriptableObject proto = this.getPrototypeScriptable(cx);
      proto.defineProperty(cx, name, value, attributes);
   }

   public void definePrototypeProperty(Context cx, Symbol key, Object value, int attributes) {
      ScriptableObject proto = this.getPrototypeScriptable(cx);
      proto.defineProperty(cx, key, value, attributes);
   }

   public void definePrototypeProperty(Context cx, String name, java.util.function.Function<Scriptable, Object> getter, int attributes) {
      ScriptableObject proto = this.getPrototypeScriptable(cx);
      proto.defineProperty(cx, name, getter, (BiConsumer<Scriptable, Object>)null, attributes);
   }

   public void definePrototypeProperty(
      Context cx, String name, java.util.function.Function<Scriptable, Object> getter, BiConsumer<Scriptable, Object> setter, int attributes
   ) {
      ScriptableObject proto = this.getPrototypeScriptable(cx);
      proto.defineProperty(cx, name, getter, setter, attributes);
   }

   public void definePrototypeAlias(Context cx, String name, String alias, int attributes) {
      ScriptableObject proto = this.getPrototypeScriptable(cx);
      Object val = proto.get(cx, name, proto);
      proto.defineProperty(cx, alias, val, attributes);
   }

   public void definePrototypeAlias(Context cx, String name, Symbol alias, int attributes) {
      ScriptableObject proto = this.getPrototypeScriptable(cx);
      Object val = proto.get(cx, name, proto);
      proto.defineProperty(cx, alias, val, attributes);
   }

   public void defineConstructorMethod(Context cx, Scriptable scope, String name, int length, Callable target) {
      LambdaFunction f = new LambdaFunction(cx, scope, name, length, target, false);
      this.defineProperty(cx, name, f, 2);
   }

   public void defineConstructorMethod(Context cx, Scriptable scope, String name, int length, Callable target, int attributes) {
      LambdaFunction f = new LambdaFunction(cx, scope, name, length, target, false);
      this.defineProperty(cx, name, f, attributes);
   }

   public void defineConstructorMethod(Context cx, Scriptable scope, Symbol key, String name, int length, Callable target, int attributes) {
      LambdaFunction f = new LambdaFunction(cx, scope, name, length, target, false);
      this.defineProperty(cx, key, f, attributes);
   }

   public void defineConstructorMethod(Context cx, Scriptable scope, String name, int length, Callable target, int attributes, int propertyAttributes) {
      LambdaFunction f = new LambdaFunction(cx, scope, name, length, target, false);
      f.setStandardPropertyAttributes(propertyAttributes);
      this.defineProperty(cx, name, f, attributes);
   }

   private ScriptableObject getPrototypeScriptable(Context cx) {
      Object prop = this.getPrototypeProperty(cx);
      if (!(prop instanceof ScriptableObject)) {
         throw ScriptRuntime.typeError(cx, "Not properly a lambda constructor");
      } else {
         return (ScriptableObject)prop;
      }
   }
}
