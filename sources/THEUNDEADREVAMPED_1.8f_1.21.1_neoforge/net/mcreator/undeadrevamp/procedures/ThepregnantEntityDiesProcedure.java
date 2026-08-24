package net.mcreator.undeadrevamp.procedures;

import java.util.Comparator;
import net.mcreator.undeadrevamp.UndeadRevamp2Mod;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ThepregnantEntityDiesProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity sourceentity) {
      if (sourceentity != null) {
         UndeadRevamp2Mod.queueServerWork(
            20,
            () -> {
               if (world instanceof Level _level) {
                  if (!_level.isClientSide()) {
                     _level.playSound(
                        null,
                        BlockPos.containing(x, y, z),
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:impact")),
                        SoundSource.NEUTRAL,
                        2.0F,
                        1.0F
                     );
                  } else {
                     _level.playLocalSound(
                        x,
                        y,
                        z,
                        (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("undead_revamp2:impact")),
                        SoundSource.NEUTRAL,
                        2.0F,
                        1.0F,
                        false
                     );
                  }
               }

               world.levelEvent(2001, BlockPos.containing(x, y, z), Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z))));
               world.levelEvent(2001, BlockPos.containing(x - 1.0, y, z), Block.getId(world.getBlockState(BlockPos.containing(x - 1.0, y - 1.0, z))));
               world.levelEvent(2001, BlockPos.containing(x + 1.0, y, z), Block.getId(world.getBlockState(BlockPos.containing(x + 1.0, y - 1.0, z))));
               world.levelEvent(2001, BlockPos.containing(x, y, z + 1.0), Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z + 1.0))));
               world.levelEvent(2001, BlockPos.containing(x, y, z - 1.0), Block.getId(world.getBlockState(BlockPos.containing(x, y - 1.0, z - 1.0))));
               Vec3 _center = new Vec3(x, y, z);

               for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(2.25), e -> true)
                  .stream()
                  .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
                  .toList()) {
                  if (!(entityiterator instanceof ItemEntity)) {
                     entityiterator.hurt(new DamageSource(world.holderOrThrow(DamageTypes.FALLING_ANVIL), sourceentity), 10.0F);
                  }
               }
            }
         );
         if (sourceentity instanceof ServerPlayer _player) {
            AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("undead_revamp2:mommy"));
            if (_adv != null) {
               AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
               if (!_ap.isDone()) {
                  for (String criteria : _ap.getRemainingCriteria()) {
                     _player.getAdvancements().award(_adv, criteria);
                  }
               }
            }
         }
      }
   }
}
