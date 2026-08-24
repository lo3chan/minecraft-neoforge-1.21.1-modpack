package net.blay09.mods.balm.client.model.geom;

import java.util.function.Supplier;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

public interface BalmModelLayerRegistrar {
   default ModelLayerLocation register(ResourceLocation location, Supplier<LayerDefinition> layerDefinition) {
      return this.register(location, "main", layerDefinition);
   }

   ModelLayerLocation register(ResourceLocation var1, String var2, Supplier<LayerDefinition> var3);
}
