package kroppeb.stareval.function;

import kroppeb.stareval.expression.ConstantExpression;
import net.irisshaders.iris.gl.uniform.UniformType;
import net.irisshaders.iris.parsing.MatrixType;
import net.irisshaders.iris.parsing.VectorType;

public abstract class Type {
   public static final Type.Boolean Boolean = new Type.Boolean();
   public static final Type.Int Int = new Type.Int();
   public static final Type.Float Float = new Type.Float();
   public static final TypedFunction.Parameter BooleanParameter = new TypedFunction.Parameter(Boolean);
   public static final TypedFunction.Parameter IntParameter = new TypedFunction.Parameter(Int);
   public static final TypedFunction.Parameter FloatParameter = new TypedFunction.Parameter(Float);
   public static final Type.Primitive[] AllPrimitives = new Type.Primitive[]{Boolean, Int, Float};

   public static UniformType convert(Type type) {
      if (type == Int || type == Boolean) {
         return UniformType.INT;
      } else if (type == Float) {
         return UniformType.FLOAT;
      } else if (type == VectorType.VEC2) {
         return UniformType.VEC2;
      } else if (type == VectorType.VEC3) {
         return UniformType.VEC3;
      } else if (type == VectorType.VEC4) {
         return UniformType.VEC4;
      } else if (type == VectorType.I_VEC2) {
         return UniformType.VEC2I;
      } else if (type == VectorType.I_VEC3) {
         return UniformType.VEC3I;
      } else if (type == MatrixType.MAT4) {
         return UniformType.MAT4;
      } else {
         throw new IllegalArgumentException("Unsupported custom uniform type: " + type);
      }
   }

   public abstract ConstantExpression createConstant(FunctionReturn var1);

   public abstract Object createArray(int var1);

   public abstract void setValueFromReturn(Object var1, int var2, FunctionReturn var3);

   public abstract void getValueFromArray(Object var1, int var2, FunctionReturn var3);

   @Override
   public abstract String toString();

   public static class Boolean extends Type.Primitive {
      @Override
      public ConstantExpression createConstant(FunctionReturn functionReturn) {
         final boolean value = functionReturn.booleanReturn;
         return new ConstantExpression(this) {
            @Override
            public void evaluateTo(FunctionContext context, FunctionReturn functionReturnx) {
               functionReturnx.booleanReturn = value;
            }
         };
      }

      @Override
      public Object createArray(int length) {
         return new boolean[length];
      }

      @Override
      public void setValueFromReturn(Object array, int index, FunctionReturn value) {
         boolean[] arr = (boolean[])array;
         arr[index] = value.booleanReturn;
      }

      @Override
      public void getValueFromArray(Object array, int index, FunctionReturn value) {
         boolean[] arr = (boolean[])array;
         value.booleanReturn = arr[index];
      }

      @Override
      public String toString() {
         return "bool";
      }
   }

   public static class Float extends Type.Primitive {
      @Override
      public ConstantExpression createConstant(FunctionReturn functionReturn) {
         final float value = functionReturn.floatReturn;
         return new ConstantExpression(this) {
            @Override
            public void evaluateTo(FunctionContext context, FunctionReturn functionReturnx) {
               functionReturnx.floatReturn = value;
            }
         };
      }

      @Override
      public Object createArray(int length) {
         return new float[length];
      }

      @Override
      public void setValueFromReturn(Object array, int index, FunctionReturn value) {
         float[] arr = (float[])array;
         arr[index] = value.floatReturn;
      }

      @Override
      public void getValueFromArray(Object array, int index, FunctionReturn value) {
         float[] arr = (float[])array;
         value.floatReturn = arr[index];
      }

      @Override
      public String toString() {
         return "float";
      }
   }

   public static class Int extends Type.Primitive {
      @Override
      public ConstantExpression createConstant(FunctionReturn functionReturn) {
         final int value = functionReturn.intReturn;
         return new ConstantExpression(this) {
            @Override
            public void evaluateTo(FunctionContext context, FunctionReturn functionReturnx) {
               functionReturnx.intReturn = value;
            }
         };
      }

      @Override
      public Object createArray(int length) {
         return new int[length];
      }

      @Override
      public void setValueFromReturn(Object array, int index, FunctionReturn value) {
         int[] arr = (int[])array;
         arr[index] = value.intReturn;
      }

      @Override
      public void getValueFromArray(Object array, int index, FunctionReturn value) {
         int[] arr = (int[])array;
         value.intReturn = arr[index];
      }

      @Override
      public String toString() {
         return "int";
      }
   }

   public static class ObjectType extends Type {
      @Override
      public ConstantExpression createConstant(FunctionReturn functionReturn) {
         final Object object = functionReturn.objectReturn;
         return new ConstantExpression(this) {
            @Override
            public void evaluateTo(FunctionContext context, FunctionReturn functionReturnx) {
               functionReturnx.objectReturn = object;
            }
         };
      }

      @Override
      public Object createArray(int length) {
         return new Object[length];
      }

      @Override
      public void setValueFromReturn(Object array, int index, FunctionReturn value) {
         Object[] arr = (Object[])array;
         arr[index] = value.objectReturn;
      }

      @Override
      public void getValueFromArray(Object array, int index, FunctionReturn value) {
         Object[] arr = (Object[])array;
         value.objectReturn = arr[index];
      }

      @Override
      public String toString() {
         return "Object";
      }
   }

   public abstract static class Primitive extends Type {
   }
}
