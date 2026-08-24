package net.mehvahdjukaar.moonlight.api.block;

import java.util.List;
import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public abstract class MimicBlock extends Block {
   protected MimicBlock(Properties properties) {
      super(properties.emissiveRendering(MimicBlock::hasEmissiveRendering));
   }

   private static boolean hasEmissiveRendering(BlockState state, BlockGetter blockGetter, BlockPos pos) {
      if (blockGetter.getBlockEntity(pos) instanceof IBlockHolder tile) {
         BlockState mimic = tile.getHeldBlock();
         return mimic.emissiveRendering(blockGetter, pos);
      } else {
         return false;
      }
   }

   public float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
      if (worldIn.getBlockEntity(pos) instanceof IBlockHolder tile) {
         BlockState mimicState = tile.getHeldBlock();
         if (!mimicState.isAir() && !(mimicState.getBlock() instanceof MimicBlock)) {
            return Math.min(super.getDestroyProgress(state, player, worldIn, pos), mimicState.getDestroyProgress(player, worldIn, pos));
         }
      }

      return super.getDestroyProgress(state, player, worldIn, pos);
   }

   public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, Entity entity) {
      if (world.getBlockEntity(pos) instanceof IBlockHolder tile) {
         BlockState mimicState = tile.getHeldBlock();
         if (!mimicState.isAir()) {
            return mimicState.getSoundType();
         }
      }

      return super.getSoundType(state);
   }

   public List<ItemStack> getDrops(BlockState state, Builder builder) {
      List<ItemStack> drops = super.getDrops(state, builder);
      if (builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof IBlockHolder tile) {
         BlockState heldState = tile.getHeldBlock();
         if (builder.getOptionalParameter(LootContextParams.THIS_ENTITY) instanceof ServerPlayer player
            && !ForgeHelper.canHarvestBlock(
               heldState, builder.getLevel(), BlockPos.containing((Position)builder.getParameter(LootContextParams.ORIGIN)), player
            )) {
            return drops;
         }

         List<ItemStack> newDrops = heldState.getDrops(builder);
         drops.addAll(newDrops);
      }

      return drops;
   }

   public float getExplosionResistance(BlockState state, BlockGetter world, BlockPos pos, Explosion explosion) {
      if (world.getBlockEntity(pos) instanceof IBlockHolder tile) {
         BlockState mimicState = tile.getHeldBlock();
         if (!mimicState.isAir() && !(mimicState.getBlock() instanceof MimicBlock)) {
            return Math.max(ForgeHelper.getExplosionResistance(mimicState, (Level)world, pos, explosion), state.getBlock().getExplosionResistance());
         }
      }

      return 2.0F;
   }

   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      if (level.getBlockEntity(pos) instanceof IBlockHolder tile) {
         BlockState mimic = tile.getHeldBlock();
         return mimic.getBlock().getCloneItemStack(level, pos, state);
      } else {
         return super.getCloneItemStack(level, pos, state);
      }
   }
}
