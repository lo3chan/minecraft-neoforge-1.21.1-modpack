package net.blay09.mods.balm.neoforge.client.renderer.block.model.internal;

import java.util.Objects;
import net.blay09.mods.balm.client.renderer.block.model.DeferredBlockStateModel;
import net.blay09.mods.balm.client.renderer.block.model.internal.AbstractBalmBlockStateModelRegistrar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent.RegisterAdditional;

public class NeoForgeBalmBlockStateModelRegistrar extends AbstractBalmBlockStateModelRegistrar {
   private final RegisterAdditional event;

   public NeoForgeBalmBlockStateModelRegistrar(RegisterAdditional event) {
      this.event = event;
   }

   @Override
   public DeferredBlockStateModel register(ResourceLocation identifier) {
      ModelResourceLocation standaloneModelKey = new ModelResourceLocation(identifier, "standalone");
      this.event.register(standaloneModelKey);
      return new NeoForgeBalmBlockStateModelRegistrar.NeoForgeDeferredBlockStateModel(standaloneModelKey);
   }

   public record NeoForgeDeferredBlockStateModel(ModelResourceLocation key) implements DeferredBlockStateModel {
      @Override
      public BakedModel asBlockStateModel() {
         return Objects.requireNonNull(Minecraft.getInstance().getModelManager().getModel(this.key));
      }
   }
}
