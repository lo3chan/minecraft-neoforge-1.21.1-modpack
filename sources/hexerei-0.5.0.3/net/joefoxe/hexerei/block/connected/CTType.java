package net.joefoxe.hexerei.block.connected;

import net.minecraft.resources.ResourceLocation;

public interface CTType {
   ResourceLocation getId();

   int getSheetSize();

   ConnectedTextureBehaviour.ContextRequirement getContextRequirement();

   int getTextureIndex(ConnectedTextureBehaviour.CTContext var1);

   default int getExtraFaceVariations() {
      return 0;
   }

   default float getPercent() {
      return 1.0F;
   }
}
