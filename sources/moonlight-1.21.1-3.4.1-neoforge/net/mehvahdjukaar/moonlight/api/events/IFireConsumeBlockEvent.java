package net.mehvahdjukaar.moonlight.api.events;

import net.mehvahdjukaar.moonlight.api.events.platform.IFireConsumeBlockEventImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IFireConsumeBlockEvent extends SimpleEvent {
   BlockPos getPos();

   BlockState getState();

   LevelAccessor getLevel();

   Direction getFace();

   int getAge();

   int getChance();

   boolean wasReplacedByFire();

   void setFinalState(BlockState var1);

   @Nullable
   BlockState getFinalState();

   static IFireConsumeBlockEvent create(BlockPos var0, Level var1, BlockState var2, int var3, int var4, Direction var5, boolean var6) {
      return IFireConsumeBlockEventImpl.create(var0, var1, var2, var3, var4, var5, var6);
   }
}
