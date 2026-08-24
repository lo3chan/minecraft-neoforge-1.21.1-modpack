package com.aetherteam.aether.entity;

import com.aetherteam.aether.block.dungeon.DoorwayBlock;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.nitrogen.entity.BossMob;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.EventHooks;
import org.apache.commons.lang3.tuple.Pair;

public interface AetherBossMob<T extends Mob & AetherBossMob<T>> extends BossMob<T> {
   default void closeRoom() {
      this.getDungeon().modifyRoom(state -> state.getBlock() instanceof DoorwayBlock ? (BlockState)state.setValue(DoorwayBlock.INVISIBLE, false) : null);
   }

   default void openRoom() {
      this.getDungeon().modifyRoom(state -> state.getBlock() instanceof DoorwayBlock ? (BlockState)state.setValue(DoorwayBlock.INVISIBLE, true) : null);
   }

   default void evaporate(T entity, BlockPos min, BlockPos max, Predicate<BlockState> check) {
      if (EventHooks.canEntityGrief(entity.level(), entity)) {
         for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (entity.level().getBlockState(pos).getBlock() instanceof LiquidBlock && check.test(entity.level().getBlockState(pos))) {
               entity.level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
               this.evaporateEffects(entity, pos);
            } else if (!entity.level().getFluidState(pos).isEmpty()
               && entity.level().getBlockState(pos).hasProperty(BlockStateProperties.WATERLOGGED)
               && check.test(entity.level().getFluidState(pos).createLegacyBlock())) {
               entity.level().setBlockAndUpdate(pos, (BlockState)entity.level().getBlockState(pos).setValue(BlockStateProperties.WATERLOGGED, false));
               this.evaporateEffects(entity, pos);
            }
         }
      }
   }

   default void evaporateEffects(T entity, BlockPos pos) {
      EntityUtil.spawnRemovalParticles(entity.level(), pos);
      entity.level()
         .playSound(
            null,
            pos,
            (SoundEvent)AetherSoundEvents.WATER_EVAPORATE.get(),
            SoundSource.BLOCKS,
            0.5F,
            2.6F + (entity.level().getRandom().nextFloat() - entity.level().getRandom().nextFloat()) * 0.8F
         );
   }

   default Pair<BlockPos, BlockPos> getDefaultBounds(T entity) {
      AABB boundingBox = entity.getBoundingBox();
      BlockPos min = BlockPos.containing(boundingBox.minX - 1.0, boundingBox.minY - 1.0, boundingBox.minZ - 1.0);
      BlockPos max = BlockPos.containing(
         Math.ceil(boundingBox.maxX - 1.0) + 1.0, Math.ceil(boundingBox.maxY - 1.0) + 1.0, Math.ceil(boundingBox.maxZ - 1.0) + 1.0
      );
      return Pair.of(min, max);
   }

   @Nullable
   ResourceLocation getBossBarTexture();

   @Nullable
   ResourceLocation getBossBarBackgroundTexture();

   @Nullable
   default Music getBossMusic() {
      return null;
   }
}
