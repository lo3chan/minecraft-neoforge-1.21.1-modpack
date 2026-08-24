package traben.entity_model_features.models.animation.math.methods.optifine;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Tuple;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.asm.ASMHelper;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.models.animation.math.expression_tree.MathComponent;
import traben.entity_model_features.models.animation.math.expression_tree.MathMethod;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;

public class IfMethod extends MathMethod {
   public IfMethod(List<String> args, boolean isNegative, AnimSetupContext context) throws EMFMathException {
      super(isNegative, context, args);
      if (this.parsedArgs.size() == 3) {
         MathComponent bool = this.parsedArgs.get(0);
         MathComponent tru = this.parsedArgs.get(1);
         MathComponent fals = this.parsedArgs.get(2);
         if (bool.isConstant()) {
            this.setOptimizedAlternativeToThis(MathValue.toBoolean(bool.getResult()) ? tru : fals);
         }

         this.setSupplierAndOptimize(() -> MathValue.toBoolean(bool.getResult()) ? tru.getResult() : fals.getResult(), this.parsedArgs);
      } else {
         List<Tuple<MathComponent, MathComponent>> ifSets = new ArrayList<>();
         MathComponent lastElse = this.parsedArgs.get(this.parsedArgs.size() - 1);

         for (int i = 0; i < this.parsedArgs.size() - 1; i += 2) {
            MathComponent condition = this.parsedArgs.get(i);
            MathComponent result = this.parsedArgs.get(i + 1);
            if (!condition.isConstant()) {
               MathValue.toBoolean(condition.getResult());
               ifSets.add(new Tuple(condition, result));
            } else if (MathValue.toBoolean(condition.getResult())) {
               lastElse = result;
               break;
            }
         }

         if (ifSets.isEmpty()) {
            this.setOptimizedAlternativeToThis(lastElse);
         }

         MathComponent finalElse = lastElse;
         this.setSupplierAndOptimize(() -> {
            for (Tuple<MathComponent, MathComponent> ifSet : ifSets) {
               if (MathValue.toBoolean(((MathComponent)ifSet.getA()).getResult())) {
                  return ((MathComponent)ifSet.getB()).getResult();
               }
            }

            return finalElse.getResult();
         }, this.parsedArgs);
      }
   }

   @Override
   protected boolean hasCorrectArgCount(int argCount) {
      return argCount >= 3 && argCount % 2 == 1;
   }

   @Override
   public void asmVisitInner(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
      if (this.parsedArgs.size() == 3) {
         vars.scopeBool();
         this.parsedArgs.get(0).asmVisit(mv, vars);
         vars.scopePop();
         vars.scopeFloat();
         ASMHelper.asmIf(mv, () -> this.parsedArgs.get(1).asmVisit(mv, vars), () -> this.parsedArgs.get(2).asmVisit(mv, vars));
         vars.scopePop();
      } else {
         vars.scopeBool();
         this.parsedArgs.get(0).asmVisit(mv, vars);
         vars.scopePop();
         vars.scopeFloat();
         ASMHelper.asmIf(mv, () -> this.parsedArgs.get(1).asmVisit(mv, vars), () -> this.deepIf(2, mv, vars));
         vars.scopePop();
      }
   }

   private void deepIf(int startFrom, MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
      if (startFrom == this.parsedArgs.size() - 1) {
         this.parsedArgs.get(startFrom).asmVisit(mv, vars);
      } else {
         vars.scopeBool();
         this.parsedArgs.get(startFrom).asmVisit(mv, vars);
         vars.scopePop();
         ASMHelper.asmIf(mv, () -> this.parsedArgs.get(startFrom + 1).asmVisit(mv, vars), () -> this.deepIf(startFrom + 2, mv, vars));
      }
   }
}
