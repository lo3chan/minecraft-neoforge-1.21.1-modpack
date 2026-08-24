package net.blay09.mods.balm.neoforge.client.model.geom.internal;

import java.util.function.Supplier;
import net.blay09.mods.balm.client.model.geom.BalmModelLayerRegistrar;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;

public class NeoForgeBalmModelLayerRegistrar implements BalmModelLayerRegistrar {
   private final RegisterLayerDefinitions event;

   public NeoForgeBalmModelLayerRegistrar(RegisterLayerDefinitions event) {
      this.event = event;
   }

   @Override
   public ModelLayerLocation register(ResourceLocation location, String layer, Supplier<LayerDefinition> layerDefinition) {
      ModelLayerLocation modelLayerLocation = new ModelLayerLocation(location, layer);
      this.event.registerLayerDefinition(modelLayerLocation, layerDefinition);
      return modelLayerLocation;
   }
}
