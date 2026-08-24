package traben.entity_model_features.models.animation.math.asm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import org.objectweb.asm.MethodVisitor;
import traben.entity_model_features.models.animation.math.EMFMathException;

public class ASMVariableHandler {
   private final Stack<Boolean> booleanScopeStack = new Stack<>();
   private final List<String> floatVarList = new ArrayList<>();
   private final List<String> boolVarList = new ArrayList<>();
   private final Set<String> readVarNames = new HashSet<>();
   private final Set<String> writeVarNames = new HashSet<>();
   private int localVarIndex = 1;

   public int getLocalVarIndex() {
      return ++this.localVarIndex;
   }

   public void popLocalVarIndex(int was) {
      if (this.localVarIndex != was) {
         throw new IllegalStateException("popLocalVarIndex was not called from correct scope");
      } else {
         this.localVarIndex--;
      }
   }

   public List<String> getFloatVarList() {
      return this.floatVarList;
   }

   public List<String> getBoolVarList() {
      return this.boolVarList;
   }

   public void scope(boolean isBoolean) {
      this.booleanScopeStack.push(isBoolean);
   }

   public void scopeFloat() {
      this.scope(false);
   }

   public void scopeBool() {
      this.scope(true);
   }

   public void scopePop() {
      this.booleanScopeStack.pop();
   }

   public void verifyEndOfParse() throws EMFMathException {
      if (!this.booleanScopeStack.isEmpty()) {
         throw new EMFMathException("ASMVariableHandler verifyEndOfParse issue: type stack is not empty");
      } else if (this.localVarIndex != 1) {
         throw new EMFMathException("ASMVariableHandler verifyEndOfParse issue: local variable index is not reset");
      } else if (this.floatVarList.stream().anyMatch(this.boolVarList::contains)) {
         throw new EMFMathException("ASMVariableHandler verifyEndOfParse issue: has variable names that are both in float and bool lists");
      }
   }

   public boolean isScopeBool() {
      return this.booleanScopeStack.peek();
   }

   public boolean isScopeFloat() {
      return !this.isScopeBool();
   }

   public boolean isReadVarName(String varName) {
      return this.readVarNames.contains(varName);
   }

   public boolean isWriteVarName(String varName) {
      return this.writeVarNames.contains(varName);
   }

   private int getAndAssignVarIndex(String varName, boolean reading) {
      if (reading) {
         this.readVarNames.add(varName);
      }

      if (!reading) {
         this.writeVarNames.add(varName);
      }

      List<String> list = this.booleanScopeStack.peek() ? this.boolVarList : this.floatVarList;
      if (list.contains(varName)) {
         return list.indexOf(varName);
      } else {
         list.add(varName);
         return list.size() - 1;
      }
   }

   public void asmVisitFrameCounter(MethodVisitor mv) {
      this.scopeFloat();
      this.asmVisitVar(mv, "frame_counter");
      this.scopePop();
   }

   public void asmVisitVar(MethodVisitor mv, String varName) {
      int index = this.getAndAssignVarIndex(varName, true);
      boolean isBoolean = this.booleanScopeStack.peek();
      mv.visitVarInsn(25, isBoolean ? 1 : 0);
      mv.visitLdcInsn(index);
      mv.visitInsn(isBoolean ? 51 : 48);
   }

   public int asmStoreVar(MethodVisitor mv, String varName) {
      int index = this.getAndAssignVarIndex(varName, false);
      boolean isBoolean = this.booleanScopeStack.peek();
      mv.visitVarInsn(25, isBoolean ? 1 : 0);
      mv.visitInsn(95);
      mv.visitLdcInsn(index);
      mv.visitInsn(95);
      mv.visitInsn(isBoolean ? 84 : 81);
      return index;
   }

   public void asmInvertBoolean(MethodVisitor mv) {
      mv.visitInsn(4);
      mv.visitInsn(130);
   }

   public void asmNegateFloat(MethodVisitor mv) {
      mv.visitInsn(118);
   }
}
