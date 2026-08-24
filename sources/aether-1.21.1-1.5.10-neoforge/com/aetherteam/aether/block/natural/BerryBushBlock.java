package com.aetherteam.aether.block.natural;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.block.AetherBlockStateProperties;
import com.aetherteam.aether.block.AetherBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BerryBushBlock extends AetherBushBlock {
   public BerryBushBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AetherBlockStateProperties.DOUBLE_DROPS, false));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{AetherBlockStateProperties.DOUBLE_DROPS});
   }

   public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      if ((Boolean)AetherConfig.SERVER.berry_bush_consistency.get()
         && entity instanceof LivingEntity
         && entity.getType() != EntityType.FOX
         && entity.getType() != EntityType.BEE) {
         entity.makeStuckInBlock(state, new Vec3(0.800000011920929, 0.75, 0.800000011920929));
      }
   }

   public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if ((Boolean)AetherConfig.SERVER.berry_bush_consistency.get()) {
         Block.dropResources(state, level, pos, null, player, ItemStack.EMPTY);
         level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.getRandom().nextFloat() * 0.4F);
         level.setBlock(
            pos,
            (BlockState)((Block)AetherBlocks.BERRY_BUSH_STEM.get())
               .defaultBlockState()
               .setValue(AetherBlockStateProperties.DOUBLE_DROPS, (Boolean)state.getValue(AetherBlockStateProperties.DOUBLE_DROPS)),
            3
         );
         level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(player, state));
         return InteractionResult.sidedSuccess(level.isClientSide());
      } else {
         return super.useWithoutItem(state, level, pos, player, hit);
      }
   }

   public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
      super.playerDestroy(level, player, pos, state, blockEntity, tool);
      if (tool.getEnchantmentLevel(level.holderOrThrow(Enchantments.SILK_TOUCH)) <= 0) {
         level.setBlock(
            pos,
            (BlockState)((Block)AetherBlocks.BERRY_BUSH_STEM.get())
               .defaultBlockState()
               .setValue(AetherBlockStateProperties.DOUBLE_DROPS, (Boolean)state.getValue(AetherBlockStateProperties.DOUBLE_DROPS)),
            3
         );
         if ((Boolean)AetherConfig.SERVER.berry_bush_consistency.get()) {
            level.destroyBlock(pos, true, player);
         }
      }
   }

   public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
      super.onBlockExploded(state, level, pos, explosion);
      level.setBlock(
         pos,
         (BlockState)((Block)AetherBlocks.BERRY_BUSH_STEM.get())
            .defaultBlockState()
            .setValue(AetherBlockStateProperties.DOUBLE_DROPS, (Boolean)state.getValue(AetherBlockStateProperties.DOUBLE_DROPS)),
         3
      );
      if ((Boolean)AetherConfig.SERVER.berry_bush_consistency.get()) {
         level.destroyBlock(pos, true);
      }
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      try {
         return AetherConfig.SERVER.berry_bush_consistency.get() ? Shapes.empty() : state.getShape(level, pos);
      } catch (IllegalStateException var6) {
         return state.getShape(level, pos);
      }
   }
}
