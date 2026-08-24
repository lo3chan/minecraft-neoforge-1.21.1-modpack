package net.mehvahdjukaar.amendments.common.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.mehvahdjukaar.amendments.common.tile.CarpetedBlockTile;
import net.mehvahdjukaar.amendments.reg.ModBlockProperties;
import net.mehvahdjukaar.moonlight.api.block.IBlockHolder;
import net.mehvahdjukaar.moonlight.api.block.IRecolorable;
import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.mehvahdjukaar.moonlight.api.set.BlocksColorAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CarpetSlabBlock extends SlabBlock implements EntityBlock, IRecolorable {
   public static final MapCodec<CarpetSlabBlock> CODEC = simpleCodec(CarpetSlabBlock::new);
   public static final IntegerProperty LIGHT_LEVEL = ModBlockProperties.LIGHT_LEVEL;
   public static final BooleanProperty SOLID = ModBlockProperties.SOLID;
   protected static final VoxelShape BOTTOM_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);

   public CarpetSlabBlock(Properties properties) {
      super(properties.lightLevel(state -> (Integer)state.getValue(LIGHT_LEVEL)));
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(SOLID, true)).setValue(LIGHT_LEVEL, 0));
   }

   public MapCodec<? extends CarpetSlabBlock> codec() {
      return CODEC;
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return BOTTOM_AABB;
   }

   public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
      return state.getValue(SOLID) ? super.getOcclusionShape(state, level, pos) : Shapes.empty();
   }

   protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
      super.spawnDestroyParticles(level, player, pos, state);
      if (level.getBlockEntity(pos) instanceof IBlockHolder tile) {
         BlockState mimicState = tile.getHeldBlock(1);
         if (!mimicState.isAir()) {
            SoundType sound = mimicState.getSoundType();
            level.playSound(null, pos, sound.getBreakSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, this.soundType.getPitch() * 0.8F);
         }
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{LIGHT_LEVEL, SOLID});
   }

   public float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
      if (worldIn.getBlockEntity(pos) instanceof IBlockHolder tile) {
         BlockState mimicState = tile.getHeldBlock();
         if (!mimicState.isAir() && !(mimicState.getBlock() instanceof CarpetSlabBlock)) {
            return mimicState.getDestroyProgress(player, worldIn, pos);
         }
      }

      return super.getDestroyProgress(state, player, worldIn, pos);
   }

   public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, Entity entity) {
      if (world.getBlockEntity(pos) instanceof CarpetedBlockTile tile) {
         SoundType mixed = tile.getSoundType();
         if (mixed != null) {
            return mixed;
         }
      }

      return super.getSoundType(state);
   }

   public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
      List<ItemStack> drops = super.getDrops(state, builder);
      if (builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof IBlockHolder tile) {
         BlockState heldState = tile.getHeldBlock(0);
         BlockState carpet = tile.getHeldBlock(1);
         if (builder.getOptionalParameter(LootContextParams.THIS_ENTITY) instanceof ServerPlayer player
            && ForgeHelper.canHarvestBlock(heldState, builder.getLevel(), BlockPos.containing((Position)builder.getParameter(LootContextParams.ORIGIN)), player)
            )
          {
            drops.addAll(heldState.getDrops(builder));
         }

         drops.addAll(carpet.getDrops(builder));
      }

      return drops;
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
      if (level.getBlockEntity(pos) instanceof CarpetedBlockTile tile) {
         if (target instanceof BlockHitResult hs && hs.getDirection() == Direction.UP) {
            return tile.getHeldBlock(1).getBlock().getCloneItemStack(level, pos, state);
         } else {
            BlockState mimic = tile.getHeldBlock();
            return mimic.getBlock().getCloneItemStack(level, pos, state);
         }
      } else {
         return super.getCloneItemStack(level, pos, state);
      }
   }

   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      if (level.getBlockEntity(pos) instanceof CarpetedBlockTile tile) {
         BlockState mimic = tile.getHeldBlock();
         return mimic.getBlock().getCloneItemStack(level, pos, state);
      } else {
         return super.getCloneItemStack(level, pos, state);
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new CarpetedBlockTile(pPos, pState);
   }

   public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
      if (!(Boolean)state.getValue(BlockStateProperties.WATERLOGGED) && fluidState.getType() == Fluids.WATER) {
         if (!level.isClientSide() && level.getBlockEntity(pos) instanceof CarpetedBlockTile te && level instanceof Level l) {
            Block.popResource(l, pos, te.getCarpet().getBlock().asItem().getDefaultInstance());
            level.setBlock(pos, (BlockState)te.getHeldBlock().getBlock().withPropertiesOf(state).setValue(BlockStateProperties.WATERLOGGED, true), 3);
            level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean tryRecolor(Level level, BlockPos blockPos, BlockState blockState, @Nullable DyeColor dyeColor) {
      if (level.getBlockEntity(blockPos) instanceof CarpetedBlockTile tile) {
         BlockState c = tile.getHeldBlock();
         if (!c.isAir()) {
            Block otherCarpet = BlocksColorAPI.changeColor(c.getBlock(), dyeColor);
            if (otherCarpet != null && !c.is(otherCarpet)) {
               tile.setHeldBlock(otherCarpet.withPropertiesOf(c));
               tile.setChanged();
               return true;
            }
         }
      }

      return false;
   }

   public boolean isDefaultColor(Level level, BlockPos blockPos, BlockState blockState) {
      if (level.getBlockEntity(blockPos) instanceof CarpetedBlockTile tile) {
         BlockState c = tile.getHeldBlock();
         return BlocksColorAPI.isDefaultColor(c.getBlock());
      } else {
         return false;
      }
   }

   public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
      return false;
   }
}
