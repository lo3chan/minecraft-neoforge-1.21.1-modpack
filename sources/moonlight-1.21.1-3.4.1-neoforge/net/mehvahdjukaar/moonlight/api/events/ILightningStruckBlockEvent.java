package net.mehvahdjukaar.moonlight.api.events;

import net.mehvahdjukaar.moonlight.api.events.platform.ILightningStruckBlockEventImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public interface ILightningStruckBlockEvent extends SimpleEvent {
   BlockPos getPos();

   BlockState getState();

   LevelAccessor getLevel();

   LightningBolt getEntity();

   static ILightningStruckBlockEvent create(BlockState var0, LevelAccessor var1, BlockPos var2, LightningBolt var3) {
      return ILightningStruckBlockEventImpl.create(var0, var1, var2, var3);
   }
}
