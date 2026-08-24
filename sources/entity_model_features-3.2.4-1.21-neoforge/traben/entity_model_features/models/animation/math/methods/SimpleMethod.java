package traben.entity_model_features.models.animation.math.methods;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.asm.ASMHelper;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.animation.math.asm.ASMVisitable;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.MathMethod;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;

public class SimpleMethod extends MathMethod {
   final Method staticMethod;
   @Nullable
   final ASMVisitable asmCompiler;

   protected SimpleMethod(List<String> args, boolean isNegative, AnimSetupContext context, Method staticMethod, @Nullable ASMVisitable asmCompiler) throws EMFMathException {
      super(isNegative, context, args, getParameterCount(staticMethod));
      this.staticMethod = staticMethod;
      boolean isBoolean = staticMethod.getReturnType().equals(boolean.class) || staticMethod.getReturnType().equals(Boolean.class);
      this.asmCompiler = asmCompiler;
      Object[] computedArgs = new Object[getParameterCount(staticMethod)];

      for (int i = 0; i < this.parsedArgs.size(); i++) {
         MathComponent arg = this.parsedArgs.get(i);
         if (arg == null) {
            computedArgs[i] = this.rawArgs.get(i);
         }
      }

      this.setSupplierAndOptimize(() -> {
         try {
            for (int ix = 0; ix < this.parsedArgs.size(); ix++) {
               MathComponent argx = this.parsedArgs.get(ix);
               if (argx != null) {
                  computedArgs[ix] = argx.getResult();
               }
            }

            return isBoolean ? MathValue.fromBoolean((Boolean)staticMethod.invoke(null, computedArgs)) : (Float)staticMethod.invoke(null, computedArgs);
         } catch (Exception var6x) {
            var6x.printStackTrace();
            return 0.0F / 0.0F;
         }
      }, this.parsedArgs);
   }

   public static MethodRegistry.MethodFactory makeFactory(String methodName, Method staticMethod, @Nullable ASMVisitable asmCompiler) {
      return (args, isNegative, calculationInstance) -> {
         try {
            if (!Modifier.isStatic(staticMethod.getModifiers())) {
               throw new EMFMathException(staticMethod.getName() + " is not static");
            } else if (!Modifier.isPublic(staticMethod.getModifiers())) {
               throw new EMFMathException(staticMethod.getName() + " is not public");
            } else if (staticMethod.getReturnType() != boolean.class && staticMethod.getReturnType() != float.class) {
               throw new EMFMathException(staticMethod.getName() + " does not return either a float or boolean primitive type");
            } else if (Arrays.stream(staticMethod.getParameterTypes()).anyMatch(it -> it != boolean.class && it != float.class && it != String.class)) {
               throw new EMFMathException(staticMethod.getName() + " has a parameter that is not a float or boolean primitive type, or a String Object");
            } else {
               return new SimpleMethod(args, isNegative, calculationInstance, staticMethod, asmCompiler);
            }
         } catch (Exception var7) {
            throw new EMFMathException("Failed to create " + methodName + "() method, because: " + var7);
         }
      };
   }

   @Override
   public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
      Class<?>[] params = this.staticMethod.getParameterTypes();

      for (int i = 0; i < this.parsedArgs.size(); i++) {
         MathComponent arg = this.parsedArgs.get(i);
         if (arg == null) {
            mv.visitLdcInsn(this.rawArgs.get(i));
         } else {
            if (params[i] == boolean.class) {
               vars.scopeBool();
            } else {
               vars.scopeFloat();
            }

            arg.asmVisit(mv, vars);
            vars.scopePop();
         }
      }

      if (this.asmCompiler != null) {
         this.asmCompiler.asmVisit(mv, vars);
      } else {
         ASMHelper.visitStaticFunctionASM(mv, this.staticMethod);
      }
   }

   private static int getParameterCount(Method method) {
      return method.getParameterCount();
   }

   @Override
   protected boolean hasCorrectArgCount(int argCount) {
      return true;
   }
}
