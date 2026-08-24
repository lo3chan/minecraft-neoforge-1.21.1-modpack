package net.nycto_team.overpacked.registry;

import java.util.function.Supplier;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nycto_team.overpacked.entity.GiantBackpack;
import net.nycto_team.overpacked.entity.model.GiantBackpackModel;
import net.nycto_team.overpacked.entity.model.GiantBackpackOnPlayerModel;
import net.nycto_team.overpacked.entity.renderer.GiantBackpackRenderer;

public class ModEntities {
   public static final DeferredRegister<EntityType<?>> reg = DeferredRegister.create(Registries.ENTITY_TYPE, "overpacked");
   public static final Supplier<EntityType<GiantBackpack>> giant_backpack = reg(
      "giant_backpack", () -> Builder.of(GiantBackpack::new, MobCategory.MISC).sized(1.1F, 1.25F).build("giant_backpack")
   );

   public static void Register(IEventBus bus) {
      reg.register(bus);
   }

   private static <T extends Entity> Supplier<EntityType<T>> reg(String name, Supplier<EntityType<T>> value) {
      return reg.register(name, value);
   }

   @OnlyIn(Dist.CLIENT)
   public static void RegisterLayers(RegisterLayerDefinitions event) {
      event.registerLayerDefinition(ModModelLayers.giant_backpack, GiantBackpackModel::model);
      event.registerLayerDefinition(ModModelLayers.giant_backpack_on_player, GiantBackpackOnPlayerModel::model);
   }

   @OnlyIn(Dist.CLIENT)
   public static void ClientSetup() {
      EntityRenderers.register(giant_backpack.get(), GiantBackpackRenderer::new);
   }
}
