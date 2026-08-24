package net.mcreator.borninchaosv.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class FragileScorchedBoardsKoghdaSushchnostKhoditPoBlokuProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      world.destroyBlock(BlockPos.containing(x, y, z), false);
      if (world instanceof Level _level) {
         if (!_level.isClientSide()) {
            _level.playSound(
               null,
               BlockPos.containing(x, y, z),
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.break_wooden_door")),
               SoundSource.NEUTRAL,
               0.3F,
               1.0F
            );
         } else {
            _level.playLocalSound(
               x,
               y,
               z,
               (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.break_wooden_door")),
               SoundSource.NEUTRAL,
               0.3F,
               1.0F,
               false
            );
         }
      }
   }
}
