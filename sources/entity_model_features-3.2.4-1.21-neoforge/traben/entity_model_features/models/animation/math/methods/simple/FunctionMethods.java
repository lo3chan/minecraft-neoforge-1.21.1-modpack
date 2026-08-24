package traben.entity_model_features.models.animation.math.methods.simple;

import java.util.List;
import java.util.function.Function;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.EMFAnimation;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.animation.math.expression_tree.MathMethod;
import traben.entity_model_features.models.animation.math.methods.MethodRegistry;

@Deprecated(
   forRemoval = true
)
public class FunctionMethods extends MathMethod {
   @Deprecated(
      forRemoval = true
   )
   protected FunctionMethods(List<String> args, boolean isNegative, EMFAnimation calculationInstance, Function<Float, Float> function) throws EMFMathException {
      super(isNegative, null, args);
   }

   @Deprecated(
      forRemoval = true
   )
   public static MethodRegistry.MethodFactory makeFactory(String methodName, Function<Float, Float> function) throws EMFMathException {
      throw new EMFMathException("Failed to create " + methodName + "() method, because: FunctionMethods is deprecated");
   }

   @Override
   public void asmVisitInner(MethodVisitor mv, ASMVariableHandler varNames) throws EMFMathException {
      throw new UnsupportedOperationException("Deprecated");
   }

   @Override
   protected boolean hasCorrectArgCount(int argCount) {
      return argCount == 1;
   }
}
