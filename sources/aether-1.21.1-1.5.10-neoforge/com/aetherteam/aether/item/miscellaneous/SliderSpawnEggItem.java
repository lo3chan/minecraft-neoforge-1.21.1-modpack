package com.aetherteam.aether.item.miscellaneous;

import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

public class SliderSpawnEggItem extends DeferredSpawnEggItem {
   public SliderSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Properties props) {
      super(type, backgroundColor, highlightColor, props);
   }

   public InteractionResult useOn(UseOnContext context) {
      Level level = context.getLevel();
      if (!(level instanceof ServerLevel)) {
         return InteractionResult.SUCCESS;
      } else {
         ItemStack itemStack = context.getItemInHand();
         BlockPos blockPos = context.getClickedPos();
         Direction direction = context.getClickedFace();
         BlockState blockState = level.getBlockState(blockPos);
         if (blockState.is(Blocks.SPAWNER)) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof SpawnerBlockEntity spawnerBlockEntity) {
               EntityType<?> entityType = this.getType(itemStack);
               spawnerBlockEntity.setEntityId(entityType, level.getRandom());
               blockEntity.setChanged();
               level.sendBlockUpdated(blockPos, blockState, blockState, 3);
               level.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, blockPos);
               itemStack.shrink(1);
               return InteractionResult.CONSUME;
            }
         }

         BlockPos relativePos;
         if (blockState.getCollisionShape(level, blockPos).isEmpty()) {
            relativePos = blockPos;
         } else {
            relativePos = blockPos.relative(direction);
         }

         Vec3 clickLoc = context.getClickLocation();
         BlockPos roundedPos = new BlockPos((int)Math.round(clickLoc.x()), relativePos.getY(), (int)Math.round(clickLoc.z()));
         EntityType<?> entityType = this.getType(itemStack);
         if (entityType.spawn(
               (ServerLevel)level,
               itemStack,
               context.getPlayer(),
               roundedPos,
               MobSpawnType.SPAWN_EGG,
               false,
               !Objects.equals(blockPos, relativePos) && direction == Direction.UP
            )
            != null) {
            itemStack.shrink(1);
            level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
         }

         return InteractionResult.CONSUME;
      }
   }
}
