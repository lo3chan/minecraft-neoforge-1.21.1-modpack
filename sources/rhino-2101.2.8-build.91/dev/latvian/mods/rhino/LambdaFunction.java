package dev.latvian.mods.rhino;

public class LambdaFunction extends BaseFunction {
   protected final transient Callable target;
   private final String name;
   private final int length;

   public LambdaFunction(Context cx, Scriptable scope, String name, int length, Callable target) {
      this(cx, scope, name, length, target, true);
   }

   public LambdaFunction(Context cx, Scriptable scope, String name, int length, Callable target, boolean defaultPrototype) {
      this.target = target;
      this.name = name;
      this.length = length;
      ScriptRuntime.setFunctionProtoAndParent(cx, scope, this);
      if (defaultPrototype) {
         this.setupDefaultPrototype(cx);
      }
   }

   public LambdaFunction(Context cx, Scriptable scope, int length, Callable target) {
      this.target = target;
      this.length = length;
      this.name = "";
      ScriptRuntime.setFunctionProtoAndParent(cx, scope, this);
   }

   @Override
   public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      return this.target.call(cx, scope, thisObj, args);
   }

   @Override
   public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
      throw ScriptRuntime.typeError1(cx, "msg.no.new", this.getFunctionName());
   }

   @Override
   public int getLength() {
      return this.length;
   }

   @Override
   public int getArity() {
      return this.length;
   }

   @Override
   public String getFunctionName() {
      return this.name;
   }
}
