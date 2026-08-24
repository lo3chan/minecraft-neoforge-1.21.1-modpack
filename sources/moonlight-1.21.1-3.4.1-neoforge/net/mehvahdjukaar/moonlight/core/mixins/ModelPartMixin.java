package net.mehvahdjukaar.moonlight.core.mixins;

import net.mehvahdjukaar.moonlight.api.client.model.IModelPartExtension;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({ModelPart.class})
public class ModelPartMixin implements IModelPartExtension {
   @Unique
   private byte moonlight$textWidth = 16;
   @Unique
   private byte moonlight$textHeight = 16;

   @Override
   public void moonlight$setDimensions(int texWidth, int texHeight) {
      this.moonlight$textWidth = (byte)(texWidth / 4);
      this.moonlight$textHeight = (byte)(texHeight / 4);
   }

   @Override
   public int moonlight$getTextHeight() {
      return this.moonlight$textHeight * 4;
   }

   @Override
   public int moonlight$getTextWidth() {
      return this.moonlight$textWidth * 4;
   }
}
