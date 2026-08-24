package traben.entity_model_features.models.parts;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.model.geom.ModelPart;
import traben.entity_model_features.EMF;
import traben.entity_model_features.config.EMFConfig;
import traben.entity_model_features.utils.EMFUtils;

public class EMFModelPartVanilla extends EMFModelPartWithState {
   final String name;
   final boolean isOptiFinePartSpecified;
   final Set<Integer> hideInTheseStates = new HashSet<>();

   public EMFModelPartVanilla(
      String name, ModelPart vanillaPart, Collection<String> optifinePartNames, Map<String, EMFModelPartVanilla> allVanillaParts, EMFModelPartRoot root
   ) {
      super(new ArrayList<>(), new HashMap<>(), root);
      this.name = name;
      if (((EMFConfig)EMF.config().getConfig()).logModelCreationData) {
         EMFUtils.log(" > EMF vanilla part made: " + name);
      }

      this.isOptiFinePartSpecified = optifinePartNames.contains(name);
      this.setFromState(this.getStateOf(vanillaPart));

      for (Entry<String, ModelPart> child : vanillaPart.children.entrySet()) {
         EMFModelPartVanilla vanilla = new EMFModelPartVanilla(child.getKey(), child.getValue(), optifinePartNames, allVanillaParts, this.getRoot());
         this.children.put(child.getKey(), vanilla);
         allVanillaParts.put(child.getKey(), vanilla);
      }

      this.vanillaChildren = this.children;
      this.allKnownStateVariants.put(0, this.getCurrentState());
   }

   public ModelPart[] getAllEMFCustomChildren() {
      return this.children.values().stream().filter(part -> part instanceof EMFModelPartCustom).toArray(ModelPart[]::new);
   }

   @Override
   protected float[] debugBoxColor() {
      return new float[]{0.0F, 1.0F, 0.0F};
   }

   @Override
   public void render(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int k) {
      if (!this.hideInTheseStates.contains(this.currentModelVariant)) {
         super.render(matrices, vertices, light, overlay, k);
      }
   }

   public void setHideInTheseStates(int variant) {
      this.hideInTheseStates.add(variant);
      this.children.values().forEach(part -> {
         if (part instanceof EMFModelPartVanilla vanilla && !vanilla.isOptiFinePartSpecified) {
            vanilla.setHideInTheseStates(variant);
         }
      });
   }

   @Override
   public String toString() {
      return "[vanilla part " + this.name + "], cubes =" + this.cubes.size() + ", children = " + this.children.size();
   }

   @Override
   public String toStringShort() {
      return "[vanilla part " + this.name + "]";
   }
}
