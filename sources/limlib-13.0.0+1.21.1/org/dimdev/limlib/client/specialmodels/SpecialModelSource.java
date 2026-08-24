package org.dimdev.limlib.client.specialmodels;

import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public interface SpecialModelSource {
   Map<ResourceLocation, ResourceLocation> corners$getSpecialModels();

   void corners$setSpecialModels(Map<ResourceLocation, ResourceLocation> var1);
}
