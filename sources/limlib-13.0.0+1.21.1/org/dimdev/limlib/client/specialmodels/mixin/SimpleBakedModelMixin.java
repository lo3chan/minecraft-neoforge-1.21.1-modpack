package org.dimdev.limlib.client.specialmodels.mixin;

import java.util.List;
import net.minecraft.client.resources.model.SimpleBakedModel;
import org.dimdev.limlib.client.specialmodels.SpecialModelLoadingPlugin;
import org.dimdev.limlib.client.specialmodels.SpecialModelPartsHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({SimpleBakedModel.class})
public class SimpleBakedModelMixin implements SpecialModelPartsHolder {
   @Unique
   private List<SpecialModelLoadingPlugin.SpecialModelPart> corners$specialModelParts = List.of();

   @Override
   public List<SpecialModelLoadingPlugin.SpecialModelPart> corners$getSpecialModelParts() {
      return this.corners$specialModelParts;
   }

   @Override
   public void corners$setSpecialModelParts(List<SpecialModelLoadingPlugin.SpecialModelPart> specialModelParts) {
      this.corners$specialModelParts = specialModelParts.isEmpty() ? List.of() : List.copyOf(specialModelParts);
   }
}
