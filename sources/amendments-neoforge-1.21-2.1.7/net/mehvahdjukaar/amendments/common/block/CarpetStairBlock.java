package net.mehvahdjukaar.amendments.common.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import net.mehvahdjukaar.amendments.common.tile.CarpetedBlockTile;
import net.mehvahdjukaar.amendments.reg.ModBlockProperties;
import net.mehvahdjukaar.amendments.reg.ModRegistry;
import net.mehvahdjukaar.moonlight.api.block.IBlockHolder;
import net.mehvahdjukaar.moonlight.api.block.IRecolorable;
import net.mehvahdjukaar.moonlight.api.block.IRotatable;
import net.mehvahdjukaar.moonlight.api.block.ModStairBlock;
import net.mehvahdjukaar.moonlight.api.platform.ForgeHelper;
import net.mehvahdjukaar.moonlight.api.set.BlocksColorAPI;
import net.mehvahdjukaar.moonlight.api.util.math.MthUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CarpetStairBlock extends ModStairBlock implements EntityBlock, IRecolorable, IRotatable {
   public static final MapCodec<CarpetStairBlock> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("base_block").forGetter(ModStairBlock::getBaseBlock), propertiesCodec())
         .apply(i, CarpetStairBlock::new)
   );
   public static final IntegerProperty LIGHT_LEVEL = ModBlockProperties.LIGHT_LEVEL;
   public static final BooleanProperty SOLID = ModBlockProperties.SOLID;
   protected static final VoxelShape BOTTOM_AABB = Block.box(0.0, 0.0, -1.0, 16.0, 9.0, 16.0);
   protected static final VoxelShape OCTET_NPN = Block.box(0.0, 8.0, 0.0, 9.0, 17.0, 9.0);
   protected static final VoxelShape OCTET_NPP = Block.box(0.0, 8.0, 7.0, 9.0, 17.0, 16.0);
   protected static final VoxelShape OCTET_PPN = Block.box(7.0, 8.0, 0.0, 16.0, 17.0, 9.0);
   protected static final VoxelShape OCTET_PPP = Block.box(7.0, 8.0, 7.0, 16.0, 17.0, 16.0);
   protected static final VoxelShape[] BOTTOM_SHAPES = makeShapes();
   private static final int[] SHAPE_BY_STATE = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};

   private static VoxelShape[] makeShapes() {
      return IntStream.range(0, 16).mapToObj(CarpetStairBlock::makeStairShape).toArray(VoxelShape[]::new);
   }

   private static VoxelShape makeStairShape(int bitfield) {
      Direction dir = switch (bitfield % 4) {
         case 1 -> Direction.EAST;
         case 2 -> Direction.WEST;
         case 3 -> Direction.SOUTH;
         default -> Direction.NORTH;
      };
      VoxelShape voxelShape = MthUtils.rotateVoxelShape(BOTTOM_AABB, dir);
      if ((bitfield & 1) != 0) {
         voxelShape = Shapes.or(voxelShape, OCTET_NPN);
      }

      if ((bitfield & 2) != 0) {
         voxelShape = Shapes.or(voxelShape, OCTET_PPN);
      }

      if ((bitfield & 4) != 0) {
         voxelShape = Shapes.or(voxelShape, OCTET_NPP);
      }

      if ((bitfield & 8) != 0) {
         voxelShape = Shapes.or(voxelShape, OCTET_PPP);
      }

      return voxelShape;
   }

   public CarpetStairBlock(Block block, Properties properties) {
      super(() -> block, properties.lightLevel(state -> Math.max(0, (Integer)state.getValue(LIGHT_LEVEL))));
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(SOLID, true)).setValue(LIGHT_LEVEL, 0));
   }

   public MapCodec<? extends CarpetStairBlock> codec() {
      return CODEC;
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

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return state.getValue(HALF) == Half.BOTTOM ? BOTTOM_SHAPES[SHAPE_BY_STATE[getShapeIndex(state)]] : super.getShape(state, level, pos, context);
   }

   public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
      return state.getValue(SOLID) ? super.getOcclusionShape(state, level, pos) : Shapes.empty();
   }

   private static int getShapeIndex(BlockState state) {
      return ((StairsShape)state.getValue(SHAPE)).ordinal() * 4 + ((Direction)state.getValue(FACING)).get2DDataValue();
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{LIGHT_LEVEL, SOLID});
   }

   public float getDestroyProgress(BlockState state, Player player, BlockGetter worldIn, BlockPos pos) {
      if (worldIn.getBlockEntity(pos) instanceof IBlockHolder tile) {
         BlockState mimicState = tile.getHeldBlock();
         if (!mimicState.isAir() && !(mimicState.getBlock() instanceof CarpetStairBlock)) {
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

   public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
      super.destroy(level, pos, state);
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

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
      BlockState newState = super.updateShape(state, facing, facingState, world, currentPos, facingPos);
      if (world.getBlockEntity(currentPos) instanceof CarpetedBlockTile tile) {
         BlockState oldHeld = tile.getHeldBlock();
         CarpetedBlockTile otherTile = null;
         if (facingState.is(ModRegistry.CARPET_STAIRS.get()) && world.getBlockEntity(facingPos) instanceof CarpetedBlockTile te2) {
            otherTile = te2;
            facingState = te2.getHeldBlock();
         }

         BlockState newHeld = oldHeld.updateShape(facing, facingState, world, currentPos, facingPos);
         BlockState newFacing = facingState.updateShape(facing.getOpposite(), newHeld, world, facingPos, currentPos);
         if (newFacing != facingState) {
            if (otherTile != null) {
               otherTile.setHeldBlock(newFacing);
               otherTile.setChanged();
            } else {
               world.setBlock(facingPos, newFacing, 2);
            }
         }

         if (newHeld != oldHeld) {
            tile.setHeldBlock(newHeld);
         }
      }

      return newState;
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

   public Optional<BlockState> getRotatedState(
      BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, Rotation rotation, Direction direction, @Nullable Vec3 vec3
   ) {
      return direction.getAxis().isVertical() ? Optional.of(this.rotate(blockState, rotation)) : Optional.empty();
   }

   public void onRotated(BlockState newState, BlockState oldState, LevelAccessor world, BlockPos pos, Rotation rotation, Direction axis, @Nullable Vec3 hit) {
      if (world.getBlockEntity(pos) instanceof CarpetedBlockTile tile) {
         BlockState held = tile.getHeldBlock();
         BlockState newHeld = held.rotate(rotation);
         tile.setHeldBlock(newHeld);
         tile.setChanged();
      }
   }
}
