package net.blay09.mods.balm.neoforge.client.color.block.internal;

import java.util.function.Supplier;
import net.blay09.mods.balm.client.color.block.internal.AbstractBalmBlockColorRegistrar;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;

public class NeoForgeBalmBlockColorRegistrar extends AbstractBalmBlockColorRegistrar {
   private final net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Block event;

   public NeoForgeBalmBlockColorRegistrar(net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Block event) {
      this.event = event;
   }

   @Override
   public void register(BlockColor color, Supplier<Block[]> blocks) {
      this.event.register(color, blocks.get());
   }
}
