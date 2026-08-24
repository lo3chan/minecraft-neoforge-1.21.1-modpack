package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.List;

public class FunctionObject extends BaseFunction {
   public static final int JAVA_UNSUPPORTED_TYPE = 0;
   public static final int JAVA_STRING_TYPE = 1;
   public static final int JAVA_INT_TYPE = 2;
   public static final int JAVA_BOOLEAN_TYPE = 3;
   public static final int JAVA_DOUBLE_TYPE = 4;
   public static final int JAVA_SCRIPTABLE_TYPE = 5;
   public static final int JAVA_OBJECT_TYPE = 6;
   private static final short VARARGS_METHOD = -1;
   private static final short VARARGS_CTOR = -2;
   private final String functionName;
   private final int parmsLength;
   private final boolean isStatic;
   MemberBox member;
   private transient byte[] typeTags;
   private transient boolean hasVoidReturn;
   private transient int returnTypeTag;

   public static int getTypeTag(Class<?> type) {
      if (type == ScriptRuntime.StringClass) {
         return 1;
      } else if (type == ScriptRuntime.IntegerClass || type == int.class) {
         return 2;
      } else if (type == ScriptRuntime.BooleanClass || type == boolean.class) {
         return 3;
      } else if (type == ScriptRuntime.DoubleClass || type == double.class) {
         return 4;
      } else if (ScriptRuntime.ScriptableClass.isAssignableFrom(type)) {
         return 5;
      } else {
         return type == ScriptRuntime.ObjectClass ? 6 : 0;
      }
   }

   public static Object convertArg(Context cx, Scriptable scope, Object arg, int typeTag) {
      switch (typeTag) {
         case 1:
            if (arg instanceof String) {
               return arg;
            }

            return ScriptRuntime.toString(cx, arg);
         case 2:
            if (arg instanceof Integer) {
               return arg;
            }

            return ScriptRuntime.toInt32(cx, arg);
         case 3:
            if (arg instanceof Boolean) {
               return arg;
            }

            return ScriptRuntime.toBoolean(cx, arg) ? Boolean.TRUE : Boolean.FALSE;
         case 4:
            if (arg instanceof Double) {
               return arg;
            }

            return ScriptRuntime.toNumber(cx, arg);
         case 5:
            return ScriptRuntime.toObjectOrNull(cx, arg, scope);
         case 6:
            return arg;
         default:
            throw new IllegalArgumentException();
      }
   }

   static CachedMethodInfo findSingleMethod(List<CachedMethodInfo> methods, String name, Context cx) {
      CachedMethodInfo found = null;
      int i = 0;

      for (int N = methods.size(); i != N; i++) {
         CachedMethodInfo method = methods.get(i);
         if (method != null && name.equals(method.getName())) {
            if (found != null) {
               throw Context.reportRuntimeError2("msg.no.overload", name, method.getDeclaringClass().getTypeInfo(), cx);
            }

            found = method;
         }
      }

      return found;
   }

   static List<CachedMethodInfo> getMethodList(Context cx, Class<?> clazz) {
      return cx.getCachedClassStorage(false).get(clazz).getDeclaredMethods();
   }

   public FunctionObject(String name, CachedExecutableInfo methodOrConstructor, Scriptable scope, Context cx) {
      this.member = new MemberBox(methodOrConstructor);
      this.isStatic = methodOrConstructor.isStatic;
      String methodName = this.member.getName();
      this.functionName = name;
      List<Class<?>> types = this.member.parameters().types();
      int arity = types.size();
      if (arity == 4 && (types.get(1).isArray() || types.get(2).isArray())) {
         if (types.get(1).isArray()) {
            if (!this.isStatic
               || types.getFirst() != ScriptRuntime.ContextClass
               || types.get(1).getComponentType() != ScriptRuntime.ObjectClass
               || types.get(2) != ScriptRuntime.FunctionClass
               || types.get(3) != boolean.class) {
               throw Context.reportRuntimeError1("msg.varargs.ctor", methodName, cx);
            }

            this.parmsLength = -2;
         } else {
            if (!this.isStatic
               || types.getFirst() != ScriptRuntime.ContextClass
               || types.get(1) != ScriptRuntime.ScriptableClass
               || types.get(2).getComponentType() != ScriptRuntime.ObjectClass
               || types.get(3) != ScriptRuntime.FunctionClass) {
               throw Context.reportRuntimeError1("msg.varargs.fun", methodName, cx);
            }

            this.parmsLength = -1;
         }
      } else {
         this.parmsLength = arity;
         if (arity > 0) {
            this.typeTags = new byte[arity];

            for (int i = 0; i != arity; i++) {
               int tag = getTypeTag(types.get(i));
               if (tag == 0) {
                  throw Context.reportRuntimeError2("msg.bad.parms", types.get(i).getName(), methodName, cx);
               }

               this.typeTags[i] = (byte)tag;
            }
         }
      }

      if (this.member.isMethod()) {
         TypeInfo returnType = this.member.getReturnType();
         if (returnType.isVoid()) {
            this.hasVoidReturn = true;
         } else {
            this.returnTypeTag = returnType.getTypeTag();
         }
      } else {
         Class<?> ctorType = this.member.executableInfo.getDeclaringClass().type;
         if (!ScriptRuntime.ScriptableClass.isAssignableFrom(ctorType)) {
            throw Context.reportRuntimeError1("msg.bad.ctor.return", ctorType.getName(), cx);
         }
      }

      ScriptRuntime.setFunctionProtoAndParent(cx, scope, this);
   }

   @Override
   public int getArity() {
      return this.parmsLength < 0 ? 1 : this.parmsLength;
   }

   @Override
   public int getLength() {
      return this.getArity();
   }

   @Override
   public String getFunctionName() {
      return this.functionName == null ? "" : this.functionName;
   }

   public void addAsConstructor(Scriptable scope, Scriptable prototype, Context cx) {
      this.initAsConstructor(scope, prototype, cx);
      defineProperty(scope, prototype.getClassName(), this, 2, cx);
   }

   void initAsConstructor(Scriptable scope, Scriptable prototype, Context cx) {
      ScriptRuntime.setFunctionProtoAndParent(cx, scope, this);
      this.setImmunePrototypeProperty(prototype);
      prototype.setParentScope(this);
      defineProperty(prototype, "constructor", this, 7, cx);
      this.setParentScope(scope);
   }

   @Override
   public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
      boolean checkMethodResult = false;
      int argsLength = args.length;
      Object result;
      if (this.parmsLength < 0) {
         if (this.parmsLength == -1) {
            Object[] invokeArgs = new Object[]{cx, thisObj, args, this};
            result = this.member.invoke(null, invokeArgs, cx, scope);
            checkMethodResult = true;
         } else {
            boolean inNewExpr = thisObj == null;
            Boolean b = inNewExpr ? Boolean.TRUE : Boolean.FALSE;
            Object[] invokeArgs = new Object[]{cx, args, this, b};
            result = this.member.isCtor() ? this.member.newInstance(invokeArgs, cx, scope) : this.member.invoke(null, invokeArgs, cx, scope);
         }
      } else {
         if (!this.isStatic) {
            Class<?> clazz = this.member.executableInfo.getDeclaringClass().type;
            if (!clazz.isInstance(thisObj)) {
               boolean compatible = false;
               if (thisObj == scope) {
                  Scriptable parentScope = this.getParentScope();
                  if (scope != parentScope) {
                     compatible = clazz.isInstance(parentScope);
                     if (compatible) {
                        thisObj = parentScope;
                     }
                  }
               }

               if (!compatible) {
                  throw ScriptRuntime.typeError1(cx, "msg.incompat.call", this.functionName);
               }
            }
         }

         Object[] invokeArgs;
         if (this.parmsLength == argsLength) {
            invokeArgs = args;

            for (int i = 0; i != this.parmsLength; i++) {
               Object arg = args[i];
               Object converted = convertArg(cx, scope, arg, this.typeTags[i]);
               if (arg != converted) {
                  if (invokeArgs == args) {
                     invokeArgs = (Object[])args.clone();
                  }

                  invokeArgs[i] = converted;
               }
            }
         } else if (this.parmsLength == 0) {
            invokeArgs = ScriptRuntime.EMPTY_OBJECTS;
         } else {
            invokeArgs = new Object[this.parmsLength];

            for (int ix = 0; ix != this.parmsLength; ix++) {
               Object arg = ix < argsLength ? args[ix] : Undefined.INSTANCE;
               invokeArgs[ix] = convertArg(cx, scope, arg, this.typeTags[ix]);
            }
         }

         if (this.member.isMethod()) {
            result = this.member.invoke(thisObj, invokeArgs, cx, scope);
            checkMethodResult = true;
         } else {
            result = this.member.newInstance(invokeArgs, cx, scope);
         }
      }

      if (checkMethodResult) {
         if (this.hasVoidReturn) {
            result = Undefined.INSTANCE;
         } else if (this.returnTypeTag == 0) {
            result = cx.wrap(scope, result, this.member.getReturnType());
         }
      }

      return result;
   }

   @Override
   public Scriptable createObject(Context cx, Scriptable scope) {
      if (!this.member.isCtor() && this.parmsLength != -2) {
         Scriptable result;
         try {
            result = (Scriptable)this.member.executableInfo.invoke(cx, scope, null, ScriptRuntime.EMPTY_OBJECTS);
         } catch (Throwable var5) {
            throw Context.throwAsScriptRuntimeEx(var5, cx);
         }

         result.setPrototype(this.getClassPrototype(cx));
         result.setParentScope(this.getParentScope());
         return result;
      } else {
         return null;
      }
   }

   boolean isVarArgsMethod() {
      return this.parmsLength == -1;
   }

   boolean isVarArgsConstructor() {
      return this.parmsLength == -2;
   }
}
