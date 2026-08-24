package net.mcreator.undeadrevamp.block.listener;

import net.mcreator.undeadrevamp.block.renderer.BasaltechesteTileRenderer;
import net.mcreator.undeadrevamp.init.UndeadRevamp2ModBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;

@EventBusSubscriber(
   modid = "undead_revamp2",
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public class ClientListener {
   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent
   public static void registerRenderers(RegisterRenderers event) {
      event.registerBlockEntityRenderer((BlockEntityType)UndeadRevamp2ModBlockEntities.BASALTECHESTE.get(), context -> new BasaltechesteTileRenderer());
   }
}
