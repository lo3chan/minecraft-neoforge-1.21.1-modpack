package traben.entity_model_features.models.animation.math.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;
import traben.entity_model_features.EMF;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.models.animation.AnimSetupContext;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.EMFAnimationHandler;
import traben.entity_model_features.models.animation.math.expression_tree.MathValue;
import traben.entity_model_features.models.animation.math.variables.EMFModelOrRenderVariable;
import traben.entity_model_features.models.animation.math.variables.VariableRegistry;
import traben.entity_model_features.models.animation.math.variables.factories.GlobalVariableFactory;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.parts.EMFModelPart;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.utils.ETFLruCache;

public class ASMAnimationHandler extends EMFAnimationHandler {
   private final ASMParser.ASMExecutor compiledAnimationExecutor;
   private final ASMVariableHandler asmVariableHandler;
   private final boolean logsASM = ((EMFConfig)EMF.config().getConfig()).logASM;
   private Supplier<ASMAnimationHandler.AnimVars> varSupplier = null;
   private ASMAnimationHandler.VarConsumer varConsumer = null;
   private final boolean lod = ((EMFConfig)EMF.config().getConfig()).animationLODDistance != 0;
   private final ETFLruCache<UUID, ASMAnimationHandler.AnimVars> lastResultsPerEntity = this.lod ? new ETFLruCache() : null;

   public ASMAnimationHandler(ASMParser.ASMExecutor compiledAnimationExecutor, ASMVariableHandler asmVariableHandler, AnimSetupContext animSetupContext) {
      super(animSetupContext.oldAnimationHandler.modelName, animSetupContext.oldAnimationHandler.lines());
      this.compiledAnimationExecutor = compiledAnimationExecutor;
      this.asmVariableHandler = asmVariableHandler;
      this.varSupplier = this.buildVarSupplier(asmVariableHandler, animSetupContext);
   }

   @Override
   protected void animateInner(ModelPart[] pausedParts) throws Throwable {
      EMFEntityRenderState state = EMFAnimationEntityContext.getEmfState();
      if (this.lod && EMFAnimationEntityContext.isLODSkippingThisFrame(this.modelName) && state != null) {
         ASMAnimationHandler.AnimVars vars = (ASMAnimationHandler.AnimVars)this.lastResultsPerEntity.get(state.uuid());
         if (vars != null) {
            this.varConsumer.accept(vars, false);
            return;
         }
      }

      ASMAnimationHandler.AnimVars vars = this.varSupplier.get();
      if (this.logsASM) {
         asmLog(this.asmVariableHandler, vars, "Start ASM anim with variable state:");
      }

      this.compiledAnimationExecutor.execute(vars.floats(), vars.bools());
      this.varConsumer.accept(vars, true);
      if (this.lod && state != null) {
         this.lastResultsPerEntity.put(state.uuid(), vars);
      }

      if (this.logsASM) {
         asmLog(this.asmVariableHandler, vars, "End ASM anim with variable state:");
      }
   }

   @Override
   public boolean finishAndValidate() {
      List<ASMAnimationHandler.FloatConsumerAsm> floats = this.buildFloatConsumers();
      List<ASMAnimationHandler.BoolConsumerAsm> bools = this.buildBoolConsumers();
      if (bools.isEmpty() && floats.isEmpty()) {
         EMFUtils.logError("ASMAnimationHandler failed ASM variable setting validation for " + this.modelName);
         return false;
      } else {
         this.varConsumer = (animVars, doVars) -> {
            floats.forEach(f -> f.accept(animVars.floats(), doVars));
            bools.forEach(f -> f.accept(animVars.bools(), doVars));
         };
         return true;
      }
   }

   @NotNull
   private List<ASMAnimationHandler.FloatConsumerAsm> buildFloatConsumers() {
      List<ASMAnimationHandler.FloatConsumerAsm> floats = new ArrayList<>();

      for (String varName : this.asmVariableHandler.getFloatVarList()) {
         if (this.asmVariableHandler.isWriteVarName(varName)) {
            EMFAnimationHandler.AnimLineData line = this.getLine(varName);
            int index = line.asmIndex;
            if (index != -1) {
               String key = line.animKey;
               ASMAnimationHandler.FloatConsumerAsm consumer = null;
               if (line.isVar) {
                  if (line.isVarGlobal) {
                     consumer = (array, doVar) -> {
                        if (doVar) {
                           GlobalVariableFactory.setGlobalVariable(key, array[index]);
                        }
                     };
                  } else {
                     consumer = (array, doVar) -> {
                        if (doVar) {
                           EMFAnimationEntityContext.setEntityVariable(key, array[index]);
                        }
                     };
                  }
               } else if (line.applier != null) {
                  EMFModelOrRenderVariable finApply = line.applier;
                  EMFModelPart finPart = line.partToApplyTo;
                  consumer = (array, doVar) -> finApply.setValue(finPart, array[index]);
                  if (finPart != null && !finApply.isBoolean()) {
                     consumer = switch (finApply) {
                        case TX -> (array, b) -> finPart.x = array[index];
                        case TY -> (array, b) -> finPart.y = array[index];
                        case TZ -> (array, b) -> finPart.z = array[index];
                        case RX -> (array, b) -> finPart.xRot = array[index];
                        case RY -> (array, b) -> finPart.yRot = array[index];
                        case RZ -> (array, b) -> finPart.zRot = array[index];
                        case SX -> (array, b) -> finPart.xScale = array[index];
                        case SY -> (array, b) -> finPart.yScale = array[index];
                        case SZ -> (array, b) -> finPart.zScale = array[index];
                        default -> consumer;
                     };
                  }
               }

               if (consumer != null) {
                  floats.add(consumer);
               }
            }
         }
      }

      return floats;
   }

   @NotNull
   private List<ASMAnimationHandler.BoolConsumerAsm> buildBoolConsumers() {
      List<ASMAnimationHandler.BoolConsumerAsm> bools = new ArrayList<>();

      for (String varName : this.asmVariableHandler.getBoolVarList()) {
         if (this.asmVariableHandler.isWriteVarName(varName)) {
            EMFAnimationHandler.AnimLineData line = this.getLine(varName);
            int index = line.asmIndex;
            if (index != -1) {
               String key = line.animKey;
               ASMAnimationHandler.BoolConsumerAsm consumer = null;
               if (line.isVar) {
                  if (line.isVarGlobal) {
                     consumer = (array, doVar) -> {
                        if (doVar) {
                           GlobalVariableFactory.setGlobalVariable(key, array[index] ? 1.0F / 0.0F : -1.0F / 0.0F);
                        }
                     };
                  } else {
                     consumer = (array, doVar) -> {
                        if (doVar) {
                           EMFAnimationEntityContext.setEntityVariable(key, array[index] ? 1.0F / 0.0F : -1.0F / 0.0F);
                        }
                     };
                  }
               } else if (line.applier != null) {
                  EMFModelOrRenderVariable finApply = line.applier;
                  EMFModelPart finPart = line.partToApplyTo;
                  consumer = (array, doVar) -> finApply.setValue(finPart, array[index] ? 1.0F / 0.0F : -1.0F / 0.0F);
                  if (finPart != null && finApply.isBoolean()) {
                     consumer = switch (finApply) {
                        case VISIBLE -> (array, b) -> finPart.visible = array[index];
                        case VISIBLE_BOXES -> (array, b) -> finPart.skipDraw = !array[index];
                        default -> consumer;
                     };
                  }
               }

               if (consumer != null) {
                  bools.add(consumer);
               }
            }
         }
      }

      return bools;
   }

   private Supplier<ASMAnimationHandler.AnimVars> buildVarSupplier(ASMVariableHandler asmVariableHandler, AnimSetupContext context) {
      context.animKey = "buildVarSupplier()";
      List<String> floatList = asmVariableHandler.getFloatVarList();
      MathValue.ResultSupplier[] floatVars = new MathValue.ResultSupplier[floatList.size()];

      for (int i = 0; i < floatList.size(); i++) {
         String varName = floatList.get(i);
         if (!asmVariableHandler.isReadVarName(varName)) {
            floatVars[i] = () -> 0.0F;
         } else {
            floatVars[i] = VariableRegistry.getInstance().getASMVarFloatOrDefault(varName, context);
         }
      }

      List<String> boolList = asmVariableHandler.getBoolVarList();
      BooleanSupplier[] boolVars = new BooleanSupplier[boolList.size()];

      for (int ix = 0; ix < boolList.size(); ix++) {
         String varName = boolList.get(ix);
         if (!asmVariableHandler.isReadVarName(varName)) {
            boolVars[ix] = () -> false;
         } else {
            boolVars[ix] = VariableRegistry.getInstance().getASMVarBoolOrDefault(varName, context);
         }
      }

      int fSize = floatVars.length;
      int bSize = boolVars.length;
      return () -> {
         float[] fArr = new float[fSize];

         for (int ixx = 0; ixx < fSize; ixx++) {
            fArr[ixx] = floatVars[ixx].get();
         }

         boolean[] bArr = new boolean[bSize];

         for (int ixx = 0; ixx < bSize; ixx++) {
            bArr[ixx] = boolVars[ixx].getAsBoolean();
         }

         return new ASMAnimationHandler.AnimVars(fArr, bArr);
      };
   }

   private static void asmLog(ASMVariableHandler asmVariableHandler, ASMAnimationHandler.AnimVars vars, String prefix) {
      StringBuilder str = new StringBuilder(prefix);
      str.append("\nFloats:");

      for (int i = 0; i < vars.floats().length; i++) {
         str.append("\n - [").append(asmVariableHandler.getFloatVarList().get(i)).append("] = ").append(vars.floats()[i]);
      }

      str.append("\nBooleans:");

      for (int i = 0; i < vars.bools().length; i++) {
         str.append("\n - [").append(asmVariableHandler.getBoolVarList().get(i)).append("] = ").append(vars.bools()[i]);
      }

      EMFUtils.log(str.toString());
   }

   private record AnimVars(float[] floats, boolean[] bools) {
   }

   private interface BoolConsumerAsm {
      void accept(boolean[] var1, boolean var2);
   }

   private interface FloatConsumerAsm {
      void accept(float[] var1, boolean var2);
   }

   private interface VarConsumer {
      void accept(ASMAnimationHandler.AnimVars var1, boolean var2);
   }
}
