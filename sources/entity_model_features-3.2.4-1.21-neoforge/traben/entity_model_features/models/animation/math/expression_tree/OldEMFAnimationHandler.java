package traben.entity_model_features.models.animation.math.expression_tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.client.model.geom.ModelPart;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;
import traben.entity_model_features.models.animation.EMFAnimationHandler;
import traben.entity_model_features.models.animation.math.variables.EMFModelOrRenderVariable;
import traben.entity_model_features.models.animation.math.variables.factories.GlobalVariableFactory;
import traben.entity_model_features.models.animation.state.EMFEntityRenderState;
import traben.entity_model_features.models.parts.EMFModelPart;
import traben.entity_model_features.utils.EMFUtils;
import traben.entity_texture_features.utils.ETFLruCache;

public class OldEMFAnimationHandler extends EMFAnimationHandler {
   public final LinkedHashMap<EMFAnimationHandler.AnimLineData, MathComponent> oldAnimLines = new LinkedHashMap<>();
   public final HashMap<String, Float> defaults = new HashMap<>();
   public final HashMap<String, Consumer<Float>> resultConsumers = new HashMap<>();
   public final ETFLruCache<UUID, ConcurrentHashMap<String, Float>> lastResultsPerEntity = new ETFLruCache();

   public OldEMFAnimationHandler(String modelName) {
      super(modelName, new ArrayList<>());
   }

   public void addParsedLine(EMFAnimationHandler.AnimLineData line, MathComponent emfCalculator) {
      this.oldAnimLines.put(line, emfCalculator);
      this.defaults.put(line.animKey, line.isBoolean ? -1.0F / 0.0F : 0.0F);
   }

   public MathValue.ResultSupplier getLastResultGetter(String variableKey) {
      return () -> this.lastResult(variableKey);
   }

   private Map<String, Float> prevResultsOfEntity() {
      EMFEntityRenderState state = EMFAnimationEntityContext.getEmfState();
      return state == null ? null : (Map)this.lastResultsPerEntity.computeIfAbsent(state.uuid(), u -> new ConcurrentHashMap());
   }

   private Float lastResult(String variableKey) {
      Map<String, Float> map = this.prevResultsOfEntity();
      if (map == null) {
         return this.defaults.get(variableKey);
      } else {
         Float last = map.get(variableKey);
         return last != null ? last : this.defaults.get(variableKey);
      }
   }

   @Override
   public boolean finishAndValidate() {
      if (this.oldAnimLines.isEmpty()) {
         EMFUtils.logError("OldEMFAnimationHandler was empty for " + this.modelName);
         return false;
      } else if (this.oldAnimLines.size() != this.lines().size()) {
         EMFUtils.logError("OldEMFAnimationHandler was not correctly sized " + this.modelName);
         return false;
      } else {
         for (EMFAnimationHandler.AnimLineData line : this.lines()) {
            Consumer<Float> consumer = f -> {};
            String key = line.animKey;
            if (line.isVar) {
               if (line.isVarGlobal) {
                  if (line.isBoolean) {
                     consumer = value -> GlobalVariableFactory.setGlobalVariable(key, MathValue.isBoolean(value) ? value : -1.0F / 0.0F);
                  } else {
                     consumer = value -> GlobalVariableFactory.setGlobalVariable(key, MathValue.isBoolean(value) ? 0.0F : value);
                  }
               } else if (line.isBoolean) {
                  consumer = value -> EMFAnimationEntityContext.setEntityVariable(key, MathValue.isBoolean(value) ? value : -1.0F / 0.0F);
               } else {
                  consumer = value -> EMFAnimationEntityContext.setEntityVariable(key, MathValue.isBoolean(value) ? 0.0F : value);
               }
            } else if (line.applier != null) {
               EMFModelOrRenderVariable finApply = line.applier;
               EMFModelPart finPart = line.partToApplyTo;
               consumer = f -> finApply.setValue(finPart, f);
            }

            this.resultConsumers.put(key, consumer);
         }

         return true;
      }
   }

   @Override
   protected void animateInner(ModelPart[] pausedParts) {
      Map<String, Float> prevVals = this.prevResultsOfEntity();
      boolean skip = prevVals != null && EMFAnimationEntityContext.isLODSkippingThisFrame(this.modelName);
      Iterator var4 = this.lines().iterator();

      while (true) {
         EMFAnimationHandler.AnimLineData line;
         boolean needsPause;
         do {
            if (!var4.hasNext()) {
               return;
            }

            line = (EMFAnimationHandler.AnimLineData)var4.next();
            if (pausedParts == null) {
               break;
            }

            needsPause = false;

            for (ModelPart part : pausedParts) {
               if (line.partToApplyTo == part) {
                  needsPause = true;
                  break;
               }
            }
         } while (needsPause);

         if (skip) {
            if (!line.isVar) {
               Float prev = prevVals.get(line.animKey);
               if (prev != null) {
                  this.resultConsumers.get(line.animKey).accept(prev);
               }
            }
         } else {
            float result = this.oldAnimLines.get(line).getResult();
            if (!Float.isNaN(result)) {
               if (prevVals != null) {
                  prevVals.put(line.animKey, result);
               }

               this.resultConsumers.get(line.animKey).accept(result);
            }
         }
      }
   }
}
