package traben.entity_model_features.models.animation.math.expression_tree;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.EMFManager;
import traben.entity_model_features.models.animation.math.EMFMathException;
import traben.entity_model_features.models.animation.math.asm.ASMVariableHandler;
import traben.entity_model_features.utils.EMFUtils;

public enum MathOperator implements MathComponent {
   ADD {
      @Override
      public float execute(MathComponent first, MathComponent second) {
         return first.getResult() + second.getResult();
      }

      @Override
      public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
         mv.visitInsn(98);
      }
   },
   SUBTRACT {
      @Override
      public float execute(MathComponent first, MathComponent second) {
         return first.getResult() - second.getResult();
      }

      @Override
      public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
         mv.visitInsn(102);
      }
   },
   MULTIPLY {
      @Override
      public float execute(MathComponent first, MathComponent second) {
         return first.getResult() * second.getResult();
      }

      @Override
      public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
         mv.visitInsn(106);
      }
   },
   DIVIDE {
      @Override
      public float execute(MathComponent first, MathComponent second) {
         float sec = second.getResult();
         return sec == 0.0F && !second.isConstant() && EMFManager.getInstance().isAnimationValidationPhase ? first.getResult() : first.getResult() / sec;
      }

      @Override
      public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
         mv.visitInsn(110);
      }
   },
   DIVISION_REMAINDER {
      @Override
      public float execute(MathComponent first, MathComponent second) {
         float sec = second.getResult();
         return sec == 0.0F && !second.isConstant() && EMFManager.getInstance().isAnimationValidationPhase ? first.getResult() : first.getResult() % sec;
      }

      @Override
      public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
         mv.visitInsn(114);
      }
   },
   COMMA,
   OPEN_BRACKET,
   CLOSED_BRACKET,
   NONE,
   AND {
      @Override
      public float execute(MathComponent first, MathComponent second) {
         return MathValue.fromBoolean(MathValue.toBoolean(first.getResult()) && MathValue.toBoolean(second.getResult()));
      }

      @Override
      public boolean isScopeBool() {
         return true;
      }

      @Override
      public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
         mv.visitInsn(126);
      }
   },
   OR {
      @Override
      public float execute(MathComponent first, MathComponent second) {
         return MathValue.fromBoolean(MathValue.toBoolean(first.getResult()) || MathValue.toBoolean(second.getResult()));
      }

      @Override
      public boolean isScopeBool() {
         return true;
      }

      @Override
      public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
         mv.visitInsn(128);
      }
   },
   LARGER_THAN {
      @Override
      public float execute(MathComponent first, MathComponent second) {
         return MathValue.fromBoolean(first.getResult() > second.getResult());
      }

      @Override
      public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
         mv.visitInsn(150);
         asmCompare(mv, 157);
      }
   },
   SMALLER_THAN {
      @Override
      public float execute(MathComponent first, MathComponent second) {
         return MathValue.fromBoolean(first.getResult() < second.getResult());
      }

      @Override
      public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
         mv.visitInsn(150);
         asmCompare(mv, 155);
      }
   },
   LARGER_THAN_OR_EQUALS {
      @Override
      public float execute(MathComponent first, MathComponent second) {
         return MathValue.fromBoolean(first.getResult() >= second.getResult());
      }

      @Override
      public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
         mv.visitInsn(150);
         asmCompare(mv, 156);
      }
   },
   SMALLER_THAN_OR_EQUALS {
      @Override
      public float execute(MathComponent first, MathComponent second) {
         return MathValue.fromBoolean(first.getResult() <= second.getResult());
      }

      @Override
      public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
         mv.visitInsn(150);
         asmCompare(mv, 158);
      }
   },
   EQUALS {
      @Override
      public float execute(MathComponent first, MathComponent second) {
         return MathValue.fromBoolean(first.getResult() == second.getResult());
      }

      @Override
      public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
         if (varNames.isScopeFloat()) {
            mv.visitInsn(149);
            asmCompare(mv, 153);
         } else {
            asmCompare(mv, 159);
         }
      }
   },
   NOT_EQUALS {
      @Override
      public float execute(MathComponent first, MathComponent second) {
         return MathValue.fromBoolean(first.getResult() != second.getResult());
      }

      @Override
      public void asmVisit(MethodVisitor mv, ASMVariableHandler varNames) {
         if (varNames.isScopeFloat()) {
            mv.visitInsn(149);
            asmCompare(mv, 154);
         } else {
            asmCompare(mv, 160);
         }
      }
   },
   BOOLEAN_CHAR;

   public static MathOperator getAction(char ch) {
      return switch (ch) {
         case '!', '&', '<', '=', '>', '|' -> BOOLEAN_CHAR;
         case '%' -> DIVISION_REMAINDER;
         case '(' -> OPEN_BRACKET;
         case ')' -> CLOSED_BRACKET;
         case '*' -> MULTIPLY;
         case '+' -> ADD;
         case ',' -> COMMA;
         case '-' -> SUBTRACT;
         case '/' -> DIVIDE;
         default -> NONE;
      };
   }

   public float execute(MathComponent first, MathComponent second) {
      EMFUtils.logError("math action execute() incorrectly called [" + this + "].");
      return 0.0F / 0.0F;
   }

   @Override
   public boolean isConstant() {
      return true;
   }

   @Override
   public float getResult() {
      EMFUtils.logError("math action incorrectly called [" + this + "].");
      return 0.0F / 0.0F;
   }

   public boolean isScopeBool() {
      return false;
   }

   public boolean isEqualsType() {
      return this == EQUALS || this == NOT_EQUALS;
   }

   @Override
   public void asmVisit(MethodVisitor mv, ASMVariableHandler vars) throws EMFMathException {
      throw new UnsupportedOperationException(this + " operator shouldn't have called this.");
   }

   public static void asmCompare(MethodVisitor mv, int opCode) {
      Label t = new Label();
      Label end = new Label();
      mv.visitJumpInsn(opCode, t);
      mv.visitInsn(3);
      mv.visitJumpInsn(167, end);
      mv.visitLabel(t);
      mv.visitInsn(4);
      mv.visitLabel(end);
   }
}
